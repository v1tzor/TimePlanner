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
package ru.aleshin.features.home.impl.presentation.mapppers

import ru.aleshin.features.home.impl.domain.entities.HomeFailures
import ru.aleshin.features.home.impl.presentation.theme.tokens.HomeStrings

/**
 * @author Stanislav Aleshin on 28.03.2023.
 */
internal fun HomeFailures.mapToMessage(homeStrings: HomeStrings) = when (this) {
    is HomeFailures.ShiftError -> homeStrings.shiftError
    is HomeFailures.ImportanceError -> homeStrings.importanceError
    is HomeFailures.OtherError -> homeStrings.otherError
}
