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

import kotlinx.coroutines.flow.flow
import ru.aleshin.core.domain.entities.template.RepeatTime
import ru.aleshin.core.domain.entities.template.Template
import ru.aleshin.core.presentation.mappers.mapToDomain
import ru.aleshin.core.presentation.mappers.mapToUi
import ru.aleshin.core.presentation.models.templates.TemplateUi
import ru.aleshin.core.presentation.notifications.TemplatesAlarmManager
import ru.aleshin.core.presentation.notifications.TimeTaskAlarmManager
import ru.aleshin.core.utils.architecture.component.EmptyOutput
import ru.aleshin.core.utils.architecture.store.work.ActionResult
import ru.aleshin.core.utils.architecture.store.work.EffectResult
import ru.aleshin.core.utils.architecture.store.work.FlowWorkProcessor
import ru.aleshin.core.utils.architecture.store.work.WorkCommand
import ru.aleshin.core.utils.functional.Either
import ru.aleshin.core.utils.functional.collectAndHandle
import ru.aleshin.core.utils.functional.handle
import ru.aleshin.features.home.impl.domain.entities.TemplatesSortedType
import ru.aleshin.features.home.impl.domain.interactors.MainCategoriesInteractor
import ru.aleshin.features.home.impl.domain.interactors.RepeatTaskInteractor
import ru.aleshin.features.home.impl.domain.interactors.TemplatesInteractor
import ru.aleshin.features.home.impl.presentation.ui.templates.contract.TemplatesAction
import ru.aleshin.features.home.impl.presentation.ui.templates.contract.TemplatesEffect
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 08.05.2023.
 */
internal interface TemplatesWorkProcessor :
    FlowWorkProcessor<TemplatesWorkCommand, TemplatesAction, TemplatesEffect, EmptyOutput> {

    class Base @Inject constructor(
        private val templatesInteractor: TemplatesInteractor,
        private val repeatTaskInteractor: RepeatTaskInteractor,
        private val categoriesInteractor: MainCategoriesInteractor,
        private val timeTaskAlarmManager: TimeTaskAlarmManager,
        private val templatesAlarmManager: TemplatesAlarmManager,
    ) : TemplatesWorkProcessor {

        override suspend fun work(command: TemplatesWorkCommand) = when (command) {
            is TemplatesWorkCommand.LoadTemplates -> loadTemplatesWork(command.sortedType)
            is TemplatesWorkCommand.LoadCategories -> loadCategories()
            is TemplatesWorkCommand.AddTemplate -> addTemplate(command.template)
            is TemplatesWorkCommand.DeleteTemplate -> deleteTemplateWork(command.template)
            is TemplatesWorkCommand.UpdateTemplate -> updateTemplate(command.oldTemplate, command.newTemplate)
            is TemplatesWorkCommand.AddRepeatTemplate -> addRepeatTemplate(command.time, command.template)
            is TemplatesWorkCommand.DeleteRepeatTemplate -> deleteRepeatTemplate(command.time, command.template)
            is TemplatesWorkCommand.RestartRepeat -> restartRepeatWork(command.template)
            is TemplatesWorkCommand.StopRepeat -> stopRepeatWork(command.template)
        }

        private fun loadTemplatesWork(sortedType: TemplatesSortedType) = flow {
            templatesInteractor.fetchTemplates(sortedType).collectAndHandle(
                onLeftAction = { emit(EffectResult(TemplatesEffect.ShowError(it))) },
                onRightAction = { templates ->
                    emit(ActionResult(TemplatesAction.UpdateTemplates(templates.map { it.mapToUi() })))
                },
            )
        }

        private fun loadCategories() = flow {
            categoriesInteractor.fetchCategories().collectAndHandle(
                onLeftAction = { emit(EffectResult(TemplatesEffect.ShowError(it))) },
                onRightAction = { categories ->
                    emit(ActionResult(TemplatesAction.UpdateCategories(categories.map { it.mapToUi() })))
                },
            )
        }

        private fun addTemplate(template: TemplateUi) = flow {
            templatesInteractor.addOrUpdateTemplate(template.mapToDomain()).handle(
                onLeftAction = { emit(EffectResult(TemplatesEffect.ShowError(it))) },
                onRightAction = { id ->
                    val savedTemplate = template.copy(templateId = id)
                    if (savedTemplate.repeatEnabled && savedTemplate.repeatTimes.isNotEmpty()) {
                        repeatTaskInteractor.addRepeatsTemplate(
                            template = savedTemplate.mapToDomain(),
                            repeatTimes = savedTemplate.repeatTimes,
                        ).handle(
                            onLeftAction = { emit(EffectResult(TemplatesEffect.ShowError(it))) },
                            onRightAction = { addNotifications(savedTemplate) }
                        )
                    }
                },
            )
        }

        private fun updateTemplate(oldTemplate: TemplateUi, newTemplate: TemplateUi) = flow {
            val oldDomainModel = oldTemplate.mapToDomain()
            templatesInteractor.addOrUpdateTemplate(newTemplate.mapToDomain()).handle(
                onLeftAction = { emit(EffectResult(TemplatesEffect.ShowError(it))) },
                onRightAction = {
                    val newTemplate = newTemplate.mapToDomain()
                    if (oldTemplate.repeatEnabled || newTemplate.repeatEnabled) {
                        repeatTaskInteractor.updateRepeatTemplate(
                            oldTemplate = oldDomainModel,
                            template = newTemplate,
                        ).handle(
                            onLeftAction = { emit(EffectResult(TemplatesEffect.ShowError(it))) },
                            onRightAction = { timeTasks ->
                                timeTasks.forEach { timeTask -> timeTaskAlarmManager.deleteNotifyAlarm(timeTask) }
                                updateNotifications(oldDomainModel, newTemplate)
                            }
                        )
                    } else {
                        updateNotifications(oldDomainModel, newTemplate)
                    }
                }
            )
        }

        private fun deleteTemplateWork(template: TemplateUi) = flow {
            val domainTemplate = template.mapToDomain()
            if (template.repeatEnabled && template.repeatTimes.isNotEmpty()) {
                when (val deleteRepeatsResult = repeatTaskInteractor.deleteRepeatsTemplates(domainTemplate, template.repeatTimes)) {
                    is Either.Left -> {
                        emit(EffectResult(TemplatesEffect.ShowError(deleteRepeatsResult.data)))
                        return@flow
                    }
                    is Either.Right -> deleteRepeatsResult.data.forEach { timeTaskAlarmManager.deleteNotifyAlarm(it) }
                }
            }
            deleteNotifications(template)
            templatesInteractor.deleteTemplate(template.templateId).handle(
                onLeftAction = { emit(EffectResult(TemplatesEffect.ShowError(it))) }
            )
        }

        private fun addRepeatTemplate(repeatTime: RepeatTime, template: TemplateUi) = flow {
            val newRepeatTimes = template.repeatTimes.toMutableList().apply { add(repeatTime) }
            val newTemplate = template.copy(repeatTimes = newRepeatTimes)

            templatesInteractor.addOrUpdateTemplate(newTemplate.mapToDomain()).handle(
                onLeftAction = { emit(EffectResult(TemplatesEffect.ShowError(it))) },
                onRightAction = {
                    if (newTemplate.repeatEnabled) {
                        val repeatTimes = listOf(repeatTime)
                        repeatTaskInteractor.addRepeatsTemplate(newTemplate.mapToDomain(), repeatTimes).handle(
                            onLeftAction = { emit(EffectResult(TemplatesEffect.ShowError(it))) },
                            onRightAction = {
                                updateNotifications(template.mapToDomain(), newTemplate.mapToDomain())
                            }
                        )
                    }
                }
            )
        }

        private fun deleteRepeatTemplate(repeatTime: RepeatTime, template: TemplateUi) = flow {
            val newRepeatTimes = template.repeatTimes.toMutableList().apply { remove(repeatTime) }
            val newTemplate = template.copy(repeatTimes = newRepeatTimes)

            templatesInteractor.addOrUpdateTemplate(newTemplate.mapToDomain()).handle(
                onLeftAction = { emit(EffectResult(TemplatesEffect.ShowError(it))) },
                onRightAction = {
                    val repeatTimes = listOf(repeatTime)
                    if (template.repeatEnabled) {
                        repeatTaskInteractor.deleteRepeatsTemplates(template.mapToDomain(), repeatTimes).handle(
                            onLeftAction = { emit(EffectResult(TemplatesEffect.ShowError(it))) },
                            onRightAction = { timeTasks ->
                                timeTasks.forEach { timeTask -> timeTaskAlarmManager.deleteNotifyAlarm(timeTask) }
                                updateNotifications(template.mapToDomain(), newTemplate.mapToDomain())
                            }
                        )
                    }
                }
            )
        }

        private fun restartRepeatWork(template: TemplateUi) = flow {
            val newTemplate = template.copy(repeatEnabled = true)

            templatesInteractor.addOrUpdateTemplate(newTemplate.mapToDomain()).handle(
                onLeftAction = { emit(EffectResult(TemplatesEffect.ShowError(it))) },
                onRightAction = {
                    repeatTaskInteractor.deleteRepeatsTemplates(template.mapToDomain(), template.repeatTimes).handle(
                        onLeftAction = { emit(EffectResult(TemplatesEffect.ShowError(it))) },
                        onRightAction = { timeTasks ->
                            timeTasks.forEach { timeTask -> timeTaskAlarmManager.deleteNotifyAlarm(timeTask) }

                            repeatTaskInteractor.addRepeatsTemplate(newTemplate.mapToDomain(), newTemplate.repeatTimes).handle(
                                onLeftAction = { emit(EffectResult(TemplatesEffect.ShowError(it))) },
                                onRightAction = {
                                    updateNotifications(template.mapToDomain(), newTemplate.mapToDomain())
                                }
                            )
                        }
                    )
                }
            )
        }

        private fun stopRepeatWork(template: TemplateUi) = flow {
            val newTemplate = template.copy(repeatEnabled = false)
            templatesInteractor.addOrUpdateTemplate(newTemplate.mapToDomain()).handle(
                onLeftAction = { emit(EffectResult(TemplatesEffect.ShowError(it))) },
                onRightAction = {
                    repeatTaskInteractor.deleteRepeatsTemplates(template.mapToDomain(), template.repeatTimes).handle(
                        onLeftAction = { emit(EffectResult(TemplatesEffect.ShowError(it))) },
                        onRightAction = { timeTasks ->
                            timeTasks.forEach { timeTask -> timeTaskAlarmManager.deleteNotifyAlarm(timeTask) }
                            updateNotifications(template.mapToDomain(), newTemplate.mapToDomain())
                        }
                    )
                }
            )
        }

        private fun addNotifications(template: TemplateUi) {
            if (template.repeatEnabled && template.isEnableNotification) {
                templatesAlarmManager.addOrUpdateNotifyAlarm(template.mapToDomain())
            }
        }

        private fun deleteNotifications(template: TemplateUi) {
            templatesAlarmManager.deleteNotifyAlarm(template.mapToDomain())
        }

        private fun updateNotifications(oldTemplate: Template, newTemplate: Template) {
            if (newTemplate.repeatEnabled && newTemplate.isEnableNotification) {
                templatesAlarmManager.addOrUpdateNotifyAlarm(newTemplate)
            } else {
                templatesAlarmManager.deleteNotifyAlarm(oldTemplate)
            }
        }
    }
}

internal sealed class TemplatesWorkCommand : WorkCommand {
    data object LoadCategories : TemplatesWorkCommand()
    data class LoadTemplates(val sortedType: TemplatesSortedType) : TemplatesWorkCommand()
    data class DeleteTemplate(val template: TemplateUi) : TemplatesWorkCommand()
    data class AddTemplate(val template: TemplateUi) : TemplatesWorkCommand()
    data class UpdateTemplate(val oldTemplate: TemplateUi, val newTemplate: TemplateUi) : TemplatesWorkCommand()
    data class RestartRepeat(val template: TemplateUi) : TemplatesWorkCommand()
    data class StopRepeat(val template: TemplateUi) : TemplatesWorkCommand()
    data class AddRepeatTemplate(val time: RepeatTime, val template: TemplateUi) : TemplatesWorkCommand()
    data class DeleteRepeatTemplate(val time: RepeatTime, val template: TemplateUi) : TemplatesWorkCommand()
}
