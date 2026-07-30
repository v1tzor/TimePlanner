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

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ru.aleshin.core.utils.functional.WeekDay
import ru.aleshin.features.templates.impl.domain.entities.templates.TemplatesPatternFilter
import ru.aleshin.features.templates.impl.presentation.models.TemplatePatternDayUi
import ru.aleshin.features.templates.impl.presentation.models.TemplatesPatternUi
import ru.aleshin.features.templates.impl.presentation.theme.TemplatesThemeRes
import ru.aleshin.features.templates.impl.presentation.theme.tokens.fetchTemplatesCategoryColors
import ru.aleshin.features.templates.impl.presentation.ui.templates.views.TemplatesPatternDayMenu
import ru.aleshin.timeplanner.core.ui.mappers.mapToString
import ru.aleshin.timeplanner.core.ui.views.PlaceholderBox
import java.text.SimpleDateFormat
import java.util.Calendar

/**
 * @author Stanislav Aleshin on 28.07.2026.
 */
@Composable
internal fun TemplatesMonthlyPatternSection(
    modifier: Modifier = Modifier,
    patternFilter: TemplatesPatternFilter,
    monthPattern: TemplatesPatternUi,
    onChangePatternFilter: (TemplatesPatternFilter) -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        TemplatesMonthlyPatternHeader()
        TemplatesPatternFilterSegmentedButton(
            patternFilter = patternFilter,
            onChangePatternFilter = onChangePatternFilter,
        )
        TemplatesMonthCalendarCard(
            pattern = monthPattern,
        )
    }
}

@Composable
internal fun TemplatesMonthlyPatternSectionPlaceholder(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            PlaceholderBox(
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
            )
            PlaceholderBox(
                modifier = Modifier
                    .weight(1f)
                    .height(22.dp),
                shape = MaterialTheme.shapes.small,
            )
        }
        PlaceholderBox(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
            shape = CircleShape,
        )
        PlaceholderBox(
            modifier = Modifier
                .fillMaxWidth()
                .height(392.dp),
            shape = MaterialTheme.shapes.large,
        )
    }
}

@Composable
private fun TemplatesMonthlyPatternHeader(
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Surface(
            modifier = Modifier.size(40.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    modifier = Modifier.size(22.dp),
                    painter = painterResource(TemplatesThemeRes.icons.pattern),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        }
        Text(
            text = TemplatesThemeRes.strings.monthlyPatternTitle,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.titleLarge,
        )
    }
}

@Composable
private fun TemplatesPatternFilterSegmentedButton(
    modifier: Modifier = Modifier,
    patternFilter: TemplatesPatternFilter,
    onChangePatternFilter: (TemplatesPatternFilter) -> Unit,
) {
    val filters = TemplatesPatternFilter.entries

    SingleChoiceSegmentedButtonRow(
        modifier = modifier.fillMaxWidth(),
    ) {
        filters.forEachIndexed { index, filter ->
            SegmentedButton(
                modifier = Modifier.weight(1f),
                selected = patternFilter == filter,
                onClick = { onChangePatternFilter(filter) },
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = filters.size,
                ),
                label = {
                    Text(
                        text = when (filter) {
                            TemplatesPatternFilter.ACTIVE -> TemplatesThemeRes.strings.activeTitle
                            TemplatesPatternFilter.ALL -> TemplatesThemeRes.strings.allTitle
                        },
                    )
                },
            )
        }
    }
}

@Composable
private fun TemplatesMonthCalendarCard(
    modifier: Modifier = Modifier,
    pattern: TemplatesPatternUi,
) {
    val locale = LocalLocale.current.platformLocale
    val firstDayOfWeek = remember(locale) { Calendar.getInstance(locale).firstDayOfWeek }
    val weekDays = remember(firstDayOfWeek) {
        WeekDay.entries.sortedBy { weekDay ->
            weekDay.priorityByFirstDayOfWeek(firstDayOfWeek)
        }
    }
    val calendarDays = remember(pattern.days, firstDayOfWeek) {
        buildTemplatesMonthCalendarDays(
            days = pattern.days,
            firstDayOfWeek = firstDayOfWeek,
        )
    }
    val monthTitle = remember(pattern.days, locale) {
        pattern.days.firstOrNull()?.date?.let { date ->
            SimpleDateFormat(MONTH_TITLE_PATTERN, locale).format(date)
        }.orEmpty()
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceContainer,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            TemplatesMonthCalendarSummary(
                monthTitle = monthTitle,
                pattern = pattern,
            )
            TemplatesMonthWeekDays(
                weekDays = weekDays,
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                calendarDays.chunked(WEEK_DAYS_COUNT).forEach { week ->
                    TemplatesMonthWeek(
                        days = week,
                    )
                }
            }
        }
    }
}

@Composable
private fun TemplatesMonthCalendarSummary(
    modifier: Modifier = Modifier,
    monthTitle: String,
    pattern: TemplatesPatternUi,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = monthTitle,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.titleLarge,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = TemplatesThemeRes.strings.repeatsThisMonthTitle,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.labelLarge,
            )
            Text(
                text = TemplatesThemeRes.strings.patternSummaryFormat.format(
                    pattern.templatesCount,
                    pattern.repeatsCount,
                ),
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1,
                style = MaterialTheme.typography.labelMedium,
            )
        }
    }
}

@Composable
private fun TemplatesMonthWeekDays(
    modifier: Modifier = Modifier,
    weekDays: List<WeekDay>,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        weekDays.forEach { weekDay ->
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = weekDay.mapToString().take(1),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelMedium,
                )
            }
        }
    }
}

@Composable
private fun TemplatesMonthWeek(
    modifier: Modifier = Modifier,
    days: List<TemplatePatternDayUi?>,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        days.forEach { day ->
            if (day != null) {
                TemplatesMonthDay(
                    modifier = Modifier.weight(1f),
                    day = day,
                )
            } else {
                Spacer(modifier = Modifier.weight(1f).aspectRatio(1f))
            }
        }
    }
}

@Composable
private fun TemplatesMonthDay(
    modifier: Modifier = Modifier,
    day: TemplatePatternDayUi,
) {
    var isMenuExpanded by rememberSaveable(day.date.time) { mutableStateOf(false) }
    val hasRepeats = day.templatesCount > 0
    val containerColor = when {
        hasRepeats -> MaterialTheme.colorScheme.primaryContainer
        else -> MaterialTheme.colorScheme.surfaceContainerHigh
    }
    val contentColor = when {
        hasRepeats -> MaterialTheme.colorScheme.onPrimaryContainer
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Box(
        modifier = modifier.aspectRatio(1f),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clip(MaterialTheme.shapes.medium)
                .background(containerColor)
                .clickable(
                    enabled = hasRepeats,
                    onClick = { isMenuExpanded = true },
                )
                .then(
                    when {
                        day.isCurrentDay -> Modifier.border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.primary,
                            shape = MaterialTheme.shapes.medium,
                        )
                        else -> Modifier
                    },
                )
                .padding(5.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = day.dayNumber.toString(),
                color = contentColor,
                style = MaterialTheme.typography.labelLarge,
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                day.templates.take(MAX_VISIBLE_CALENDAR_MARKERS).forEach { template ->
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .clip(CircleShape)
                            .background(
                                fetchTemplatesCategoryColors(template.category.id).accent,
                            ),
                    )
                }
            }
        }
        TemplatesPatternDayMenu(
            modifier = Modifier.align(Alignment.TopEnd),
            day = day,
            isExpanded = isMenuExpanded,
            onDismiss = { isMenuExpanded = false },
        )
    }
}

internal fun buildTemplatesMonthCalendarDays(
    days: List<TemplatePatternDayUi>,
    firstDayOfWeek: Int,
): List<TemplatePatternDayUi?> {
    if (days.isEmpty()) return emptyList()

    val leadingDayCount = days.first().weekDay.priorityByFirstDayOfWeek(firstDayOfWeek)
    val occupiedDayCount = leadingDayCount + days.size
    val trailingDayCount = (WEEK_DAYS_COUNT - occupiedDayCount % WEEK_DAYS_COUNT) % WEEK_DAYS_COUNT

    return buildList {
        repeat(leadingDayCount) { add(null) }
        addAll(days)
        repeat(trailingDayCount) { add(null) }
    }
}

private const val MONTH_TITLE_PATTERN = "LLLL yyyy"
private const val WEEK_DAYS_COUNT = 7
private const val MAX_VISIBLE_CALENDAR_MARKERS = 3
