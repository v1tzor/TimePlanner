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
package ru.aleshin.features.editor.impl.presentation.ui.editor.screenmodel

import kotlinx.parcelize.Parcelize
import ru.aleshin.core.utils.functional.TimeRange
import ru.aleshin.core.utils.validation.ValidateError
import ru.aleshin.core.utils.validation.ValidateResult
import ru.aleshin.core.utils.validation.Validator
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 26.03.2023.
 */
internal interface TimeRangeValidator : Validator<TimeRange, TimeRangeError> {

    class Base @Inject constructor() : TimeRangeValidator {
        override fun validate(data: TimeRange): ValidateResult<TimeRangeError> {
            return if (data.to.time - data.from.time < 1) {
                ValidateResult(false, TimeRangeError.DurationError)
            } else {
                ValidateResult(true, null)
            }
        }
    }
}

@Parcelize
internal sealed class TimeRangeError : ValidateError {
    object DurationError : TimeRangeError()
}
