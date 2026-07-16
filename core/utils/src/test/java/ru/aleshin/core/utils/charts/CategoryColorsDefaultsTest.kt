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
package ru.aleshin.core.utils.charts

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * @author Stanislav Aleshin on 16.07.2026.
 */
class CategoryColorsDefaultsTest {

    @Test
    fun fetchColor_assignsDifferentColorsForFirstCategories() {
        val categoryColors = (1L..24L).map { categoryId ->
            CategoryColorsDefaults.fetchColor(categoryId)
        }

        assertEquals(categoryColors.size, categoryColors.distinct().size)
    }

    @Test
    fun fetchColor_assignsDifferentColorsForDefaultCategories() {
        val categoryColors = (-2L..12L).map { categoryId ->
            CategoryColorsDefaults.fetchColor(categoryId)
        }

        assertEquals(categoryColors.size, categoryColors.distinct().size)
    }
}
