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
package ru.aleshin.timeplanner.presentation.ui.main.store

import kotlinx.coroutines.delay
import ru.aleshin.core.domain.entities.schedules.TimeTask
import ru.aleshin.core.utils.architecture.store.work.ActionResult
import ru.aleshin.core.utils.architecture.store.work.OutputResult
import ru.aleshin.core.utils.architecture.store.work.WorkCommand
import ru.aleshin.core.utils.architecture.store.work.WorkProcessor
import ru.aleshin.core.utils.architecture.store.work.WorkResult
import ru.aleshin.core.utils.functional.Constants
import ru.aleshin.core.utils.functional.TimeRange
import ru.aleshin.core.utils.managers.DateManager
import ru.aleshin.features.editor.api.EditorFeatureComponent
import ru.aleshin.timeplanner.presentation.ui.main.contract.DeepLinkTarget
import ru.aleshin.timeplanner.presentation.ui.main.contract.MainAction
import ru.aleshin.timeplanner.presentation.ui.main.contract.MainEffect
import ru.aleshin.timeplanner.presentation.ui.main.contract.MainOutput
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 04.08.2023.
 */
interface NavigationWorkProcessor : WorkProcessor<NavWorkCommand, MainAction, MainEffect, MainOutput> {

    class Base @Inject constructor(
        private val dateManager: DateManager,
    ) : NavigationWorkProcessor {

        override suspend fun work(command: NavWorkCommand) = when (command) {
            is NavWorkCommand.InitialNavigation -> initialNavigationWork()
            is NavWorkCommand.ProcessDeepLink -> processDeepLink(command.deepLinkTarget)
        }

        private suspend fun initialNavigationWork(): NavWorkResult {
            delay(Constants.Delay.SPLASH_NAV)
            return OutputResult(MainOutput.NavigateToTabNavigation)
        }

        private fun processDeepLink(deepLinkTarget: DeepLinkTarget): NavWorkResult {
            return if (deepLinkTarget == DeepLinkTarget.EDITOR) {
                val currentTime = dateManager.fetchCurrentDate()
                val currentDate = dateManager.fetchBeginningCurrentDay()
                val timeRange = TimeRange(currentTime, currentTime)
                val timeTask = TimeTask(date = currentDate, createdAt = currentTime, timeRange = timeRange)

                val config = EditorFeatureComponent.EditorConfig.Editor(timeTask, null)
                OutputResult(MainOutput.NavigateToEditor(config))
            } else {
                ActionResult(MainAction.Navigate)
            }
        }
    }
}

sealed class NavWorkCommand : WorkCommand {
    data object InitialNavigation : NavWorkCommand()
    data class ProcessDeepLink(val deepLinkTarget: DeepLinkTarget) : NavWorkCommand()
}

typealias NavWorkResult = WorkResult<MainAction, MainEffect, MainOutput>