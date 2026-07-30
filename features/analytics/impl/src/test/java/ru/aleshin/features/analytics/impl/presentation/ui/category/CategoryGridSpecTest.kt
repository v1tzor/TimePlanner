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
    fun `uses eight columns for medium layout`() {
        val columnCount = CategoryGridSpec.fetchColumnCount(isExpanded = false)

        assertEquals(8, columnCount)
    }

    @Test
    fun `uses twelve columns for expanded layout`() {
        val columnCount = CategoryGridSpec.fetchColumnCount(isExpanded = true)

        assertEquals(12, columnCount)
    }
}
