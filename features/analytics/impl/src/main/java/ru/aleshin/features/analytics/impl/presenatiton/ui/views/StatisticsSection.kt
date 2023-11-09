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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ru.aleshin.core.ui.theme.TimePlannerRes
import ru.aleshin.core.ui.views.toMinutesAndHoursTitle
import ru.aleshin.features.analytics.impl.presenatiton.models.analytics.ScheduleAnalyticsUi
import ru.aleshin.features.analytics.impl.presenatiton.theme.AnalyticsThemeRes

/**
 * @author Stanislav Aleshin on 27.10.2023.
 */
@Composable
internal fun StatisticsSection(
    modifier: Modifier = Modifier,
    isLoading: Boolean,
    schedulesAnalytics: ScheduleAnalyticsUi?,
) {
    AnimatedContent(
        modifier = modifier.padding(top = 8.dp),
        targetState = isLoading,
        label = "Executed analytics",
        transitionSpec = {
            fadeIn(animationSpec = tween(220, delayMillis = 90)).togetherWith(
                fadeOut(animationSpec = tween(90)),
            )
        },
    ) { loading ->
        if (!loading && schedulesAnalytics != null) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp).height(320.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                item {
                    StatisticInfoView(
                        icon = AnalyticsThemeRes.icons.numberedList,
                        name = AnalyticsThemeRes.strings.totalCountTaskTitle,
                        value = schedulesAnalytics.totalTasksCount.toString(),
                    )
                }
                item {
                    StatisticInfoView(
                        icon = AnalyticsThemeRes.icons.numericOneCircle,
                        name = AnalyticsThemeRes.strings.averageCountTaskTitle,
                        value = "~ ${schedulesAnalytics.averageDayLoad}",
                    )
                }
                item {
                    StatisticInfoView(
                        icon = AnalyticsThemeRes.icons.timeComplete,
                        name = AnalyticsThemeRes.strings.totalTimeTaskTitle,
                        value = schedulesAnalytics.totalTasksTime.toMinutesAndHoursTitle(),
                    )
                }
                item {
                    StatisticInfoView(
                        icon = AnalyticsThemeRes.icons.timeCheck,
                        name = AnalyticsThemeRes.strings.averageTimeTaskTitle,
                        value = schedulesAnalytics.averageTaskTime.toMinutesAndHoursTitle(),
                    )
                }
            }
        }
    }
}

@Composable
internal fun StatisticInfoView(
    modifier: Modifier = Modifier,
    icon: Int,
    name: String,
    value: String,
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = TimePlannerRes.elevations.levelOne,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = name,
                tint = MaterialTheme.colorScheme.onSurface,
            )
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = name,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = value,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleLarge,
                )
            }
        }
    }
}
