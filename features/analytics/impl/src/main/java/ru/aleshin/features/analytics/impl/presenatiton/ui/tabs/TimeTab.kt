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
 * imitations under the License.
 */
package ru.aleshin.features.analytics.impl.presenatiton.ui.tabs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
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
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import hu.ma.charts.legend.data.LegendPosition
import hu.ma.charts.pie.PieChart
import hu.ma.charts.pie.data.PieChartData
import hu.ma.charts.pie.data.PieChartEntry
import ru.aleshin.core.ui.views.toMinutesAndHoursTitle
import ru.aleshin.core.utils.charts.fetchPieColorByTop
import ru.aleshin.core.utils.functional.TimePeriod
import ru.aleshin.features.analytics.impl.presenatiton.models.CategoriesAnalyticsUi
import ru.aleshin.features.analytics.impl.presenatiton.theme.AnalyticsThemeRes
import ru.aleshin.features.analytics.impl.presenatiton.ui.contract.AnalyticsViewState
import ru.aleshin.features.analytics.impl.presenatiton.ui.views.AnalyticsTimeLegend
import ru.aleshin.features.analytics.impl.presenatiton.ui.views.SubAnalyticsTimeLegend
import ru.aleshin.features.analytics.impl.presenatiton.ui.views.TimeSelectorSection
import ru.aleshin.features.home.api.presentation.mappers.fetchNameByLanguage

/**
 * @author Stanislav Aleshin on 20.04.2023.
 */
@Composable
internal fun TimeTab(
    state: AnalyticsViewState,
    onTimePeriodChanged: (TimePeriod) -> Unit,
    onRefresh: () -> Unit,
) {
    // Pullrefresh not available for Material Design 3
    val refreshState = rememberSwipeRefreshState(
        isRefreshing = state.scheduleAnalytics?.categoriesAnalytics == null,
    )
    SwipeRefresh(state = refreshState, onRefresh = onRefresh) {
        Column(modifier = Modifier.fillMaxSize().padding(vertical = 12.dp)) {
            TimeSelectorSection(
                modifier = Modifier.padding(start = 16.dp, end = 8.dp, bottom = 16.dp),
                timePeriod = state.timePeriod,
                title = AnalyticsThemeRes.strings.categoryStatisticsTitle,
                onTimePeriodChanged = onTimePeriodChanged,
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp, end = 16.dp)
                    .verticalScroll(rememberScrollState()),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    val analytics = state.scheduleAnalytics?.categoriesAnalytics
                    var selectedItem by remember { mutableIntStateOf(0) }
                    if (analytics != null) {
                        CategoriesAnalyticsChart(
                            analytics = analytics,
                            selectedItem = selectedItem,
                            onSelectItem = { selectedItem = it },
                        )
                        SubAnalyticsTimeLegend(
                            modifier = Modifier.height(400.dp).padding(top = 12.dp),
                            analytics = analytics,
                            selectedItem = selectedItem,
                        )
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
            val data = PieChartEntry(
                value = analytic.duration.toFloat() + 1f,
                label = AnnotatedString(analytic.mainCategory.fetchNameByLanguage()),
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
    Box {
        PieChart(
            modifier = modifier.height(220.dp),
            data = PieChartData(
                entries = pieDataList,
                legendPosition = LegendPosition.End,
                legendShape = RoundedCornerShape(8.dp),
            ),
            chartSize = 160.dp,
            sliceWidth = 24.dp,
        ) { legendEntries ->
            AnalyticsTimeLegend(
                modifier = Modifier,
                legendEntries = legendEntries,
                selectedItem = selectedItem,
                onSelectedItem = onSelectItem,
            )
        }
        Text(
            modifier = Modifier.align(Alignment.CenterStart).offset(x = 55.dp, y = (-5).dp),
            text = analytics[selectedItem].duration.toMinutesAndHoursTitle(),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.titleMedium,
        )
    }
}
