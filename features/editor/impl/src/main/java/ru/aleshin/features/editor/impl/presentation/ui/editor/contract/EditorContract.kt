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
package ru.aleshin.features.editor.impl.presentation.ui.editor.contract

import kotlinx.serialization.Serializable
import ru.aleshin.core.domain.entities.schedules.TimeTask
import ru.aleshin.core.domain.entities.template.Template
import ru.aleshin.core.utils.architecture.component.BaseInput
import ru.aleshin.core.utils.architecture.component.BaseOutput
import ru.aleshin.core.utils.architecture.store.contract.StoreAction
import ru.aleshin.core.utils.architecture.store.contract.StoreEffect
import ru.aleshin.core.utils.architecture.store.contract.StoreEvent
import ru.aleshin.core.utils.architecture.store.contract.StoreState
import ru.aleshin.core.utils.functional.TimeRange
import ru.aleshin.features.editor.impl.domain.entites.EditorFailures
import ru.aleshin.features.editor.impl.presentation.models.categories.CategoriesUi
import ru.aleshin.features.editor.impl.presentation.models.categories.MainCategoryUi
import ru.aleshin.features.editor.impl.presentation.models.categories.SubCategoryUi
import ru.aleshin.features.editor.impl.presentation.models.editmodel.EditModelUi
import ru.aleshin.features.editor.impl.presentation.models.editmodel.EditParametersUi
import ru.aleshin.features.editor.impl.presentation.models.tasks.UndefinedTaskUi
import ru.aleshin.features.editor.impl.presentation.models.template.TemplateUi
import ru.aleshin.features.editor.impl.presentation.ui.editor.store.CategoryValidateError
import ru.aleshin.features.editor.impl.presentation.ui.editor.store.TimeRangeError

/**
 * @author Stanislav Aleshin on 25.02.2023.
 */
@Serializable
internal data class EditorState(
    val editModel: EditModelUi? = null,
    val categories: List<CategoriesUi> = emptyList(),
    val templates: List<TemplateUi>? = null,
    val undefinedTasks: List<UndefinedTaskUi>? = null,
    val timeRangeValid: TimeRangeError? = null,
    val categoryValid: CategoryValidateError? = null,
) : StoreState

internal sealed class EditorEvent : StoreEvent {
    data class Init(val input: EditorInput, val isRestore: Boolean) : EditorEvent()
    data object CreateTemplate : EditorEvent()
    data class ApplyTemplate(val template: TemplateUi) : EditorEvent()
    data class ApplyUndefinedTask(val task: UndefinedTaskUi) : EditorEvent()
    data class ChangeTime(val timeRange: TimeRange) : EditorEvent()
    data class ChangeCategories(val category: MainCategoryUi, val subCategory: SubCategoryUi?) : EditorEvent()
    data class ChangeNote(val note: String?) : EditorEvent()
    data class ChangeParameters(val parameters: EditParametersUi) : EditorEvent()
    data class AddSubCategory(val name: String) : EditorEvent()
    data class NavigateToCategoryEditor(val category: MainCategoryUi) : EditorEvent()
    data class NavigateToSubCategoryEditor(val category: SubCategoryUi) : EditorEvent()
    data object PressDeleteButton : EditorEvent()
    data object PressSaveButton : EditorEvent()
    data object PressControlTemplateButton : EditorEvent()
    data object PressBackButton : EditorEvent()
}

internal sealed class EditorEffect : StoreEffect {
    data class ShowError(val failures: EditorFailures) : EditorEffect()
    data class ShowOverlayError(
        val currentTimeRange: TimeRange,
        val failures: EditorFailures.TimeOverlayError,
    ) : EditorEffect()
}

internal sealed class EditorAction : StoreAction {
    object Navigate : EditorAction()
    data class SetUp(val editModel: EditModelUi, val categories: List<CategoriesUi>) : EditorAction()
    data class UpdateUndefinedTasks(val tasks: List<UndefinedTaskUi>) : EditorAction()
    data class UpdateCategories(val categories: List<CategoriesUi>) : EditorAction()
    data class UpdateTemplates(val templates: List<TemplateUi>) : EditorAction()
    data class UpdateTemplateId(val templateId: Int?) : EditorAction()
    data class UpdateEditModel(val editModel: EditModelUi?) : EditorAction()
    data class SetValidError(val timeRange: TimeRangeError?, val category: CategoryValidateError?) : EditorAction()
}

internal data class EditorInput(
    val timeTask: TimeTask,
    val template: Template?,
    val undefinedTaskId: Long?,
) : BaseInput