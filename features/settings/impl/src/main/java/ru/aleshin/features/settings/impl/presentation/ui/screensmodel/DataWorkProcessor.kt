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
package ru.aleshin.features.settings.impl.presentation.ui.screensmodel

import android.net.Uri
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import ru.aleshin.core.utils.functional.Constants
import ru.aleshin.core.utils.functional.Either
import ru.aleshin.core.utils.platform.screenmodel.work.ActionResult
import ru.aleshin.core.utils.platform.screenmodel.work.EffectResult
import ru.aleshin.core.utils.platform.screenmodel.work.FlowWorkProcessor
import ru.aleshin.core.utils.platform.screenmodel.work.WorkCommand
import ru.aleshin.features.editor.api.presentation.TimeTaskAlarmManager
import ru.aleshin.features.home.api.domains.entities.categories.Categories
import ru.aleshin.features.home.api.domains.entities.schedules.Schedule
import ru.aleshin.features.home.api.domains.entities.template.Template
import ru.aleshin.features.settings.impl.domain.common.SettingsFailures
import ru.aleshin.features.settings.impl.domain.interactors.CategoriesInteractor
import ru.aleshin.features.settings.impl.domain.interactors.ScheduleInteractor
import ru.aleshin.features.settings.impl.domain.interactors.SettingsInteractor
import ru.aleshin.features.settings.impl.domain.interactors.TemplatesInteractor
import ru.aleshin.features.settings.impl.presentation.models.BackupModel
import ru.aleshin.features.settings.impl.presentation.ui.contract.SettingsAction
import ru.aleshin.features.settings.impl.presentation.ui.contract.SettingsEffect
import ru.aleshin.features.settings.impl.presentation.ui.managers.BackupManager
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
        private val backupManager: BackupManager,
        private val alarmManager: TimeTaskAlarmManager,
    ) : DataWorkProcessor {

        override suspend fun work(command: DataWorkCommand) = when (command) {
            is DataWorkCommand.RestoreBackupData -> restoreBackupDataWork(command.uri)
            is DataWorkCommand.SaveBackupData -> saveBackupDataWork(command.uri)
            is DataWorkCommand.ClearAllData -> clearAllDataWork()
        }

        private fun restoreBackupDataWork(uri: Uri) = flow {
            emit(ActionResult(SettingsAction.ShowLoadingBackup(true)))
            val backupResult = backupManager.restoreBackup(uri)
            if (backupResult is Either.Right) {
                val restoreWorkResult = when (val clearResult = clearAllData()) {
                    is Either.Right -> with(backupResult.data) {
                        when (val restoreResult = restoreAllData(categories, schedules, templates)) {
                            is Either.Right -> {
                                ActionResult(SettingsAction.ShowLoadingBackup(false))
                            }
                            is Either.Left -> EffectResult(SettingsEffect.ShowError(restoreResult.data))
                        }
                    }
                    is Either.Left -> EffectResult(SettingsEffect.ShowError(clearResult.data))
                }
                delay(Constants.Delay.LOAD_ANIMATION)
                emit(restoreWorkResult)
            } else if (backupResult is Either.Left) {
                emit(EffectResult(SettingsEffect.ShowError(backupResult.data)))
            }
        }

        private fun saveBackupDataWork(uri: Uri) = flow {
            emit(ActionResult(SettingsAction.ShowLoadingBackup(true)))
            run {
                val schedules = scheduleInteractor.fetchAllSchedules().let { schedules ->
                    when (schedules) {
                        is Either.Right -> schedules.data
                        is Either.Left -> return@run emit(EffectResult(SettingsEffect.ShowError(schedules.data)))
                    }
                }
                val categories = categoriesInteractor.fetchAllCategories().let { categories ->
                    when (categories) {
                        is Either.Right -> categories.data
                        is Either.Left -> return@run emit(EffectResult(SettingsEffect.ShowError(categories.data)))
                    }
                }
                val templates = templatesInteractor.fetchAllTemplates().let { templates ->
                    when (templates) {
                        is Either.Right -> templates.data
                        is Either.Left -> return@run emit(EffectResult(SettingsEffect.ShowError(templates.data)))
                    }
                }
                val backupModel = BackupModel(schedules, templates, categories)
                backupManager.saveBackup(uri, backupModel)
            }
            delay(Constants.Delay.LOAD_ANIMATION)
            emit(ActionResult(SettingsAction.ShowLoadingBackup(false)))
        }

        private fun clearAllDataWork() = flow {
            val resetSettings = when (val clearResult = clearAllData()) {
                is Either.Right -> when (val result = settingsInteractor.resetAllSettings()) {
                    is Either.Right -> when (val settings = settingsInteractor.fetchAllSettings()) {
                        is Either.Right -> ActionResult(SettingsAction.ChangeAllSettings(settings.data))
                        is Either.Left -> EffectResult(SettingsEffect.ShowError(settings.data))
                    }
                    is Either.Left -> EffectResult(SettingsEffect.ShowError(result.data))
                }
                is Either.Left -> EffectResult(SettingsEffect.ShowError(clearResult.data))
            }
            emit(resetSettings)
        }

        private suspend fun clearAllData(): Either<SettingsFailures, Unit> {
            scheduleInteractor.removeAllSchedules().apply { if (this is Either.Left) return this }
            templatesInteractor.removeAllTemplates().apply { if (this is Either.Left) return this }
            categoriesInteractor.removeAllCategories().apply { if (this is Either.Left) return this }
            return Either.Right(Unit)
        }

        private suspend fun restoreAllData(
            categories: List<Categories>,
            schedules: List<Schedule>,
            templates: List<Template>,
        ): Either<SettingsFailures, Unit> {
            categoriesInteractor.addCategories(categories).apply { if (this is Either.Left) return this }
            templatesInteractor.addTemplates(templates).apply { if (this is Either.Left) return this }
            scheduleInteractor.addSchedules(schedules).apply { if (this is Either.Left) return this }
            return Either.Right(Unit)
        }
    }
}

internal sealed class DataWorkCommand : WorkCommand {
    object ClearAllData : DataWorkCommand()
    data class SaveBackupData(val uri: Uri) : DataWorkCommand()
    data class RestoreBackupData(val uri: Uri) : DataWorkCommand()
}
