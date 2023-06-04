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
package ru.aleshin.features.home.impl.presentation.ui.templates.screenmodel

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import ru.aleshin.core.utils.managers.CoroutineManager
import ru.aleshin.core.utils.platform.screenmodel.BaseScreenModel
import ru.aleshin.core.utils.platform.screenmodel.work.WorkScope
import ru.aleshin.features.home.impl.di.holder.HomeComponentHolder
import ru.aleshin.features.home.impl.presentation.ui.home.views.ViewToggleStatus
import ru.aleshin.features.home.impl.presentation.ui.templates.contract.TemplatesAction
import ru.aleshin.features.home.impl.presentation.ui.templates.contract.TemplatesEffect
import ru.aleshin.features.home.impl.presentation.ui.templates.contract.TemplatesEvent
import ru.aleshin.features.home.impl.presentation.ui.templates.contract.TemplatesViewState
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 08.05.2023.
 */
internal class TemplatesScreenModel @Inject constructor(
    private val templatesWorkProcessor: TemplatesWorkProcessor,
    stateCommunicator: TemplatesStateCommunicator,
    effectCommunicator: TemplatesEffectCommunicator,
    coroutineManager: CoroutineManager,
) : BaseScreenModel<TemplatesViewState, TemplatesEvent, TemplatesAction, TemplatesEffect>(
    stateCommunicator = stateCommunicator,
    effectCommunicator = effectCommunicator,
    coroutineManager = coroutineManager,
) {

    override fun init() {
        if (!isInitialize.get()) {
            super.init()
            dispatchEvent(TemplatesEvent.Init)
        }
    }

    override suspend fun WorkScope<TemplatesViewState, TemplatesAction, TemplatesEffect>.handleEvent(
        event: TemplatesEvent,
    ) = when (event) {
        is TemplatesEvent.Init -> with(state()) {
            templatesWorkProcessor.work(TemplatesWorkCommand.LoadTemplates(sortedType)).handleWork()
            templatesWorkProcessor.work(TemplatesWorkCommand.LoadCategories).handleWork()
        }
        is TemplatesEvent.AddTemplate -> with(state()) {
            templatesWorkProcessor.work(TemplatesWorkCommand.AddTemplate(event.template, sortedType)).handleWork()
        }
        is TemplatesEvent.UpdateTemplate -> with(state()) {
            templatesWorkProcessor.work(TemplatesWorkCommand.UpdateTemplate(event.template, sortedType)).handleWork()
        }
        is TemplatesEvent.DeleteTemplate -> with(state()) {
            templatesWorkProcessor.work(TemplatesWorkCommand.DeleteTemplate(event.id, sortedType)).handleWork()
        }
        is TemplatesEvent.UpdatedSortedType -> {
            sendAction(TemplatesAction.ChangeSortedType(event.type))
            templatesWorkProcessor.work(TemplatesWorkCommand.LoadTemplates(event.type)).handleWork()
        }
        is TemplatesEvent.UpdatedToggleStatus -> {
            val status = when (event.status) {
                ViewToggleStatus.EXPANDED -> ViewToggleStatus.COMPACT
                ViewToggleStatus.COMPACT -> ViewToggleStatus.EXPANDED
            }
            sendAction(TemplatesAction.ChangeToggleStatus(status))
        }
    }

    override suspend fun reduce(
        action: TemplatesAction,
        currentState: TemplatesViewState,
    ) = when (action) {
        is TemplatesAction.UpdateTemplates -> currentState.copy(
            templates = action.templates,
        )
        is TemplatesAction.ChangeSortedType -> currentState.copy(
            templates = null,
            sortedType = action.type,
        )
        is TemplatesAction.ChangeToggleStatus -> currentState.copy(
            viewToggleStatus = action.status,
        )
        is TemplatesAction.UpdateCategories -> currentState.copy(
            categories = action.categories,
        )
    }
}

@Composable
internal fun Screen.rememberTemplatesScreenModel() = rememberScreenModel {
    HomeComponentHolder.fetchComponent().fetchTemplatesScreenModel()
}
