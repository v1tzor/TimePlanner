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
import ru.aleshin.features.editor.impl.presentation.mappers.mapToDomain
import ru.aleshin.features.editor.impl.presentation.mappers.mapToUi
import ru.aleshin.features.editor.impl.presentation.models.categories.MainCategoryUi
import ru.aleshin.features.editor.impl.presentation.models.categories.SubCategoryUi
import ru.aleshin.features.editor.impl.presentation.models.editmodel.EditModelUi
import ru.aleshin.features.editor.impl.presentation.models.tasks.UndefinedTaskUi
import ru.aleshin.features.editor.impl.presentation.models.tasks.convertToEditModel
import ru.aleshin.features.editor.impl.presentation.models.template.TemplateUi
import ru.aleshin.features.editor.impl.presentation.ui.editor.contract.EditorAction
import ru.aleshin.features.editor.impl.presentation.ui.editor.contract.EditorEffect
import java.util.Date
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 26.03.2023.
 */
internal interface EditorWorkProcessor : WorkProcessor<EditorWorkCommand, EditorAction, EditorEffect> {

    class Base @Inject constructor(
        private val editorInteractor: EditorInteractor,
        private val categoriesInteractor: CategoriesInteractor,
        private val templatesInteractor: TemplatesInteractor,
    ) : EditorWorkProcessor {

        override suspend fun work(command: EditorWorkCommand) = when (command) {
            is EditorWorkCommand.LoadSendEditModel -> loadSendModelWork()
            is EditorWorkCommand.LoadTemplates -> loadTemplatesWork()
            is EditorWorkCommand.AddSubCategory -> addSubCategoryWork(command.name, command.mainCategory)
            is EditorWorkCommand.AddTemplate -> addTemplateWork(command.editModel)
            is EditorWorkCommand.ApplyTemplate -> applyTemplateWork(command.template, command.model)
            is EditorWorkCommand.ApplyUndefinedTask -> applyUndefinedTaskWork(command.task, command.model)
        }

        private suspend fun loadSendModelWork(): WorkResult<EditorAction, EditorEffect> {
            val editModel = editorInteractor.fetchEditModel().mapToUi()
            return when (val result = categoriesInteractor.fetchCategories()) {
                is Either.Right -> ActionResult(EditorAction.SetUp(editModel, result.data.map { it.mapToUi() }))
                is Either.Left -> EffectResult(EditorEffect.ShowError(result.data))
            }
        }

        private suspend fun loadTemplatesWork(): WorkResult<EditorAction, EditorEffect> {
            return when (val templates = templatesInteractor.fetchTemplates()) {
                is Either.Right -> ActionResult(EditorAction.UpdateTemplates(templates.data.map { it.mapToUi() }))
                is Either.Left -> EffectResult(EditorEffect.ShowError(templates.data))
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

        private suspend fun addTemplateWork(editModel: EditModelUi): WorkResult<EditorAction, EditorEffect> {
            val template = editModel.mapToDomain().convertToTemplate()
            val templateId = templatesInteractor.addTemplate(template).rightOrElse(null)
            return ActionResult(EditorAction.UpdateTemplateId(templateId))
        }

        private fun applyTemplateWork(template: TemplateUi, model: EditModelUi): WorkResult<EditorAction, EditorEffect> {
            val domainModel = template.mapToDomain().convertToEditModel(model.date).copy(key = model.key)
            return ActionResult(EditorAction.UpdateEditModel(domainModel.mapToUi()))
        }

        private fun applyUndefinedTaskWork(task: UndefinedTaskUi, model: EditModelUi): WorkResult<EditorAction, EditorEffect> {
            val editModel = task.convertToEditModel(model.date, model.timeRange).copy(
                key = model.key,
                createdAt = Date(),
            )
            return ActionResult(EditorAction.UpdateEditModel(editModel))
        }
    }
}

internal sealed class EditorWorkCommand : WorkCommand {
    object LoadSendEditModel : EditorWorkCommand()
    object LoadTemplates : EditorWorkCommand()
    data class AddSubCategory(val name: String, val mainCategory: MainCategoryUi) : EditorWorkCommand()
    data class AddTemplate(val editModel: EditModelUi) : EditorWorkCommand()
    data class ApplyTemplate(val template: TemplateUi, val model: EditModelUi) : EditorWorkCommand()
    data class ApplyUndefinedTask(val task: UndefinedTaskUi, val model: EditModelUi) : EditorWorkCommand()
}
