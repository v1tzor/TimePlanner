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
package ru.aleshin.timeplanner.widgets.presentation.ui.goals

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
import ru.aleshin.timeplanner.core.ui.theme.TimePlannerRes
import ru.aleshin.timeplanner.widgets.R
import ru.aleshin.timeplanner.widgets.presentation.actions.RefreshWidgetsAction
import ru.aleshin.timeplanner.widgets.presentation.navigation.WidgetDeepLinkFactory
import ru.aleshin.timeplanner.widgets.presentation.theme.tokens.WidgetDimensions
import ru.aleshin.timeplanner.widgets.presentation.theme.widgetString
import ru.aleshin.timeplanner.widgets.presentation.ui.common.WidgetEmptyState
import ru.aleshin.timeplanner.widgets.presentation.ui.common.WidgetHeader
import ru.aleshin.timeplanner.widgets.presentation.ui.common.WidgetScaffold
import ru.aleshin.timeplanner.widgets.presentation.ui.goals.state.GoalsWidgetStateUi
import ru.aleshin.timeplanner.widgets.presentation.ui.goals.views.GoalProgressRow
import ru.aleshin.timeplanner.widgets.presentation.utils.WidgetSizeClass

/**
 * @author Stanislav Aleshin on 29.07.2026.
 */
@Composable
fun GoalsWidgetContent(
    state: GoalsWidgetStateUi,
) {
    val context = LocalContext.current
    val sizeClass = WidgetSizeClass.fetch(LocalSize.current)
    WidgetScaffold(
        header = {
            WidgetHeader(
                title = widgetString(R.string.widget_goals_title),
                titleAction = actionStartActivity(WidgetDeepLinkFactory.createOverviewIntent(context)),
                actionIcon = ImageProvider(TimePlannerRes.icons.add),
                actionDescription = widgetString(R.string.widget_add_goal_content_description),
                action = actionStartActivity(WidgetDeepLinkFactory.createGoalCreatorIntent(context)),
                secondaryActionIcon = ImageProvider(TimePlannerRes.icons.reset),
                secondaryActionDescription = widgetString(R.string.widget_refresh_content_description),
                secondaryAction = actionRunCallback<RefreshWidgetsAction>(),
            )
        },
    ) {
        if (state.goals.isEmpty()) {
            WidgetEmptyState(title = widgetString(R.string.widget_no_goals))
        } else {
            GoalsList(
                state = state,
                sizeClass = sizeClass,
            )
        }
    }
}

@Composable
private fun GoalsList(
    state: GoalsWidgetStateUi,
    sizeClass: WidgetSizeClass,
) {
    val context = LocalContext.current
    val compact = sizeClass.height == WidgetSizeClass.Height.COMPACT
    val itemSpacing = if (compact) {
        WidgetDimensions.spacingExtraSmall
    } else {
        WidgetDimensions.spacingSmall
    }
    LazyColumn(GlanceModifier.fillMaxSize()) {
        itemsIndexed(
            items = state.goals,
            itemId = { _, goal -> goal.id },
        ) { index, goal ->
            val bottomPadding = if (index != state.goals.lastIndex) itemSpacing else 0.dp
            Box(modifier = GlanceModifier.padding(bottom = bottomPadding)) {
                GoalProgressRow(
                    goal = goal,
                    sizeClass = sizeClass,
                    action = actionStartActivity(
                        WidgetDeepLinkFactory.createGoalDetailsIntent(context, goal.id),
                    ),
                )
            }
        }
    }
}
