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
package ru.aleshin.features.analytics.impl.presentation.ui.analytics.views.sections

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ru.aleshin.core.presentation.mappers.mapToIcon
import ru.aleshin.core.utils.charts.CategoryColorsDefaults
import ru.aleshin.features.analytics.impl.domain.entities.AnalyticsCategorySort
import ru.aleshin.features.analytics.impl.domain.entities.AnalyticsComparisonState
import ru.aleshin.features.analytics.impl.presentation.mappers.toTitle
import ru.aleshin.features.analytics.impl.presentation.models.analytics.AnalyticsCategoryBucketUi
import ru.aleshin.features.analytics.impl.presentation.models.analytics.AnalyticsCategoryDistributionUi
import ru.aleshin.features.analytics.impl.presentation.theme.AnalyticsThemeRes
import ru.aleshin.features.analytics.impl.presentation.ui.common.views.AnalyticsComparisonLabel
import ru.aleshin.features.analytics.impl.presentation.ui.common.views.AnalyticsSectionTitle
import ru.aleshin.features.analytics.impl.presentation.utils.fetchAnalyticsLocale
import ru.aleshin.features.analytics.impl.presentation.utils.rememberAnalyticsValueFormatter
import ru.aleshin.timeplanner.core.ui.theme.TimePlannerRes
import ru.aleshin.timeplanner.core.ui.views.CategoryIconMonogram
import ru.aleshin.timeplanner.core.ui.views.CategoryTextMonogram

/**
 * @author Stanislav Aleshin on 23.07.2026.
 */
@Composable
internal fun AnalyticsCategoriesSection(
    modifier: Modifier = Modifier,
    categories: AnalyticsCategoryDistributionUi,
    categorySortType: AnalyticsCategorySort,
    isExpanded: Boolean,
    onChangeSort: (AnalyticsCategorySort) -> Unit,
    onToggle: () -> Unit,
    onOpenCategory: (Long) -> Unit,
) {
    val rows = if (isExpanded) {
        categories.buckets
    } else {
        categories.buckets.take(categories.collapsedBucketCount) + listOfNotNull(categories.otherBucket)
    }
    val hasExpandAction = categories.buckets.size > categories.collapsedBucketCount
    val maximumDuration = rows.maxOfOrNull { it.durationMillis }?.coerceAtLeast(1L) ?: 1L

    Column(modifier = modifier) {
        AnalyticsCategoriesHeader(
            categorySortType = categorySortType,
            onChangeSort = onChangeSort,
        )
        rows.forEachIndexed { index, row ->
            val isLastRow = index == rows.lastIndex && !hasExpandAction
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = categoryRowShape(
                    index = index,
                    rowCount = rows.size,
                    isLastRow = isLastRow,
                    hasExpandAction = hasExpandAction,
                ),
                color = MaterialTheme.colorScheme.surfaceContainerLow,
            ) {
                Column {
                    AnalyticsCategoryRow(
                        rank = index + 1,
                        row = row,
                        maximumDuration = maximumDuration,
                        onToggle = onToggle,
                        onOpenCategory = onOpenCategory,
                    )
                    if (!isLastRow) {
                        HorizontalDivider()
                    }
                }
            }
        }
        if (hasExpandAction) {
            AnalyticsCategoriesExpandButton(
                isExpanded = isExpanded,
                onToggle = onToggle,
            )
        } else {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun AnalyticsCategoriesHeader(
    modifier: Modifier = Modifier,
    categorySortType: AnalyticsCategorySort,
    onChangeSort: (AnalyticsCategorySort) -> Unit,
) {
    var isSortMenuExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AnalyticsSectionTitle(
            title = AnalyticsThemeRes.strings.categoriesTitle,
            modifier = Modifier.weight(1f),
        )
        Box {
            Row(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.large)
                    .clickable(onClick = { isSortMenuExpanded = true }, role = Role.Button)
                    .padding(vertical = 4.dp, horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = categorySortType.toTitle(AnalyticsThemeRes.strings),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Icon(
                    painter = painterResource(TimePlannerRes.icons.arrowDown),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            DropdownMenu(
                expanded = isSortMenuExpanded,
                onDismissRequest = { isSortMenuExpanded = false },
                shape = MaterialTheme.shapes.large
            ) {
                AnalyticsCategorySort.entries.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(text = item.toTitle(AnalyticsThemeRes.strings)) },
                        onClick = {
                            isSortMenuExpanded = false
                            onChangeSort(item)
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun AnalyticsCategoryRow(
    modifier: Modifier = Modifier,
    rank: Int,
    row: AnalyticsCategoryBucketUi,
    maximumDuration: Long,
    onToggle: () -> Unit,
    onOpenCategory: (Long) -> Unit,
) {
    val strings = AnalyticsThemeRes.strings
    val language = TimePlannerRes.language
    val locale = remember(language) { language.fetchAnalyticsLocale() }
    val formatter = rememberAnalyticsValueFormatter()
    val category = row.category
    val categoryName = category?.fetchName() ?: strings.other
    val categoryColor = category?.let {
        CategoryColorsDefaults.fetchColor(it.id)
    } ?: MaterialTheme.colorScheme.onSurfaceVariant
    val onClick = if (category != null && !row.isOther) {
        { onOpenCategory(category.id) }
    } else {
        onToggle
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = if (category != null && !row.isOther) {
                    strings.openCategoryFormat.format(categoryName)
                } else {
                    categoryName
                }
                role = Role.Button
            }
            .clickable(onClick = onClick)
            .heightIn(min = 72.dp)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = rank.toString(),
                modifier = Modifier.width(20.dp),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            val defaultType = category?.defaultType
            if (defaultType != null) {
                CategoryIconMonogram(
                    icon = painterResource(defaultType.mapToIcon(TimePlannerRes.icons)),
                    iconDescription = null,
                    iconColor = categoryColor,
                    backgroundColor = categoryColor.copy(alpha = 0.16f),
                )
            } else {
                CategoryTextMonogram(
                    text = categoryName.take(1),
                    textColor = categoryColor,
                    backgroundColor = categoryColor.copy(alpha = 0.16f),
                )
            }
        }
        Column(modifier = Modifier.weight(1f)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = categoryName,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                AnalyticsComparisonLabel(
                    value = if (row.comparison.state == AnalyticsComparisonState.PREVIOUS_ZERO) {
                        strings.newCategory
                    } else {
                        row.comparison.formatAnalyticsComparison(
                            formatter = formatter,
                            locale = locale,
                            strings = strings,
                        )
                    },
                    isPositive = row.comparison.changePercent
                        ?.takeIf { row.comparison.state != AnalyticsComparisonState.PREVIOUS_ZERO && it != 0.0 }
                        ?.let { it > 0 },
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = formatter.formatDuration(
                        durationMillis = row.durationMillis,
                        hourSymbol = strings.hourShort,
                        minuteSymbol = strings.minuteShort,
                    ),
                    maxLines = 1,
                    style = MaterialTheme.typography.titleMedium,
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                LinearProgressIndicator(
                    progress = {
                        (row.durationMillis.toDouble() / maximumDuration).toFloat().coerceIn(0f, 1f)
                    },
                    modifier = Modifier.weight(1f).height(4.dp).clip(CircleShape),
                    color = categoryColor,
                    trackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                )
                Text(
                    text = formatter.formatPercent(value = row.share, locale = locale),
                    maxLines = 1,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun AnalyticsCategoriesExpandButton(
    isExpanded: Boolean,
    onToggle: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp),
        shape = RoundedCornerShape(
            bottomStart = 16.dp,
            bottomEnd = 16.dp,
        ),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clickable(
                    role = Role.Button,
                    onClick = onToggle,
                )
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = if (isExpanded) {
                    AnalyticsThemeRes.strings.collapse
                } else {
                    AnalyticsThemeRes.strings.showAllCategories
                },
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                painter = painterResource(
                    if (isExpanded) {
                        TimePlannerRes.icons.arrowUp
                    } else {
                        TimePlannerRes.icons.arrowDown
                    },
                ),
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

private fun categoryRowShape(
    index: Int,
    rowCount: Int,
    isLastRow: Boolean,
    hasExpandAction: Boolean,
) = when {
    rowCount == 1 && !hasExpandAction -> RoundedCornerShape(16.dp)
    index == 0 -> RoundedCornerShape(
        topStart = 16.dp,
        topEnd = 16.dp,
    )
    isLastRow -> RoundedCornerShape(
        bottomStart = 16.dp,
        bottomEnd = 16.dp,
    )
    else -> RoundedCornerShape(0.dp)
}
