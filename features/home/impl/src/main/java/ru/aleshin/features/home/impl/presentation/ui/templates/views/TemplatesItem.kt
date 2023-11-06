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
package ru.aleshin.features.home.impl.presentation.ui.templates.views

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import ru.aleshin.core.ui.theme.TimePlannerRes
import ru.aleshin.core.ui.views.CategoryIconMonogram
import ru.aleshin.core.ui.views.CategoryTextMonogram
import ru.aleshin.core.ui.views.toMinutesOrHoursTitle
import ru.aleshin.core.utils.extensions.duration
import ru.aleshin.features.home.api.domain.entities.template.RepeatTime
import ru.aleshin.features.home.api.presentation.mappers.mapToIconPainter
import ru.aleshin.features.home.api.presentation.mappers.mapToString
import ru.aleshin.features.home.impl.presentation.models.categories.CategoriesUi
import ru.aleshin.features.home.impl.presentation.models.templates.TemplateUi
import ru.aleshin.features.home.impl.presentation.theme.HomeThemeRes
import java.text.SimpleDateFormat
import java.util.Date

/**
 * @author Stanislav Aleshin on 08.05.2023.
 */
@Composable
internal fun TemplatesItem(
    modifier: Modifier = Modifier,
    categories: List<CategoriesUi>,
    model: TemplateUi,
    onAddRepeat: (RepeatTime) -> Unit,
    onUpdate: (TemplateUi) -> Unit = {},
    onRestartRepeat: () -> Unit,
    onStopRepeat: () -> Unit,
    onDeleteRepeat: (RepeatTime) -> Unit,
    onDeleteTemplate: () -> Unit,
) {
    var isShowTemplateEditor by rememberSaveable { mutableStateOf(false) }
    val categoryIcon = model.category.defaultType?.mapToIconPainter()
    val categoryName = model.category.fetchName() ?: "*"

    Surface(
        onClick = { isShowTemplateEditor = true },
        modifier = modifier.fillMaxWidth().animateContentSize(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = TimePlannerRes.elevations.levelOne,
    ) {
        Row(
            modifier = Modifier.padding(start = 16.dp, end = 8.dp, top = 16.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                if (categoryIcon != null) {
                    CategoryIconMonogram(
                        icon = categoryIcon,
                        iconDescription = categoryName,
                        iconColor = MaterialTheme.colorScheme.primary,
                        backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                    )
                } else {
                    CategoryTextMonogram(
                        text = categoryName.first().toString(),
                        textColor = MaterialTheme.colorScheme.primary,
                        backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                    )
                }
                Text(
                    text = when (model.subCategory != null) {
                        true -> TimePlannerRes.strings.splitFormat.format(categoryName, model.subCategory.name)
                        false -> categoryName
                    },
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleMedium,
                )
                TemplateItemInfo(
                    startTime = model.startTime,
                    endTime = model.endTime,
                    isEnableNotification = model.isEnableNotification,
                    isConsiderInStatistics = model.isConsiderInStatistics,
                    repeatTimes = model.repeatTimes,
                )
            }
            TemplateItemControlButtons(
                repeatEnabled = model.repeatEnabled,
                repeatTimes = model.repeatTimes,
                onDeleteTemplate = onDeleteTemplate,
                onAddRepeat = onAddRepeat,
                onDeleteRepeat = onDeleteRepeat,
                onRestartRepeat = onRestartRepeat,
                onStopRepeat = onStopRepeat,
            )
        }
    }

    if (isShowTemplateEditor) {
        TemplateEditorDialog(
            categories = categories,
            model = model,
            onDismiss = { isShowTemplateEditor = false },
            onConfirm = { template ->
                onUpdate(template)
                isShowTemplateEditor = false
            },
        )
    }
}

@Composable
internal fun TemplateItemInfo(
    modifier: Modifier = Modifier,
    startTime: Date,
    endTime: Date,
    isEnableNotification: Boolean,
    isConsiderInStatistics: Boolean,
    repeatTimes: List<RepeatTime>,
) {
    val timeFormat = SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT)
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                TemplateInfoIcon(
                    icon = painterResource(HomeThemeRes.icons.time),
                    title = "${timeFormat.format(startTime)} - ${timeFormat.format(endTime)}",
                )
                TemplateInfoIcon(
                    icon = painterResource(HomeThemeRes.icons.duration),
                    title = duration(startTime, endTime).toMinutesOrHoursTitle(),
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                TemplateInfoIcon(
                    icon = painterResource(HomeThemeRes.icons.statistics),
                    title = when (isConsiderInStatistics) {
                        true -> HomeThemeRes.strings.statisticsActiveTitle
                        false -> HomeThemeRes.strings.statisticsDisabledTitle
                    },
                )
                TemplateInfoIcon(
                    icon = painterResource(HomeThemeRes.icons.notification),
                    title = when (isEnableNotification) {
                        true -> HomeThemeRes.strings.notificationEnabledTitle
                        false -> HomeThemeRes.strings.notificationDisabledTitle
                    },
                )
            }
        }
        if (repeatTimes.isNotEmpty()) {
            TemplateInfoIcon(
                icon = painterResource(HomeThemeRes.icons.repeatVariant),
                title = "${repeatTimes.first().repeatType.mapToString()} (${repeatTimes.size})",
            )
        }
    }
}

@Composable
internal fun TemplateInfoIcon(
    modifier: Modifier = Modifier,
    icon: Painter,
    title: String,
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Icon(
            modifier = Modifier.size(18.dp),
            painter = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.labelMedium,
        )
    }
}

@Composable
internal fun TemplateItemControlButtons(
    modifier: Modifier = Modifier,
    repeatEnabled: Boolean,
    repeatTimes: List<RepeatTime>,
    onDeleteTemplate: () -> Unit,
    onAddRepeat: (RepeatTime) -> Unit,
    onDeleteRepeat: (RepeatTime) -> Unit,
    onRestartRepeat: () -> Unit,
    onStopRepeat: () -> Unit,
) {
    var isShowRepeatTimesMenu by rememberSaveable { mutableStateOf(false) }

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        if (repeatTimes.isEmpty()) {
            IconButton(onClick = { isShowRepeatTimesMenu = true }) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(id = HomeThemeRes.icons.repeat),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            IconButton(onClick = onDeleteTemplate) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        } else {
            IconButton(onClick = { isShowRepeatTimesMenu = true }) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(id = HomeThemeRes.icons.updateRepeat),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            if (repeatEnabled) {
                IconButton(onClick = onStopRepeat) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(id = HomeThemeRes.icons.stop),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            } else {
                IconButton(onClick = onRestartRepeat) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(id = HomeThemeRes.icons.start),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                    )
                }
            }
        }
    }
    Box(contentAlignment = Alignment.TopEnd) {
        RepeatTimeMenu(
            isExpanded = isShowRepeatTimesMenu,
            selectedTimes = repeatTimes,
            onDismiss = { isShowRepeatTimesMenu = false },
            onAddRepeat = onAddRepeat,
            onDeleteRepeat = onDeleteRepeat,
        )
    }
}
