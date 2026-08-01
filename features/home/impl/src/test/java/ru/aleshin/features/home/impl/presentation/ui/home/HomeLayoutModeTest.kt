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
package ru.aleshin.features.home.impl.presentation.ui.home

import androidx.compose.material3.adaptive.Posture
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import ru.aleshin.timeplanner.core.ui.views.AdaptiveLayoutInfo

/**
 * @author Stanislav Aleshin on 30.07.2026.
 */
class HomeLayoutModeTest {

    @Test
    fun `selects layout mode at width boundaries`() {
        assertEquals(HomeLayoutMode.COMPACT, createAdaptiveLayoutInfo(width = 599).fetchHomeLayoutMode())
        assertEquals(HomeLayoutMode.MEDIUM, createAdaptiveLayoutInfo(width = 600).fetchHomeLayoutMode())
        assertEquals(HomeLayoutMode.MEDIUM, createAdaptiveLayoutInfo(width = 839).fetchHomeLayoutMode())
        assertEquals(HomeLayoutMode.SUPPORTING_PANE, createAdaptiveLayoutInfo(width = 840).fetchHomeLayoutMode())
    }

    @Test
    fun `avoids supporting pane when window height is compact`() {
        assertEquals(
            HomeLayoutMode.COMPACT,
            createAdaptiveLayoutInfo(width = 599, height = 479).fetchHomeLayoutMode(),
        )
        assertEquals(
            HomeLayoutMode.MEDIUM,
            createAdaptiveLayoutInfo(width = 840, height = 479).fetchHomeLayoutMode(),
        )
    }

    @Test
    fun `coordinates screen app bars with layout mode`() {
        assertTrue(HomeLayoutMode.COMPACT.showScreenTopAppBar)
        assertTrue(HomeLayoutMode.MEDIUM.showScreenTopAppBar)
        assertFalse(HomeLayoutMode.SUPPORTING_PANE.showScreenTopAppBar)
        assertFalse(HomeLayoutMode.BOOK.showScreenTopAppBar)
        assertTrue(HomeLayoutMode.TABLETOP.showScreenTopAppBar)
    }

    @Test
    fun `shows date bottom bar only in single pane layouts`() {
        assertTrue(HomeLayoutMode.COMPACT.showDateBottomBar)
        assertTrue(HomeLayoutMode.MEDIUM.showDateBottomBar)
        assertFalse(HomeLayoutMode.SUPPORTING_PANE.showDateBottomBar)
        assertFalse(HomeLayoutMode.BOOK.showDateBottomBar)
        assertFalse(HomeLayoutMode.TABLETOP.showDateBottomBar)
    }

    private fun createAdaptiveLayoutInfo(
        width: Int,
        height: Int = WINDOW_HEIGHT,
    ): AdaptiveLayoutInfo {
        return AdaptiveLayoutInfo(
            windowSizeClass = WindowSizeClass(width, height),
            windowSize = DpSize(width.dp, height.dp),
            posture = Posture(),
        )
    }

    private companion object {
        const val WINDOW_HEIGHT = 900
    }
}
