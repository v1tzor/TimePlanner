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
package ru.aleshin.features.editor.impl.presentation.ui.task.store

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Test
import ru.aleshin.core.domain.entities.categories.MainCategoryDetails
import ru.aleshin.core.domain.entities.categories.SubCategory
import ru.aleshin.core.domain.entities.settings.TasksSettings
import ru.aleshin.core.domain.entities.tasks.UndefinedTask
import ru.aleshin.core.domain.entities.template.Template
import ru.aleshin.core.utils.architecture.store.work.WorkResult
import ru.aleshin.core.utils.functional.DomainResult
import ru.aleshin.core.utils.functional.FlowDomainResult
import ru.aleshin.core.utils.functional.TimeRange
import ru.aleshin.core.utils.functional.UnitDomainResult
import ru.aleshin.core.utils.managers.DateManager
import ru.aleshin.features.editor.impl.domain.entites.EditorFailures
import ru.aleshin.features.editor.impl.domain.interactors.CategoriesInteractor
import ru.aleshin.features.editor.impl.domain.interactors.SettingsInteractor
import ru.aleshin.features.editor.impl.domain.interactors.TemplatesInteractor
import ru.aleshin.features.editor.impl.domain.interactors.UndefinedTasksInteractor
import ru.aleshin.features.editor.impl.presentation.models.tasks.TimeTaskEditUi
import ru.aleshin.features.editor.impl.presentation.ui.task.contract.TaskAction
import java.util.Date

/**
 * @author Stanislav Aleshin on 21.07.2026.
 */
internal class EditorWorkProcessorTest {

    @Test
    fun unlinkTemplateClearsLinkAndRepeatMarker() = runBlocking {
        val editModel = TimeTaskEditUi(
            date = Date(0L),
            timeRange = TimeRange(Date(0L), Date(1L)),
            linkedTemplateId = 12L,
            isRepeat = true,
        )
        val processor = EditorWorkProcessor.Base(
            categoriesInteractor = UnusedCategoriesInteractor(),
            undefinedTasksInteractor = UnusedUndefinedTasksInteractor(),
            templatesInteractor = UnusedTemplatesInteractor(),
            settingsInteractor = UnusedSettingsInteractor(),
            dateManager = UnusedDateManager(),
        )

        val result = processor.work(EditorWorkCommand.UnlinkTemplate(editModel)).first()
        val action = (result as WorkResult.Action).action as TaskAction.UpdateEditModel

        assertNull(action.editModel?.linkedTemplateId)
        assertFalse(checkNotNull(action.editModel).isRepeat)
    }
}

private class UnusedCategoriesInteractor : CategoriesInteractor {

    override suspend fun fetchCategories(): FlowDomainResult<EditorFailures, List<MainCategoryDetails>> = unused()

    override suspend fun addSubCategory(subCategory: SubCategory): DomainResult<EditorFailures, Unit> = unused()
}

private class UnusedUndefinedTasksInteractor : UndefinedTasksInteractor {

    override suspend fun fetchAllUndefinedTasks(): FlowDomainResult<EditorFailures, List<UndefinedTask>> = unused()

    override suspend fun fetchUndefinedTaskById(taskId: Long): DomainResult<EditorFailures, UndefinedTask?> = unused()

    override suspend fun deleteUndefinedTask(taskId: Long): UnitDomainResult<EditorFailures> = unused()
}

private class UnusedTemplatesInteractor : TemplatesInteractor {

    override suspend fun addOrUpdateTemplate(template: Template): DomainResult<EditorFailures, Long> = unused()

    override suspend fun fetchAllTemplates(): FlowDomainResult<EditorFailures, List<Template>> = unused()

    override suspend fun fetchTemplateById(templateId: Long): DomainResult<EditorFailures, Template?> = unused()

    override suspend fun deleteTemplateById(id: Long): DomainResult<EditorFailures, Unit> = unused()
}

private class UnusedSettingsInteractor : SettingsInteractor {

    override suspend fun fetchTasksSettings(): FlowDomainResult<EditorFailures, TasksSettings> = unused()

    override suspend fun updateTasksSettings(settings: TasksSettings): UnitDomainResult<EditorFailures> = unused()
}

private class UnusedDateManager : DateManager {

    override fun fetchCurrentDate(): Date = unused()

    override fun fetchBeginningCurrentDay(): Date = unused()

    override fun fetchEndCurrentDay(): Date = unused()

    override fun fetchTicker(): Flow<Date> = unused()

    override fun fetchMinuteTicker(): Flow<Date> = unused()

    override fun calculateLeftTime(endTime: Date): Long = unused()

    override fun calculateProgress(startTime: Date, endTime: Date): Float = unused()

    override fun setCurrentHMS(date: Date): Date = unused()
}

private fun unused(): Nothing = error("Unused dependency")
