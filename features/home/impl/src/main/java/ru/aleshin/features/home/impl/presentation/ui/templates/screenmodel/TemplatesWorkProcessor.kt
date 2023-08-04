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
package ru.aleshin.features.home.impl.presentation.ui.templates.screenmodel

import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import ru.aleshin.core.utils.extensions.duration
import ru.aleshin.core.utils.functional.Either
import ru.aleshin.core.utils.functional.handle
import ru.aleshin.core.utils.platform.screenmodel.work.ActionResult
import ru.aleshin.core.utils.platform.screenmodel.work.EffectResult
import ru.aleshin.core.utils.platform.screenmodel.work.FlowWorkProcessor
import ru.aleshin.core.utils.platform.screenmodel.work.WorkCommand
import ru.aleshin.features.editor.api.presentation.TimeTaskAlarmManager
import ru.aleshin.features.home.api.domain.entities.schedules.TimeTask
import ru.aleshin.features.home.api.domain.entities.template.RepeatTime
import ru.aleshin.features.home.impl.domain.interactors.CategoriesInteractor
import ru.aleshin.features.home.impl.domain.interactors.RepeatTaskInteractor
import ru.aleshin.features.home.impl.domain.interactors.TemplatesInteractor
import ru.aleshin.features.home.impl.presentation.mapppers.categories.mapToUi
import ru.aleshin.features.home.impl.presentation.mapppers.templates.mapToDomain
import ru.aleshin.features.home.impl.presentation.mapppers.templates.mapToUi
import ru.aleshin.features.home.impl.presentation.models.templates.TemplateUi
import ru.aleshin.features.home.impl.presentation.models.templates.TemplatesSortedType
import ru.aleshin.features.home.impl.presentation.ui.templates.contract.TemplatesAction
import ru.aleshin.features.home.impl.presentation.ui.templates.contract.TemplatesEffect
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 08.05.2023.
 */
internal interface TemplatesWorkProcessor : FlowWorkProcessor<TemplatesWorkCommand, TemplatesAction, TemplatesEffect> {

    class Base @Inject constructor(
        private val templatesInteractor: TemplatesInteractor,
        private val repeatTaskInteractor: RepeatTaskInteractor,
        private val categoriesInteractor: CategoriesInteractor,
        private val timeTaskAlarmManager: TimeTaskAlarmManager,
    ) : TemplatesWorkProcessor {

        override suspend fun work(command: TemplatesWorkCommand) = when (command) {
            is TemplatesWorkCommand.LoadTemplates -> loadTemplatesWork(command.sortedType)
            is TemplatesWorkCommand.LoadCategories -> loadCategories()
            is TemplatesWorkCommand.AddTemplate -> addTemplate(command.template)
            is TemplatesWorkCommand.DeleteTemplate -> deleteTemplateWork(command.id)
            is TemplatesWorkCommand.UpdateTemplate -> updateTemplate(command.oldTemplate, command.newTemplate)
            is TemplatesWorkCommand.AddRepeatTemplate -> addRepeatTemplate(command.time, command.template)
            is TemplatesWorkCommand.DeleteRepeatTemplate -> deleteRepeatTemplate(command.time, command.template)
        }

        private fun loadTemplatesWork(sortedType: TemplatesSortedType) = flow {
            templatesInteractor.fetchTemplates().collect { templatesEither ->
                templatesEither.handle(
                    onLeftAction = { emit(EffectResult(TemplatesEffect.ShowError(it))) },
                    onRightAction = { templates ->
                        val sortedTemplates = when (sortedType) {
                            TemplatesSortedType.DATE -> templates.sortedBy { it.startTime }
                            TemplatesSortedType.CATEGORIES -> templates.sortedBy { it.category.id }
                            TemplatesSortedType.DURATION -> templates.sortedBy { duration(it.startTime, it.endTime) }
                        }
                        emit(ActionResult(TemplatesAction.UpdateTemplates(sortedTemplates.map { it.mapToUi() })))
                    },
                )
            }
        }

        private fun updateTemplate(oldTemplate: TemplateUi, newTemplate: TemplateUi) = flow {
            val oldDomainModel = oldTemplate.mapToDomain()
            val newDomainModel = newTemplate.mapToDomain()
            templatesInteractor.updateTemplate(newDomainModel).handle(
                onLeftAction = { emit(EffectResult(TemplatesEffect.ShowError(it))) },
                onRightAction = {
                    repeatTaskInteractor.updateRepeatTemplate(oldDomainModel, newDomainModel).handle(
                        onRightAction = { updateNotifications(it) },
                        onLeftAction = { emit(EffectResult(TemplatesEffect.ShowError(it))) },
                    )
                },
            )
        }

        private fun addTemplate(template: TemplateUi) = flow {
            templatesInteractor.addTemplate(template.mapToDomain()).let {
                if (it is Either.Left) emit(EffectResult(TemplatesEffect.ShowError(it.data)))
            }
        }

        private fun deleteTemplateWork(templateId: Int) = flow {
            templatesInteractor.deleteTemplate(templateId).let {
                if (it is Either.Left) emit(EffectResult(TemplatesEffect.ShowError(it.data)))
            }
        }

        private fun loadCategories() = flow {
            categoriesInteractor.fetchCategories().firstOrNull()?.handle(
                onLeftAction = { emit(EffectResult(TemplatesEffect.ShowError(it))) },
                onRightAction = { categories ->
                    emit(ActionResult(TemplatesAction.UpdateCategories(categories.map { it.mapToUi() })))
                },
            )
        }

        private fun addRepeatTemplate(repeatTime: RepeatTime, template: TemplateUi) = flow {
            repeatTaskInteractor.addRepeatTemplate(template.mapToDomain(), repeatTime).handle(
                onLeftAction = { emit(EffectResult(TemplatesEffect.ShowError(it))) },
                onRightAction = { plannedTasks ->
                    val newRepeatTimes = template.repeatTimes.toMutableList().apply { add(repeatTime) }
                    val newTemplate = template.copy(repeatTimes = newRepeatTimes)
                    templatesInteractor.updateTemplate(newTemplate.mapToDomain()).handle(
                        onRightAction = { addNotifications(plannedTasks) },
                        onLeftAction = { emit(EffectResult(TemplatesEffect.ShowError(it))) },
                    )
                },
            )
        }

        private fun deleteRepeatTemplate(repeatTime: RepeatTime, template: TemplateUi) = flow {
            repeatTaskInteractor.deleteRepeatTemplates(template.mapToDomain(), repeatTime).handle(
                onLeftAction = { emit(EffectResult(TemplatesEffect.ShowError(it))) },
                onRightAction = { deletedTimeTasks ->
                    val newRepeatTimes = template.repeatTimes.toMutableList().apply { remove(repeatTime) }
                    val newTemplate = template.copy(repeatTimes = newRepeatTimes)
                    templatesInteractor.updateTemplate(newTemplate.mapToDomain()).handle(
                        onRightAction = { cancelNotifications(deletedTimeTasks) },
                        onLeftAction = { emit(EffectResult(TemplatesEffect.ShowError(it))) },
                    )
                },
            )
        }

        private fun addNotifications(timeTaskList: List<TimeTask>) {
            timeTaskList.forEach { timeTask ->
                if (timeTask.isEnableNotification) timeTaskAlarmManager.addNotifyAlarm(timeTask)
            }
        }

        private fun updateNotifications(timeTaskList: List<TimeTask>) {
            timeTaskList.forEach { timeTask ->
                if (timeTask.isEnableNotification) timeTaskAlarmManager.updateNotifyAlarm(timeTask)
            }
        }

        private fun cancelNotifications(timeTaskList: List<TimeTask>) {
            timeTaskList.forEach { timeTask ->
                timeTaskAlarmManager.deleteNotifyAlarm(timeTask)
            }
        }
    }
}

internal sealed class TemplatesWorkCommand : WorkCommand {
    object LoadCategories : TemplatesWorkCommand()
    data class LoadTemplates(val sortedType: TemplatesSortedType) : TemplatesWorkCommand()
    data class DeleteTemplate(val id: Int) : TemplatesWorkCommand()
    data class AddTemplate(val template: TemplateUi) : TemplatesWorkCommand()
    data class UpdateTemplate(val oldTemplate: TemplateUi, val newTemplate: TemplateUi) : TemplatesWorkCommand()
    data class AddRepeatTemplate(val time: RepeatTime, val template: TemplateUi) : TemplatesWorkCommand()
    data class DeleteRepeatTemplate(val time: RepeatTime, val template: TemplateUi) : TemplatesWorkCommand()
}
