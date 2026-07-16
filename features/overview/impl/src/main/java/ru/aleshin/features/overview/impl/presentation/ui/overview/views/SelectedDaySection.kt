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
package ru.aleshin.features.overview.impl.presentation.ui.overview.views

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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.unit.dp
import ru.aleshin.core.presentation.mappers.mapToIconPainter
import ru.aleshin.core.presentation.models.tasks.TimeTaskUi
import ru.aleshin.features.overview.impl.presentation.models.overview.WeekScheduleUi
import ru.aleshin.features.overview.impl.presentation.theme.OverviewThemeRes
import ru.aleshin.features.overview.impl.presentation.theme.tokens.fetchOverviewCategoryColors
import ru.aleshin.timeplanner.core.ui.views.CategoryIconMonogram
import ru.aleshin.timeplanner.core.ui.views.CategoryTextMonogram
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
    onOpenTimeTask: (TimeTaskUi) -> Unit,
) {
    val schedule = remember(selectedDate, schedules) {
        schedules.find { item -> item.date == selectedDate }
    }
    val timeTasks = schedule?.timeTasks ?: emptyList()

    AnimatedContent(
        modifier = modifier,
        targetState = isLoading || selectedDate == null,
        label = "SelectedDaySection",
        transitionSpec = {
            fadeIn(animationSpec = tween(600, delayMillis = 90)).togetherWith(
                fadeOut(animationSpec = tween(300)),
            )
        },
    ) { loading ->
        Column(
            modifier = modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (loading || selectedDate == null) {
                PlaceholderBox(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    shape = MaterialTheme.shapes.large,
                )
            } else {
                SelectedDayHeader(
                    selectedDate = selectedDate,
                    tasksCount = timeTasks.size,
                )
                if (timeTasks.isEmpty()) {
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
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(MaterialTheme.shapes.large)
                            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                            .border(
                                0.5.dp,
                                MaterialTheme.colorScheme.outlineVariant,
                                MaterialTheme.shapes.large
                            )
                            .animateContentSize(spring(stiffness = Spring.StiffnessHigh))
                            .height(64.dp * timeTasks.size.coerceAtMost(3))
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
                                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
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

@Composable
private fun SelectedDayTaskItem(
    modifier: Modifier = Modifier,
    task: TimeTaskUi,
    onClick: () -> Unit,
) {
    val timeFormat = remember { SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT) }
    val categoryTitle = task.category.fetchName() ?: OverviewThemeRes.strings.noneTitle
    val subCategoryTitle = task.subCategory?.name?.takeIf { name -> name.isNotBlank() }
    val noteTitle = task.note?.takeIf { note -> note.isNotBlank() }
    val taskTitle = subCategoryTitle ?: categoryTitle
    val taskSubtitle = noteTitle ?: categoryTitle.takeIf { subCategoryTitle != null }
    val categoryColors = fetchOverviewCategoryColors(task.category.id)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .clickable(onClick = onClick)
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.width(48.dp),
            horizontalAlignment = Alignment.End,
        ) {
            Text(
                text = timeFormat.format(task.timeRanges.from),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall,
            )
            Text(
                text = timeFormat.format(task.timeRanges.to),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall,
            )
        }
        Box(
            modifier = Modifier
                .width(18.dp)
                .fillMaxHeight(),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.outlineVariant),
            )
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(categoryColors.accent),
            )
        }
        val categoryIcon = task.category.defaultType?.mapToIconPainter()
        if (categoryIcon != null) {
            CategoryIconMonogram(
                modifier = Modifier.size(36.dp),
                icon = categoryIcon,
                iconSize = 18.dp,
                iconDescription = categoryTitle,
                iconColor = categoryColors.accent,
                backgroundColor = categoryColors.container,
            )
        } else {
            CategoryTextMonogram(
                modifier = Modifier.size(36.dp),
                text = remember(categoryTitle) { categoryTitle.fetchMonogram() },
                textColor = categoryColors.accent,
                backgroundColor = categoryColors.container,
            )
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = taskTitle,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleSmall,
            )
            if (taskSubtitle != null) {
                Text(
                    text = taskSubtitle,
                    color = when (noteTitle != null) {
                        true -> MaterialTheme.colorScheme.onSurfaceVariant
                        false -> categoryColors.accent
                    },
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}

private fun String.fetchMonogram(): String {
    return filter { char -> char.isLetterOrDigit() }.take(2).ifEmpty { "*" }
}
