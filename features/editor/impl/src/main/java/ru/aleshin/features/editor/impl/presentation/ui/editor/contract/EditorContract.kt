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
package ru.aleshin.features.editor.impl.presentation.ui.editor.contract

import kotlinx.serialization.Serializable
import ru.aleshin.core.presentation.models.categories.MainCategoryDetailsUi
import ru.aleshin.core.presentation.models.categories.MainCategoryUi
import ru.aleshin.core.presentation.models.categories.SubCategoryUi
import ru.aleshin.core.presentation.models.tasks.UndefinedTaskUi
import ru.aleshin.core.presentation.models.templates.TemplateUi
import ru.aleshin.core.utils.architecture.component.BaseInput
import ru.aleshin.core.utils.architecture.store.contract.StoreAction
import ru.aleshin.core.utils.architecture.store.contract.StoreEffect
import ru.aleshin.core.utils.architecture.store.contract.StoreEvent
import ru.aleshin.core.utils.architecture.store.contract.StoreState
import ru.aleshin.core.utils.functional.DateSerializer
import ru.aleshin.core.utils.functional.TimeRange
import ru.aleshin.features.editor.impl.domain.entites.EditorFailures
import ru.aleshin.features.editor.impl.presentation.models.tasks.TimeTaskEditParametersUi
import ru.aleshin.features.editor.impl.presentation.models.tasks.TimeTaskEditUi
import ru.aleshin.features.editor.impl.presentation.ui.editor.validators.CategoryValidateError
import ru.aleshin.features.editor.impl.presentation.ui.editor.validators.TimeRangeError
import java.util.Date

/**
 * @author Stanislav Aleshin on 25.02.2023.
 */
@Serializable
internal data class EditorState(
    val editModel: TimeTaskEditUi? = null,
    val categories: List<MainCategoryDetailsUi> = emptyList(),
    val templates: List<TemplateUi>? = null,
    val undefinedTasks: List<UndefinedTaskUi>? = null,
    val durationPresets: List<Long>? = null,
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
    data class ChangeParameters(val parameters: TimeTaskEditParametersUi) : EditorEvent()
    data class UpdateDurationPresets(val presets: List<Long>) : EditorEvent()
    data class AddSubCategory(val name: String) : EditorEvent()
    data class NavigateToCategoryEditor(val category: MainCategoryUi) : EditorEvent()
    data class NavigateToSubCategoryEditor(val category: SubCategoryUi) : EditorEvent()
    data object PressDeleteButton : EditorEvent()
    data object PressSaveButton : EditorEvent()
    data object PressUnlinkTemplateButton : EditorEvent()
    data object PressControlTemplateButton : EditorEvent()
    data object PressBackButton : EditorEvent()
}

internal sealed class EditorEffect : StoreEffect {
    data class ShowError(val failures: EditorFailures) : EditorEffect()
    data class ShowOverlayError(val currentTimeRange: TimeRange, val failures: EditorFailures.TimeOverlayError) : EditorEffect()
}

internal sealed class EditorAction : StoreAction {
    data class UpdateEditModel(val editModel: TimeTaskEditUi?) : EditorAction()
    data class UpdateCategories(val categories: List<MainCategoryDetailsUi>) : EditorAction()
    data class UpdateUndefinedTasks(val tasks: List<UndefinedTaskUi>) : EditorAction()
    data class UpdateTemplates(val templates: List<TemplateUi>) : EditorAction()
    data class UpdateDurationPresets(val presets: List<Long>) : EditorAction()
    data class SetValidError(val timeRange: TimeRangeError?, val category: CategoryValidateError?) : EditorAction()
}

internal data class EditorInput(
    val timeTaskId: Long? = null,
    val timeRange: TimeRange? = null,
    @Serializable(DateSerializer::class) val date: Date? = null,
    val undefinedTaskId: Long? = null,
) : BaseInput
