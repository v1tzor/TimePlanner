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
package ru.aleshin.features.editor.impl.presentation.ui.task.contract

import kotlinx.serialization.Serializable
import ru.aleshin.core.presentation.models.categories.MainCategoryDetailsUi
import ru.aleshin.core.presentation.models.categories.MainCategoryUi
import ru.aleshin.core.presentation.models.categories.SubCategoryUi
import ru.aleshin.core.presentation.models.tasks.UndefinedTaskUi
import ru.aleshin.core.presentation.models.templates.TemplateUi
import ru.aleshin.core.utils.architecture.component.BaseInput
import ru.aleshin.core.utils.architecture.component.BaseOutput
import ru.aleshin.core.utils.architecture.store.contract.StoreAction
import ru.aleshin.core.utils.architecture.store.contract.StoreEffect
import ru.aleshin.core.utils.architecture.store.contract.StoreEvent
import ru.aleshin.core.utils.architecture.store.contract.StoreState
import ru.aleshin.core.utils.functional.DateSerializer
import ru.aleshin.core.utils.functional.TimeRange
import ru.aleshin.features.editor.api.EditorConfig
import ru.aleshin.features.editor.impl.domain.entites.EditorFailures
import ru.aleshin.features.editor.impl.presentation.models.tasks.TimeTaskEditParametersUi
import ru.aleshin.features.editor.impl.presentation.models.tasks.TimeTaskEditUi
import ru.aleshin.features.editor.impl.presentation.ui.task.validators.CategoryValidateError
import ru.aleshin.features.editor.impl.presentation.ui.task.validators.TimeRangeError
import java.util.Date

/**
 * @author Stanislav Aleshin on 25.02.2023.
 */
@Serializable
internal data class TaskState(
    val editModel: TimeTaskEditUi? = null,
    val categories: List<MainCategoryDetailsUi> = emptyList(),
    val templates: List<TemplateUi>? = null,
    val undefinedTasks: List<UndefinedTaskUi>? = null,
    val durationPresets: List<Long>? = null,
    val timeRangeValid: TimeRangeError? = null,
    val categoryValid: CategoryValidateError? = null,
) : StoreState

internal sealed class TaskEvent : StoreEvent {
    data class Init(val input: TaskInput, val isRestore: Boolean) : TaskEvent()
    data object CreateTemplate : TaskEvent()
    data class ApplyTemplate(val template: TemplateUi) : TaskEvent()
    data class ApplyUndefinedTask(val task: UndefinedTaskUi) : TaskEvent()
    data class ChangeTime(val timeRange: TimeRange) : TaskEvent()
    data class ChangeCategories(val category: MainCategoryUi, val subCategory: SubCategoryUi?) : TaskEvent()
    data class ChangeNote(val note: String?) : TaskEvent()
    data class ChangeParameters(val parameters: TimeTaskEditParametersUi) : TaskEvent()
    data class UpdateDurationPresets(val presets: List<Long>) : TaskEvent()
    data class AddSubCategory(val name: String) : TaskEvent()
    data class NavigateToCategoryEditor(val category: MainCategoryUi) : TaskEvent()
    data class NavigateToSubCategoryEditor(val category: SubCategoryUi) : TaskEvent()
    data object PressDeleteButton : TaskEvent()
    data object PressSaveButton : TaskEvent()
    data object PressUnlinkTemplateButton : TaskEvent()
    data object PressControlTemplateButton : TaskEvent()
    data object PressBackButton : TaskEvent()
}

internal sealed class TaskEffect : StoreEffect {
    data class ShowError(val failures: EditorFailures) : TaskEffect()
    data class ShowOverlayError(val currentTimeRange: TimeRange, val failures: EditorFailures.TimeOverlayError) : TaskEffect()
}

internal sealed class TaskAction : StoreAction {
    data class UpdateEditModel(val editModel: TimeTaskEditUi?) : TaskAction()
    data class UpdateCategories(val categories: List<MainCategoryDetailsUi>) : TaskAction()
    data class UpdateUndefinedTasks(val tasks: List<UndefinedTaskUi>) : TaskAction()
    data class UpdateTemplates(val templates: List<TemplateUi>) : TaskAction()
    data class UpdateDurationPresets(val presets: List<Long>) : TaskAction()
    data class SetValidError(val timeRange: TimeRangeError?, val category: CategoryValidateError?) : TaskAction()
}

internal data class TaskInput(
    val timeTaskId: Long? = null,
    val timeRange: TimeRange? = null,
    @Serializable(DateSerializer::class) val date: Date? = null,
    val undefinedTaskId: Long? = null,
) : BaseInput

internal sealed interface TaskOutput : BaseOutput {
    data class NavigateToCategories(val config: EditorConfig.Categories) : TaskOutput
    data object NavigateToTemplates : TaskOutput
    data object NavigateToBack : TaskOutput
}