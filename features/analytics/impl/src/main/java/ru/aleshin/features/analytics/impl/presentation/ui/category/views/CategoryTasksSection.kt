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
package ru.aleshin.features.analytics.impl.presentation.ui.category.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ru.aleshin.features.analytics.impl.domain.entities.AnalyticsTaskStatus
import ru.aleshin.features.analytics.impl.presentation.models.category.CategoryKeyMetricsUi
import ru.aleshin.features.analytics.impl.presentation.models.category.CategoryTaskRowUi
import ru.aleshin.features.analytics.impl.presentation.theme.AnalyticsThemeRes
import ru.aleshin.features.analytics.impl.presentation.theme.tokens.AnalyticsStrings
import ru.aleshin.features.analytics.impl.presentation.ui.common.views.AnalyticsSectionTitle
import ru.aleshin.features.analytics.impl.presentation.utils.AnalyticsValueFormatter
import ru.aleshin.features.analytics.impl.presentation.utils.fetchAnalyticsLocale
import ru.aleshin.features.analytics.impl.presentation.utils.rememberAnalyticsValueFormatter
import ru.aleshin.timeplanner.core.ui.theme.TimePlannerRes
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * @author Stanislav Aleshin on 23.07.2026.
 */
internal fun LazyListScope.CategoryTasksSection(
    taskRows: List<CategoryTaskRowUi>,
    metrics: CategoryKeyMetricsUi,
    isExpanded: Boolean,
    hasFollowingSection: Boolean,
    onToggle: () -> Unit,
) {
    val visibleRows = if (isExpanded) {
        taskRows
    } else {
        taskRows.take(n = PREVIEW_TASK_COUNT)
    }
    val hasExpandAction = taskRows.size > PREVIEW_TASK_COUNT

    item(key = TASKS_HEADER_KEY) {
        AnalyticsSectionTitle(
            title = AnalyticsThemeRes.strings.categoryTasksTitle,
            modifier = Modifier
                .fillMaxWidth()
                .widthInContent()
                .padding(bottom = 8.dp),
        )
    }
    item(key = TASKS_SUMMARY_KEY) {
        CategoryTasksSummary(
            metrics = metrics,
        )
    }
    itemsIndexed(
        items = visibleRows,
        key = { _, row -> "category-task-${row.task.key}" },
    ) { index, row ->
        val isLast = index == visibleRows.lastIndex && !hasExpandAction
        CategoryTaskItem(
            row = row,
            isLast = isLast,
        )
    }
    if (hasExpandAction) {
        item(key = TASKS_EXPAND_KEY) {
            CategoryTasksExpandButton(
                isExpanded = isExpanded,
                hasFollowingSection = hasFollowingSection,
                onClick = onToggle,
            )
        }
    } else if (hasFollowingSection) {
        item(key = TASKS_SPACING_KEY) {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun CategoryTasksSummary(
    metrics: CategoryKeyMetricsUi,
) {
    val strings = AnalyticsThemeRes.strings
    val formatter = rememberAnalyticsValueFormatter()

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .widthInContent(),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        shape = RoundedCornerShape(
            topStart = 16.dp,
            topEnd = 16.dp,
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 64.dp)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = strings.tasksFormat.format(metrics.taskCount),
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.titleMedium,
            )
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = strings.averageDuration,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = metrics.averageDurationMillis?.let {
                        formatter.formatDuration(
                            durationMillis = it,
                            hourSymbol = strings.hourShort,
                            minuteSymbol = strings.minuteShort,
                        )
                    } ?: strings.unavailableValue,
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        }
    }
}

@Composable
private fun CategoryTaskItem(
    row: CategoryTaskRowUi,
    isLast: Boolean,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .widthInContent(),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        shape = if (isLast) {
            RoundedCornerShape(
                bottomStart = 16.dp,
                bottomEnd = 16.dp,
            )
        } else {
            RoundedCornerShape(0.dp)
        },
    ) {
        Column {
            CategoryTaskRow(
                row = row,
            )
            if (!isLast) {
                HorizontalDivider(
                    modifier = Modifier.padding(start = 64.dp),
                    color = MaterialTheme.colorScheme.outlineVariant,
                )
            }
        }
    }
}

@Composable
private fun CategoryTasksExpandButton(
    isExpanded: Boolean,
    hasFollowingSection: Boolean,
    onClick: () -> Unit,
) {
    val strings = AnalyticsThemeRes.strings
    val actionTitle = if (isExpanded) {
        strings.collapse
    } else {
        strings.showAllTasks
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .widthInContent()
            .padding(bottom = if (hasFollowingSection) 24.dp else 0.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        shape = RoundedCornerShape(
            bottomStart = 16.dp,
            bottomEnd = 16.dp,
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clickable(
                    role = Role.Button,
                    onClick = onClick,
                )
                .semantics {
                    contentDescription = actionTitle
                }
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = actionTitle,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                painter = painterResource(
                    id = if (isExpanded) {
                        TimePlannerRes.icons.arrowUp
                    } else {
                        TimePlannerRes.icons.arrowDown
                    },
                ),
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
private fun CategoryTaskRow(
    row: CategoryTaskRowUi,
) {
    val strings = AnalyticsThemeRes.strings
    val language = TimePlannerRes.language
    val locale = remember(language) { language.fetchAnalyticsLocale() }
    val dateFormatter = remember(locale) {
        SimpleDateFormat(TASK_DATE_PATTERN, locale)
    }
    val timeFormatter = remember(locale) {
        SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT, locale)
    }
    val valueFormatter = rememberAnalyticsValueFormatter()
    val statusTitle = row.status.fetchTitle(strings = strings)
    val taskDate = dateFormatter.format(row.task.date)
    val taskTime = "${
        timeFormatter.format(row.task.timeRanges.from)
    }–${timeFormatter.format(row.task.timeRanges.to)}"
    val taskDuration = valueFormatter.formatDuration(
        durationMillis = row.safeDurationMillis,
        hourSymbol = strings.hourShort,
        minuteSymbol = strings.minuteShort,
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 72.dp)
            .semantics(mergeDescendants = true) {
                contentDescription = buildString {
                    append(row.task.subCategory?.name ?: strings.withoutSubCategory)
                    append(", ")
                    append(taskDate)
                    append(", ")
                    append(taskTime)
                    append(", ")
                    append(taskDuration)
                    append(", ")
                    append(statusTitle)
                }
            }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CategoryTaskStatusIndicator(status = row.status)
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = row.task.subCategory?.name ?: strings.withoutSubCategory,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = "$taskDate · $taskTime",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = taskDuration,
            style = MaterialTheme.typography.titleMedium,
        )
    }
}

@Composable
private fun CategoryTaskStatusIndicator(
    status: AnalyticsTaskStatus,
) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .background(
                color = status.fetchContainerColor(),
                shape = RoundedCornerShape(10.dp),
            ),
        contentAlignment = Alignment.Center,
    ) {
        if (status == AnalyticsTaskStatus.UNFINISHED) {
            Box(
                modifier = Modifier
                    .width(16.dp)
                    .height(2.dp)
                    .background(
                        color = status.fetchContentColor(),
                        shape = RoundedCornerShape(2.dp),
                    ),
            )
        } else {
            Icon(
                imageVector = if (status == AnalyticsTaskStatus.COMPLETED) {
                    Icons.Default.Check
                } else {
                    Icons.Default.Close
                },
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = status.fetchContentColor(),
            )
        }
    }
}

@Composable
private fun AnalyticsTaskStatus.fetchContainerColor(): Color = when (this) {
    AnalyticsTaskStatus.COMPLETED -> MaterialTheme.colorScheme.primaryContainer
    AnalyticsTaskStatus.SKIPPED -> MaterialTheme.colorScheme.errorContainer
    AnalyticsTaskStatus.UNFINISHED -> MaterialTheme.colorScheme.surfaceContainer
}

@Composable
private fun AnalyticsTaskStatus.fetchContentColor(): Color = when (this) {
    AnalyticsTaskStatus.COMPLETED -> MaterialTheme.colorScheme.onPrimaryContainer
    AnalyticsTaskStatus.SKIPPED -> MaterialTheme.colorScheme.onErrorContainer
    AnalyticsTaskStatus.UNFINISHED -> MaterialTheme.colorScheme.onSurfaceVariant
}

private fun AnalyticsTaskStatus.fetchTitle(
    strings: AnalyticsStrings,
) = when (this) {
    AnalyticsTaskStatus.COMPLETED -> strings.statusCompleted
    AnalyticsTaskStatus.SKIPPED -> strings.statusSkipped
    AnalyticsTaskStatus.UNFINISHED -> strings.statusUnfinished
}

private fun Modifier.widthInContent() = this.then(
    Modifier.widthIn(max = MAX_CONTENT_WIDTH),
)

private val MAX_CONTENT_WIDTH = 680.dp
private const val PREVIEW_TASK_COUNT = 5
private const val TASK_DATE_PATTERN = "d MMM"
private const val TASKS_HEADER_KEY = "category-tasks-header"
private const val TASKS_SUMMARY_KEY = "category-tasks-summary"
private const val TASKS_EXPAND_KEY = "category-tasks-expand"
private const val TASKS_SPACING_KEY = "category-tasks-spacing"
