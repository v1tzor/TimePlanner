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
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ru.aleshin.core.presentation.models.tasks.TimeTaskUi
import ru.aleshin.features.overview.impl.presentation.models.overview.WeekScheduleUi
import ru.aleshin.features.overview.impl.presentation.theme.OverviewThemeRes
import ru.aleshin.features.overview.impl.presentation.ui.overview.views.SelectedDayTaskItem
import ru.aleshin.timeplanner.core.ui.views.PlaceholderBox
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * @author Stanislav Aleshin on 16.07.2026.
 */
@Composable
internal fun SelectedDaySection(
    modifier: Modifier = Modifier,
    isLoading: Boolean,
    selectedDate: Date?,
    schedules: List<WeekScheduleUi>,
    horizontalPadding: Dp = 16.dp,
    useParentScroll: Boolean = false,
    onOpenTimeTask: (TimeTaskUi) -> Unit,
) {
    val schedule = remember(selectedDate, schedules) {
        schedules.find { item -> item.date == selectedDate }
    }
    val timeTasks = schedule?.timeTasks ?: emptyList()
    val contentState = when {
        isLoading || selectedDate == null -> SelectedDaySectionContentState.LOADING
        timeTasks.isNotEmpty() -> SelectedDaySectionContentState.DATA
        else -> SelectedDaySectionContentState.EMPTY
    }

    AnimatedContent(
        modifier = modifier,
        targetState = contentState,
        transitionSpec = {
            fadeIn(animationSpec = tween(600, delayMillis = 90)).togetherWith(
                fadeOut(animationSpec = tween(300)),
            )
        },
        contentKey = { state -> state },
        label = "SelectedDaySectionContent",
    ) { state ->
        Column(
            modifier = Modifier.padding(horizontal = horizontalPadding),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            when (state) {
                SelectedDaySectionContentState.LOADING -> {
                    PlaceholderBox(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        shape = MaterialTheme.shapes.large,
                    )
                }
                SelectedDaySectionContentState.EMPTY -> {
                    if (selectedDate != null) {
                        SelectedDayHeader(
                            selectedDate = selectedDate,
                            tasksCount = timeTasks.size,
                        )
                    }
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.large,
                        color = MaterialTheme.colorScheme.surfaceContainerLow,
                    ) {
                        Text(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp),
                            text = OverviewThemeRes.strings.noScheduledTasksTitle,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
                SelectedDaySectionContentState.DATA -> {
                    if (selectedDate != null) {
                        SelectedDayHeader(
                            selectedDate = selectedDate,
                            tasksCount = timeTasks.size,
                        )
                    }
                    val taskListModifier = Modifier
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.large)
                        .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                        .border(
                            0.5.dp,
                            MaterialTheme.colorScheme.outlineVariant,
                            MaterialTheme.shapes.large,
                        )
                        .animateContentSize(spring(stiffness = Spring.StiffnessHigh))
                    if (useParentScroll) {
                        Column(modifier = taskListModifier) {
                            timeTasks.forEachIndexed { index, task ->
                                SelectedDayTaskItem(
                                    task = task,
                                    onClick = { onOpenTimeTask(task) },
                                )
                                if (index != timeTasks.lastIndex) {
                                    HorizontalDivider(
                                        color = MaterialTheme.colorScheme.outlineVariant,
                                    )
                                }
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = taskListModifier.height(
                                64.dp * timeTasks.size.coerceAtMost(3),
                            ),
                        ) {
                            itemsIndexed(
                                items = timeTasks,
                                key = { _, task -> task.key },
                            ) { index, task ->
                                SelectedDayTaskItem(
                                    task = task,
                                    onClick = { onOpenTimeTask(task) },
                                )
                                if (index != timeTasks.lastIndex) {
                                    HorizontalDivider(
                                        color = MaterialTheme.colorScheme.outlineVariant,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SelectedDayHeader(
    selectedDate: Date,
    tasksCount: Int,
) {
    val strings = OverviewThemeRes.strings
    val dateFormat = remember { SimpleDateFormat("EEEE, d MMM", Locale.getDefault()) }
    val title = remember(selectedDate, tasksCount, strings) {
        strings.selectedDayHeaderFormat.format(
            dateFormat.format(selectedDate),
            strings.tasksCountFormat.format(tasksCount),
        )
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = title,
            color = MaterialTheme.colorScheme.primary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.titleSmall,
        )
    }
}

private enum class SelectedDaySectionContentState {
    LOADING,
    EMPTY,
    DATA,
}
