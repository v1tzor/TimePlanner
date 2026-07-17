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
package ru.aleshin.features.templates.impl.presentation.ui.templates.views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import ru.aleshin.core.domain.entities.template.RepeatTime
import ru.aleshin.core.presentation.mappers.mapToIconPainter
import ru.aleshin.core.presentation.models.categories.MainCategoryDetailsUi
import ru.aleshin.core.presentation.models.templates.TemplateUi
import ru.aleshin.core.utils.extensions.duration
import ru.aleshin.features.templates.impl.presentation.theme.TemplatesThemeRes
import ru.aleshin.features.templates.impl.presentation.theme.tokens.TemplatesCategoryColors
import ru.aleshin.features.templates.impl.presentation.theme.tokens.fetchTemplatesCategoryColors
import ru.aleshin.timeplanner.core.ui.mappers.mapToString
import ru.aleshin.timeplanner.core.ui.views.CategoryIconMonogram
import ru.aleshin.timeplanner.core.ui.views.CategoryTextMonogram
import ru.aleshin.timeplanner.core.ui.views.WarningDeleteDialog
import ru.aleshin.timeplanner.core.ui.views.toMinutesOrHoursTitle
import java.text.SimpleDateFormat

/**
 * @author Stanislav Aleshin on 08.05.2023.
 */
@Composable
internal fun TemplatesItem(
    modifier: Modifier = Modifier,
    categories: List<MainCategoryDetailsUi>,
    model: TemplateUi,
    onAddRepeat: (RepeatTime) -> Unit,
    onUpdate: (TemplateUi) -> Unit,
    onRestartRepeat: () -> Unit,
    onStopRepeat: () -> Unit,
    onDeleteRepeat: (RepeatTime) -> Unit,
    onDeleteTemplate: () -> Unit,
) {
    var isShowTemplateEditor by rememberSaveable { mutableStateOf(false) }
    var isShowDeleteDialog by rememberSaveable { mutableStateOf(false) }

    val categoryTitle = model.category.fetchName() ?: TemplatesThemeRes.strings.subCategoryEmptyTitle
    val subCategoryTitle = model.subCategory?.name?.takeIf { title -> title.isNotBlank() }
    val title = subCategoryTitle ?: categoryTitle
    val subtitle = categoryTitle.takeIf { subCategoryTitle != null }
    val categoryColors = fetchTemplatesCategoryColors(model.category.id)

    Surface(
        onClick = { isShowTemplateEditor = true },
        modifier = modifier.fillMaxWidth().height(TEMPLATE_CARD_HEIGHT),
        shape = MaterialTheme.shapes.large,
        color = when (model.repeatEnabled) {
            true -> MaterialTheme.colorScheme.surfaceContainerLow
            false -> MaterialTheme.colorScheme.surfaceContainerLowest
        },
        border = when (model.repeatEnabled) {
            true -> null
            false -> BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceContainerHighest)
        },
    ) {
        Column {
            TemplateItemHeader(
                title = title,
                categoryTitle = categoryTitle,
                categoryColors = categoryColors,
                categoryIcon = model.category.defaultType?.mapToIconPainter(),
                repeatEnabled = model.repeatEnabled,
                repeatTimes = model.repeatTimes,
                onAddRepeat = onAddRepeat,
                onDeleteRepeat = onDeleteRepeat,
                onRestartRepeat = onRestartRepeat,
                onStopRepeat = onStopRepeat,
                onDeleteTemplate = { isShowDeleteDialog = true },
            )
            Column(modifier = Modifier.padding(horizontal = 12.dp)) {
                val timeFormat = remember { SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT) }
                val durationTitle = remember(model) { duration(model.startTime, model.endTime) }.toMinutesOrHoursTitle()

                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleMedium,
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        color = categoryColors.accent,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.labelLarge,
                    )
                } else {
                    Spacer(modifier = Modifier.height(2.dp))
                }
                Text(
                    modifier = modifier.padding(top = 2.dp),
                    text = "${timeFormat.format(model.startTime)}–${timeFormat.format(model.endTime)} • $durationTitle",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceContainerHighest)
            RepeatLabels(
                repeatTimes = model.repeatTimes,
                categoryColors = categoryColors,
                onAddRepeat = onAddRepeat,
                onDeleteRepeat = onDeleteRepeat,
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

    if (isShowDeleteDialog) {
        WarningDeleteDialog(
            text = when (model.repeatTimes.isNotEmpty()) {
                true -> TemplatesThemeRes.strings.warningDeleteRepeatTemplateText
                false -> TemplatesThemeRes.strings.warningDeleteTemplateText
            },
            onDismiss = { isShowDeleteDialog = false },
            onAction = {
                onDeleteTemplate()
                isShowDeleteDialog = false
            },
        )
    }
}

@Composable
private fun TemplateItemHeader(
    modifier: Modifier = Modifier,
    title: String,
    categoryTitle: String,
    categoryColors: TemplatesCategoryColors,
    categoryIcon: androidx.compose.ui.graphics.painter.Painter?,
    repeatEnabled: Boolean,
    repeatTimes: List<RepeatTime>,
    onAddRepeat: (RepeatTime) -> Unit,
    onDeleteRepeat: (RepeatTime) -> Unit,
    onRestartRepeat: () -> Unit,
    onStopRepeat: () -> Unit,
    onDeleteTemplate: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 12.dp, end = 8.dp, top = 12.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        if (categoryIcon != null) {
            CategoryIconMonogram(
                icon = categoryIcon,
                iconDescription = categoryTitle,
                iconColor = categoryColors.accent,
                backgroundColor = categoryColors.container,
            )
        } else {
            CategoryTextMonogram(
                text = title.fetchMonogram(),
                textColor = categoryColors.accent,
                backgroundColor = categoryColors.container,
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        if (!repeatEnabled && repeatTimes.isNotEmpty()) {
            Surface(
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.errorContainer,
            ) {
                Text(
                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                    text = TemplatesThemeRes.patternStrings.pausedTitle,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    style = MaterialTheme.typography.labelSmall,
                )
            }
        }
        TemplateActionsMenu(
            repeatEnabled = repeatEnabled,
            repeatTimes = repeatTimes,
            onAddRepeat = onAddRepeat,
            onDeleteRepeat = onDeleteRepeat,
            onRestartRepeat = onRestartRepeat,
            onStopRepeat = onStopRepeat,
            onDeleteTemplate = onDeleteTemplate,
        )
    }
}

@Composable
private fun TemplateActionsMenu(
    modifier: Modifier = Modifier,
    repeatEnabled: Boolean,
    repeatTimes: List<RepeatTime>,
    onAddRepeat: (RepeatTime) -> Unit,
    onDeleteRepeat: (RepeatTime) -> Unit,
    onRestartRepeat: () -> Unit,
    onStopRepeat: () -> Unit,
    onDeleteTemplate: () -> Unit,
) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }
    var isRepeatMenuExpanded by rememberSaveable { mutableStateOf(false) }
    val strings = TemplatesThemeRes.patternStrings

    Box(modifier = modifier) {
        IconButton(
            modifier = Modifier.size(32.dp),
            onClick = { isExpanded = true },
        ) {
            Icon(
                modifier = Modifier.size(24.dp),
                imageVector = Icons.Default.MoreVert,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        DropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false },
            shape = MaterialTheme.shapes.large,
            offset = DpOffset(0.dp, 4.dp),
        ) {
            DropdownMenuItem(
                onClick = {
                    isExpanded = false
                    isRepeatMenuExpanded = true
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(TemplatesThemeRes.icons.updateRepeat),
                        contentDescription = null,
                    )
                },
                text = { Text(text = strings.repeatSettingsTitle) },
            )
            if (repeatTimes.isNotEmpty()) {
                DropdownMenuItem(
                    onClick = {
                        isExpanded = false
                        if (repeatEnabled) onStopRepeat() else onRestartRepeat()
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(
                                id = when (repeatEnabled) {
                                    true -> TemplatesThemeRes.icons.stop
                                    false -> TemplatesThemeRes.icons.start
                                },
                            ),
                            contentDescription = null,
                        )
                    },
                    text = {
                        Text(
                            text = when (repeatEnabled) {
                                true -> strings.stopRepeatTitle
                                false -> strings.restartRepeatTitle
                            },
                        )
                    },
                )
            }
            DropdownMenuItem(
                onClick = {
                    isExpanded = false
                    onDeleteTemplate()
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                    )
                },
                text = { Text(text = strings.deleteTitle) },
            )
        }
        RepeatTimeMenu(
            isExpanded = isRepeatMenuExpanded,
            selectedTimes = repeatTimes,
            onDismiss = { isRepeatMenuExpanded = false },
            onAddRepeat = onAddRepeat,
            onDeleteRepeat = onDeleteRepeat,
        )
    }
}

@Composable
private fun RepeatLabels(
    modifier: Modifier = Modifier,
    repeatTimes: List<RepeatTime>,
    categoryColors: TemplatesCategoryColors,
    onAddRepeat: (RepeatTime) -> Unit,
    onDeleteRepeat: (RepeatTime) -> Unit,
) {
    Box(modifier = modifier.padding(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 12.dp)) {
        var isRepeatMenuExpanded by rememberSaveable { mutableStateOf(false) }

        if (repeatTimes.isEmpty()) {
            Box(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.small)
                    .clickable(
                        onClick = { isRepeatMenuExpanded = true },
                        interactionSource = remember { MutableInteractionSource() },
                        role = Role.Button,
                        indication = ripple()
                    )
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = MaterialTheme.shapes.small
                    )
            ) {
                Row(
                    modifier = Modifier
                        .height(28.dp)
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        modifier = Modifier.size(15.dp),
                        painter = painterResource(TemplatesThemeRes.icons.updateRepeat),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        text = TemplatesThemeRes.patternStrings.addRepeatTitle,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        maxLines = 1,
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
            }
        } else {
            LazyRow(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                items(
                    items = repeatTimes,
                    key = { repeatTime -> repeatTime.toAlarmKey() },
                ) { repeatTime ->
                    Box(
                        modifier = Modifier
                            .clip(MaterialTheme.shapes.small)
                            .clickable(
                                onClick = { isRepeatMenuExpanded = true },
                                interactionSource = remember { MutableInteractionSource() },
                                role = Role.Button,
                                indication = ripple()
                            )
                            .border(
                                border = BorderStroke(1.dp, categoryColors.accent),
                                shape = MaterialTheme.shapes.small
                            )
                            .background(
                                color = MaterialTheme.colorScheme.surface,
                                shape = MaterialTheme.shapes.small
                            )
                    ) {
                        Box(
                            modifier = Modifier.size(28.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = repeatTime.fetchShortLabel(),
                                color = categoryColors.accent,
                                maxLines = 1,
                                style = MaterialTheme.typography.labelMedium,
                            )
                        }
                    }
                }
            }
        }
        RepeatTimeMenu(
            isExpanded = isRepeatMenuExpanded,
            selectedTimes = repeatTimes,
            onDismiss = { isRepeatMenuExpanded = false },
            onAddRepeat = onAddRepeat,
            onDeleteRepeat = onDeleteRepeat,
        )
    }
}

@Composable
private fun RepeatTime.fetchShortLabel() = when (this) {
    is RepeatTime.WeekDays -> day.mapToString().take(1)
    is RepeatTime.WeekDayInMonth -> day.mapToString().take(1)
    is RepeatTime.MonthDay -> dayNumber.toString()
    is RepeatTime.YearDay -> dayNumber.toString()
}

private fun String.fetchMonogram(): String {
    return filter { char -> char.isLetterOrDigit() }.take(2).ifEmpty { "*" }
}

private val TEMPLATE_CARD_HEIGHT = 170.dp
