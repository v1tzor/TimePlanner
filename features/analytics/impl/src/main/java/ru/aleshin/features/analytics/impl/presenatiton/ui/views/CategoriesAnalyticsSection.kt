/*
 * Copyright 2023 Stanislav Aleshin
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
package ru.aleshin.features.analytics.impl.presenatiton.ui.views

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import hu.ma.charts.legend.data.LegendPosition
import hu.ma.charts.pie.PieChart
import hu.ma.charts.pie.data.PieChartData
import hu.ma.charts.pie.data.PieChartEntry
import ru.aleshin.core.ui.theme.TimePlannerRes
import ru.aleshin.core.ui.views.toMinutesAndHoursTitle
import ru.aleshin.core.utils.charts.fetchPieColorByTop
import ru.aleshin.core.utils.functional.TimePeriod
import ru.aleshin.features.analytics.impl.presenatiton.models.analytics.CategoriesAnalyticsUi
import ru.aleshin.features.analytics.impl.presenatiton.models.analytics.CategoryAnalyticUi
import ru.aleshin.features.analytics.impl.presenatiton.theme.AnalyticsThemeRes

/**
 * @author Stanislav Aleshin on 27.10.2023.
 */
@Composable
internal fun CategoriesAnalyticsSection(
    modifier: Modifier = Modifier,
    isLoading: Boolean,
    categoriesAnalytics: CategoriesAnalyticsUi?,
    timePeriod: TimePeriod?,
    onTimePeriodChanged: (TimePeriod) -> Unit,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        TimeSelectorSection(
            modifier = Modifier.padding(start = 16.dp, end = 8.dp),
            timePeriod = timePeriod,
            title = AnalyticsThemeRes.strings.categoryStatisticsTitle,
            onTimePeriodChanged = onTimePeriodChanged,
        )
        AnimatedContent(
            targetState = isLoading,
            label = "Categories analytics",
            transitionSpec = {
                fadeIn(animationSpec = tween(220, delayMillis = 90)).togetherWith(
                    fadeOut(animationSpec = tween(90)),
                )
            },
        ) { loading ->
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                var selectedItem by remember { mutableIntStateOf(0) }
                if (!loading && categoriesAnalytics != null) {
                    CategoriesAnalyticsChart(
                        analytics = categoriesAnalytics,
                        selectedItem = selectedItem,
                        onSelectItem = { selectedItem = it },
                    )
                    SubAnalyticsTimeLegend(
                        modifier = Modifier.height(400.dp).padding(top = 12.dp),
                        analytics = categoriesAnalytics,
                        selectedItem = selectedItem,
                    )
                } else {
                    Surface(
                        modifier = Modifier,
                        shape = MaterialTheme.shapes.large,
                        tonalElevation = TimePlannerRes.elevations.levelOne,
                        content = { Box(Modifier.fillMaxWidth().height(190.dp)) },
                    )
                    LazyColumn(
                        modifier = Modifier.height(400.dp).padding(top = 12.dp),
                        state = rememberLazyListState(),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        items(6) {
                            Surface(
                                shape = MaterialTheme.shapes.large,
                                color = MaterialTheme.colorScheme.surface,
                                tonalElevation = TimePlannerRes.elevations.levelOne,
                            ) {
                                Box(Modifier.fillMaxWidth().height(58.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun CategoriesAnalyticsChart(
    modifier: Modifier = Modifier,
    analytics: CategoriesAnalyticsUi,
    selectedItem: Int,
    onSelectItem: (Int) -> Unit,
) {
    val topList = analytics.subList(fromIndex = 0, toIndex = 5)
    val otherList = analytics.subList(5, analytics.lastIndex)
    val pieDataList = mutableListOf<PieChartEntry>().apply {
        topList.forEachIndexed { index, analytic ->
            val label = analytic.mainCategory.fetchName() ?: "*"
            val data = PieChartEntry(
                value = analytic.duration.toFloat() + 1f,
                label = AnnotatedString(label),
                color = fetchPieColorByTop(index),
            )
            add(data)
        }
        val otherPieData = PieChartEntry(
            value = otherList.sumOf { it.duration }.toFloat(),
            label = AnnotatedString(AnalyticsThemeRes.strings.otherAnalyticsName),
            color = fetchPieColorByTop(5),
        )
        add(otherPieData)
    }
    BoxWithConstraints(modifier = modifier.height(230.dp)) {
        PieChart(
            data = PieChartData(
                entries = pieDataList,
                legendPosition = LegendPosition.End,
                legendShape = RoundedCornerShape(8.dp),
            ),
            chartSize = 160.dp,
            sliceWidth = 24.dp,
        ) { legendEntries ->
            AnalyticsTimeLegend(
                modifier = Modifier.height(230.dp),
                legendEntries = legendEntries,
                selectedItem = selectedItem,
                onSelectedItem = onSelectItem,
            )
        }
        Text(
            modifier = Modifier.align(Alignment.CenterStart).offset(x = 55.dp, y = (-1).dp),
            text = analytics[selectedItem].duration.toMinutesAndHoursTitle(),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.titleMedium,
        )
    }
}

@Composable
internal fun SubAnalyticsTimeLegend(
    modifier: Modifier = Modifier,
    analytics: List<CategoryAnalyticUi>,
    selectedItem: Int,
) {
    LazyColumn(
        modifier = modifier,
        state = rememberLazyListState(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        if (selectedItem < 5) {
            val allSubCategoryAnalytics = analytics[selectedItem]
            val subCategoryAnalytic = allSubCategoryAnalytics.subCategoriesInfo

            items(subCategoryAnalytic) { analytic ->
                val percent = analytic.duration.toFloat() / allSubCategoryAnalytics.duration * 100

                SubAnalyticsTimeLegendItem(
                    name = analytic.subCategory.name ?: TimePlannerRes.strings.categoryEmptyTitle,
                    duration = analytic.duration,
                    percent = percent,
                )
            }
        } else {
            val otherAnalytics = analytics.subList(5, analytics.lastIndex)
            items(otherAnalytics) { analytic ->
                val percent = analytic.duration.toFloat() / otherAnalytics.sumOf { it.duration } * 100

                SubAnalyticsTimeLegendItem(
                    name = analytic.mainCategory.fetchName() ?: "*",
                    duration = analytic.duration,
                    percent = percent,
                )
            }
        }
    }
}

@Composable
internal fun SubAnalyticsTimeLegendItem(
    modifier: Modifier = Modifier,
    name: String,
    duration: Long,
    percent: Float,
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = TimePlannerRes.elevations.levelOne,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = name,
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.titleSmall,
                )
                Text(
                    text = duration.toMinutesAndHoursTitle(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "${percent.toInt()}%",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.titleSmall,
            )
        }
    }
}
