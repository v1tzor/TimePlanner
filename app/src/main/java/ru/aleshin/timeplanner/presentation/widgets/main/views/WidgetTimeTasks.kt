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
package ru.aleshin.timeplanner.presentation.widgets.main.views

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
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
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.Text
import androidx.glance.unit.ColorProvider
import ru.aleshin.core.domain.entities.schedules.TaskPriority
import ru.aleshin.core.ui.mappers.mapToUi
import ru.aleshin.core.ui.theme.TimePlannerRes
import ru.aleshin.timeplanner.presentation.widgets.compatCornerBackground
import ru.aleshin.timeplanner.presentation.widgets.surfaceColorAtElevation
import ru.aleshin.timeplanner.presentation.widgets.typography

/**
 * @author Stanislav Aleshin on 28.04.2024.
 */
@Composable
internal fun CompletedWidgetTimeTask(
    modifier: GlanceModifier = GlanceModifier,
    onViewClickedAction: () -> Action,
    taskTitle: String,
    taskSubTitle: String?,
    categoryIcon: ImageProvider?,
    isCompleted: Boolean,
) {
    Box(
        modifier = modifier
            .compatCornerBackground(GlanceTheme.colors.tertiaryContainer, 16)
            .clickable(onViewClickedAction()),
    ) {
        Row(
            modifier = GlanceModifier.padding(8.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (categoryIcon != null) {
                CategoryWidgetIconMonogram(
                    icon = categoryIcon,
                    iconDescription = taskTitle,
                    iconColor = GlanceTheme.colors.onTertiary,
                    backgroundColor = GlanceTheme.colors.tertiary,
                )
            } else {
                CategoryWidgetTextMonogram(
                    text = taskTitle.first().toString(),
                    textColor = GlanceTheme.colors.onTertiary,
                    backgroundColor = GlanceTheme.colors.tertiary,
                )
            }
            Spacer(modifier = GlanceModifier.width(8.dp))
            TimeTaskTitles(
                modifier = GlanceModifier.defaultWeight(),
                title = taskTitle,
                titleColor = GlanceTheme.colors.onTertiaryContainer,
                subTitle = taskSubTitle,
            )
            Spacer(modifier = GlanceModifier.width(8.dp))
            if (isCompleted) {
                Image(
                    modifier = GlanceModifier.size(24.dp),
                    provider = ImageProvider(TimePlannerRes.icons.check),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(GlanceTheme.colors.onSurfaceVariant),
                )
            } else {
                Image(
                    modifier = GlanceModifier.size(24.dp),
                    provider = ImageProvider(TimePlannerRes.icons.cancel),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(GlanceTheme.colors.onSurface),
                )
            }
        }
    }
}


@Composable
internal fun PlannedWidgetTimeTask(
    modifier: GlanceModifier = GlanceModifier,
    onViewClickedAction: () -> Action,
    taskTitle: String,
    taskSubTitle: String?,
    taskDurationTitle: String,
    categoryIcon: ImageProvider?,
    priority: TaskPriority,
) {
    Box(
        modifier = modifier
            .compatCornerBackground(
                color = GlanceTheme.colors.surfaceColorAtElevation(elevation = 2.dp),
                cornerRadius = 16
            )
            .clickable(onViewClickedAction()),
    ) {
        Row(
            modifier = GlanceModifier.padding(8.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (categoryIcon != null) {
                CategoryWidgetIconMonogram(
                    icon = categoryIcon,
                    iconDescription = taskTitle,
                    iconColor = GlanceTheme.colors.primary,
                    priority = priority.mapToUi(),
                    backgroundColor = GlanceTheme.colors.primaryContainer,
                )
            } else {
                CategoryWidgetTextMonogram(
                    text = taskTitle.first().toString(),
                    textColor = GlanceTheme.colors.primary,
                    priority = priority.mapToUi(),
                    backgroundColor = GlanceTheme.colors.primaryContainer,
                )
            }
            Spacer(modifier = GlanceModifier.width(8.dp))
            TimeTaskTitles(
                modifier = GlanceModifier.defaultWeight(),
                title = taskTitle,
                titleColor = GlanceTheme.colors.onSurface,
                subTitle = taskSubTitle,
            )
            Spacer(modifier = GlanceModifier.width(8.dp))
            Text(
                text = taskDurationTitle,
                maxLines = 1,
                style = GlanceTheme.typography().titleSmall.copy(
                    color = GlanceTheme.colors.onSurfaceVariant,
                ),
            )
        }
    }
}

@Composable
internal fun RunningWidgetTimeTask(
    modifier: GlanceModifier = GlanceModifier,
    onViewClickedAction: () -> Action,
    taskTitle: String,
    taskSubTitle: String?,
    categoryIcon: ImageProvider?,
    priority: TaskPriority,
) {
    Box(
        modifier = modifier
            .compatCornerBackground(GlanceTheme.colors.primaryContainer, 16)
            .clickable(onViewClickedAction()),
    ) {
        Row(
            modifier = GlanceModifier.padding(8.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (categoryIcon != null) {
                CategoryWidgetIconMonogram(
                    icon = categoryIcon,
                    iconDescription = taskTitle,
                    iconColor = GlanceTheme.colors.onPrimary,
                    priority = priority.mapToUi(),
                    backgroundColor = GlanceTheme.colors.primary,
                )
            } else {
                CategoryWidgetTextMonogram(
                    text = taskTitle.first().toString(),
                    textColor = GlanceTheme.colors.onPrimary,
                    priority = priority.mapToUi(),
                    backgroundColor = GlanceTheme.colors.primary,
                )
            }
            Spacer(modifier = GlanceModifier.width(8.dp))
            TimeTaskTitles(
                modifier = GlanceModifier.defaultWeight(),
                title = taskTitle,
                titleColor = GlanceTheme.colors.onPrimaryContainer,
                subTitle = taskSubTitle,
            )
        }
    }
}

@Composable
internal fun TimeTaskTitles(
    modifier: GlanceModifier = GlanceModifier,
    title: String,
    titleColor: ColorProvider,
    subTitle: String?,
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            style = GlanceTheme.typography().titleSmall.copy(
                color = titleColor,
            ),
            maxLines = 1,
        )
        if (subTitle != null) {
            Text(
                text = subTitle,
                style = GlanceTheme.typography().titleSmall.copy(
                    color = GlanceTheme.colors.onSurfaceVariant,
                ),
                maxLines = 1,
            )
        }
    }
}