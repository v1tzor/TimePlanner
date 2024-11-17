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
package ru.aleshin.timeplanner.presentation.widgets.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import androidx.glance.ColorFilter
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.Action
import androidx.glance.action.clickable
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
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
import ru.aleshin.core.domain.entities.categories.MainCategory
import ru.aleshin.core.domain.entities.categories.SubCategory
import ru.aleshin.core.domain.entities.schedules.TaskPriority
import ru.aleshin.core.domain.entities.schedules.TimeTask
import ru.aleshin.core.ui.mappers.mapToIcon
import ru.aleshin.core.ui.mappers.mapToName
import ru.aleshin.core.ui.theme.TimePlannerRes
import ru.aleshin.core.ui.views.toMinutesOrHoursTitle
import ru.aleshin.core.utils.extensions.duration
import ru.aleshin.core.utils.extensions.setZeroSecond
import ru.aleshin.core.utils.functional.TimeRange
import ru.aleshin.timeplanner.presentation.widgets.common.CompatScaffold
import ru.aleshin.timeplanner.presentation.widgets.compatCornerBackground
import ru.aleshin.timeplanner.presentation.widgets.main.views.CompletedWidgetTimeTask
import ru.aleshin.timeplanner.presentation.widgets.main.views.EmptyWidgetTimeTask
import ru.aleshin.timeplanner.presentation.widgets.main.views.PlannedWidgetTimeTask
import ru.aleshin.timeplanner.presentation.widgets.main.views.RunningWidgetTimeTask
import ru.aleshin.timeplanner.presentation.widgets.typography
import java.text.SimpleDateFormat
import java.util.Date

/**
 * @author Stanislav Aleshin on 28.04.2024.
 */
@Composable
fun MainWidgetContent(
    modifier: GlanceModifier = GlanceModifier,
    currentTime: Date,
    timeTasks: List<TimeTask>,
    onTimeTaskClickAction: (TimeTask) -> Action,
    onUpdateClickAction: () -> Action,
    onAddAction: () -> Action,
) {
    CompatScaffold(
        modifier = modifier.fillMaxSize(),
        titleBar = { MainWidgetTitleBar(onUpdateAction = onUpdateClickAction, onAddAction = onAddAction) },
        backgroundColor = GlanceTheme.colors.background,
    ) {
        val sortedTimeTasks = remember(timeTasks) { timeTasks.sortedBy { it.timeRange.from.time } }
        if (sortedTimeTasks.isNotEmpty()) {
            LazyColumn {
                items(sortedTimeTasks, itemId = { it.key }) { task ->
                    Row(modifier = GlanceModifier.fillMaxWidth().padding(bottom = 8.dp)) {
                        WidgetTimeRange(
                            timeRange = task.timeRange,
                            isTwoLines = task.subCategory != null,
                        )
                        Spacer(modifier = GlanceModifier.width(8.dp))
                        WidgetTimeTask(
                            modifier = GlanceModifier.defaultWeight(),
                            currentTime = currentTime,
                            onTimeTaskClickAction = { onTimeTaskClickAction(task) },
                            timeRange = task.timeRange.copy(
                                from = task.timeRange.from.setZeroSecond(),
                                to = task.timeRange.to.setZeroSecond(),
                            ),
                            category = task.category,
                            subCategory = task.subCategory,
                            priority = task.priority,
                            isCompleted = task.isCompleted,
                        )
                    }
                }
            }
        } else {
            Column(modifier.fillMaxSize()) {
                EmptyWidgetTimeTask(modifier = GlanceModifier.defaultWeight())
                Spacer(modifier = GlanceModifier.height(12.dp))
            }
        }
    }
}

@Composable
fun MainWidgetTitleBar(
    modifier: GlanceModifier = GlanceModifier,
    onUpdateAction: () -> Action,
    onAddAction: () -> Action,
) {
    Row(
        modifier = modifier.fillMaxWidth().height(48.dp).padding(start = 4.dp, end = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = modifier.size(48.dp),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                provider = ImageProvider(TimePlannerRes.icons.logoCircular),
                contentDescription = null,
                modifier = GlanceModifier.size(24.dp),
            )
        }
        Spacer(GlanceModifier.defaultWeight())
        Box(
            modifier = modifier
                .size(48.dp)
                .compatCornerBackground(GlanceTheme.colors.background, 100)
                .clickable(onUpdateAction()),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                provider = ImageProvider(TimePlannerRes.icons.reset),
                contentDescription = null,
                modifier = GlanceModifier.size(24.dp),
                colorFilter = ColorFilter.tint(GlanceTheme.colors.onBackground),
            )
        }
        Box(
            modifier = modifier
                .size(48.dp)
                .compatCornerBackground(GlanceTheme.colors.background, 100)
                .clickable(onAddAction()),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                provider = ImageProvider(TimePlannerRes.icons.add),
                contentDescription = null,
                modifier = GlanceModifier.size(24.dp),
                colorFilter = ColorFilter.tint(GlanceTheme.colors.onBackground),
            )
        }
    }
}

@Composable
fun WidgetTimeRange(
    modifier: GlanceModifier = GlanceModifier,
    timeRange: TimeRange,
    isTwoLines: Boolean,
) {
    val timeFormat = SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT)
    Column {
        Text(
            modifier = modifier.defaultWeight(),
            text = timeFormat.format(timeRange.from),
            style = GlanceTheme.typography().titleSmall.copy(
                color = GlanceTheme.colors.onSurfaceVariant
            ),
            maxLines = 1,
        )
        if (isTwoLines) {
            Spacer(modifier = GlanceModifier.height(8.dp))
        }
        Text(
            text = timeFormat.format(timeRange.to),
            style = GlanceTheme.typography().titleSmall.copy(
                color = GlanceTheme.colors.onSurfaceVariant,
            ),
            maxLines = 1,
        )
    }
}

@Composable
fun WidgetTimeTask(
    modifier: GlanceModifier = GlanceModifier,
    currentTime: Date,
    onTimeTaskClickAction: () -> Action,
    timeRange: TimeRange,
    category: MainCategory,
    subCategory: SubCategory?,
    priority: TaskPriority,
    isCompleted: Boolean,
) {
    val categoryIcon = category.default?.mapToIcon(TimePlannerRes.icons)
    val taskTitle =  when (category.customName != null && category.customName != "null") {
        true -> category.customName
        false -> category.default?.mapToName()
    }
    if (currentTime.time > timeRange.from.time && currentTime.time < timeRange.to.time) {
        RunningWidgetTimeTask(
            modifier = modifier,
            onViewClickedAction = onTimeTaskClickAction,
            taskTitle = taskTitle ?: "",
            taskSubTitle = subCategory?.name,
            categoryIcon = categoryIcon?.let { ImageProvider(it) },
            priority = priority,
        )
    } else if (currentTime.time > timeRange.to.time) {
        CompletedWidgetTimeTask(
            modifier = modifier,
            onViewClickedAction = onTimeTaskClickAction,
            taskTitle = taskTitle ?: "",
            taskSubTitle = subCategory?.name,
            categoryIcon = categoryIcon?.let { ImageProvider(it) },
            isCompleted = isCompleted,
        )
    } else {
        PlannedWidgetTimeTask(
            modifier = modifier,
            onViewClickedAction = onTimeTaskClickAction,
            taskTitle = taskTitle ?: "",
            taskSubTitle = subCategory?.name,
            categoryIcon = categoryIcon?.let { ImageProvider(it) },
            taskDurationTitle = duration(timeRange).toMinutesOrHoursTitle(),
            priority = priority,
        )
    }
}