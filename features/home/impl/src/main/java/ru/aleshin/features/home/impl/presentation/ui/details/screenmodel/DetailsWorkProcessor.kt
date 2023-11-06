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
package ru.aleshin.features.home.impl.presentation.ui.details.screenmodel

import kotlinx.coroutines.flow.flow
import ru.aleshin.core.utils.functional.handle
import ru.aleshin.core.utils.managers.DateManager
import ru.aleshin.core.utils.platform.screenmodel.work.ActionResult
import ru.aleshin.core.utils.platform.screenmodel.work.EffectResult
import ru.aleshin.core.utils.platform.screenmodel.work.FlowWorkProcessor
import ru.aleshin.core.utils.platform.screenmodel.work.WorkCommand
import ru.aleshin.features.home.impl.domain.interactors.ScheduleInteractor
import ru.aleshin.features.home.impl.presentation.mapppers.schedules.ScheduleDomainToUiMapper
import ru.aleshin.features.home.impl.presentation.ui.details.contract.DetailsAction
import ru.aleshin.features.home.impl.presentation.ui.details.contract.DetailsEffect
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 02.11.2023.
 */
internal interface DetailsWorkProcessor : FlowWorkProcessor<DetailsWorkCommand, DetailsAction, DetailsEffect> {

    class Base @Inject constructor(
        private val scheduleInteractor: ScheduleInteractor,
        private val schedulesUiMapper: ScheduleDomainToUiMapper,
        private val dateManager: DateManager,
    ) : DetailsWorkProcessor {

        override suspend fun work(command: DetailsWorkCommand) = when (command) {
            is DetailsWorkCommand.LoadSchedules -> loadSchedulesWork()
        }

        private fun loadSchedulesWork() = flow {
            val currentDate = dateManager.fetchBeginningCurrentDay()
            scheduleInteractor.fetchOverviewSchedules().handle(
                onLeftAction = { emit(EffectResult(DetailsEffect.ShowError(it))) },
                onRightAction = { schedules ->
                    emit(ActionResult(DetailsAction.UpdateSchedules(currentDate, schedules.map { schedulesUiMapper.map(it) })))
                },
            )
        }
    }
}

internal sealed class DetailsWorkCommand : WorkCommand {
    object LoadSchedules : DetailsWorkCommand()
}
