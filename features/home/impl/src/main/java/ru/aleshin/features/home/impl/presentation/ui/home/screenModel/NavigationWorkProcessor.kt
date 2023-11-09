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
package ru.aleshin.features.home.impl.presentation.ui.home.screenModel

import ru.aleshin.core.utils.functional.TimeRange
import ru.aleshin.core.utils.functional.rightOrElse
import ru.aleshin.core.utils.platform.screenmodel.work.ActionResult
import ru.aleshin.core.utils.platform.screenmodel.work.WorkCommand
import ru.aleshin.core.utils.platform.screenmodel.work.WorkProcessor
import ru.aleshin.core.utils.platform.screenmodel.work.WorkResult
import ru.aleshin.features.editor.api.navigations.EditorScreens
import ru.aleshin.features.home.api.domain.entities.categories.MainCategory
import ru.aleshin.features.home.api.domain.entities.schedules.TimeTask
import ru.aleshin.features.home.api.navigation.HomeScreens
import ru.aleshin.features.home.impl.domain.interactors.TemplatesInteractor
import ru.aleshin.features.home.impl.navigation.NavigationManager
import ru.aleshin.features.home.impl.presentation.mapppers.schedules.mapToDomain
import ru.aleshin.features.home.impl.presentation.models.schedules.TimeTaskUi
import ru.aleshin.features.home.impl.presentation.ui.home.contract.HomeAction
import ru.aleshin.features.home.impl.presentation.ui.home.contract.HomeEffect
import java.util.*
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 07.03.2023.
 */
internal interface NavigationWorkProcessor : WorkProcessor<NavigationWorkCommand, HomeAction, HomeEffect> {

    class Base @Inject constructor(
        private val navigationManager: NavigationManager,
        private val templatesInteractor: TemplatesInteractor,
    ) : NavigationWorkProcessor {

        override suspend fun work(command: NavigationWorkCommand) = when (command) {
            is NavigationWorkCommand.NavigateToOverview -> navigateToOverviewWork()
            is NavigationWorkCommand.NavigateToEditorCreator -> navigateToEditorCreator(command.currentDate, command.timeRange)
            is NavigationWorkCommand.NavigateToEditor -> navigateToEditor(command.timeTask)
        }

        private fun navigateToOverviewWork(): WorkResult<HomeAction, HomeEffect> {
            val screen = HomeScreens.Overview
            return navigationManager.navigateToLocal(screen, false).let {
                ActionResult(HomeAction.Navigate)
            }
        }

        private suspend fun navigateToEditor(timeTask: TimeTaskUi): WorkResult<HomeAction, HomeEffect> {
            val template = templatesInteractor.checkIsTemplate(timeTask.mapToDomain()).rightOrElse(null)
            val screen = EditorScreens.Editor(timeTask.mapToDomain(), template)
            return navigationManager.navigateToEditorFeature(screen).let {
                ActionResult(HomeAction.Navigate)
            }
        }

        private fun navigateToEditorCreator(date: Date, timeRange: TimeRange): WorkResult<HomeAction, HomeEffect> {
            val timeTask = TimeTask(date = date, category = MainCategory(), createdAt = Date(), timeRange = timeRange)
            val screen = EditorScreens.Editor(timeTask, null)
            return navigationManager.navigateToEditorFeature(screen).let {
                ActionResult(HomeAction.Navigate)
            }
        }
    }
}

internal sealed class NavigationWorkCommand : WorkCommand {
    object NavigateToOverview : NavigationWorkCommand()
    data class NavigateToEditor(val timeTask: TimeTaskUi) : NavigationWorkCommand()
    data class NavigateToEditorCreator(val currentDate: Date, val timeRange: TimeRange) : NavigationWorkCommand()
}
