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
package ru.aleshin.features.home.impl.presentation.ui.templates.store

import ru.aleshin.core.utils.architecture.component.EmptyInput
import ru.aleshin.core.utils.architecture.component.EmptyOutput
import ru.aleshin.core.utils.architecture.store.BaseSimpleComposeStore
import ru.aleshin.core.utils.architecture.store.communicators.EffectCommunicator
import ru.aleshin.core.utils.architecture.store.communicators.StateCommunicator
import ru.aleshin.core.utils.architecture.store.work.BackgroundWorkKey
import ru.aleshin.core.utils.architecture.store.work.WorkScope
import ru.aleshin.core.utils.managers.CoroutineManager
import ru.aleshin.features.home.impl.presentation.ui.templates.contract.TemplatesAction
import ru.aleshin.features.home.impl.presentation.ui.templates.contract.TemplatesEffect
import ru.aleshin.features.home.impl.presentation.ui.templates.contract.TemplatesEvent
import ru.aleshin.features.home.impl.presentation.ui.templates.contract.TemplatesState
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 08.05.2023.
 */
internal class TemplatesComposeStore @Inject constructor(
    private val templatesWorkProcessor: TemplatesWorkProcessor,
    stateCommunicator: StateCommunicator<TemplatesState>,
    effectCommunicator: EffectCommunicator<TemplatesEffect>,
    coroutineManager: CoroutineManager,
) : BaseSimpleComposeStore<TemplatesState, TemplatesEvent, TemplatesAction, TemplatesEffect>(
    stateCommunicator = stateCommunicator,
    effectCommunicator = effectCommunicator,
    coroutineManager = coroutineManager,
) {

    override fun initialize(input: EmptyInput, isRestore: Boolean) {
        dispatchEvent(TemplatesEvent.Init)
    }

    override suspend fun WorkScope<TemplatesState, TemplatesAction, TemplatesEffect, EmptyOutput>.handleEvent(
        event: TemplatesEvent,
    ) {
        when (event) {
            is TemplatesEvent.Init -> {
                launchBackgroundWork(BackgroundKey.LOAD_CATEGORIES) {
                    val command = TemplatesWorkCommand.LoadCategories
                    templatesWorkProcessor.work(command).collectAndHandleWork()
                }
                launchBackgroundWork(BackgroundKey.LOAD_TEMPLATES) {
                    val command = TemplatesWorkCommand.LoadTemplates(state().sortedType)
                    templatesWorkProcessor.work(command).collectAndHandleWork()
                }
            }
            is TemplatesEvent.AddTemplate -> launchBackgroundWork(BackgroundKey.TEMPLATE_ACTION) {
                val command = TemplatesWorkCommand.AddTemplate(event.template)
                templatesWorkProcessor.work(command).collectAndHandleWork()
            }
            is TemplatesEvent.UpdateTemplate -> launchBackgroundWork(BackgroundKey.TEMPLATE_ACTION) {
                val oldModel = state().templates?.find { it.templateId == event.template.templateId }!!
                val command = TemplatesWorkCommand.UpdateTemplate(oldModel, event.template)
                templatesWorkProcessor.work(command).collectAndHandleWork()
            }
            is TemplatesEvent.DeleteTemplate -> launchBackgroundWork(BackgroundKey.TEMPLATE_ACTION) {
                val command = TemplatesWorkCommand.DeleteTemplate(event.template)
                templatesWorkProcessor.work(command).collectAndHandleWork()
            }
            is TemplatesEvent.RestartTemplateRepeat -> launchBackgroundWork(BackgroundKey.REPEAT_ACTION) {
                val command = TemplatesWorkCommand.RestartRepeat(event.template)
                templatesWorkProcessor.work(command).collectAndHandleWork()
            }
            is TemplatesEvent.StopTemplateRepeat -> launchBackgroundWork(BackgroundKey.REPEAT_ACTION) {
                val command = TemplatesWorkCommand.StopRepeat(event.template)
                templatesWorkProcessor.work(command).collectAndHandleWork()
            }
            is TemplatesEvent.AddRepeatTemplate -> launchBackgroundWork(BackgroundKey.REPEAT_ACTION) {
                val command = TemplatesWorkCommand.AddRepeatTemplate(event.time, event.template)
                templatesWorkProcessor.work(command).collectAndHandleWork()
            }
            is TemplatesEvent.DeleteRepeatTemplate -> launchBackgroundWork(BackgroundKey.REPEAT_ACTION) {
                val command = TemplatesWorkCommand.DeleteRepeatTemplate(event.time, event.template)
                templatesWorkProcessor.work(command).collectAndHandleWork()
            }
            is TemplatesEvent.UpdatedSortedType -> {
                sendAction(TemplatesAction.ChangeSortedType(event.type))
                launchBackgroundWork(BackgroundKey.LOAD_TEMPLATES) {
                    val command = TemplatesWorkCommand.LoadTemplates(event.type)
                    templatesWorkProcessor.work(command).collectAndHandleWork()
                }
            }
        }
    }

    override suspend fun reduce(
        action: TemplatesAction,
        currentState: TemplatesState,
    ) = when (action) {
        is TemplatesAction.UpdateTemplates -> currentState.copy(
            templates = action.templates,
        )
        is TemplatesAction.ChangeSortedType -> currentState.copy(
            sortedType = action.type,
        )
        is TemplatesAction.UpdateCategories -> currentState.copy(
            categories = action.categories,
        )
    }

    enum class BackgroundKey : BackgroundWorkKey {
        LOAD_TEMPLATES, LOAD_CATEGORIES, TEMPLATE_ACTION, REPEAT_ACTION
    }

    class Factory @Inject constructor(
        private val templatesWorkProcessor: TemplatesWorkProcessor,
        private val coroutineManager: CoroutineManager,
    ) : BaseSimpleComposeStore.Factory<TemplatesComposeStore, TemplatesState> {

        override fun create(savedState: TemplatesState): TemplatesComposeStore {
            return TemplatesComposeStore(
                templatesWorkProcessor = templatesWorkProcessor,
                stateCommunicator = StateCommunicator.Default(savedState),
                effectCommunicator = EffectCommunicator.Default(),
                coroutineManager = coroutineManager,
            )
        }
    }
}
