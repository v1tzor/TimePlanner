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
package ru.aleshin.timeplanner.widgets.presentation.ui.deadlines.views

import androidx.compose.runtime.Composable
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.Action
import androidx.glance.action.clickable
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.Text
import androidx.glance.unit.ColorProvider
import ru.aleshin.timeplanner.widgets.domain.entities.deadlines.WidgetDeadlineType
import ru.aleshin.timeplanner.widgets.presentation.models.WidgetUndefinedTaskUi
import ru.aleshin.timeplanner.widgets.presentation.theme.compatCornerBackground
import ru.aleshin.timeplanner.widgets.presentation.theme.surfaceColorAtElevation
import ru.aleshin.timeplanner.widgets.presentation.theme.tokens.WidgetDimensions
import ru.aleshin.timeplanner.widgets.presentation.theme.tokens.WidgetShapes
import ru.aleshin.timeplanner.widgets.presentation.theme.widgetTypography
import ru.aleshin.timeplanner.widgets.presentation.ui.common.WidgetTaskMonogram
import ru.aleshin.timeplanner.widgets.presentation.utils.WidgetSizeClass

/**
 * @author Stanislav Aleshin on 28.07.2026.
 */
@Composable
fun DeadlineTaskRow(
    task: WidgetUndefinedTaskUi,
    sizeClass: WidgetSizeClass,
    action: Action,
) {
    val isCompactWidth = sizeClass.width == WidgetSizeClass.Width.COMPACT
    val isExpandedWidth = sizeClass.width == WidgetSizeClass.Width.EXPANDED
    val compact = isCompactWidth || sizeClass.height == WidgetSizeClass.Height.COMPACT
    val spacing = if (compact) {
        WidgetDimensions.spacingExtraSmall
    } else {
        WidgetDimensions.spacingSmall
    }
    val background = when (task.deadlineType) {
        WidgetDeadlineType.OVERDUE -> GlanceTheme.colors.errorContainer
        WidgetDeadlineType.TODAY -> GlanceTheme.colors.primaryContainer
        WidgetDeadlineType.INBOX -> GlanceTheme.colors.surfaceVariant
        WidgetDeadlineType.UPCOMING -> GlanceTheme.colors.surfaceColorAtElevation(WidgetDimensions.elevationLow)
    }
    val deadlineColor = when (task.deadlineType) {
        WidgetDeadlineType.OVERDUE -> GlanceTheme.colors.error
        WidgetDeadlineType.TODAY -> GlanceTheme.colors.primary
        WidgetDeadlineType.UPCOMING,
        WidgetDeadlineType.INBOX, -> GlanceTheme.colors.onSurfaceVariant
    }
    Row(
        modifier = GlanceModifier
            .fillMaxWidth()
            .compatCornerBackground(background, WidgetShapes.large)
            .clickable(action)
            .padding(if (compact) {
                WidgetDimensions.spacingExtraSmall
            } else {
                WidgetDimensions.spacingSmall
            }),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (!isCompactWidth) {
            WidgetTaskMonogram(
                title = task.title,
                categoryType = task.categoryType,
                compact = compact,
            )
            Spacer(GlanceModifier.width(spacing))
        }
        Column(GlanceModifier.defaultWeight()) {
            Text(
                text = task.title,
                maxLines = 1,
                style = GlanceTheme.widgetTypography().label.copy(
                    color = GlanceTheme.colors.onSurface,
                ),
            )
            task.subtitle?.takeIf { !compact && isExpandedWidth }?.let { subtitle ->
                Text(
                    text = subtitle,
                    maxLines = 1,
                    style = GlanceTheme.widgetTypography().caption.copy(
                        color = GlanceTheme.colors.onSurfaceVariant,
                    ),
                )
            }
            if (!isExpandedWidth) {
                DeadlineLabel(task.deadlineTitle, deadlineColor)
            }
        }
        if (isExpandedWidth) {
            Spacer(GlanceModifier.width(WidgetDimensions.spacingSmall))
            DeadlineLabel(task.deadlineTitle, deadlineColor)
        }
    }
}

@Composable
private fun DeadlineLabel(
    title: String,
    color: ColorProvider,
) {
    Text(
        text = title,
        maxLines = 1,
        style = GlanceTheme.widgetTypography().caption.copy(color = color),
    )
}
