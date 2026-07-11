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
package ru.aleshin.features.home.impl.presentation.ui.templates.contract

import kotlinx.serialization.Serializable
import ru.aleshin.core.domain.entities.template.RepeatTime
import ru.aleshin.core.utils.architecture.store.contract.StoreAction
import ru.aleshin.core.utils.architecture.store.contract.StoreEffect
import ru.aleshin.core.utils.architecture.store.contract.StoreEvent
import ru.aleshin.core.utils.architecture.store.contract.StoreState
import ru.aleshin.features.home.impl.domain.entities.HomeFailures
import ru.aleshin.core.presentation.models.categories.MainCategoryDetailsUi
import ru.aleshin.core.presentation.models.templates.TemplateUi
import ru.aleshin.features.home.impl.domain.entities.TemplatesSortedType

/**
 * @author Stanislav Aleshin on 08.05.2023.
 */
@Serializable
internal data class TemplatesState(
    val templates: List<TemplateUi>? = null,
    val categories: List<MainCategoryDetailsUi> = emptyList(),
    val sortedType: TemplatesSortedType = TemplatesSortedType.DATE,
) : StoreState

internal sealed class TemplatesEvent : StoreEvent {
    object Init : TemplatesEvent()
    data class AddTemplate(val template: TemplateUi) : TemplatesEvent()
    data class UpdateTemplate(val template: TemplateUi) : TemplatesEvent()
    data class RestartTemplateRepeat(val template: TemplateUi) : TemplatesEvent()
    data class StopTemplateRepeat(val template: TemplateUi) : TemplatesEvent()
    data class AddRepeatTemplate(val time: RepeatTime, val template: TemplateUi) : TemplatesEvent()
    data class DeleteRepeatTemplate(val time: RepeatTime, val template: TemplateUi) : TemplatesEvent()
    data class UpdatedSortedType(val type: TemplatesSortedType) : TemplatesEvent()
    data class DeleteTemplate(val template: TemplateUi) : TemplatesEvent()
}

internal sealed class TemplatesEffect : StoreEffect {
    data class ShowError(val failures: HomeFailures) : TemplatesEffect()
}

internal sealed class TemplatesAction : StoreAction {
    data class UpdateCategories(val categories: List<MainCategoryDetailsUi>) : TemplatesAction()
    data class UpdateTemplates(val templates: List<TemplateUi>) : TemplatesAction()
    data class ChangeSortedType(val type: TemplatesSortedType) : TemplatesAction()
}
