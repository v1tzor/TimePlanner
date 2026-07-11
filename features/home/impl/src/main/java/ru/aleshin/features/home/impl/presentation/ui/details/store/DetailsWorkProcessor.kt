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
package ru.aleshin.features.home.impl.presentation.ui.details.store

import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import ru.aleshin.core.presentation.mappers.mapToUi
import ru.aleshin.core.utils.architecture.store.work.ActionResult
import ru.aleshin.core.utils.architecture.store.work.EffectResult
import ru.aleshin.core.utils.architecture.store.work.FlowWorkProcessor
import ru.aleshin.core.utils.architecture.store.work.WorkCommand
import ru.aleshin.core.utils.architecture.store.work.WorkResult
import ru.aleshin.core.utils.extensions.shiftDay
import ru.aleshin.core.utils.functional.Constants.Date.OVERVIEW_NEXT_DAYS
import ru.aleshin.core.utils.functional.Constants.Date.OVERVIEW_PREVIOUS_DAYS
import ru.aleshin.core.utils.functional.collectAndHandle
import ru.aleshin.core.utils.functional.toRange
import ru.aleshin.core.utils.managers.DateManager
import ru.aleshin.features.home.impl.domain.interactors.ScheduleInteractor
import ru.aleshin.features.home.impl.presentation.ui.details.contract.DetailsAction
import ru.aleshin.features.home.impl.presentation.ui.details.contract.DetailsEffect
import ru.aleshin.features.home.impl.presentation.ui.details.contract.DetailsOutput
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 02.11.2023.
 */
internal interface DetailsWorkProcessor :
    FlowWorkProcessor<DetailsWorkCommand, DetailsAction, DetailsEffect, DetailsOutput> {

    class Base @Inject constructor(
        private val scheduleInteractor: ScheduleInteractor,
        private val dateManager: DateManager,
    ) : DetailsWorkProcessor {

        override suspend fun work(command: DetailsWorkCommand) = when (command) {
            is DetailsWorkCommand.LoadSchedules -> loadSchedulesWork()
        }

        private fun loadSchedulesWork() = flow<DetailsWorkResult> {
            val currentDate = dateManager.fetchBeginningCurrentDay()
            val startDate = currentDate.shiftDay(-OVERVIEW_PREVIOUS_DAYS)
            val endDate = currentDate.shiftDay(OVERVIEW_NEXT_DAYS)

            scheduleInteractor.fetchOverviewSchedules(startDate toRange endDate).collectAndHandle(
                onLeftAction = { emit(EffectResult(DetailsEffect.ShowError(it))) },
                onRightAction = { schedules ->
                    val schedules = schedules.map { it.mapToUi() }
                    emit(ActionResult(DetailsAction.UpdateSchedules(currentDate, schedules)))
                }
            )
        }.onStart {
            emit(ActionResult(DetailsAction.UpdateLoading(true)))
        }
    }
}

internal sealed class DetailsWorkCommand : WorkCommand {
    object LoadSchedules : DetailsWorkCommand()
}

internal typealias DetailsWorkResult = WorkResult<DetailsAction, DetailsEffect, DetailsOutput>