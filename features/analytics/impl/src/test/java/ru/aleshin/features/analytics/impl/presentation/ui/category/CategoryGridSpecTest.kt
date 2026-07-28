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

package ru.aleshin.features.analytics.impl.presentation.ui.category

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * @author Stanislav Aleshin on 26.07.2026.
 */
class CategoryGridSpecTest {

    @Test
    fun `uses planned medium spans`() {
        assertEquals(8, CategoryGridSpec.fetchColumnCount(isExpanded = false))
        assertEquals(8, fetchSpan(CategoryGridSection.SUMMARY, false))
        assertEquals(8, fetchSpan(CategoryGridSection.KEY_METRICS, false))
        assertEquals(8, fetchSpan(CategoryGridSection.SUBCATEGORIES, false))
        assertEquals(8, fetchSpan(CategoryGridSection.LOAD, false))
        assertEquals(8, fetchSpan(CategoryGridSection.DAY_PARTS, false))
        assertEquals(8, fetchSpan(CategoryGridSection.TASKS, false))
        assertEquals(8, fetchSpan(CategoryGridSection.OBSERVATION, false))
    }

    @Test
    fun `uses planned expanded spans`() {
        assertEquals(12, CategoryGridSpec.fetchColumnCount(isExpanded = true))
        assertEquals(4, fetchSpan(CategoryGridSection.SUMMARY, true))
        assertEquals(8, fetchSpan(CategoryGridSection.KEY_METRICS, true))
        assertEquals(5, fetchSpan(CategoryGridSection.SUBCATEGORIES, true))
        assertEquals(12, fetchSpan(CategoryGridSection.LOAD, true))
        assertEquals(7, fetchSpan(CategoryGridSection.DAY_PARTS, true))
        assertEquals(12, fetchSpan(CategoryGridSection.TASKS, true))
        assertEquals(12, fetchSpan(CategoryGridSection.OBSERVATION, true))
    }

    private fun fetchSpan(
        section: CategoryGridSection,
        isExpanded: Boolean,
    ): Int {
        return CategoryGridSpec.fetchSpan(
            section = section,
            isExpanded = isExpanded,
        )
    }
}
