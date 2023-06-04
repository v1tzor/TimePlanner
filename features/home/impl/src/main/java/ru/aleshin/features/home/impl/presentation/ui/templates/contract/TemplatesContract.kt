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
package ru.aleshin.features.home.impl.presentation.ui.templates.contract

import kotlinx.parcelize.Parcelize
import ru.aleshin.core.utils.platform.screenmodel.contract.BaseAction
import ru.aleshin.core.utils.platform.screenmodel.contract.BaseEvent
import ru.aleshin.core.utils.platform.screenmodel.contract.BaseUiEffect
import ru.aleshin.core.utils.platform.screenmodel.contract.BaseViewState
import ru.aleshin.features.home.api.domains.entities.categories.Categories
import ru.aleshin.features.home.api.domains.entities.template.Template
import ru.aleshin.features.home.impl.domain.entities.HomeFailures
import ru.aleshin.features.home.impl.presentation.models.TemplatesSortedType
import ru.aleshin.features.home.impl.presentation.ui.home.views.ViewToggleStatus

/**
 * @author Stanislav Aleshin on 08.05.2023.
 */
@Parcelize
internal data class TemplatesViewState(
    val templates: List<Template>? = null,
    val categories: List<Categories> = emptyList(),
    val sortedType: TemplatesSortedType = TemplatesSortedType.DATE,
    val viewToggleStatus: ViewToggleStatus = ViewToggleStatus.EXPANDED,
) : BaseViewState

internal sealed class TemplatesEvent : BaseEvent {
    object Init : TemplatesEvent()
    data class AddTemplate(val template: Template) : TemplatesEvent()
    data class UpdateTemplate(val template: Template) : TemplatesEvent()
    data class UpdatedSortedType(val type: TemplatesSortedType) : TemplatesEvent()
    data class UpdatedToggleStatus(val status: ViewToggleStatus) : TemplatesEvent()
    data class DeleteTemplate(val id: Int) : TemplatesEvent()
}

internal sealed class TemplatesEffect : BaseUiEffect {
    data class ShowError(val failure: HomeFailures) : TemplatesEffect()
}

internal sealed class TemplatesAction : BaseAction {
    data class UpdateCategories(val categories: List<Categories>) : TemplatesAction()
    data class UpdateTemplates(val templates: List<Template>) : TemplatesAction()
    data class ChangeSortedType(val type: TemplatesSortedType) : TemplatesAction()
    data class ChangeToggleStatus(val status: ViewToggleStatus) : TemplatesAction()
}
