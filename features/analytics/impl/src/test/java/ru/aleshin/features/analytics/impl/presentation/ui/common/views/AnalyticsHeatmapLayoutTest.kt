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
package ru.aleshin.features.analytics.impl.presentation.ui.common.views

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * @author Stanislav Aleshin on 22.07.2026.
 */
internal class AnalyticsHeatmapLayoutTest {

    @Test
    fun logicalSelectionMirrorsWithLayoutDirection() {
        assertEquals(0, heatmapLogicalIndex(1f, 7, 10f, 2f, false))
        assertEquals(6, heatmapLogicalIndex(1f, 7, 10f, 2f, true))
        assertEquals(6, heatmapLogicalIndex(73f, 7, 10f, 2f, false))
        assertEquals(0, heatmapLogicalIndex(73f, 7, 10f, 2f, true))
    }

    @Test
    fun labelCenterMatchesHeatmapCellCenter() {
        assertEquals(14f, calculateHeatmapItemCenter(0, 28f, 4f))
        assertEquals(46f, calculateHeatmapItemCenter(1, 28f, 4f))
        assertEquals(206f, calculateHeatmapItemCenter(6, 28f, 4f))
    }
}
