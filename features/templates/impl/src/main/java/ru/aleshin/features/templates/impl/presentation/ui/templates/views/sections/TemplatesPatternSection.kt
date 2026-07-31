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
package ru.aleshin.features.templates.impl.presentation.ui.templates.views.sections

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import ru.aleshin.features.templates.impl.domain.entities.templates.TemplatesPatternFilter
import ru.aleshin.features.templates.impl.presentation.models.TemplatePatternDayUi
import ru.aleshin.features.templates.impl.presentation.models.TemplatesPatternUi
import ru.aleshin.features.templates.impl.presentation.models.TemplatesPatternViewUi
import ru.aleshin.features.templates.impl.presentation.theme.TemplatesThemeRes
import ru.aleshin.features.templates.impl.presentation.theme.tokens.fetchTemplatesCategoryColors
import ru.aleshin.features.templates.impl.presentation.ui.templates.views.TemplatesPatternDayMenu
import ru.aleshin.timeplanner.core.ui.mappers.mapToString
import ru.aleshin.timeplanner.core.ui.views.ExpandedIcon

/**
 * @author Stanislav Aleshin on 16.07.2026.
 */
@Composable
internal fun TemplatesPatternSection(
    modifier: Modifier = Modifier,
    patternFilter: TemplatesPatternFilter,
    patternView: TemplatesPatternViewUi,
    weekPattern: TemplatesPatternUi,
    monthPattern: TemplatesPatternUi,
    onChangePatternFilter: (TemplatesPatternFilter) -> Unit,
    onChangePatternView: (TemplatesPatternViewUi) -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        TemplatesPatternHeader(
            patternFilter = patternFilter,
            patternView = patternView,
            onChangePatternFilter = onChangePatternFilter,
            onChangePatternView = onChangePatternView,
        )
        Crossfade(
            targetState = patternView,
            label = "TemplatesPatternView",
        ) { view ->
            when (view) {
                TemplatesPatternViewUi.WEEK -> TemplatesWeekPattern(pattern = weekPattern)
                TemplatesPatternViewUi.MONTH -> TemplatesMonthPattern(pattern = monthPattern)
            }
        }
    }
}

@Composable
private fun TemplatesPatternHeader(
    modifier: Modifier = Modifier,
    patternFilter: TemplatesPatternFilter,
    patternView: TemplatesPatternViewUi,
    onChangePatternFilter: (TemplatesPatternFilter) -> Unit,
    onChangePatternView: (TemplatesPatternViewUi) -> Unit,
) {
    var isViewMenuExpanded by rememberSaveable { mutableStateOf(false) }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.large)
                    .clickable { isViewMenuExpanded = true }
                    .padding(horizontal = 4.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    modifier = Modifier.size(22.dp),
                    painter = painterResource(TemplatesThemeRes.icons.pattern),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = when (patternView) {
                        TemplatesPatternViewUi.WEEK -> TemplatesThemeRes.strings.weeklyPatternTitle
                        TemplatesPatternViewUi.MONTH -> TemplatesThemeRes.strings.monthlyPatternTitle
                    },
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleMedium,
                )
                ExpandedIcon(isExpanded = isViewMenuExpanded)
            }
            DropdownMenu(
                expanded = isViewMenuExpanded,
                onDismissRequest = { isViewMenuExpanded = false },
                shape = MaterialTheme.shapes.large,
                offset = DpOffset(0.dp, 4.dp),
            ) {
                TemplatesPatternViewUi.entries.forEach { view ->
                    DropdownMenuItem(
                        onClick = {
                            isViewMenuExpanded = false
                            onChangePatternView(view)
                        },
                        text = {
                            Text(
                                text = when (view) {
                                    TemplatesPatternViewUi.WEEK -> TemplatesThemeRes.strings.weeklyPatternTitle
                                    TemplatesPatternViewUi.MONTH -> TemplatesThemeRes.strings.monthlyPatternTitle
                                },
                                style = MaterialTheme.typography.titleMedium,
                            )
                        },
                    )
                }
            }
        }
        TemplatesPatternFilterToggle(
            patternFilter = patternFilter,
            onChangePatternFilter = onChangePatternFilter,
        )
    }
}

@Composable
private fun TemplatesPatternFilterToggle(
    modifier: Modifier = Modifier,
    patternFilter: TemplatesPatternFilter,
    onChangePatternFilter: (TemplatesPatternFilter) -> Unit,
) {
    Row(
        modifier = modifier
            .size(width = 130.dp, height = 40.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surface)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = CircleShape,
            )
            .padding(3.dp),
    ) {
        TemplatesPatternFilterButton(
            modifier = Modifier.weight(1f),
            title = TemplatesThemeRes.strings.activeTitle,
            isSelected = patternFilter == TemplatesPatternFilter.ACTIVE,
            onClick = { onChangePatternFilter(TemplatesPatternFilter.ACTIVE) },
        )
        TemplatesPatternFilterButton(
            modifier = Modifier.weight(1f),
            title = TemplatesThemeRes.strings.allTitle,
            isSelected = patternFilter == TemplatesPatternFilter.ALL,
            onClick = { onChangePatternFilter(TemplatesPatternFilter.ALL) },
        )
    }
}

@Composable
private fun TemplatesPatternFilterButton(
    modifier: Modifier = Modifier,
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(CircleShape)
            .background(
                when (isSelected) {
                    true -> MaterialTheme.colorScheme.primaryContainer
                    false -> Color.Transparent
                },
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = title,
            color = when (isSelected) {
                true -> MaterialTheme.colorScheme.onPrimaryContainer
                false -> MaterialTheme.colorScheme.onSurfaceVariant
            },
            maxLines = 1,
            style = MaterialTheme.typography.labelMedium,
        )
    }
}

@Composable
private fun TemplatesWeekPattern(
    modifier: Modifier = Modifier,
    pattern: TemplatesPatternUi,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        TemplatesPatternTitle(
            modifier = Modifier.padding(horizontal = 6.dp),
            title = TemplatesThemeRes.strings.repeatsThisWeekTitle,
            summary = TemplatesThemeRes.strings.patternSummaryFormat.format(
                pattern.templatesCount,
                pattern.repeatsCount,
            ),
        )
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surfaceContainerLowest,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceContainerHighest),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize(spring(stiffness = Spring.StiffnessHigh))
                    .height(IntrinsicSize.Min)
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                verticalAlignment = Alignment.Top,
            ) {
                pattern.days.forEachIndexed { index, day ->
                    TemplatesWeekPatternDay(
                        modifier = Modifier.weight(1f),
                        day = day,
                    )
                    if (index != pattern.days.lastIndex) {
                        VerticalDivider(
                            modifier = Modifier.fillMaxHeight().padding(vertical = 2.dp),
                            color = MaterialTheme.colorScheme.surfaceContainerHighest,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TemplatesWeekPatternDay(
    modifier: Modifier = Modifier,
    day: TemplatePatternDayUi,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = day.weekDay.mapToString().take(3),
                color = when (day.isCurrentDay) {
                    true -> MaterialTheme.colorScheme.primary
                    false -> MaterialTheme.colorScheme.onSurface
                },
                maxLines = 1,
                style = MaterialTheme.typography.labelMedium,
            )
            Text(
                text = day.dayNumber.toString(),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelSmall,
            )
        }
        if (day.templates.isNotEmpty()) {
            Column(
                verticalArrangement = Arrangement.spacedBy(5.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                day.templates.take(MAX_VISIBLE_WEEK_MARKERS).forEach { template ->
                    val colors = fetchTemplatesCategoryColors(template.category.id)
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(MaterialTheme.shapes.extraSmall)
                            .background(
                                colors.accent.copy(
                                    alpha = when (template.repeatEnabled) {
                                        true -> 1f
                                        false -> 0.45f
                                    },
                                ),
                            ),
                    )
                }
                if (day.templatesCount > MAX_VISIBLE_WEEK_MARKERS) {
                    Text(
                        text = "+${day.templatesCount - MAX_VISIBLE_WEEK_MARKERS}",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
            }
        }
    }
}

@Composable
private fun TemplatesMonthPattern(
    modifier: Modifier = Modifier,
    pattern: TemplatesPatternUi,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        TemplatesPatternTitle(
            modifier = Modifier.padding(horizontal = 6.dp),
            title = TemplatesThemeRes.strings.repeatsThisMonthTitle,
            summary = TemplatesThemeRes.strings.patternSummaryFormat.format(
                pattern.templatesCount,
                pattern.repeatsCount,
            ),
        )
        TemplatesMonthPatternCalendar(pattern = pattern)
    }
}

@Composable
private fun TemplatesPatternTitle(
    modifier: Modifier = Modifier,
    title: String,
    summary: String,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = title,
            color = MaterialTheme.colorScheme.primary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.titleMedium,
        )
        Text(
            text = summary,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            style = MaterialTheme.typography.labelMedium,
        )
    }
}

@Composable
private fun TemplatesMonthPatternCalendar(
    modifier: Modifier = Modifier,
    pattern: TemplatesPatternUi,
) {
    val currentDayIndex = remember(pattern.days) {
        pattern.days.indexOfFirst { day -> day.isCurrentDay }.coerceAtLeast(0)
    }
    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = (currentDayIndex - 2).coerceAtLeast(0),
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceContainerLowest,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceContainerHighest),
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            state = listState,
            contentPadding = PaddingValues(horizontal = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(7.dp),
        ) {
            items(
                items = pattern.days,
                key = { day -> day.date.time },
            ) { day ->
                TemplatesMonthPatternDay(
                    modifier = Modifier.size(width = 48.dp, height = 64.dp),
                    day = day,
                )
            }
        }
    }
}

@Composable
private fun TemplatesMonthPatternDay(
    modifier: Modifier = Modifier,
    day: TemplatePatternDayUi,
) {
    var isExpanded by rememberSaveable(day.date.time) { mutableStateOf(false) }
    val containerColor = when (day.templatesCount) {
        0 -> MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.6f)
        else -> MaterialTheme.colorScheme.primaryContainer
    }
    val contentColor = when (day.templatesCount) {
        0 -> MaterialTheme.colorScheme.onSurfaceVariant
        else -> MaterialTheme.colorScheme.onPrimaryContainer
    }

    Box(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clip(MaterialTheme.shapes.medium)
                .background(containerColor)
                .clickable(
                    enabled = day.templatesCount > 0,
                    onClick = { isExpanded = true },
                )
                .then(
                    when (day.isCurrentDay) {
                        true -> Modifier.border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.primary,
                            shape = MaterialTheme.shapes.medium,
                        )
                        false -> Modifier
                    },
                )
                .padding(horizontal = 5.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = day.weekDay.mapToString().take(1),
                color = contentColor,
                style = MaterialTheme.typography.labelSmall,
            )
            Text(
                text = day.dayNumber.toString(),
                color = contentColor,
                style = MaterialTheme.typography.titleSmall,
            )
            Row(
                modifier = Modifier.height(6.dp),
                horizontalArrangement = Arrangement.spacedBy(3.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                day.templates.take(MAX_VISIBLE_MONTH_MARKERS).forEach { template ->
                    val colors = fetchTemplatesCategoryColors(template.category.id)
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(MaterialTheme.shapes.extraSmall)
                            .background(colors.accent),
                    )
                }
            }
        }
        TemplatesPatternDayMenu(
            modifier = Modifier.align(Alignment.TopEnd),
            day = day,
            isExpanded = isExpanded,
            onDismiss = { isExpanded = false },
        )
    }
}

private const val MAX_VISIBLE_WEEK_MARKERS = 3
private const val MAX_VISIBLE_MONTH_MARKERS = 3
