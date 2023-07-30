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
package ru.aleshin.features.editor.impl.presentation.ui.editor.screenmodel

import kotlinx.parcelize.Parcelize
import ru.aleshin.core.utils.validation.ValidateError
import ru.aleshin.core.utils.validation.ValidateResult
import ru.aleshin.core.utils.validation.Validator
import ru.aleshin.features.editor.impl.presentation.models.categories.MainCategoryUi
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 26.03.2023.
 */
internal interface CategoryValidator : Validator<MainCategoryUi, CategoryValidateError> {

    class Base @Inject constructor() : CategoryValidator {
        override fun validate(data: MainCategoryUi): ValidateResult<CategoryValidateError> {
            return if (data.id == 0) {
                ValidateResult(false, CategoryValidateError.EmptyCategoryError)
            } else {
                ValidateResult(true, null)
            }
        }
    }
}

@Parcelize
internal sealed class CategoryValidateError : ValidateError {
    object EmptyCategoryError : CategoryValidateError()
}
