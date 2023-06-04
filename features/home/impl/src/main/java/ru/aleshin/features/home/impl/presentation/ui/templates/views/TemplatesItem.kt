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
 * imitations under the License.
 */
package ru.aleshin.features.home.impl.presentation.ui.templates.views

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.unit.dp
import ru.aleshin.core.ui.theme.TimePlannerRes
import ru.aleshin.core.ui.views.CategoryIconMonogram
import ru.aleshin.core.ui.views.CategoryTextMonogram
import ru.aleshin.core.ui.views.toMinutesOrHoursTitle
import ru.aleshin.core.utils.extensions.duration
import ru.aleshin.features.home.api.domains.entities.categories.Categories
import ru.aleshin.features.home.api.domains.entities.categories.MainCategory
import ru.aleshin.features.home.api.domains.entities.categories.SubCategory
import ru.aleshin.features.home.api.domains.entities.template.Template
import ru.aleshin.features.home.api.presentation.mappers.fetchNameByLanguage
import ru.aleshin.features.home.api.presentation.mappers.toDescription
import ru.aleshin.features.home.api.presentation.mappers.toIconPainter
import ru.aleshin.features.home.impl.presentation.theme.HomeThemeRes
import java.text.SimpleDateFormat
import java.util.Date

/**
 * @author Stanislav Aleshin on 08.05.2023.
 */
@Composable
internal fun TemplatesItem(
    modifier: Modifier = Modifier,
    categories: List<Categories>,
    model: Template,
    isFullInfo: Boolean,
    onUpdateTemplate: (Template) -> Unit = {},
    onDeleteTemplate: () -> Unit,
) {
    var isShowTemplateEditor by rememberSaveable { mutableStateOf(false) }

    Surface(
        onClick = { isShowTemplateEditor = true },
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = TimePlannerRes.elevations.levelOne,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Column(modifier = Modifier.animateContentSize()) {
            Row(
                modifier = Modifier.padding(start = 16.dp, end = 8.dp, top = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                val categoryIcon = model.category.icon
                if (categoryIcon != null) {
                    CategoryIconMonogram(
                        modifier.align(Alignment.Top),
                        icon = categoryIcon.toIconPainter(),
                        iconDescription = categoryIcon.toDescription(),
                        iconColor = MaterialTheme.colorScheme.primary,
                        backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                    )
                } else {
                    CategoryTextMonogram(
                        modifier.align(Alignment.Top),
                        text = model.category.fetchNameByLanguage().first().toString(),
                        textColor = MaterialTheme.colorScheme.primary,
                        backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                    )
                }
                TemplatesItemInfo(
                    modifier = Modifier.weight(1f),
                    isFullInfo = isFullInfo,
                    mainCategory = model.category,
                    subCategory = model.subCategory,
                    startTime = model.startTime,
                    endTime = model.endTime,
                    isEnableNotification = model.isEnableNotification,
                    isConsiderInStatistics = model.isConsiderInStatistics,
                )
                IconButton(onClick = onDeleteTemplate) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }

    if (isShowTemplateEditor) {
        TemplateEditorDialog(
            categories = categories,
            editTemplateModel = model,
            onDismiss = { isShowTemplateEditor = false },
            onConfirm = { template ->
                onUpdateTemplate(template)
                isShowTemplateEditor = false
            },
        )
    }
}

@Composable
internal fun TemplatesItemInfo(
    modifier: Modifier = Modifier,
    isFullInfo: Boolean,
    mainCategory: MainCategory,
    subCategory: SubCategory?,
    startTime: Date,
    endTime: Date,
    isEnableNotification: Boolean,
    isConsiderInStatistics: Boolean,
) {
    val timeFormat = SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT)
    val startTimeFormat = timeFormat.format(startTime)
    val endTimeFormat = timeFormat.format(endTime)
    val duration = duration(startTime, endTime).toMinutesOrHoursTitle()
    val mainText = when (subCategory != null) {
        true -> TimePlannerRes.strings.splitFormat.format(
            mainCategory.fetchNameByLanguage(),
            subCategory.fetchNameByLanguage(),
        )
        false -> mainCategory.fetchNameByLanguage()
    }
    val notificationTitle = when (isEnableNotification) {
        true -> HomeThemeRes.strings.notificationEnabledTitle
        false -> HomeThemeRes.strings.notificationDisabledTitle
    }
    val statisticsTitle = when (isConsiderInStatistics) {
        true -> HomeThemeRes.strings.statisticsActiveTitle
        false -> HomeThemeRes.strings.statisticsDisabledTitle
    }
    val subText = when (isFullInfo) {
        true -> StringBuilder()
            .appendLine(HomeThemeRes.strings.timeRangeFormat.format(startTimeFormat, endTimeFormat))
            .appendLine(HomeThemeRes.strings.durationFormat.format(duration))
            .appendLine(notificationTitle)
            .appendLine(statisticsTitle)

        false -> StringBuilder()
            .appendLine(HomeThemeRes.strings.timeRangeFormat.format(startTimeFormat, endTimeFormat))
            .appendLine(HomeThemeRes.strings.durationFormat.format(duration))
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = mainText,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleMedium,
        )
        Text(
            text = subText.toString(),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}
