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
 * imitations under the License.
 */
package ru.aleshin.features.home.impl.presentation.ui.home.screenModel

import ru.aleshin.core.utils.functional.TimeRange
import ru.aleshin.core.utils.functional.rightOrElse
import ru.aleshin.core.utils.platform.screenmodel.work.ActionResult
import ru.aleshin.core.utils.platform.screenmodel.work.WorkCommand
import ru.aleshin.core.utils.platform.screenmodel.work.WorkProcessor
import ru.aleshin.core.utils.platform.screenmodel.work.WorkResult
import ru.aleshin.features.editor.api.domain.EditModel
import ru.aleshin.features.home.api.domains.entities.categories.MainCategory
import ru.aleshin.features.home.impl.domain.interactors.TemplatesInteractor
import ru.aleshin.features.home.impl.navigation.NavigationManager
import ru.aleshin.features.home.impl.presentation.mapppers.TimeTaskToEditModelMapper
import ru.aleshin.features.home.impl.presentation.mapppers.TimeTaskUiToDomainMapper
import ru.aleshin.features.home.impl.presentation.models.TimeTaskUi
import ru.aleshin.features.home.impl.presentation.ui.home.contract.HomeAction
import ru.aleshin.features.home.impl.presentation.ui.home.contract.HomeEffect
import java.util.*
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 07.03.2023.
 */
internal interface NavigationWorkProcessor : WorkProcessor<NavigationWorkCommand, HomeAction, HomeEffect> {

    suspend fun navigateToEditor(timeTask: TimeTaskUi): WorkResult<HomeAction, HomeEffect>

    suspend fun navigateToEditorWithEmptyData(date: Date, timeRange: TimeRange): WorkResult<HomeAction, HomeEffect>

    class Base @Inject constructor(
        private val navigationManager: NavigationManager,
        private val templatesInteractor: TemplatesInteractor,
        private val mapperToDomain: TimeTaskUiToDomainMapper,
        private val mapperToEditModel: TimeTaskToEditModelMapper,
    ) : NavigationWorkProcessor {

        override suspend fun navigateToEditor(timeTask: TimeTaskUi) = work(
            command = NavigationWorkCommand.NavigateToEditorWithTimeTask(timeTask),
        )

        override suspend fun navigateToEditorWithEmptyData(date: Date, timeRange: TimeRange) = work(
            command = NavigationWorkCommand.NavigateToEditorWithEmptyTimeTask(date, timeRange),
        )

        override suspend fun work(command: NavigationWorkCommand) = when (command) {
            is NavigationWorkCommand.NavigateToEditorWithEmptyTimeTask -> {
                navigateWithEmptyTimeTask(command.currentDate, command.timeRange)
            }
            is NavigationWorkCommand.NavigateToEditorWithTimeTask -> {
                navigateWithTimeTask(command.timeTask)
            }
        }

        private suspend fun navigateWithTimeTask(timeTask: TimeTaskUi): WorkResult<HomeAction, HomeEffect> {
            val templateId = templatesInteractor.checkIsTemplate(timeTask.map(mapperToDomain))
            val editModel = mapperToEditModel.map(timeTask, templateId.rightOrElse(null))
            return navigationManager.navigateToEditorFeature(editModel).let {
                ActionResult(HomeAction.Navigate)
            }
        }

        private fun navigateWithEmptyTimeTask(
            date: Date,
            timeRange: TimeRange,
        ): WorkResult<HomeAction, HomeEffect> {
            val editModel = EditModel(
                date = date,
                mainCategory = MainCategory.empty(),
                timeRanges = timeRange,
            )
            return navigationManager.navigateToEditorFeature(editModel).let {
                ActionResult(HomeAction.Navigate)
            }
        }
    }
}

internal sealed class NavigationWorkCommand : WorkCommand {
    data class NavigateToEditorWithTimeTask(val timeTask: TimeTaskUi) : NavigationWorkCommand()
    data class NavigateToEditorWithEmptyTimeTask(
        val currentDate: Date,
        val timeRange: TimeRange,
    ) : NavigationWorkCommand()
}
