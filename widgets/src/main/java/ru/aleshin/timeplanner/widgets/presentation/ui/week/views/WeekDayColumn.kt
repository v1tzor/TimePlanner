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
package ru.aleshin.timeplanner.widgets.presentation.ui.week.views

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.Action
import androidx.glance.action.clickable
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.Text
import ru.aleshin.timeplanner.widgets.presentation.models.WidgetWeekDayUi
import ru.aleshin.timeplanner.widgets.presentation.theme.compatCornerBackground
import ru.aleshin.timeplanner.widgets.presentation.theme.tokens.WidgetDimensions
import ru.aleshin.timeplanner.widgets.presentation.theme.tokens.WidgetShapes
import ru.aleshin.timeplanner.widgets.presentation.theme.widgetTypography
import ru.aleshin.timeplanner.widgets.presentation.utils.WidgetSizeClass

/**
 * @author Stanislav Aleshin on 28.07.2026.
 */
@Composable
fun WeekDayColumn(
    modifier: GlanceModifier = GlanceModifier,
    day: WidgetWeekDayUi,
    sizeClass: WidgetSizeClass,
    action: Action,
) {
    val isCompactWidth = sizeClass.width != WidgetSizeClass.Width.EXPANDED
    val isCompactHeight = sizeClass.height == WidgetSizeClass.Height.COMPACT
    val showTasksCount = sizeClass.height == WidgetSizeClass.Height.EXPANDED
    val horizontalPadding = WidgetDimensions.spacingExtraSmall
    val verticalPadding = if (isCompactHeight) {
        WidgetDimensions.spacingExtraSmall
    } else {
        WidgetDimensions.spacingSmall
    }
    val background = if (day.isToday) {
        GlanceTheme.colors.primaryContainer
    } else {
        GlanceTheme.colors.surfaceVariant
    }
    Column(
        modifier = modifier
            .fillMaxHeight()
            .compatCornerBackground(background, WidgetShapes.medium)
            .clickable(action)
            .padding(vertical = verticalPadding, horizontal = horizontalPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = day.dayTitle,
            maxLines = 1,
            style = if (isCompactHeight || isCompactWidth) {
                GlanceTheme.widgetTypography().micro.copy(
                    color = GlanceTheme.colors.onSurfaceVariant,
                )
            } else {
                GlanceTheme.widgetTypography().caption.copy(
                    color = GlanceTheme.colors.onSurfaceVariant,
                )
            },
        )
        Text(
            text = day.dayNumber,
            style = if (isCompactHeight) {
                GlanceTheme.widgetTypography().label.copy(
                    color = day.titleColor(),
                )
            } else {
                GlanceTheme.widgetTypography().title.copy(
                    color = day.titleColor(),
                )
            },
        )
        if (sizeClass.height == WidgetSizeClass.Height.EXPANDED) {
            Spacer(GlanceModifier.defaultWeight())
        } else {
            Spacer(
                modifier = GlanceModifier.height(
                    if (isCompactHeight) {
                        WidgetDimensions.spacingExtraSmall
                    } else {
                        WidgetDimensions.spacingSmall
                    },
                ),
            )
        }
        DayLoadBar(
            progress = day.workloadProgress,
            sizeClass = sizeClass,
        )
        if (showTasksCount) {
            Spacer(GlanceModifier.height(WidgetDimensions.spacingExtraSmall))
            Text(
                text = day.tasksCount.toString(),
                style = GlanceTheme.widgetTypography().caption.copy(
                    color = GlanceTheme.colors.onSurfaceVariant,
                ),
            )
        }
    }
}

@Composable
private fun DayLoadBar(
    progress: Float,
    sizeClass: WidgetSizeClass,
) {
    val barHeight = when (sizeClass.height) {
        WidgetSizeClass.Height.COMPACT -> WidgetDimensions.dayLoadHeightCompact
        WidgetSizeClass.Height.MEDIUM -> WidgetDimensions.dayLoadHeight
        WidgetSizeClass.Height.EXPANDED -> WidgetDimensions.dayLoadHeightExpanded
    }
    val minimumActiveHeight = if (sizeClass.height == WidgetSizeClass.Height.COMPACT) {
        WidgetDimensions.progressHeightCompact
    } else {
        WidgetDimensions.progressHeight
    }
    val activeHeight = (
        minimumActiveHeight.value +
            (barHeight.value - minimumActiveHeight.value) * progress.coerceIn(0f, 1f)
        ).dp
    Box(
        modifier = GlanceModifier.height(barHeight).fillMaxWidth(),
        contentAlignment = Alignment.BottomCenter,
    ) {
        Box(
            modifier = GlanceModifier
                .height(activeHeight)
                .fillMaxWidth()
                .compatCornerBackground(GlanceTheme.colors.primary, WidgetShapes.full),
            content = {},
        )
    }
}

@Composable
private fun WidgetWeekDayUi.titleColor() = if (isToday) {
    GlanceTheme.colors.primary
} else {
    GlanceTheme.colors.onSurface
}
