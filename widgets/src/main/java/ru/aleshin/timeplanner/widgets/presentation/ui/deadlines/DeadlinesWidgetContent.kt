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
package ru.aleshin.timeplanner.widgets.presentation.ui.deadlines

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceModifier
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.itemsIndexed
import androidx.glance.layout.Box
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import ru.aleshin.core.utils.extensions.startThisDay
import ru.aleshin.timeplanner.core.ui.theme.TimePlannerRes
import ru.aleshin.timeplanner.widgets.R
import ru.aleshin.timeplanner.widgets.presentation.actions.RefreshWidgetsAction
import ru.aleshin.timeplanner.widgets.presentation.navigation.WidgetDeepLinkFactory
import ru.aleshin.timeplanner.widgets.presentation.theme.tokens.WidgetDimensions
import ru.aleshin.timeplanner.widgets.presentation.theme.widgetString
import ru.aleshin.timeplanner.widgets.presentation.ui.common.WidgetEmptyState
import ru.aleshin.timeplanner.widgets.presentation.ui.common.WidgetHeader
import ru.aleshin.timeplanner.widgets.presentation.ui.common.WidgetScaffold
import ru.aleshin.timeplanner.widgets.presentation.ui.deadlines.state.DeadlinesWidgetStateUi
import ru.aleshin.timeplanner.widgets.presentation.ui.deadlines.views.DeadlineCounters
import ru.aleshin.timeplanner.widgets.presentation.ui.deadlines.views.DeadlineTaskRow
import ru.aleshin.timeplanner.widgets.presentation.utils.WidgetSizeClass
import java.util.Date

/**
 * @author Stanislav Aleshin on 28.07.2026.
 */
@Composable
fun DeadlinesWidgetContent(
    state: DeadlinesWidgetStateUi,
) {
    val context = LocalContext.current
    val currentTime = Date()
    val sizeClass = WidgetSizeClass.fetch(LocalSize.current)
    WidgetScaffold(
        header = {
            WidgetHeader(
                title = widgetString(R.string.widget_deadlines_title),
                titleAction = actionStartActivity(WidgetDeepLinkFactory.createOverviewIntent(context)),
                actionIcon = ImageProvider(TimePlannerRes.icons.add),
                actionDescription = widgetString(R.string.widget_add_task_content_description),
                action = actionStartActivity(WidgetDeepLinkFactory.createOverviewIntent(context)),
                secondaryActionIcon = ImageProvider(TimePlannerRes.icons.reset),
                secondaryActionDescription = widgetString(R.string.widget_refresh_content_description),
                secondaryAction = actionRunCallback<RefreshWidgetsAction>(),
            )
        },
    ) {
        if (state.tasks.isEmpty()) {
            WidgetEmptyState(title = widgetString(R.string.widget_no_deadlines))
        } else {
            LazyColumn(GlanceModifier.fillMaxSize()) {
                item(itemId = COUNTERS_ITEM_ID) {
                    DeadlineCounters(
                        state = state,
                        sizeClass = sizeClass,
                    )
                }
                itemsIndexed(items = state.tasks, itemId = { _, task -> task.id }) { index, task ->
                    val bottomPadding = if (index != state.tasks.lastIndex) WidgetDimensions.spacingTiny else 0.dp

                    Box(modifier = GlanceModifier.padding(bottom = bottomPadding)) {
                        DeadlineTaskRow(
                            task = task,
                            sizeClass = sizeClass,
                            action = actionStartActivity(
                                intent = WidgetDeepLinkFactory.createUndefinedTaskIntent(
                                    context = context,
                                    undefinedTaskId = task.id,
                                    date = currentTime.startThisDay().time,
                                    from = currentTime.time,
                                    to = currentTime.time,
                                ),
                            ),
                        )
                    }
                }
            }
        }
    }
}

private const val COUNTERS_ITEM_ID = -1L
