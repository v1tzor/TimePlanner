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
package ru.aleshin.timeplanner.widgets.presentation.ui.today

import androidx.compose.runtime.Composable
import androidx.glance.ColorFilter
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.ContentScale
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.text.Text
import ru.aleshin.core.utils.extensions.startThisDay
import ru.aleshin.timeplanner.core.ui.theme.TimePlannerRes
import ru.aleshin.timeplanner.widgets.R
import ru.aleshin.timeplanner.widgets.presentation.actions.RefreshWidgetsAction
import ru.aleshin.timeplanner.widgets.presentation.navigation.WidgetDeepLinkFactory
import ru.aleshin.timeplanner.widgets.presentation.theme.tokens.WidgetDimensions
import ru.aleshin.timeplanner.widgets.presentation.theme.widgetString
import ru.aleshin.timeplanner.widgets.presentation.theme.widgetTypography
import ru.aleshin.timeplanner.widgets.presentation.ui.common.WidgetHeader
import ru.aleshin.timeplanner.widgets.presentation.ui.common.WidgetScaffold
import ru.aleshin.timeplanner.widgets.presentation.ui.today.state.TodayWidgetStateUi
import ru.aleshin.timeplanner.widgets.presentation.ui.today.views.TodayTaskRow
import ru.aleshin.timeplanner.widgets.presentation.utils.WidgetSizeClass
import java.util.Date

/**
 * @author Stanislav Aleshin on 28.07.2026.
 */
@Composable
fun TodayWidgetContent(
    state: TodayWidgetStateUi,
) {
    val context = LocalContext.current
    val currentTime = Date()
    val currentDay = currentTime.startThisDay().time
    val sizeClass = WidgetSizeClass.fetch(LocalSize.current)

    WidgetScaffold(
        header = {
            WidgetHeader(
                title = null,
                titleAction = null,
                actionIcon = ImageProvider(TimePlannerRes.icons.add),
                actionDescription = widgetString(R.string.widget_add_task_content_description),
                action = actionStartActivity(
                    intent = WidgetDeepLinkFactory.createTaskCreatorIntent(
                        context = context,
                        date = currentDay,
                        from = currentTime.time,
                        to = currentTime.time,
                    ),
                ),
                secondaryActionIcon = ImageProvider(TimePlannerRes.icons.reset),
                secondaryActionDescription = widgetString(R.string.widget_refresh_content_description),
                secondaryAction = actionRunCallback<RefreshWidgetsAction>(),
            )
        },
    ) {
        if (state.tasks.isEmpty()) {
            TodayEmptyState()
        } else {
            LazyColumn(GlanceModifier.fillMaxSize()) {
                items(state.tasks, itemId = { it.id }) { task ->
                    TodayTaskRow(
                        task = task,
                        sizeClass = sizeClass,
                        action = actionStartActivity(
                            intent = WidgetDeepLinkFactory.createEditTaskIntent(context, task.id),
                        ),
                    )
                }
            }
        }
    }
}

@Composable
private fun TodayEmptyState() {
    val sizeClass = WidgetSizeClass.fetch(LocalSize.current)
    val bottomPadding = if (sizeClass.height == WidgetSizeClass.Height.COMPACT) {
        WidgetDimensions.spacingSmall
    } else {
        WidgetDimensions.spacingMedium
    }
    Column(GlanceModifier.fillMaxSize()) {
        Box(
            modifier = GlanceModifier
                .fillMaxWidth()
                .defaultWeight()
                .background(
                    imageProvider = ImageProvider(R.drawable.stoke_rouned_background),
                    contentScale = ContentScale.FillBounds,
                    colorFilter = ColorFilter.tint(GlanceTheme.colors.secondary),
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = widgetString(R.string.widget_no_schedule),
                maxLines = if (sizeClass.width == WidgetSizeClass.Width.COMPACT) 2 else 1,
                style = if (sizeClass.width == WidgetSizeClass.Width.COMPACT) {
                    GlanceTheme.widgetTypography().label.copy(
                        color = GlanceTheme.colors.secondary,
                    )
                } else {
                    GlanceTheme.widgetTypography().title.copy(
                        color = GlanceTheme.colors.secondary,
                    )
                },
            )
        }
        Spacer(GlanceModifier.height(bottomPadding))
    }
}
