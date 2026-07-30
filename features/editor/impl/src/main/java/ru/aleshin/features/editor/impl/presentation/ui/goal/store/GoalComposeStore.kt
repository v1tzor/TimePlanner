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
package ru.aleshin.features.editor.impl.presentation.ui.goal.store

import ru.aleshin.core.domain.entities.goals.GoalMetric
import ru.aleshin.core.utils.architecture.store.BaseComposeStore
import ru.aleshin.core.utils.architecture.store.communicators.EffectCommunicator
import ru.aleshin.core.utils.architecture.store.communicators.StateCommunicator
import ru.aleshin.core.utils.architecture.store.work.BackgroundWorkKey
import ru.aleshin.core.utils.architecture.store.work.WorkScope
import ru.aleshin.core.utils.managers.CoroutineManager
import ru.aleshin.features.editor.impl.presentation.ui.goal.contract.GoalAction
import ru.aleshin.features.editor.impl.presentation.ui.goal.contract.GoalEffect
import ru.aleshin.features.editor.impl.presentation.ui.goal.contract.GoalEvent
import ru.aleshin.features.editor.impl.presentation.ui.goal.contract.GoalInput
import ru.aleshin.features.editor.impl.presentation.ui.goal.contract.GoalOutput
import ru.aleshin.features.editor.impl.presentation.ui.goal.contract.GoalState
import ru.aleshin.features.editor.impl.presentation.ui.goal.validators.GoalValidator
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 28.07.2026.
 */
internal class GoalComposeStore @Inject constructor(
    private val workProcessor: GoalWorkProcessor,
    private val validator: GoalValidator,
    stateCommunicator: StateCommunicator<GoalState>,
    effectCommunicator: EffectCommunicator<GoalEffect>,
    coroutineManager: CoroutineManager,
) : BaseComposeStore<GoalState, GoalEvent, GoalAction, GoalEffect, GoalInput, GoalOutput>(
    stateCommunicator = stateCommunicator,
    effectCommunicator = effectCommunicator,
    coroutineManager = coroutineManager,
) {

    override fun initialize(input: GoalInput, isRestore: Boolean) {
        dispatchEvent(GoalEvent.Init(input, isRestore))
    }

    override suspend fun WorkScope<GoalState, GoalAction, GoalEffect, GoalOutput>.handleEvent(event: GoalEvent) {
        when (event) {
            is GoalEvent.Init -> with(state) {
                if (!event.isRestore || editModel == null) {
                    launchBackgroundWork(BackgroundKey.LOAD) {
                        val command = GoalWorkCommand.SetupEditModel(event.input.goalId)
                        workProcessor.work(command).collectAndHandleWork()
                    }
                }
            }
            is GoalEvent.ChangeTitle -> with(state) {
                if (editModel != null) {
                    sendAction(GoalAction.UpdateEditModel(editModel.copy(title = event.title)))
                }
            }
            is GoalEvent.ChangeScope -> with(state) {
                if (editModel != null) {
                    val updatedEditModel = editModel.copy(
                        scopeType = event.scopeType,
                        mainCategory = null,
                        subCategory = null,
                    )
                    sendAction(GoalAction.UpdateEditModel(updatedEditModel))
                }
            }
            is GoalEvent.ChangeMainCategory -> with(state) {
                if (editModel != null) {
                    val updatedEditModel = editModel.copy(
                        mainCategory = event.category,
                        subCategory = null,
                    )
                    sendAction(GoalAction.UpdateEditModel(updatedEditModel))
                }
            }
            is GoalEvent.ChangeSubCategory -> with(state) {
                if (editModel != null) {
                    sendAction(GoalAction.UpdateEditModel(editModel.copy(subCategory = event.subCategory)))
                }
            }
            is GoalEvent.ChangeMetric -> with(state) {
                if (editModel != null) {
                    val targetValue = when (event.metric) {
                        GoalMetric.DURATION -> DEFAULT_DURATION_MINUTES
                        GoalMetric.TASK_COUNT -> DEFAULT_TASK_COUNT
                    }
                    val updatedEditModel = editModel.copy(
                        metric = event.metric,
                        targetValue = targetValue,
                    )
                    sendAction(GoalAction.UpdateEditModel(updatedEditModel))
                }
            }
            is GoalEvent.ChangeDirection -> with(state) {
                if (editModel != null) {
                    val updatedEditModel = editModel.copy(direction = event.direction)
                    sendAction(GoalAction.UpdateEditModel(updatedEditModel))
                }
            }
            is GoalEvent.ChangeTargetValue -> with(state) {
                if (editModel != null && event.targetValue.all { char -> char.isDigit() }) {
                    val updatedEditModel = editModel.copy(targetValue = event.targetValue.take(MAX_TARGET_LENGTH))
                    sendAction(GoalAction.UpdateEditModel(updatedEditModel))
                }
            }
            is GoalEvent.ChangeDeadline -> with(state) {
                if (editModel != null) {
                    val updatedEditModel = editModel.copy(deadline = event.deadline)
                    sendAction(GoalAction.UpdateEditModel(updatedEditModel))
                }
            }
            is GoalEvent.PressSave -> with(state) {
                if (editModel != null) {
                    val validationErrors = validator.validate(editModel)
                    sendAction(GoalAction.UpdateValidation(validationErrors))

                    if (validationErrors.isEmpty()) {
                        launchBackgroundWork(BackgroundKey.SAVE) {
                            val command = GoalWorkCommand.SaveGoal(editModel)
                            workProcessor.work(command).collectAndHandleWork()
                        }
                    }
                }
            }
            is GoalEvent.PressBack -> {
                consumeOutput(GoalOutput.NavigateBack)
            }
        }
    }

    override suspend fun reduce(
        action: GoalAction,
        currentState: GoalState,
    ) = when (action) {
        is GoalAction.SetupEditor -> currentState.copy(
            editModel = action.editModel,
            categories = action.categories,
            isLoading = action.isLoading,
        )
        is GoalAction.UpdateEditModel -> currentState.copy(
            editModel = action.editModel
        )
        is GoalAction.UpdateValidation -> currentState.copy(
            validationErrors = action.errors
        )
    }

    enum class BackgroundKey : BackgroundWorkKey {
        LOAD,
        SAVE,
    }

    companion object {
        private const val DEFAULT_DURATION_MINUTES = "300"
        private const val DEFAULT_TASK_COUNT = "5"
        private const val MAX_TARGET_LENGTH = 8
    }

    class Factory @Inject constructor(
        private val workProcessor: GoalWorkProcessor,
        private val validator: GoalValidator,
        private val coroutineManager: CoroutineManager,
    ) : BaseComposeStore.Factory<GoalComposeStore, GoalState> {

        override fun create(savedState: GoalState): GoalComposeStore {
            return GoalComposeStore(
                workProcessor = workProcessor,
                validator = validator,
                stateCommunicator = StateCommunicator.Default(savedState),
                effectCommunicator = EffectCommunicator.Default(),
                coroutineManager = coroutineManager,
            )
        }
    }
}
