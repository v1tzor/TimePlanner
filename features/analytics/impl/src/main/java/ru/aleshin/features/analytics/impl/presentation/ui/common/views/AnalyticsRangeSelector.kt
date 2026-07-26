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
package ru.aleshin.features.analytics.impl.presentation.ui.common.views

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import ru.aleshin.core.utils.functional.TimePeriod
import ru.aleshin.features.analytics.impl.presentation.mappers.toRangeTitle
import ru.aleshin.features.analytics.impl.presentation.models.analytics.AnalyticsRangeUi
import ru.aleshin.features.analytics.impl.presentation.theme.AnalyticsThemeRes
import ru.aleshin.features.analytics.impl.presentation.utils.fetchAnalyticsLocale
import ru.aleshin.features.analytics.impl.presentation.utils.rememberAnalyticsRangeFormatter
import ru.aleshin.timeplanner.core.ui.theme.TimePlannerRes

/**
 * @author Stanislav Aleshin on 21.07.2026.
 */
@Composable
internal fun AnalyticsRangeSelector(
    modifier: Modifier = Modifier,
    range: AnalyticsRangeUi,
    onSelectPeriod: (TimePeriod) -> Unit,
    onMoveToCurrent: () -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onOpenCalendar: () -> Unit,
) {
    val rangeFormatter = rememberAnalyticsRangeFormatter()

    Surface(
        modifier = Modifier.fillMaxWidth().height(40.dp),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.background,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        val language = TimePlannerRes.language
        val locale = remember(language) { language.fetchAnalyticsLocale() }
        val strings = AnalyticsThemeRes.strings
        val rangeTitle = remember(range, locale) { rangeFormatter.format(range, locale) }

        Row(verticalAlignment = Alignment.CenterVertically) {
            RangePeriodChip(
                selectedPeriod = range.period,
                onMoveToCurrent = onMoveToCurrent,
                onSelectPeriod = onSelectPeriod,
            )
            Spacer(modifier = Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .height(36.dp)
                    .widthIn(min = 48.dp)
                    .semantics {
                        contentDescription = "${strings.selectRangeDesc}: $rangeTitle"
                        role = Role.Button
                    }
                    .clip(MaterialTheme.shapes.large)
                    .clickable(role = Role.Button, onClick = onOpenCalendar)
                    .padding(horizontal = 4.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = rangeTitle,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            AnalyticsRangeArrow(
                isPrevious = true,
                contentDescription = strings.previousPeriodDesc,
                onClick = onPrevious,
            )
            AnalyticsRangeArrow(
                isPrevious = false,
                contentDescription = strings.nextPeriodDesc,
                onClick = onNext,
            )
        }
    }
}

@Composable
private fun RangePeriodChip(
    modifier: Modifier = Modifier,
    selectedPeriod: TimePeriod,
    onSelectPeriod: (TimePeriod) -> Unit,
    onMoveToCurrent: () -> Unit,
) {
    Box {
        var isMenuExpanded by remember { mutableStateOf(false) }
        val strings = AnalyticsThemeRes.strings

        Row(
            modifier = Modifier
                .semantics {
                    contentDescription = selectedPeriod.toRangeTitle(strings)
                    role = Role.Button
                }
                .clip(MaterialTheme.shapes.large)
                .clickable(role = Role.Button) { isMenuExpanded = true }
                .height(40.dp)
                .padding(start = 12.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                modifier = Modifier.size(18.dp),
                imageVector = Icons.Default.DateRange,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = selectedPeriod.toRangeTitle(strings),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.width(4.dp))
            val dropdownIconRotationAnim = animateFloatAsState(
                targetValue = if (isMenuExpanded) 0f else 180f
            )
            Icon(
                modifier = Modifier
                    .size(18.dp)
                    .graphicsLayer {
                        rotationZ = dropdownIconRotationAnim.value
                    },
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        DropdownMenu(
            expanded = isMenuExpanded,
            onDismissRequest = { isMenuExpanded = false },
            shape = MaterialTheme.shapes.large,
            offset = DpOffset(0.dp, 2.dp),
        ) {
            PERIODS.forEach { period ->
                DropdownMenuItem(
                    text = { Text(period.toRangeTitle(strings)) },
                    onClick = {
                        isMenuExpanded = false
                        onSelectPeriod(period)
                    },
                )
            }
            HorizontalDivider()
            DropdownMenuItem(
                text = { Text(strings.currentPeriod) },
                onClick = {
                    isMenuExpanded = false
                    onMoveToCurrent()
                },
            )
        }
    }
}

@Composable
private fun AnalyticsRangeArrow(
    isPrevious: Boolean,
    contentDescription: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .width(40.dp)
            .height(40.dp)
            .semantics {
                this.contentDescription = contentDescription
                role = Role.Button
            }
            .clickable(onClick = onClick),
        contentAlignment = if (isPrevious) Alignment.CenterEnd else Alignment.CenterStart,
    ) {
        Box(
            modifier = Modifier.size(32.dp),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                modifier = Modifier.size(24.dp),
                imageVector = if (isPrevious) {
                    Icons.AutoMirrored.Filled.KeyboardArrowLeft
                } else {
                    Icons.AutoMirrored.Filled.KeyboardArrowRight
                },
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

private val PERIODS = listOf(
    TimePeriod.LAST_7_DAYS,
    TimePeriod.WEEK,
    TimePeriod.MONTH,
    TimePeriod.HALF_YEAR,
    TimePeriod.YEAR,
)
