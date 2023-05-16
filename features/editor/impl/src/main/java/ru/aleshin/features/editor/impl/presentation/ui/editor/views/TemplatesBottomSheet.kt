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
package ru.aleshin.features.editor.impl.presentation.ui.editor.views

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Badge
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import ru.aleshin.core.ui.theme.TimePlannerRes
import ru.aleshin.core.ui.theme.material.surfaceThree
import ru.aleshin.core.ui.views.CategoryIconMonogram
import ru.aleshin.core.ui.views.CategoryTextMonogram
import ru.aleshin.core.ui.views.ExpandedIcon
import ru.aleshin.core.ui.views.toMinutesOrHoursTitle
import ru.aleshin.core.utils.extensions.duration
import ru.aleshin.features.editor.impl.presentation.theme.EditorThemeRes
import ru.aleshin.features.home.api.domains.entities.categories.MainCategory
import ru.aleshin.features.home.api.domains.entities.categories.SubCategory
import ru.aleshin.features.home.api.domains.entities.template.Template
import ru.aleshin.features.home.api.presentation.mappers.fetchNameByLanguage
import ru.aleshin.features.home.api.presentation.mappers.toDescription
import ru.aleshin.features.home.api.presentation.mappers.toIconPainter
import java.text.SimpleDateFormat
import java.util.Date

/**
 * @author Stanislav Aleshin on 06.05.2023.
 */
@Composable
@ExperimentalMaterial3Api
internal fun TemplatesBottomSheet(
    modifier: Modifier = Modifier,
    isShow: Boolean,
    templates: List<Template>?,
    onDismiss: () -> Unit,
    onControlClick: () -> Unit,
    onChooseTemplate: (Template) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState()
    val systemUiController = rememberSystemUiController()
    val containerColor = MaterialTheme.colorScheme.surfaceThree()
    val backgroundColor = MaterialTheme.colorScheme.background

    if (isShow) {
        ModalBottomSheet(
            modifier = modifier.height(462.dp),
            sheetState = sheetState,
            containerColor = containerColor,
            onDismissRequest = onDismiss,
        ) {
            TemplatesBottomSheetHeader(
                templateCount = templates?.size,
                onControlClick = onControlClick,
            )
            Divider(Modifier.padding(horizontal = 16.dp, vertical = 16.dp))
            LazyColumn(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                if (templates != null) {
                    if (templates.isNotEmpty()) {
                        items(
                            items = templates,
                            key = { it.templateId },
                        ) { template ->
                            TemplateBottomSheetItem(
                                model = template,
                                onChooseTemplate = { onChooseTemplate(template) },
                            )
                        }
                    } else {
                        item {
                            Text(
                                modifier = Modifier.fillMaxSize(),
                                text = EditorThemeRes.strings.emptyTemplatesTitle,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.headlineSmall,
                            )
                        }
                    }
                } else {
                    item {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }
        }
    }
    DisposableEffect(key1 = isShow) {
        systemUiController.setNavigationBarColor(color = if (isShow) containerColor else backgroundColor)
        onDispose {
            systemUiController.setNavigationBarColor(color = backgroundColor)
        }
    }
}

@Composable
@ExperimentalMaterial3Api
internal fun TemplatesBottomSheetHeader(
    modifier: Modifier = Modifier,
    templateCount: Int?,
    onControlClick: () -> Unit,
) {
    Row(
        modifier = modifier.padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = EditorThemeRes.strings.templatesSheetTitle,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleLarge,
        )
        Badge(modifier = Modifier.align(Alignment.CenterVertically).width(22.dp)) {
            Text(templateCount?.toString() ?: "-")
        }
        Spacer(modifier = Modifier.weight(1f))
        TextButton(modifier = Modifier.height(34.dp), onClick = onControlClick) {
            Text(text = EditorThemeRes.strings.controlTitle)
        }
    }
}

@Composable
internal fun TemplateBottomSheetItem(
    modifier: Modifier = Modifier,
    model: Template,
    onChooseTemplate: () -> Unit,
) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }
    Surface(
        onClick = { isExpanded = !isExpanded },
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
    ) {
        Column(modifier = Modifier.animateContentSize()) {
            Row(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 12.dp),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                val categoryIcon = model.category.icon
                if (categoryIcon != null) {
                    CategoryIconMonogram(
                        icon = categoryIcon.toIconPainter(),
                        iconDescription = categoryIcon.toDescription(),
                        iconColor = MaterialTheme.colorScheme.primary,
                        backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                    )
                } else {
                    CategoryTextMonogram(
                        text = model.category.fetchNameByLanguage().first().toString(),
                        textColor = MaterialTheme.colorScheme.primary,
                        backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                    )
                }
                TemplateBottomSheetItemInfo(
                    modifier = Modifier.weight(1f),
                    isFullInfo = isExpanded,
                    mainCategory = model.category,
                    subCategory = model.subCategory,
                    startTime = model.startTime,
                    endTime = model.endTime,
                    isEnableNotification = model.isEnableNotification,
                )
                ExpandedIcon(
                    modifier = Modifier.size(24.dp),
                    isExpanded = isExpanded,
                )
            }
            if (isExpanded) {
                Row(Modifier.fillMaxWidth().padding(end = 16.dp, bottom = 4.dp)) {
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(onClick = onChooseTemplate) {
                        Icon(
                            modifier = Modifier.size(18.dp),
                            imageVector = Icons.Default.Check,
                            contentDescription = EditorThemeRes.strings.applyTitle,
                        )
                        Text(
                            modifier = Modifier.offset(4.dp),
                            text = EditorThemeRes.strings.applyTitle,
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun TemplateBottomSheetItemInfo(
    modifier: Modifier = Modifier,
    isFullInfo: Boolean,
    mainCategory: MainCategory,
    subCategory: SubCategory?,
    startTime: Date,
    endTime: Date,
    isEnableNotification: Boolean,
) {
    val timeFormat = SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT)
    val startTimeFormat = timeFormat.format(startTime)
    val endTimeFormat = timeFormat.format(endTime)
    val duration = duration(startTime, endTime).toMinutesOrHoursTitle()
    val mainText = when (subCategory != null) {
        true -> TimePlannerRes.strings.splitFormat.format(mainCategory.fetchNameByLanguage(), subCategory.name)
        false -> mainCategory.fetchNameByLanguage()
    }
    val notificationTitle = when (isEnableNotification) {
        true -> EditorThemeRes.strings.notificationEnabledTitle
        false -> EditorThemeRes.strings.notificationDisabledTitle
    }
    val subText = when (isFullInfo) {
        true -> StringBuilder()
            .appendLine(EditorThemeRes.strings.timeRangeFormat.format(startTimeFormat, endTimeFormat))
            .appendLine(EditorThemeRes.strings.durationFormat.format(duration))
            .appendLine(notificationTitle)

        false -> StringBuilder()
            .appendLine(EditorThemeRes.strings.timeRangeFormat.format(startTimeFormat, endTimeFormat))
            .appendLine(EditorThemeRes.strings.durationFormat.format(duration))
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
