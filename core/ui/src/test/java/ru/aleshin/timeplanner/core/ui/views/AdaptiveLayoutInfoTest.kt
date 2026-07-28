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

package ru.aleshin.timeplanner.core.ui.views

import androidx.compose.material3.adaptive.Posture
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * @author Stanislav Aleshin on 26.07.2026.
 */
class AdaptiveLayoutInfoTest {

    @Test
    fun `classifies width boundaries`() {
        assertTrue(createInfo(599, 480).isCompactWidth)
        assertTrue(createInfo(600, 480).isMediumWidth)
        assertTrue(createInfo(839, 480).isMediumWidth)
        assertTrue(createInfo(840, 480).isExpandedWidth)
        assertTrue(createInfo(1199, 480).isExpandedWidth)
        assertTrue(createInfo(1200, 480).isLargeWidth)
        assertTrue(createInfo(1599, 480).isLargeWidth)
        assertTrue(createInfo(1600, 480).isExtraLargeWidth)
    }

    @Test
    fun `classifies height boundaries`() {
        assertTrue(createInfo(600, 479).isCompactHeight)
        assertTrue(createInfo(600, 480).isMediumHeight)
        assertTrue(createInfo(600, 899).isMediumHeight)
        assertTrue(createInfo(600, 900).isExpandedHeight)
    }

    @Test
    fun `uses bottom navigation for compact height and tabletop`() {
        assertFalse(createInfo(840, 479).useNavigationRail)
        assertFalse(createInfo(840, 479).useExpandedLayout)
        assertFalse(createInfo(840, 900, Posture(isTabletop = true)).useNavigationRail)
        assertTrue(createInfo(840, 900).useNavigationRail)
    }

    private fun createInfo(
        width: Int,
        height: Int,
        posture: Posture = Posture(),
    ): AdaptiveLayoutInfo {
        return AdaptiveLayoutInfo(
            windowSizeClass = WindowSizeClass(width, height),
            windowSize = DpSize(width.dp, height.dp),
            posture = posture,
        )
    }
}
