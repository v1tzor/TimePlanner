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

import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Test
import ru.aleshin.features.analytics.impl.presentation.models.analytics.AnalyticsChartPointUi
import ru.aleshin.features.analytics.impl.presentation.models.analytics.AnalyticsDonutSliceUi
import ru.aleshin.features.analytics.impl.presentation.models.analytics.AnalyticsLineSeriesUi

/**
 * @author Stanislav Aleshin on 22.07.2026.
 */
internal class AnalyticsChartsAdapterTest {

    @Test
    fun horizontalLabelsUseSpaceBetweenVisiblePoints() {
        val widths = calculateAnalyticsChartLabelWidths(
            centers = listOf(25f, 150f, 275f),
            totalWidth = 300f,
            gap = 8f,
        )

        assertEquals(listOf(42f, 117f, 42f), widths)
    }

    @Test
    fun multilineDataIsTransposedToChartyPointGroupsAndKeepsSelectedLabel() {
        val groups = buildAnalyticsLineGroups(
            series = listOf(
                AnalyticsLineSeriesUi("Category", listOf(1f, 2f, 3f, 4f)),
                AnalyticsLineSeriesUi("All", listOf(5f, 6f, 7f, 8f)),
            ),
            labels = listOf("1", "2", "3", "4"),
            labelStride = 3,
            selectedIndex = 2,
        )

        assertEquals(4, groups.size)
        assertEquals(listOf(1f, 5f), groups[0].values)
        assertEquals(listOf(2f, 6f), groups[1].values)
        assertEquals(listOf(3f, 7f), groups[2].values)
        assertEquals(listOf(4f, 8f), groups[3].values)
        assertEquals(listOf("1", "", "3", "4"), groups.map { it.label })
    }

    @Test
    fun multilineDataClipsMismatchedInputsToShortestSeries() {
        val groups = buildAnalyticsLineGroups(
            series = listOf(
                AnalyticsLineSeriesUi("Category", listOf(1f, 2f)),
                AnalyticsLineSeriesUi("All", listOf(3f)),
            ),
            labels = listOf("1", "2", "3"),
            labelStride = 1,
            selectedIndex = null,
        )

        assertEquals(1, groups.size)
        assertEquals(listOf(1f, 3f), groups.single().values)
    }

    @Test
    fun comboLabelsThinVisualTextAndKeepUniqueInteractionIdentity() {
        val points = List(5) { index ->
            AnalyticsChartPointUi(index.toLong(), "Day", index.toFloat())
        }

        val labels = buildAnalyticsChartLabels(points, labelStride = 3, selectedKey = 2L)

        assertEquals(listOf("Day", "", "Day", "Day", "Day"), labels.map { it.replace("\u2060", "") })
        assertEquals(labels.size, labels.toSet().size)
    }

    @Test
    fun chartValuesAreNormalizedWithoutChangingTheirRelativeScale() {
        assertEquals(0f, normalizeAnalyticsChartValue(0f, 7f), 0.0001f)
        assertEquals(4.9995f, normalizeAnalyticsChartValue(3.5f, 7f), 0.0001f)
        assertEquals(9.999f, normalizeAnalyticsChartValue(7f, 7f), 0.0001f)
    }

    @Test
    fun emptyLineSeriesKeepsSharedChartyScaleWithoutChangingRawValues() {
        val normalizedValues = normalizeAnalyticsLineValues(
            values = listOf(0f, 0f, 0f),
            maximum = 4f,
        )

        assertEquals(listOf(0.0001f, 0.0001f, 0.0001f), normalizedValues)
    }

    @Test
    fun donutDataExcludesZeroSlicesRejectedByCharty() {
        val slices = filterAnalyticsDonutSlices(
            listOf(
                AnalyticsDonutSliceUi(1L, "Zero", 0f, Color.Red),
                AnalyticsDonutSliceUi(2L, "Positive", 1f, Color.Blue),
            ),
        )

        assertEquals(listOf(2L), slices.map { it.key })
    }

    @Test
    fun donutDataRemainsEmptyWhenAllSlicesAreZero() {
        val slices = filterAnalyticsDonutSlices(
            listOf(
                AnalyticsDonutSliceUi(1L, "First", 0f, Color.Red),
                AnalyticsDonutSliceUi(2L, "Second", 0f, Color.Blue),
            ),
        )

        assertEquals(emptyList<AnalyticsDonutSliceUi>(), slices)
    }

    @Test
    fun multilineSeriesAreSplitForIndependentChartyStyles() {
        val groups = buildAnalyticsLineGroupsBySeries(
            series = listOf(
                AnalyticsLineSeriesUi("Category", listOf(1f, 2f)),
                AnalyticsLineSeriesUi("All", listOf(3f, 4f)),
            ),
            labels = listOf("1", "2"),
            labelStride = 1,
            selectedIndex = null,
        )

        assertEquals(listOf(listOf(1f), listOf(2f)), groups[0].map { it.values })
        assertEquals(listOf(listOf(3f), listOf(4f)), groups[1].map { it.values })
    }

    @Test
    fun splitMultilineSeriesShareTheShortestPointGrid() {
        val groups = buildAnalyticsLineGroupsBySeries(
            series = listOf(
                AnalyticsLineSeriesUi("Category", listOf(1f, 2f, 3f)),
                AnalyticsLineSeriesUi("All", listOf(4f, 5f)),
            ),
            labels = listOf("1", "2", "3"),
            labelStride = 1,
            selectedIndex = null,
        )

        assertEquals(2, groups[0].size)
        assertEquals(2, groups[1].size)
        assertEquals(listOf("1", "2"), groups[0].map { it.label })
        assertEquals(listOf("1", "2"), groups[1].map { it.label })
    }

    @Test
    fun lineTapMapsDirectlyToCenteredBucketSlot() {
        assertEquals(0, calculateAnalyticsChartPointIndex(0f, 400f, 4))
        assertEquals(0, calculateAnalyticsChartPointIndex(109f, 400f, 4))
        assertEquals(1, calculateAnalyticsChartPointIndex(110f, 400f, 4))
        assertEquals(3, calculateAnalyticsChartPointIndex(400f, 400f, 4))
        assertEquals(65f, calculateAnalyticsChartPointCenter(400f, 4, 0), 0.0001f)
        assertEquals(155f, calculateAnalyticsChartPointCenter(400f, 4, 1), 0.0001f)
        assertEquals(335f, calculateAnalyticsChartPointCenter(400f, 4, 3), 0.0001f)
    }
}
