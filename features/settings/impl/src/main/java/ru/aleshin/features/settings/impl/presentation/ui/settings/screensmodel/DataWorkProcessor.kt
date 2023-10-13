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
import ru.aleshin.core.utils.functional.Either
import ru.aleshin.core.utils.functional.handle
import ru.aleshin.core.utils.platform.screenmodel.work.ActionResult
import ru.aleshin.core.utils.platform.screenmodel.work.EffectResult
import ru.aleshin.core.utils.platform.screenmodel.work.FlowWorkProcessor
import ru.aleshin.core.utils.platform.screenmodel.work.WorkCommand
import ru.aleshin.core.utils.platform.screenmodel.work.WorkResult
import ru.aleshin.features.settings.impl.domain.common.SettingsFailures
import ru.aleshin.features.settings.impl.domain.interactors.CategoriesInteractor
import ru.aleshin.features.settings.impl.domain.interactors.ScheduleInteractor
import ru.aleshin.features.settings.impl.domain.interactors.SettingsInteractor
import ru.aleshin.features.settings.impl.domain.interactors.TemplatesInteractor
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
        private val backupManager: BackupManager,
    ) : DataWorkProcessor {

        override suspend fun work(command: DataWorkCommand) = when (command) {
            is DataWorkCommand.RestoreBackupData -> restoreBackupDataWork(command.uri)
            is DataWorkCommand.SaveBackupData -> saveBackupDataWork(command.uri)
            is DataWorkCommand.ClearAllData -> clearAllDataWork()
        }

        private fun restoreBackupDataWork(uri: Uri) = flow {
            emit(ActionResult(SettingsAction.ShowLoadingBackup(true)))
            backupManager.restoreBackup(uri).handle(
                onLeftAction = { emit(EffectResult(SettingsEffect.ShowError(it))) },
                onRightAction = { backupModel ->
                    scheduleInteractor.removeAllSchedules().dataOrError(this@flow) ?: return@handle
                    templatesInteractor.removeAllTemplates().dataOrError(this@flow) ?: return@handle
                    categoriesInteractor.removeAllCategories().dataOrError(this@flow) ?: return@handle
                    with(backupModel) {
                        categoriesInteractor.addCategories(categoriesList).dataOrError(this@flow)
                        templatesInteractor.addTemplates(templates).dataOrError(this@flow)
                        scheduleInteractor.addSchedules(schedules).dataOrError(this@flow)
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
                val backupModel = BackupModel(schedules, templates, categories)
                
                backupManager.saveBackup(uri, backupModel)
            }
            emit(ActionResult(SettingsAction.ShowLoadingBackup(false)))
        }

        private fun clearAllDataWork() = flow {
            scheduleInteractor.removeAllSchedules().dataOrError(this)
            templatesInteractor.removeAllTemplates().dataOrError(this)
            categoriesInteractor.removeAllCategories().dataOrError(this)
            
            settingsInteractor.resetAllSettings().dataOrError(this)?.let {
                emit(ActionResult(SettingsAction.ChangeAllSettings(it.mapToUi())))
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
