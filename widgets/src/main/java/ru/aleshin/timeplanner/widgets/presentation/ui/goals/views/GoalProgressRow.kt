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
package ru.aleshin.timeplanner.widgets.presentation.ui.goals.views

import androidx.compose.runtime.Composable
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.Action
import androidx.glance.action.clickable
import androidx.glance.appwidget.LinearProgressIndicator
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.Text
import androidx.glance.unit.ColorProvider
import ru.aleshin.core.domain.entities.goals.GoalProgressStatus
import ru.aleshin.timeplanner.widgets.presentation.models.WidgetGoalUi
import ru.aleshin.timeplanner.widgets.presentation.theme.compatCornerBackground
import ru.aleshin.timeplanner.widgets.presentation.theme.surfaceColorAtElevation
import ru.aleshin.timeplanner.widgets.presentation.theme.tokens.WidgetDimensions
import ru.aleshin.timeplanner.widgets.presentation.theme.tokens.WidgetShapes
import ru.aleshin.timeplanner.widgets.presentation.theme.widgetTypography
import ru.aleshin.timeplanner.widgets.presentation.ui.common.WidgetTaskMonogram
import ru.aleshin.timeplanner.widgets.presentation.utils.WidgetSizeClass

/**
 * @author Stanislav Aleshin on 29.07.2026.
 */
@Composable
fun GoalProgressRow(
    modifier: GlanceModifier = GlanceModifier,
    goal: WidgetGoalUi,
    sizeClass: WidgetSizeClass,
    action: Action,
) {
    val isCompactWidth = sizeClass.width == WidgetSizeClass.Width.COMPACT
    val isExpandedWidth = sizeClass.width == WidgetSizeClass.Width.EXPANDED
    val compact = isCompactWidth || sizeClass.height == WidgetSizeClass.Height.COMPACT
    val contentPadding = if (compact) {
        WidgetDimensions.spacingExtraSmall
    } else {
        WidgetDimensions.spacingSmall
    }
    val statusColor = goal.statusColor()
    Column(
        modifier = modifier
            .fillMaxWidth()
            .compatCornerBackground(
                color = GlanceTheme.colors.surfaceColorAtElevation(WidgetDimensions.elevationLow),
                cornerRadius = WidgetShapes.large,
            )
            .clickable(action)
            .padding(contentPadding),
    ) {
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (!isCompactWidth) {
                WidgetTaskMonogram(
                    title = goal.title,
                    categoryType = goal.categoryType,
                    compact = compact,
                )
                Spacer(
                    GlanceModifier.width(
                        if (compact) {
                            WidgetDimensions.spacingExtraSmall
                        } else {
                            WidgetDimensions.spacingSmall
                        },
                    ),
                )
            }
            Column(GlanceModifier.defaultWeight()) {
                Text(
                    text = goal.title,
                    maxLines = 1,
                    style = GlanceTheme.widgetTypography().label.copy(
                        color = GlanceTheme.colors.onSurface,
                    ),
                )
                Row(
                    modifier = GlanceModifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = goal.valueTitle,
                        maxLines = 1,
                        style = GlanceTheme.widgetTypography().caption.copy(
                            color = GlanceTheme.colors.onSurfaceVariant,
                        ),
                    )
                    if (isExpandedWidth) {
                        Spacer(GlanceModifier.defaultWeight())
                        Box(
                            modifier = GlanceModifier.width(WidgetDimensions.goalDeadlineWidth),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = goal.deadlineTitle,
                                maxLines = 1,
                                style = GlanceTheme.widgetTypography().caption.copy(
                                    color = goal.deadlineColor(),
                                ),
                            )
                        }
                    }
                }
            }
            Spacer(
                GlanceModifier.width(
                    if (isExpandedWidth) {
                        WidgetDimensions.spacingMedium
                    } else {
                        WidgetDimensions.spacingSmall
                    },
                ),
            )
            Text(
                text = goal.progressTitle,
                maxLines = 1,
                style = if (compact) {
                    GlanceTheme.widgetTypography().label.copy(color = statusColor)
                } else {
                    GlanceTheme.widgetTypography().title.copy(color = statusColor)
                },
            )
        }
        Spacer(
            GlanceModifier.height(
                if (compact) {
                    WidgetDimensions.spacingExtraSmall
                } else {
                    WidgetDimensions.spacingSmall
                },
            ),
        )
        LinearProgressIndicator(
            progress = goal.progressFraction.coerceIn(0f, 1f),
            modifier = GlanceModifier
                .fillMaxWidth()
                .height(
                    if (compact) {
                        WidgetDimensions.progressHeightCompact
                    } else {
                        WidgetDimensions.progressHeight
                    },
                ),
            color = statusColor,
            backgroundColor = GlanceTheme.colors.surfaceVariant,
        )
    }
}

@Composable
private fun WidgetGoalUi.statusColor(): ColorProvider {
    return when (status) {
        GoalProgressStatus.IN_PROGRESS -> GlanceTheme.colors.primary
        GoalProgressStatus.ACHIEVED -> GlanceTheme.colors.tertiary
        GoalProgressStatus.EXCEEDED,
        GoalProgressStatus.EXPIRED, -> GlanceTheme.colors.error
        GoalProgressStatus.UNAVAILABLE -> GlanceTheme.colors.outline
    }
}

@Composable
private fun WidgetGoalUi.deadlineColor(): ColorProvider {
    return when (status) {
        GoalProgressStatus.EXCEEDED,
        GoalProgressStatus.EXPIRED, -> GlanceTheme.colors.error
        else -> GlanceTheme.colors.onSurfaceVariant
    }
}
