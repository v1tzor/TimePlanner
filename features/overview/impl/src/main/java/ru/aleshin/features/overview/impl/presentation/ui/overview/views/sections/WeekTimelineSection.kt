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
package ru.aleshin.features.overview.impl.presentation.ui.overview.views.sections

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ru.aleshin.features.overview.impl.presentation.models.overview.WeekScheduleUi
import ru.aleshin.features.overview.impl.presentation.theme.OverviewThemeRes
import ru.aleshin.features.overview.impl.presentation.ui.overview.views.DaySummaryCards
import ru.aleshin.features.overview.impl.presentation.ui.overview.views.WeekTimeline
import ru.aleshin.timeplanner.core.ui.views.PlaceholderBox
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * @author Stanislav Aleshin on 16.07.2026.
 */
@Composable
internal fun WeekTimelineSection(
    modifier: Modifier = Modifier,
    isLoading: Boolean,
    selectedDate: Date?,
    schedules: List<WeekScheduleUi>,
    weekTasksCount: Int,
    horizontalPadding: Dp = 16.dp,
    useCompactSize: Boolean = true,
    onSelectSchedule: (Date) -> Unit,
) {
    val daySummary = remember(selectedDate, schedules) {
        schedules.find { schedule -> schedule.date == selectedDate }?.summary
    }
    val contentState = when {
        isLoading -> WeekTimelineSectionContentState.LOADING
        else -> WeekTimelineSectionContentState.DATA
    }

    Column(
        modifier = modifier.padding(horizontal = horizontalPadding),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        WeekTimelineHeader(
            isLoading = isLoading,
            schedules = schedules,
            tasksCount = weekTasksCount,
        )
        DaySummaryCards(
            isLoading = isLoading,
            daySummary = daySummary,
            useCompactSummary = useCompactSize,
        )
        AnimatedContent(
            modifier = Modifier.fillMaxWidth(),
            targetState = contentState,
            transitionSpec = {
                fadeIn(animationSpec = tween(600, delayMillis = 90)).togetherWith(
                    fadeOut(animationSpec = tween(300)),
                )
            },
            contentKey = { state -> state },
            label = "WeekTimelineSectionContent",
        ) { state ->
            when (state) {
                WeekTimelineSectionContentState.LOADING -> {
                    PlaceholderBox(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(294.dp),
                        shape = MaterialTheme.shapes.extraLarge,
                    )
                }
                WeekTimelineSectionContentState.DATA -> {
                    WeekTimeline(
                        selectedDate = selectedDate,
                        schedules = schedules,
                        useCompactSize = useCompactSize,
                        onSelectSchedule = onSelectSchedule,
                    )
                }
            }
        }
    }
}

@Composable
private fun WeekTimelineHeader(
    isLoading: Boolean,
    schedules: List<WeekScheduleUi>,
    tasksCount: Int,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Bottom,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = OverviewThemeRes.strings.weekTimelineTitle,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = remember(schedules) { schedules.fetchWeekRangeTitle() },
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        if (!isLoading) {
            Text(
                text = OverviewThemeRes.strings.tasksCountFormat.format(tasksCount),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

private fun List<WeekScheduleUi>.fetchWeekRangeTitle(): String {
    val firstDate = firstOrNull()?.date ?: return ""
    val lastDate = lastOrNull()?.date ?: return ""
    val dayFormat = SimpleDateFormat("d", Locale.getDefault())
    val monthFormat = SimpleDateFormat("MMM", Locale.getDefault())
    val fullFormat = SimpleDateFormat("d MMM", Locale.getDefault())

    return if (monthFormat.format(firstDate) == monthFormat.format(lastDate)) {
        "${dayFormat.format(firstDate)}–${fullFormat.format(lastDate)}"
    } else {
        "${fullFormat.format(firstDate)}–${fullFormat.format(lastDate)}"
    }
}

private enum class WeekTimelineSectionContentState {
    LOADING,
    DATA,
}