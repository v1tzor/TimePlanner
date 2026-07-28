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
package ru.aleshin.timeplanner.widgets.presentation.ui.week

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.text.Text
import ru.aleshin.timeplanner.core.ui.theme.TimePlannerRes
import ru.aleshin.timeplanner.widgets.R
import ru.aleshin.timeplanner.widgets.presentation.actions.RefreshWidgetsAction
import ru.aleshin.timeplanner.widgets.presentation.navigation.WidgetDeepLinkFactory
import ru.aleshin.timeplanner.widgets.presentation.theme.tokens.WidgetDimensions
import ru.aleshin.timeplanner.widgets.presentation.theme.widgetQuantityString
import ru.aleshin.timeplanner.widgets.presentation.theme.widgetString
import ru.aleshin.timeplanner.widgets.presentation.theme.widgetTypography
import ru.aleshin.timeplanner.widgets.presentation.ui.common.WidgetEmptyState
import ru.aleshin.timeplanner.widgets.presentation.ui.common.WidgetHeader
import ru.aleshin.timeplanner.widgets.presentation.ui.common.WidgetScaffold
import ru.aleshin.timeplanner.widgets.presentation.ui.week.state.WeekOverviewWidgetStateUi
import ru.aleshin.timeplanner.widgets.presentation.ui.week.views.WeekDayColumn
import ru.aleshin.timeplanner.widgets.presentation.utils.WidgetSizeClass

/**
 * @author Stanislav Aleshin on 28.07.2026.
 */
@Composable
fun WeekOverviewWidgetContent(
    state: WeekOverviewWidgetStateUi,
) {
    val context = LocalContext.current
    val sizeClass = WidgetSizeClass.fetch(LocalSize.current)
    val contentSpacing = if (sizeClass.height == WidgetSizeClass.Height.COMPACT) {
        WidgetDimensions.spacingExtraSmall
    } else {
        WidgetDimensions.spacingSmall
    }
    WidgetScaffold(
        header = {
            WidgetHeader(
                title = widgetString(R.string.widget_week_title),
                titleAction = actionStartActivity(WidgetDeepLinkFactory.createOverviewIntent(context)),
                actionIcon = ImageProvider(TimePlannerRes.icons.reset),
                actionDescription = widgetString(R.string.widget_refresh_content_description),
                action = actionRunCallback<RefreshWidgetsAction>(),
            )
        },
    ) {
        if (state.days.isEmpty()) {
            WidgetEmptyState(title = widgetString(R.string.widget_no_data))
        } else {
            Column(GlanceModifier.fillMaxSize()) {
                Row(
                    modifier = GlanceModifier
                        .fillMaxWidth()
                        .padding(bottom = contentSpacing),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = widgetQuantityString(
                            R.plurals.widget_tasks_count,
                            state.tasksCount,
                            state.tasksCount,
                        ),
                        style = GlanceTheme.widgetTypography().label.copy(
                            color = GlanceTheme.colors.onSurface,
                        ),
                    )
                    Spacer(GlanceModifier.defaultWeight())
                    Text(
                        text = widgetString(
                            R.string.widget_hours_short,
                            state.totalWorkload / MILLIS_IN_HOUR.toFloat(),
                        ),
                        style = GlanceTheme.widgetTypography().caption.copy(
                            color = GlanceTheme.colors.onSurfaceVariant,
                        ),
                    )
                }
                Row(
                    modifier = GlanceModifier
                        .fillMaxWidth()
                        .defaultWeight()
                ) {
                    state.days.forEachIndexed { index, day ->
                        val endPadding = if (index != state.days.lastIndex) WidgetDimensions.spacingTiny else 0.dp

                        Box(modifier = GlanceModifier.defaultWeight().padding(end = endPadding)) {
                            WeekDayColumn(
                                day = day,
                                sizeClass = sizeClass,
                                action = actionStartActivity(
                                    intent = WidgetDeepLinkFactory.createHomeIntent(context, day.date),
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

private const val MILLIS_IN_HOUR = 3_600_000L
