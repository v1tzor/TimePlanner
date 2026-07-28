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
package ru.aleshin.timeplanner.widgets.presentation.ui.summary

import androidx.compose.runtime.Composable
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.Text
import ru.aleshin.timeplanner.core.ui.theme.TimePlannerRes
import ru.aleshin.timeplanner.widgets.R
import ru.aleshin.timeplanner.widgets.presentation.actions.RefreshWidgetsAction
import ru.aleshin.timeplanner.widgets.presentation.navigation.WidgetDeepLinkFactory
import ru.aleshin.timeplanner.widgets.presentation.theme.tokens.WidgetDimensions
import ru.aleshin.timeplanner.widgets.presentation.theme.widgetString
import ru.aleshin.timeplanner.widgets.presentation.theme.widgetTypography
import ru.aleshin.timeplanner.widgets.presentation.ui.common.WidgetHeader
import ru.aleshin.timeplanner.widgets.presentation.ui.common.WidgetProgressBar
import ru.aleshin.timeplanner.widgets.presentation.ui.common.WidgetScaffold
import ru.aleshin.timeplanner.widgets.presentation.ui.summary.state.DailySummaryWidgetStateUi
import ru.aleshin.timeplanner.widgets.presentation.ui.summary.views.SummaryMetric
import ru.aleshin.timeplanner.widgets.presentation.utils.WidgetSizeClass

/**
 * @author Stanislav Aleshin on 28.07.2026.
 */
@Composable
fun DailySummaryWidgetContent(
    state: DailySummaryWidgetStateUi,
) {
    val context = LocalContext.current
    val sizeClass = WidgetSizeClass.fetch(LocalSize.current)
    val compactWidth = sizeClass.width == WidgetSizeClass.Width.COMPACT
    val compactHeight = sizeClass.height == WidgetSizeClass.Height.COMPACT
    val compact = compactWidth || compactHeight
    val spacing = if (compact) {
        WidgetDimensions.spacingExtraSmall
    } else {
        WidgetDimensions.spacingSmall
    }
    WidgetScaffold(
        header = {
            WidgetHeader(
                title = widgetString(R.string.widget_summary_title),
                titleAction = actionStartActivity(WidgetDeepLinkFactory.createAnalyticsIntent(context)),
                actionIcon = ImageProvider(TimePlannerRes.icons.reset),
                actionDescription = widgetString(R.string.widget_refresh_content_description),
                action = actionRunCallback<RefreshWidgetsAction>(),
            )
        },
    ) {
        Column(
            modifier = GlanceModifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "${(state.completion * 100).toInt()}%",
                    style = if (compact) {
                        GlanceTheme.widgetTypography().title.copy(
                            color = GlanceTheme.colors.primary,
                        )
                    } else {
                        GlanceTheme.widgetTypography().headline.copy(
                            color = GlanceTheme.colors.primary,
                        )
                    },
                )
                Spacer(GlanceModifier.defaultWeight())
                if (!compactWidth) {
                    Text(
                        text = widgetString(
                            R.string.widget_hours_short,
                            state.plannedDuration / MILLIS_IN_HOUR.toFloat(),
                        ),
                        style = GlanceTheme.widgetTypography().label.copy(
                            color = GlanceTheme.colors.onSurfaceVariant,
                        ),
                    )
                }
            }
            Spacer(GlanceModifier.height(spacing))
            WidgetProgressBar(
                progress = state.completion,
                segments = if (compactWidth) COMPACT_SEGMENTS else DEFAULT_SEGMENTS,
                compact = compact,
            )
            if (compactHeight || compactWidth) {
                Spacer(GlanceModifier.height(WidgetDimensions.spacingSmall))
            } else {
                Spacer(GlanceModifier.defaultWeight())
            }
            if (compactWidth && !compactHeight) {
                Column(
                    modifier = GlanceModifier
                        .fillMaxWidth()
                        .padding(bottom = spacing),
                ) {
                    SummaryMetric(
                        modifier = GlanceModifier.fillMaxWidth(),
                        value = state.completedCount,
                        title = widgetString(R.string.widget_completed),
                        color = GlanceTheme.colors.primary,
                        compact = true,
                        showTitle = true,
                        inline = true,
                    )
                    SummaryMetric(
                        modifier = GlanceModifier.fillMaxWidth(),
                        value = state.skippedCount,
                        title = widgetString(R.string.widget_skipped),
                        color = GlanceTheme.colors.error,
                        compact = true,
                        showTitle = true,
                        inline = true,
                    )
                    SummaryMetric(
                        modifier = GlanceModifier.fillMaxWidth(),
                        value = state.remainingCount,
                        title = widgetString(R.string.widget_remaining),
                        color = GlanceTheme.colors.tertiary,
                        compact = true,
                        showTitle = true,
                        inline = true,
                    )
                }
            } else {
                Row(
                    modifier = GlanceModifier
                        .fillMaxWidth()
                        .padding(bottom = spacing),
                ) {
                    SummaryMetric(
                        modifier = GlanceModifier.defaultWeight(),
                        value = state.completedCount,
                        title = widgetString(R.string.widget_completed),
                        color = GlanceTheme.colors.primary,
                        compact = compact,
                        showTitle = !compactWidth,
                    )
                    SummaryMetric(
                        modifier = GlanceModifier.defaultWeight(),
                        value = state.skippedCount,
                        title = widgetString(R.string.widget_skipped),
                        color = GlanceTheme.colors.error,
                        compact = compact,
                        showTitle = !compactWidth,
                    )
                    SummaryMetric(
                        modifier = GlanceModifier.defaultWeight(),
                        value = state.remainingCount,
                        title = widgetString(R.string.widget_remaining),
                        color = GlanceTheme.colors.tertiary,
                        compact = compact,
                        showTitle = !compactWidth,
                    )
                }
            }
        }
    }
}

private const val MILLIS_IN_HOUR = 3_600_000L
private const val COMPACT_SEGMENTS = 4
private const val DEFAULT_SEGMENTS = 8
