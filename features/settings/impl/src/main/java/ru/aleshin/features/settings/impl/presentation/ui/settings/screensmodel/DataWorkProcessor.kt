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
package ru.aleshin.features.settings.impl.presentation.ui.settings.screensmodel

import android.net.Uri
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import ru.aleshin.core.utils.extensions.extractAllItem
import ru.aleshin.core.utils.functional.Either
import ru.aleshin.core.utils.functional.handle
import ru.aleshin.core.utils.managers.DateManager
import ru.aleshin.core.utils.platform.screenmodel.work.ActionResult
import ru.aleshin.core.utils.platform.screenmodel.work.EffectResult
import ru.aleshin.core.utils.platform.screenmodel.work.FlowWorkProcessor
import ru.aleshin.core.utils.platform.screenmodel.work.WorkCommand
import ru.aleshin.core.utils.platform.screenmodel.work.WorkResult
import ru.aleshin.features.editor.api.presentation.TemplatesAlarmManager
import ru.aleshin.features.editor.api.presentation.TimeTaskAlarmManager
import ru.aleshin.features.home.api.domain.entities.schedules.TimeTask
import ru.aleshin.features.home.api.domain.entities.template.Template
import ru.aleshin.features.settings.impl.domain.common.SettingsFailures
import ru.aleshin.features.settings.impl.domain.interactors.CategoriesInteractor
import ru.aleshin.features.settings.impl.domain.interactors.ScheduleInteractor
import ru.aleshin.features.settings.impl.domain.interactors.SettingsInteractor
import ru.aleshin.features.settings.impl.domain.interactors.TemplatesInteractor
import ru.aleshin.features.settings.impl.domain.interactors.UndefinedTasksInteractor
import ru.aleshin.features.settings.impl.presentation.mappers.mapToUi
import ru.aleshin.features.settings.impl.presentation.models.BackupModel
import ru.aleshin.features.settings.impl.presentation.ui.settings.contract.SettingsAction
import ru.aleshin.features.settings.impl.presentation.ui.settings.contract.SettingsEffect
import ru.aleshin.features.settings.impl.presentation.ui.settings.managers.BackupManager
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 10.06.2023.
 */
internal interface DataWorkProcessor : FlowWorkProcessor<DataWorkCommand, SettingsAction, SettingsEffect> {

    class Base @Inject constructor(
        private val settingsInteractor: SettingsInteractor,
        private val scheduleInteractor: ScheduleInteractor,
        private val categoriesInteractor: CategoriesInteractor,
        private val templatesInteractor: TemplatesInteractor,
        private val timeTaskAlarmManager: TimeTaskAlarmManager,
        private val templatesAlarmManager: TemplatesAlarmManager,
        private val undefinedTasksInteractor: UndefinedTasksInteractor,
        private val dateManager: DateManager,
        private val backupManager: BackupManager,
    ) : DataWorkProcessor {

        override suspend fun work(command: DataWorkCommand) = when (command) {
            is DataWorkCommand.RestoreBackupData -> restoreBackupDataWork(command.uri)
            is DataWorkCommand.SaveBackupData -> saveBackupDataWork(command.uri)
            is DataWorkCommand.ClearAllData -> clearAllDataWork()
        }

        private fun restoreBackupDataWork(uri: Uri) = flow {
            val currentDate = dateManager.fetchCurrentDate()
            emit(ActionResult(SettingsAction.ShowLoadingBackup(true)))
            backupManager.restoreBackup(uri).handle(
                onLeftAction = { emit(EffectResult(SettingsEffect.ShowError(it))) },
                onRightAction = { backupModel ->
                    val deletedTemplates = templatesInteractor.removeAllTemplates().dataOrError(this@flow) ?: return@handle
                    val deletableSchedules = scheduleInteractor.removeAllSchedules().dataOrError(this@flow) ?: return@handle
                    val deletableTimeTasks = deletableSchedules.map { it.timeTasks }.extractAllItem()
                    categoriesInteractor.removeAllCategories().dataOrError(this@flow) ?: return@handle
                    undefinedTasksInteractor.removeAllUndefinedTask().dataOrError(this@flow) ?: return@handle

                    deleteRepeatNotifications(deletedTemplates)
                    deleteNotifications(deletableTimeTasks, deletedTemplates)

                    with(backupModel) {
                        val restoredSchedules = schedules.map { schedule ->
                            val timeTasks = schedule.timeTasks.filter { timeTask ->
                                val taskTemplate = templates.find { it.equalsIsTemplate(timeTask) }
                                return@filter taskTemplate == null || !(taskTemplate.repeatEnabled && timeTask.timeRange.from > currentDate)
                            }
                            schedule.copy(timeTasks = timeTasks)
                        }
                        val restoreTemplates = templates.map { it.copy(repeatEnabled = false) }
                        categoriesInteractor.addCategories(categories).dataOrError(this@flow)
                        templatesInteractor.addTemplates(restoreTemplates).dataOrError(this@flow)
                        undefinedTasksInteractor.addUndefinedTasks(undefinedTasks).dataOrError(this@flow)
                        scheduleInteractor.addSchedules(restoredSchedules).dataOrError(this@flow).apply {
                            addNotifications(restoredSchedules.map { it.timeTasks }.extractAllItem())
                        }
                    }
                },
            )
            emit(ActionResult(SettingsAction.ShowLoadingBackup(false)))
        }

        private fun saveBackupDataWork(uri: Uri) = flow {
            emit(ActionResult(SettingsAction.ShowLoadingBackup(true)))
            run {
                val schedules = scheduleInteractor.fetchAllSchedules().dataOrError(this) ?: return@run 
                val categories = categoriesInteractor.fetchAllCategories().dataOrError(this) ?: return@run
                val templates = templatesInteractor.fetchAllTemplates().dataOrError(this) ?: return@run
                val undefinedTasks = undefinedTasksInteractor.fetchAllUndefinedTasks().dataOrError(this) ?: return@run
                val backupModel = BackupModel(schedules, templates, categories, undefinedTasks)
                
                backupManager.saveBackup(uri, backupModel)
            }
            emit(ActionResult(SettingsAction.ShowLoadingBackup(false)))
        }

        private fun clearAllDataWork() = flow {
            val deletedTemplates = templatesInteractor.removeAllTemplates().dataOrError(this) ?: return@flow
            val deletedSchedules = scheduleInteractor.removeAllSchedules().dataOrError(this) ?: return@flow
            val deletedTimeTasks = deletedSchedules.map { it.timeTasks }.extractAllItem()
            categoriesInteractor.removeAllCategories().dataOrError(this)
            undefinedTasksInteractor.removeAllUndefinedTask().dataOrError(this)

            deleteRepeatNotifications(deletedTemplates)
            deleteNotifications(deletedTimeTasks, deletedTemplates)

            settingsInteractor.resetAllSettings().dataOrError(this)?.let {
                emit(ActionResult(SettingsAction.ChangeAllSettings(it.mapToUi())))
            }
        }

        private fun addNotifications(timeTasks: List<TimeTask>) {
            val currentDate = dateManager.fetchCurrentDate()
            timeTasks.forEach { timeTask ->
                if (timeTask.isEnableNotification && timeTask.timeRange.to > currentDate) {
                    timeTaskAlarmManager.addOrUpdateNotifyAlarm(timeTask)
                }
            }
        }

        private fun deleteNotifications(timeTasks: List<TimeTask>, templates: List<Template>) {
            val currentDate = dateManager.fetchCurrentDate()
            timeTasks.forEach { timeTask ->
                val taskTemplate = templates.find { it.equalsIsTemplate(timeTask) }
                if (timeTask.timeRange.to > currentDate && (taskTemplate == null || !taskTemplate.repeatEnabled)) {
                    timeTaskAlarmManager.deleteNotifyAlarm(timeTask)
                }
            }
        }

        private fun deleteRepeatNotifications(templates: List<Template>) {
            templates.forEach { template ->
                template.repeatTimes.forEach { repeatTime ->
                    if (template.repeatEnabled) templatesAlarmManager.deleteNotifyAlarm(template, repeatTime)
                }
            }
        }

        private suspend fun <R> Either<SettingsFailures, R>.dataOrError(
            collector: FlowCollector<WorkResult<SettingsAction, SettingsEffect>>,
        ): R? = when (this) {
            is Either.Right -> data
            is Either.Left -> collector.emit(EffectResult(SettingsEffect.ShowError(data))).let { null }
        }
    }
}

internal sealed class DataWorkCommand : WorkCommand {
    object ClearAllData : DataWorkCommand()
    data class SaveBackupData(val uri: Uri) : DataWorkCommand()
    data class RestoreBackupData(val uri: Uri) : DataWorkCommand()
}
