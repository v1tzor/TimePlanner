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
package ru.aleshin.timeplanner.widgets.presentation.ui.today.views

import androidx.compose.runtime.Composable
import androidx.glance.ColorFilter
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.Action
import androidx.glance.action.clickable
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.Text
import androidx.glance.unit.ColorProvider
import ru.aleshin.core.domain.entities.categories.DefaultCategoryType
import ru.aleshin.core.domain.entities.tasks.TaskPriority
import ru.aleshin.core.domain.entities.tasks.TimeTaskStatus
import ru.aleshin.core.presentation.mappers.mapToIcon
import ru.aleshin.timeplanner.core.ui.theme.TimePlannerRes
import ru.aleshin.timeplanner.core.ui.theme.material.badgePriorityMax
import ru.aleshin.timeplanner.core.ui.theme.material.badgePriorityMedium
import ru.aleshin.timeplanner.core.ui.views.toMinutesOrHoursTitle
import ru.aleshin.timeplanner.widgets.R
import ru.aleshin.timeplanner.widgets.presentation.models.WidgetTaskUi
import ru.aleshin.timeplanner.widgets.presentation.theme.compatCornerBackground
import ru.aleshin.timeplanner.widgets.presentation.theme.surfaceColorAtElevation
import ru.aleshin.timeplanner.widgets.presentation.theme.tokens.WidgetDimensions
import ru.aleshin.timeplanner.widgets.presentation.theme.tokens.WidgetShapes
import ru.aleshin.timeplanner.widgets.presentation.theme.widgetString
import ru.aleshin.timeplanner.widgets.presentation.theme.widgetTypography
import ru.aleshin.timeplanner.widgets.presentation.utils.WidgetSizeClass
import java.text.DateFormat
import java.util.Date

/**
 * @author Stanislav Aleshin on 28.07.2026.
 */
@Composable
fun TodayTaskRow(
    task: WidgetTaskUi,
    sizeClass: WidgetSizeClass,
    action: Action,
) {
    val isCompactWidth = sizeClass.width == WidgetSizeClass.Width.COMPACT
    val isCompactHeight = sizeClass.height == WidgetSizeClass.Height.COMPACT
    val rowHeight = if (isCompactHeight || isCompactWidth) {
        WidgetDimensions.taskRowHeightCompact
    } else {
        WidgetDimensions.taskRowHeight
    }
    val timeColumnWidth = if (isCompactWidth) {
        WidgetDimensions.timeColumnWidthCompact
    } else {
        WidgetDimensions.timeColumnWidth
    }
    val itemSpacing = if (isCompactHeight) {
        WidgetDimensions.spacingExtraSmall
    } else {
        WidgetDimensions.spacingSmall
    }
    val timeTextStyle = if (isCompactWidth) {
        GlanceTheme.widgetTypography().caption
    } else {
        GlanceTheme.widgetTypography().label
    }
    val showTrailing = !isCompactWidth && task.status in TRAILING_STATUSES
    val timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT)
    val background = when (task.status) {
        TimeTaskStatus.RUNNING -> GlanceTheme.colors.primaryContainer
        TimeTaskStatus.COMPLETED -> GlanceTheme.colors.tertiaryContainer
        TimeTaskStatus.PLANNED -> GlanceTheme.colors.surfaceColorAtElevation(WidgetDimensions.elevationLow)
    }
    val titleColor = when (task.status) {
        TimeTaskStatus.RUNNING -> GlanceTheme.colors.onPrimaryContainer
        TimeTaskStatus.COMPLETED -> GlanceTheme.colors.onTertiaryContainer
        TimeTaskStatus.PLANNED -> GlanceTheme.colors.onSurface
    }
    val monogramBackground = when (task.status) {
        TimeTaskStatus.RUNNING -> GlanceTheme.colors.primary
        TimeTaskStatus.COMPLETED -> GlanceTheme.colors.tertiary
        TimeTaskStatus.PLANNED -> GlanceTheme.colors.primaryContainer
    }
    val monogramColor = when (task.status) {
        TimeTaskStatus.RUNNING -> GlanceTheme.colors.onPrimary
        TimeTaskStatus.COMPLETED -> GlanceTheme.colors.onTertiary
        TimeTaskStatus.PLANNED -> GlanceTheme.colors.primary
    }
    Row(
        modifier = GlanceModifier
            .fillMaxWidth()
            .height(rowHeight)
            .padding(bottom = itemSpacing),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = GlanceModifier.width(timeColumnWidth),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = timeFormat.format(Date(task.startTime)),
                maxLines = 1,
                style = timeTextStyle.copy(
                    color = GlanceTheme.colors.onSurfaceVariant,
                ),
            )
            Spacer(GlanceModifier.height(WidgetDimensions.spacingExtraSmall))
            Text(
                text = timeFormat.format(Date(task.endTime)),
                maxLines = 1,
                style = timeTextStyle.copy(
                    color = GlanceTheme.colors.onSurfaceVariant,
                ),
            )
        }
        Spacer(GlanceModifier.width(itemSpacing))
        Box(
            modifier = GlanceModifier
                .defaultWeight()
                .fillMaxSize()
                .compatCornerBackground(background, WidgetShapes.large)
                .clickable(action),
        ) {
            Row(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .padding(if (isCompactWidth) {
                        WidgetDimensions.spacingExtraSmall
                    } else {
                        WidgetDimensions.spacingSmall
                    }),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TodayTaskMonogram(
                    title = task.title,
                    categoryType = task.categoryType,
                    priority = task.priority,
                    iconColor = monogramColor,
                    backgroundColor = monogramBackground,
                    compact = isCompactWidth || isCompactHeight,
                )
                Spacer(GlanceModifier.width(itemSpacing))
                TaskTitles(
                    modifier = GlanceModifier.defaultWeight(),
                    task = task,
                    color = titleColor,
                    showSubtitle = !isCompactWidth,
                )
                if (showTrailing) {
                    Spacer(GlanceModifier.width(WidgetDimensions.spacingSmall))
                    TodayTaskTrailing(task)
                }
            }
        }
    }
}

@Composable
private fun TaskTitles(
    modifier: GlanceModifier,
    task: WidgetTaskUi,
    color: ColorProvider,
    showSubtitle: Boolean,
) {
    Column(modifier) {
        Text(
            text = task.title,
            maxLines = 1,
            style = GlanceTheme.widgetTypography().label.copy(color = color),
        )
        task.subtitle?.takeIf { showSubtitle }?.let { subtitle ->
            Text(
                text = subtitle,
                maxLines = 1,
                style = GlanceTheme.widgetTypography().caption.copy(
                    color = GlanceTheme.colors.onSurfaceVariant,
                ),
            )
        }
    }
}

@Composable
private fun TodayTaskMonogram(
    title: String,
    categoryType: DefaultCategoryType?,
    priority: TaskPriority,
    iconColor: ColorProvider,
    backgroundColor: ColorProvider,
    compact: Boolean,
) {
    val monogramSize = if (compact) {
        WidgetDimensions.monogramCompact
    } else {
        WidgetDimensions.monogram
    }
    val iconSize = if (compact) {
        WidgetDimensions.iconExtraSmall
    } else {
        WidgetDimensions.iconSmall
    }
    Box(
        modifier = GlanceModifier.size(monogramSize),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .compatCornerBackground(backgroundColor, WidgetShapes.full),
            contentAlignment = Alignment.Center,
        ) {
            if (categoryType != null) {
                Image(
                    modifier = GlanceModifier.size(iconSize),
                    provider = ImageProvider(categoryType.mapToIcon(TimePlannerRes.icons)),
                    contentDescription = title,
                    colorFilter = ColorFilter.tint(iconColor),
                )
            } else {
                Text(
                    text = taskTitleMonogram(title),
                    style = if (compact) {
                        GlanceTheme.widgetTypography().label.copy(color = iconColor)
                    } else {
                        GlanceTheme.widgetTypography().title.copy(color = iconColor)
                    },
                )
            }
        }
        if (priority != TaskPriority.STANDARD) {
            Box(
                modifier = GlanceModifier.fillMaxSize(),
                contentAlignment = Alignment.TopEnd,
            ) {
                Box(
                    modifier = GlanceModifier
                        .size(WidgetDimensions.priorityIndicator)
                        .compatCornerBackground(
                            color = ColorProvider(
                                if (priority == TaskPriority.MEDIUM) {
                                    badgePriorityMedium
                                } else {
                                    badgePriorityMax
                                },
                            ),
                            cornerRadius = WidgetShapes.full,
                        ),
                ) {}
            }
        }
    }
}

@Composable
private fun TodayTaskTrailing(task: WidgetTaskUi) {
    when (task.status) {
        TimeTaskStatus.COMPLETED -> {
            Image(
                modifier = GlanceModifier.size(WidgetDimensions.icon),
                provider = ImageProvider(
                    if (task.isCompleted) TimePlannerRes.icons.check else TimePlannerRes.icons.cancel,
                ),
                contentDescription = widgetString(
                    if (task.isCompleted) {
                        R.string.widget_task_completed_content_description
                    } else {
                        R.string.widget_task_not_completed_content_description
                    },
                ),
                colorFilter = ColorFilter.tint(
                    if (task.isCompleted) {
                        GlanceTheme.colors.onSurfaceVariant
                    } else {
                        GlanceTheme.colors.onSurface
                    },
                ),
            )
        }
        TimeTaskStatus.PLANNED -> {
            Text(
                text = (task.endTime - task.startTime).coerceAtLeast(0L).toMinutesOrHoursTitle(),
                maxLines = 1,
                style = GlanceTheme.widgetTypography().label.copy(
                    color = GlanceTheme.colors.onSurfaceVariant,
                ),
            )
        }
        TimeTaskStatus.RUNNING -> Unit
    }
}

private fun taskTitleMonogram(title: String): String {
    return title.firstOrNull()?.uppercase() ?: "•"
}

private val TRAILING_STATUSES = setOf(TimeTaskStatus.COMPLETED, TimeTaskStatus.PLANNED)
