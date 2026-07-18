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
package ru.aleshin.features.home.impl.presentation.ui.home.store

import ru.aleshin.core.utils.architecture.store.work.OutputResult
import ru.aleshin.core.utils.architecture.store.work.WorkCommand
import ru.aleshin.core.utils.architecture.store.work.WorkProcessor
import ru.aleshin.core.utils.architecture.store.work.WorkResult
import ru.aleshin.core.utils.functional.TimeRange
import ru.aleshin.features.editor.api.EditorConfig
import ru.aleshin.features.home.impl.presentation.ui.home.contract.HomeAction
import ru.aleshin.features.home.impl.presentation.ui.home.contract.HomeEffect
import ru.aleshin.features.home.impl.presentation.ui.home.contract.HomeOutput
import java.util.Date
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 07.03.2023.
 */
internal interface NavigationWorkProcessor :
    WorkProcessor<NavigationWorkCommand, HomeAction, HomeEffect, HomeOutput> {

    class Base @Inject constructor() : NavigationWorkProcessor {

        override suspend fun work(command: NavigationWorkCommand) = when (command) {
            is NavigationWorkCommand.NavigateToSettings -> navigateToSettingsWork()
            is NavigationWorkCommand.NavigateToEditorCreator -> navigateToEditorCreator(command.currentDate, command.timeRange)
            is NavigationWorkCommand.NavigateToEditor -> navigateToEditor(command.timeTaskId)
        }

        private fun navigateToSettingsWork(): NavigationWorkResult {
            return OutputResult(HomeOutput.NavigateToSettings)
        }

        private suspend fun navigateToEditor(timeTaskId: Long): NavigationWorkResult {
            val config = EditorConfig.Task(timeTaskId = timeTaskId)
            return OutputResult(HomeOutput.NavigateToEditor(config))
        }

        private fun navigateToEditorCreator(date: Date, timeRange: TimeRange?): NavigationWorkResult {
            val config = EditorConfig.Task(date = date, timeRange = timeRange)
            return OutputResult(HomeOutput.NavigateToEditor(config))
        }
    }
}

internal sealed class NavigationWorkCommand : WorkCommand {
    object NavigateToSettings : NavigationWorkCommand()
    data class NavigateToEditor(val timeTaskId: Long) : NavigationWorkCommand()
    data class NavigateToEditorCreator(val currentDate: Date, val timeRange: TimeRange?) : NavigationWorkCommand()
}

internal typealias NavigationWorkResult = WorkResult<HomeAction, HomeEffect, HomeOutput>
