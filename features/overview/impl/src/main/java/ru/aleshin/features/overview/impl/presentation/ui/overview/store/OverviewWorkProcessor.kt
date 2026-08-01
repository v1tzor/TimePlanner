/*
 * Copyright 2026 Stanislav Aleshin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ru.aleshin.features.overview.impl.presentation.ui.overview.store

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import ru.aleshin.core.presentation.mappers.mapToDomain
import ru.aleshin.core.presentation.mappers.mapToUi
import ru.aleshin.core.presentation.models.tasks.UndefinedTaskUi
import ru.aleshin.core.utils.architecture.store.work.ActionResult
import ru.aleshin.core.utils.architecture.store.work.EffectResult
import ru.aleshin.core.utils.architecture.store.work.FlowWorkProcessor
import ru.aleshin.core.utils.architecture.store.work.OutputResult
import ru.aleshin.core.utils.architecture.store.work.WorkCommand
import ru.aleshin.core.utils.architecture.store.work.WorkResult
import ru.aleshin.core.utils.extensions.startThisDay
import ru.aleshin.core.utils.functional.Constants
import ru.aleshin.core.utils.functional.TimeRange
import ru.aleshin.core.utils.functional.collectAndHandle
import ru.aleshin.core.utils.functional.handle
import ru.aleshin.core.utils.managers.DateManager
import ru.aleshin.features.editor.api.EditorConfig
import ru.aleshin.features.overview.impl.domain.interactors.GoalsHistoryInteractor
import ru.aleshin.features.overview.impl.domain.interactors.GoalsInteractor
import ru.aleshin.features.overview.impl.domain.interactors.MainCategoriesInteractor
import ru.aleshin.features.overview.impl.domain.interactors.ScheduleInteractor
import ru.aleshin.features.overview.impl.domain.interactors.ShareTextInteractor
import ru.aleshin.features.overview.impl.domain.interactors.UndefinedTasksInteractor
import ru.aleshin.features.overview.impl.presentation.mapppers.mapToUi
import ru.aleshin.features.overview.impl.presentation.ui.overview.contract.OverviewAction
import ru.aleshin.features.overview.impl.presentation.ui.overview.contract.OverviewEffect
import ru.aleshin.features.overview.impl.presentation.ui.overview.contract.OverviewOutput
import java.util.Date
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 02.11.2023.
 */
internal interface OverviewWorkProcessor :
    FlowWorkProcessor<OverviewWorkCommand, OverviewAction, OverviewEffect, OverviewOutput> {

    class Base @Inject constructor(
        private val scheduleInteractor: ScheduleInteractor,
        private val goalsInteractor: GoalsInteractor,
        private val goalsHistoryInteractor: GoalsHistoryInteractor,
        private val categoriesInteractor: MainCategoriesInteractor,
        private val undefinedTasksInteractor: UndefinedTasksInteractor,
        private val shareTextInteractor: ShareTextInteractor,
        private val dateManager: DateManager,
    ) : OverviewWorkProcessor {

        override suspend fun work(command: OverviewWorkCommand) = when (command) {
            is OverviewWorkCommand.LoadSchedules -> loadSchedulesWork(command.selectedDate)
            is OverviewWorkCommand.LoadGoals -> loadGoalsWork()
            is OverviewWorkCommand.SyncGoalHistory -> syncGoalHistoryWork()
            is OverviewWorkCommand.LoadUndefinedTasks -> loadUndefinedTasks()
            is OverviewWorkCommand.LoadCategories -> loadCategoriesWork()
            is OverviewWorkCommand.CreateOrUpdateUndefinedTasks -> createOrUpdateTasksWork(command.tasks)
            is OverviewWorkCommand.PrepareSharedTextImport -> prepareSharedTextImportWork(command.text)
            is OverviewWorkCommand.ExecuteUndefinedTask -> executeUndefinedTaskWork(command.data, command.task)
            is OverviewWorkCommand.DeleteUndefinedTask -> deleteUndefinedTaskWork(command.taskId)
        }

        private fun loadSchedulesWork(selectedDate: Date?) = flow<OverviewWorkResult> {
            scheduleInteractor.fetchWeekOverview().collectAndHandle(
                onLeftAction = { emit(EffectResult(OverviewEffect.ShowError(it))) },
                onRightAction = { weekOverview ->
                    val weekOverviewUi = weekOverview.mapToUi()
                    val actualSelectedDate = selectedDate ?: weekOverviewUi.schedules.first().date
                    emit(ActionResult(OverviewAction.UpdateWeekOverview(weekOverviewUi, actualSelectedDate)))
                    delay(Constants.Delay.OVERVIEW)
                    emit(ActionResult(OverviewAction.UpdateLoading(false)))
                }
            )
        }.onStart {
            emit(ActionResult(OverviewAction.UpdateLoading(true)))
        }

        private fun loadGoalsWork() = flow<OverviewWorkResult> {
            goalsInteractor.fetchGoalsProgress().collectAndHandle(
                onLeftAction = { emit(EffectResult(OverviewEffect.ShowError(it))) },
                onRightAction = { goals ->
                    val goals = goals.map { goal -> goal.mapToUi() }
                    emit(ActionResult(OverviewAction.UpdateGoals(goals = goals)))
                }
            )
        }

        private fun syncGoalHistoryWork() = flow<OverviewWorkResult> {
            goalsHistoryInteractor.syncCompletedGoals().handle(
                onLeftAction = { emit(EffectResult(OverviewEffect.ShowError(it))) },
            )
        }

        private fun loadUndefinedTasks() = flow {
            undefinedTasksInteractor.fetchAllUndefinedTasks().collectAndHandle(
                onLeftAction = { emit(EffectResult(OverviewEffect.ShowError(it))) },
                onRightAction = { tasks ->
                    emit(ActionResult(OverviewAction.UpdateUndefinedTasks(tasks.map { task -> task.mapToUi() })))
                },
            )
        }

        private fun loadCategoriesWork() = flow {
            categoriesInteractor.fetchCategories().collectAndHandle(
                onLeftAction = { emit(EffectResult(OverviewEffect.ShowError(it))) },
                onRightAction = { categories ->
                    emit(ActionResult(OverviewAction.UpdateCategories(categories.map { it.mapToUi() })))
                },
            )
        }

        private fun createOrUpdateTasksWork(tasks: List<UndefinedTaskUi>) = flow {
            undefinedTasksInteractor.addOrUpdateUndefinedTasks(tasks.map { it.mapToDomain() }).handle(
                onLeftAction = { emit(EffectResult(OverviewEffect.ShowError(it))) },
            )
        }

        private fun prepareSharedTextImportWork(text: String) = flow {
            shareTextInteractor.fetchSharedTextTasks(text).handle(
                onLeftAction = { emit(EffectResult(OverviewEffect.ShowError(it))) },
                onRightAction = { tasks ->
                    val tasks = tasks.map { it.mapToUi() }
                    emit(ActionResult(OverviewAction.UpdateSharedTextTasks(tasks)))
                }
            )
        }

        private fun executeUndefinedTaskWork(date: Date, task: UndefinedTaskUi) = flow<OverviewWorkResult> {
            val targetTime = dateManager.setCurrentHMS(date)
            val config = EditorConfig.Task(
                date = date.startThisDay(),
                timeRange = TimeRange(targetTime, targetTime),
                undefinedTaskId = task.id,
            )
            emit(OutputResult(OverviewOutput.NavigateToTaskEditor(config)))
        }

        private fun deleteUndefinedTaskWork(taskId: Long) = flow<OverviewWorkResult> {
            undefinedTasksInteractor.deleteUndefinedTaskById(taskId).handle(
                onLeftAction = { emit(EffectResult(OverviewEffect.ShowError(it))) },
            )
        }

    }
}

internal sealed class OverviewWorkCommand : WorkCommand {
    data class LoadSchedules(val selectedDate: Date?) : OverviewWorkCommand()
    data object LoadGoals : OverviewWorkCommand()
    data object SyncGoalHistory : OverviewWorkCommand()
    data object LoadUndefinedTasks : OverviewWorkCommand()
    data object LoadCategories : OverviewWorkCommand()
    data class CreateOrUpdateUndefinedTasks(val tasks: List<UndefinedTaskUi>) : OverviewWorkCommand()
    data class PrepareSharedTextImport(val text: String) : OverviewWorkCommand()
    data class ExecuteUndefinedTask(val data: Date, val task: UndefinedTaskUi) : OverviewWorkCommand()
    data class DeleteUndefinedTask(val taskId: Long) : OverviewWorkCommand()
}

internal typealias OverviewWorkResult = WorkResult<OverviewAction, OverviewEffect, OverviewOutput>
