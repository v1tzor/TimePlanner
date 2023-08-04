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
package ru.aleshin.features.editor.impl.presentation.ui.editor.processors

import ru.aleshin.core.utils.functional.Either
import ru.aleshin.core.utils.functional.rightOrElse
import ru.aleshin.core.utils.platform.screenmodel.work.*
import ru.aleshin.features.editor.impl.domain.common.convertToEditModel
import ru.aleshin.features.editor.impl.domain.common.convertToTemplate
import ru.aleshin.features.editor.impl.domain.interactors.CategoriesInteractor
import ru.aleshin.features.editor.impl.domain.interactors.EditorInteractor
import ru.aleshin.features.editor.impl.domain.interactors.TemplatesInteractor
import ru.aleshin.features.editor.impl.navigation.NavigationManager
import ru.aleshin.features.editor.impl.presentation.mappers.mapToDomain
import ru.aleshin.features.editor.impl.presentation.mappers.mapToUi
import ru.aleshin.features.editor.impl.presentation.models.categories.MainCategoryUi
import ru.aleshin.features.editor.impl.presentation.models.categories.SubCategoryUi
import ru.aleshin.features.editor.impl.presentation.models.editmodel.EditModelUi
import ru.aleshin.features.editor.impl.presentation.models.template.TemplateUi
import ru.aleshin.features.editor.impl.presentation.ui.editor.contract.EditorAction
import ru.aleshin.features.editor.impl.presentation.ui.editor.contract.EditorEffect
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 26.03.2023.
 */
internal interface EditorWorkProcessor : WorkProcessor<EditorWorkCommand, EditorAction, EditorEffect> {

    class Base @Inject constructor(
        private val editorInteractor: EditorInteractor,
        private val categoriesInteractor: CategoriesInteractor,
        private val templatesInteractor: TemplatesInteractor,
        private val navigationManager: NavigationManager,
    ) : EditorWorkProcessor {

        override suspend fun work(command: EditorWorkCommand) = when (command) {
            is EditorWorkCommand.LoadSendEditModel -> loadSendModel()
            is EditorWorkCommand.AddSubCategory -> addSubCategoryWork(command.name, command.mainCategory)
            is EditorWorkCommand.AddTemplate -> addTemplate(command.editModel)
            is EditorWorkCommand.ApplyTemplate -> applyTemplate(command.template, command.model)
            is EditorWorkCommand.GoBack -> navigateToBack()
            is EditorWorkCommand.GoTemplates -> navigateToTemplates()
        }

        private suspend fun loadSendModel(): WorkResult<EditorAction, EditorEffect> {
            val editModel = editorInteractor.fetchEditModel().mapToUi()
            return when (val result = categoriesInteractor.fetchCategories()) {
                is Either.Right -> ActionResult(EditorAction.SetUp(editModel, result.data.map { it.mapToUi() }))
                is Either.Left -> EffectResult(EditorEffect.ShowError(result.data))
            }
        }

        private suspend fun addSubCategoryWork(
            name: String,
            mainCategory: MainCategoryUi,
        ): WorkResult<EditorAction, EditorEffect> {
            val subCategory = SubCategoryUi(id = 0, name = name, mainCategory = mainCategory)
            return when (val result = categoriesInteractor.addSubCategory(subCategory.mapToDomain())) {
                is Either.Right -> {
                    when (val categories = categoriesInteractor.fetchCategories()) {
                        is Either.Right -> ActionResult(EditorAction.UpdateCategories(categories.data.map { it.mapToUi() }))
                        is Either.Left -> EffectResult(EditorEffect.ShowError(categories.data))
                    }
                }
                is Either.Left -> EffectResult(EditorEffect.ShowError(result.data))
            }
        }

        private suspend fun addTemplate(editModel: EditModelUi): WorkResult<EditorAction, EditorEffect> {
            val template = editModel.mapToDomain().convertToTemplate()
            val templateId = templatesInteractor.addTemplate(template).rightOrElse(null)
            return ActionResult(EditorAction.UpdateTemplateId(templateId))
        }

        private fun applyTemplate(template: TemplateUi, model: EditModelUi): WorkResult<EditorAction, EditorEffect> {
            val domainModel = template.mapToDomain().convertToEditModel(model.date).copy(key = model.key)
            return ActionResult(EditorAction.UpdateEditModel(domainModel.mapToUi()))
        }

        private fun navigateToBack(): WorkResult<EditorAction, EditorEffect> {
            return navigationManager.navigateToPreviousFeature().let {
                ActionResult(EditorAction.Navigate)
            }
        }

        private fun navigateToTemplates(): WorkResult<EditorAction, EditorEffect> {
            return navigationManager.navigateToTemplatesScreen().let {
                ActionResult(EditorAction.Navigate)
            }
        }
    }
}

internal sealed class EditorWorkCommand : WorkCommand {
    object GoBack : EditorWorkCommand()
    object GoTemplates : EditorWorkCommand()
    object LoadSendEditModel : EditorWorkCommand()
    data class AddSubCategory(val name: String, val mainCategory: MainCategoryUi) : EditorWorkCommand()
    data class AddTemplate(val editModel: EditModelUi) : EditorWorkCommand()
    data class ApplyTemplate(val template: TemplateUi, val model: EditModelUi) : EditorWorkCommand()
}
