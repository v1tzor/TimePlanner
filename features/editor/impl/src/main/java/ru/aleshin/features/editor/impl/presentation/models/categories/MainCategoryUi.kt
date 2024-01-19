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
package ru.aleshin.features.editor.impl.presentation.models.categories

import android.os.Parcelable
import androidx.compose.runtime.Composable
import kotlinx.parcelize.Parcelize
import ru.aleshin.core.domain.entities.categories.DefaultCategoryType
import ru.aleshin.core.ui.mappers.mapToName

/**
 * @author Stanislav Aleshin on 30.07.2023.
 */
@Parcelize
internal data class MainCategoryUi(
    val id: Int = 0,
    val customName: String? = null,
    val defaultType: DefaultCategoryType? = DefaultCategoryType.EMPTY,
) : Parcelable {

    @Composable
    fun fetchName() = when (customName != null && customName != "null") {
        true -> customName
        false -> defaultType?.mapToName()
    }
}
