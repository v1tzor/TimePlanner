/*
 * Copyright 2023 Stanislav Aleshin
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
package ru.aleshin.features.home.impl.presentation.ui.overview.screenmodel

import kotlinx.coroutines.flow.flow
import ru.aleshin.core.utils.extensions.isIncludeTime
import ru.aleshin.core.utils.extensions.mapToDate
import ru.aleshin.core.utils.extensions.shiftDay
import ru.aleshin.core.utils.extensions.startThisDay
import ru.aleshin.core.utils.functional.TimeRange
import ru.aleshin.core.utils.functional.collectAndHandle
import ru.aleshin.core.utils.functional.handle
import ru.aleshin.core.utils.managers.DateManager
import ru.aleshin.core.utils.platform.screenmodel.work.ActionResult
import ru.aleshin.core.utils.platform.screenmodel.work.EffectResult
import ru.aleshin.core.utils.platform.screenmodel.work.FlowWorkProcessor
import ru.aleshin.core.utils.platform.screenmodel.work.WorkCommand
import ru.aleshin.core.utils.platform.screenmodel.work.WorkResult
import ru.aleshin.features.editor.api.navigations.EditorScreens
import ru.aleshin.features.home.impl.domain.interactors.CategoriesInteractor
import ru.aleshin.features.home.impl.domain.interactors.ScheduleInteractor
import ru.aleshin.features.home.impl.domain.interactors.UndefinedTasksInteractor
import ru.aleshin.features.home.impl.navigation.NavigationManager
import ru.aleshin.features.home.impl.presentation.mapppers.categories.mapToUi
import ru.aleshin.features.home.impl.presentation.mapppers.schedules.ScheduleDomainToUiMapper
import ru.aleshin.features.home.impl.presentation.mapppers.schedules.mapToDomain
import ru.aleshin.features.home.impl.presentation.mapppers.schedules.mapToUi
import ru.aleshin.features.home.impl.presentation.models.schedules.UndefinedTaskUi
import ru.aleshin.features.home.impl.presentation.models.schedules.convertToTimeTask
import ru.aleshin.features.home.impl.presentation.ui.overview.contract.OverviewAction
import ru.aleshin.features.home.impl.presentation.ui.overview.contract.OverviewEffect
import java.util.Date
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 02.11.2023.
 */
internal interface OverviewWorkProcessor : FlowWorkProcessor<OverviewWorkCommand, OverviewAction, OverviewEffect> {

    class Base @Inject constructor(
        private val scheduleInteractor: ScheduleInteractor,
        private val categoriesInteractor: CategoriesInteractor,
        private val undefinedTasksInteractor: UndefinedTasksInteractor,
        private val schedulesUiMapper: ScheduleDomainToUiMapper,
        private val navigationManager: NavigationManager,
        private val dateManager: DateManager,
    ) : OverviewWorkProcessor {

        override suspend fun work(command: OverviewWorkCommand) = when (command) {
            is OverviewWorkCommand.LoadSchedules -> loadSchedulesWork()
            is OverviewWorkCommand.LoadUndefinedTasks -> loadUndefinedTasks()
            is OverviewWorkCommand.LoadCategories -> loadCategoriesWork()
            is OverviewWorkCommand.CreateOrUpdateUndefinedTask -> createOrUpdateTaskWork(command.task)
            is OverviewWorkCommand.ExecuteUndefinedTask -> executeUndefinedTaskWork(command.data, command.task)
            is OverviewWorkCommand.DeleteUndefinedTask -> deleteUndefinedTaskWork(command.task)
        }

        private fun loadSchedulesWork() = flow {
            val currentDate = dateManager.fetchBeginningCurrentDay()
            val previewTimeRange = TimeRange(currentDate.shiftDay(-1), currentDate.shiftDay(2))
            scheduleInteractor.fetchOverviewSchedules().handle(
                onLeftAction = { emit(EffectResult(OverviewEffect.ShowError(it))) },
                onRightAction = { schedules ->
                    val previewSchedules = schedules.filter { previewTimeRange.isIncludeTime(it.date.mapToDate()) }
                    emit(ActionResult(OverviewAction.UpdateSchedules(currentDate, previewSchedules.map { schedulesUiMapper.map(it) })))
                },
            )
        }

        private fun loadUndefinedTasks() = flow {
            undefinedTasksInteractor.fetchAllUndefinedTasks().collectAndHandle(
                onLeftAction = { emit(EffectResult(OverviewEffect.ShowError(it))) },
                onRightAction = { tasks ->
                    emit(ActionResult(OverviewAction.UpdateUndefinedTasks(tasks.map { it.mapToUi() })))
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

        private fun createOrUpdateTaskWork(task: UndefinedTaskUi) = flow {
            undefinedTasksInteractor.addOrUpdateUndefinedTask(task.mapToDomain()).handle(
                onLeftAction = { emit(EffectResult(OverviewEffect.ShowError(it))) },
            )
        }

        private fun executeUndefinedTaskWork(date: Date, task: UndefinedTaskUi) = flow<WorkResult<OverviewAction, OverviewEffect>> {
            val targetTime = dateManager.setCurrentHMS(date)
            val timeTask = task.convertToTimeTask(date.startThisDay(), TimeRange(targetTime, targetTime))
            val screen = EditorScreens.Editor(
                timeTask = timeTask.mapToDomain(), 
                template = null, 
                undefinedTaskId = task.id,
            )
            navigationManager.navigateToEditorFeature(screen)
        }

        private fun deleteUndefinedTaskWork(task: UndefinedTaskUi) = flow {
            undefinedTasksInteractor.deleteUndefinedTask(task.mapToDomain()).handle(
                onLeftAction = { emit(EffectResult(OverviewEffect.ShowError(it))) },
            )
        }
    }
}

internal sealed class OverviewWorkCommand : WorkCommand {
    object LoadSchedules : OverviewWorkCommand()
    object LoadUndefinedTasks : OverviewWorkCommand()
    object LoadCategories : OverviewWorkCommand()
    data class CreateOrUpdateUndefinedTask(val task: UndefinedTaskUi) : OverviewWorkCommand()
    data class ExecuteUndefinedTask(val data: Date, val task: UndefinedTaskUi) : OverviewWorkCommand()
    data class DeleteUndefinedTask(val task: UndefinedTaskUi) : OverviewWorkCommand()
}
