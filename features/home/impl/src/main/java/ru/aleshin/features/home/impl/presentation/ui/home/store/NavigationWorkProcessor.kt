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
package ru.aleshin.features.home.impl.presentation.ui.home.store

import ru.aleshin.core.domain.entities.categories.MainCategory
import ru.aleshin.core.domain.entities.schedules.TimeTask
import ru.aleshin.core.utils.architecture.store.work.OutputResult
import ru.aleshin.core.utils.architecture.store.work.WorkCommand
import ru.aleshin.core.utils.architecture.store.work.WorkProcessor
import ru.aleshin.core.utils.architecture.store.work.WorkResult
import ru.aleshin.core.utils.functional.TimeRange
import ru.aleshin.core.utils.functional.rightOrElse
import ru.aleshin.features.editor.api.EditorConfig
import ru.aleshin.features.home.impl.domain.interactors.TemplatesInteractor
import ru.aleshin.features.home.impl.presentation.mapppers.schedules.mapToDomain
import ru.aleshin.features.home.impl.presentation.models.schedules.TimeTaskUi
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

    class Base @Inject constructor(
        private val templatesInteractor: TemplatesInteractor,
    ) : NavigationWorkProcessor {

        override suspend fun work(command: NavigationWorkCommand) = when (command) {
            is NavigationWorkCommand.NavigateToOverview -> navigateToOverviewWork()
            is NavigationWorkCommand.NavigateToEditorCreator -> navigateToEditorCreator(command.currentDate, command.timeRange)
            is NavigationWorkCommand.NavigateToEditor -> navigateToEditor(command.timeTask)
        }

        private fun navigateToOverviewWork(): NavigationWorkResult {
            return OutputResult(HomeOutput.NavigateToOverview)
        }

        private suspend fun navigateToEditor(timeTask: TimeTaskUi): NavigationWorkResult {
            val template = templatesInteractor.checkIsTemplate(timeTask.mapToDomain()).rightOrElse(null)
            val config = EditorConfig.Editor(timeTask.mapToDomain(), template)
            return OutputResult(HomeOutput.NavigateToEditor(config))
        }

        private fun navigateToEditorCreator(date: Date, timeRange: TimeRange): NavigationWorkResult {
            val timeTask = TimeTask(date = date, category = MainCategory(), createdAt = Date(), timeRange = timeRange)
            val config = EditorConfig.Editor(timeTask, null)
            return OutputResult(HomeOutput.NavigateToEditor(config))
        }
    }
}

internal sealed class NavigationWorkCommand : WorkCommand {
    object NavigateToOverview : NavigationWorkCommand()
    data class NavigateToEditor(val timeTask: TimeTaskUi) : NavigationWorkCommand()
    data class NavigateToEditorCreator(val currentDate: Date, val timeRange: TimeRange) : NavigationWorkCommand()
}

internal typealias NavigationWorkResult = WorkResult<HomeAction, HomeEffect, HomeOutput>