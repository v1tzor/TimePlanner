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
package ru.aleshin.features.templates.impl.presentation.ui.templates

import androidx.compose.material3.adaptive.Posture
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import org.junit.Assert.assertEquals
import org.junit.Test
import ru.aleshin.timeplanner.core.ui.views.AdaptiveLayoutInfo

/**
 * @author Stanislav Aleshin on 28.07.2026.
 */
class TemplatesLayoutModeTest {

    @Test
    fun `uses compact layout below medium width`() {
        assertEquals(
            TemplatesLayoutMode.COMPACT,
            TemplatesLayoutMode.from(createAdaptiveLayoutInfo(width = 599)),
        )
    }

    @Test
    fun `uses supporting layout from medium width`() {
        assertEquals(
            TemplatesLayoutMode.SUPPORTING,
            TemplatesLayoutMode.from(createAdaptiveLayoutInfo(width = 600)),
        )
        assertEquals(
            TemplatesLayoutMode.SUPPORTING,
            TemplatesLayoutMode.from(createAdaptiveLayoutInfo(width = 840)),
        )
        assertEquals(
            TemplatesLayoutMode.SUPPORTING,
            TemplatesLayoutMode.from(createAdaptiveLayoutInfo(width = 1600)),
        )
    }

    private fun createAdaptiveLayoutInfo(width: Int): AdaptiveLayoutInfo {
        return AdaptiveLayoutInfo(
            windowSizeClass = WindowSizeClass(width, WINDOW_HEIGHT),
            windowSize = DpSize(width.dp, WINDOW_HEIGHT.dp),
            posture = Posture(),
        )
    }

    private companion object {
        const val WINDOW_HEIGHT = 900
    }
}
