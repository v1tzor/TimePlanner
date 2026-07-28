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
package ru.aleshin.timeplanner.widgets.presentation.models

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Test
import ru.aleshin.timeplanner.widgets.presentation.utils.WidgetSizeClass

/**
 * @author Stanislav Aleshin on 28.07.2026.
 */
class WidgetSizeClassTest {

    @Test
    fun `compact widget size is classified correctly`() {
        val result = WidgetSizeClass.fetch(DpSize(width = 110.dp, height = 110.dp))

        assertEquals(WidgetSizeClass.Width.COMPACT, result.width)
        assertEquals(WidgetSizeClass.Height.COMPACT, result.height)
    }

    @Test
    fun `medium widget size is classified correctly`() {
        val result = WidgetSizeClass.fetch(DpSize(width = 250.dp, height = 180.dp))

        assertEquals(WidgetSizeClass.Width.MEDIUM, result.width)
        assertEquals(WidgetSizeClass.Height.MEDIUM, result.height)
    }

    @Test
    fun `expanded widget size is classified correctly`() {
        val result = WidgetSizeClass.fetch(DpSize(width = 320.dp, height = 240.dp))

        assertEquals(WidgetSizeClass.Width.EXPANDED, result.width)
        assertEquals(WidgetSizeClass.Height.EXPANDED, result.height)
    }

    @Test
    fun `size class changes exactly at adaptive boundaries`() {
        val medium = WidgetSizeClass.fetch(DpSize(width = 180.dp, height = 150.dp))
        val expanded = WidgetSizeClass.fetch(DpSize(width = 280.dp, height = 220.dp))

        assertEquals(WidgetSizeClass.Width.MEDIUM, medium.width)
        assertEquals(WidgetSizeClass.Height.MEDIUM, medium.height)
        assertEquals(WidgetSizeClass.Width.EXPANDED, expanded.width)
        assertEquals(WidgetSizeClass.Height.EXPANDED, expanded.height)
    }
}
