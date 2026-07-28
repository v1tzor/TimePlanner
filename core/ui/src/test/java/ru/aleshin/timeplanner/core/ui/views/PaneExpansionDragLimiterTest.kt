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

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * @author Stanislav Aleshin on 27.07.2026.
 */
class PaneExpansionDragLimiterTest {

    @Test
    fun `keeps free movement inside pane limits`() {
        val limiter = PaneExpansionDragLimiter(
            firstPaneMinWidth = 350f,
            secondPaneMinWidth = 300f,
        )
        limiter.updateLayout(dividerOffset = 500f, scaffoldWidth = 1000f)

        assertEquals(75f, limiter.consumeDragDelta(delta = 75f))
        assertEquals(-25f, limiter.consumeDragDelta(delta = -25f))
        assertEquals(125f, limiter.consumeDragDelta(delta = 125f))
    }

    @Test
    fun `limits first pane width`() {
        val limiter = PaneExpansionDragLimiter(
            firstPaneMinWidth = 350f,
            secondPaneMinWidth = 300f,
        )
        limiter.updateLayout(dividerOffset = 500f, scaffoldWidth = 1000f)

        assertEquals(-150f, limiter.consumeDragDelta(delta = -300f))
        assertEquals(0f, limiter.consumeDragDelta(delta = -20f))
    }

    @Test
    fun `limits second pane width`() {
        val limiter = PaneExpansionDragLimiter(
            firstPaneMinWidth = 350f,
            secondPaneMinWidth = 300f,
        )
        limiter.updateLayout(dividerOffset = 500f, scaffoldWidth = 1000f)

        assertEquals(200f, limiter.consumeDragDelta(delta = 300f))
        assertEquals(0f, limiter.consumeDragDelta(delta = 20f))
    }

    @Test
    fun `limits first pane maximum width`() {
        val limiter = PaneExpansionDragLimiter(
            firstPaneMinWidth = 300f,
            secondPaneMinWidth = 300f,
            firstPaneMaxWidth = 550f,
        )
        limiter.updateLayout(dividerOffset = 500f, scaffoldWidth = 1000f)

        assertEquals(50f, limiter.consumeDragDelta(delta = 100f))
        assertEquals(0f, limiter.consumeDragDelta(delta = 20f))
    }

    @Test
    fun `limits second pane maximum width`() {
        val limiter = PaneExpansionDragLimiter(
            firstPaneMinWidth = 300f,
            secondPaneMinWidth = 300f,
            secondPaneMaxWidth = 420f,
        )
        limiter.updateLayout(dividerOffset = 650f, scaffoldWidth = 1000f)

        assertEquals(-70f, limiter.consumeDragDelta(delta = -100f))
        assertEquals(0f, limiter.consumeDragDelta(delta = -20f))
    }

    @Test
    fun `disables resizing when minimum widths do not fit`() {
        val limiter = PaneExpansionDragLimiter(
            firstPaneMinWidth = 350f,
            secondPaneMinWidth = 350f,
        )
        limiter.updateLayout(dividerOffset = 300f, scaffoldWidth = 600f)

        assertEquals(0f, limiter.consumeDragDelta(delta = 40f))
    }
}
