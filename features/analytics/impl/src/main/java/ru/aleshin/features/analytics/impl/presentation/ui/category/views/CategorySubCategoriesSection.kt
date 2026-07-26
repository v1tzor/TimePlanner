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
package ru.aleshin.features.analytics.impl.presentation.ui.category.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ru.aleshin.core.utils.charts.CategoryColorsDefaults
import ru.aleshin.features.analytics.impl.presentation.models.analytics.AnalyticsDonutSliceUi
import ru.aleshin.features.analytics.impl.presentation.models.category.SubCategoryBucketUi
import ru.aleshin.features.analytics.impl.presentation.models.category.SubCategoryDistributionUi
import ru.aleshin.features.analytics.impl.presentation.theme.AnalyticsThemeRes
import ru.aleshin.features.analytics.impl.presentation.theme.tokens.AnalyticsStrings
import ru.aleshin.features.analytics.impl.presentation.theme.tokens.fetchCategoryChartPalette
import ru.aleshin.features.analytics.impl.presentation.ui.common.views.AnalyticsDonutCenterLabel
import ru.aleshin.features.analytics.impl.presentation.ui.common.views.AnalyticsDonutChart
import ru.aleshin.features.analytics.impl.presentation.utils.AnalyticsValueFormatter
import ru.aleshin.features.analytics.impl.presentation.utils.fetchAnalyticsLocale
import ru.aleshin.features.analytics.impl.presentation.utils.rememberAnalyticsValueFormatter
import ru.aleshin.timeplanner.core.ui.theme.TimePlannerRes
import java.util.Locale

/**
 * @author Stanislav Aleshin on 23.07.2026.
 */
@Composable
internal fun CategorySubCategoriesSection(
    categoryId: Long,
    categoryDurationMillis: Long,
    distribution: SubCategoryDistributionUi,
    selectedBucketKey: Long?,
    onSelect: (Long?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val strings = AnalyticsThemeRes.strings
    val language = TimePlannerRes.language
    val locale = remember(language) { language.fetchAnalyticsLocale() }
    val formatter = rememberAnalyticsValueFormatter()
    val categoryColor = CategoryColorsDefaults.fetchColor(categoryId = categoryId)
    val palette = MaterialTheme.colorScheme.fetchCategoryChartPalette(
        categoryColor = categoryColor,
    )

    CategorySection(
        title = strings.subCategoriesTitle,
        modifier = modifier,
    ) {
        if (distribution.isSingleUnassigned) {
            CategoryUnassignedSummary(
                durationMillis = categoryDurationMillis,
                formatter = formatter,
                strings = strings,
            )
        } else {
            val slices = remember(distribution, palette, strings) {
                distribution.buckets.mapIndexed { index, bucket ->
                    AnalyticsDonutSliceUi(
                        key = bucket.selectionKey(),
                        label = bucket.fetchLabel(strings = strings),
                        value = bucket.durationMillis.toFloat(),
                        color = palette[index % palette.size],
                        rawValue = bucket.durationMillis,
                    )
                }
            }
            CategorySubCategoryChart(
                categoryDurationMillis = categoryDurationMillis,
                distribution = distribution,
                slices = slices,
                palette = palette,
                selectedBucketKey = selectedBucketKey,
                locale = locale,
                formatter = formatter,
                strings = strings,
                onSelect = onSelect,
            )
        }
    }
}

@Composable
private fun CategoryUnassignedSummary(
    durationMillis: Long,
    formatter: AnalyticsValueFormatter,
    strings: AnalyticsStrings,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = strings.allWithoutSubCategory,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium,
        )
        Text(
            text = formatter.formatDuration(
                durationMillis = durationMillis,
                hourSymbol = strings.hourShort,
                minuteSymbol = strings.minuteShort,
            ),
            style = MaterialTheme.typography.titleMedium,
        )
    }
}

@Composable
private fun CategorySubCategoryChart(
    categoryDurationMillis: Long,
    distribution: SubCategoryDistributionUi,
    slices: List<AnalyticsDonutSliceUi>,
    palette: List<Color>,
    selectedBucketKey: Long?,
    locale: Locale,
    formatter: AnalyticsValueFormatter,
    strings: AnalyticsStrings,
    onSelect: (Long?) -> Unit,
) {
    val donutContent: @Composable () -> Unit = {
        Box(
            modifier = Modifier.size(136.dp),
            contentAlignment = Alignment.Center,
        ) {
            AnalyticsDonutChart(
                slices = slices,
                sliceDescription = { slice ->
                    "${slice.label}: ${
                        formatter.formatDuration(
                            durationMillis = checkNotNull(slice.rawValue),
                            hourSymbol = strings.hourShort,
                            minuteSymbol = strings.minuteShort,
                        )
                    }"
                },
                size = 136.dp,
                selectedKey = selectedBucketKey,
                onSelect = onSelect,
            )
            AnalyticsDonutCenterLabel(
                value = formatter.formatDuration(
                    durationMillis = categoryDurationMillis,
                    hourSymbol = strings.hourShort,
                    minuteSymbol = strings.minuteShort,
                ),
                label = strings.inCategory,
            )
        }
    }
    val legendContent: @Composable () -> Unit = {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            distribution.buckets.forEachIndexed { index, bucket ->
                CategorySubCategoryLegendItem(
                    bucket = bucket,
                    color = palette[index % palette.size],
                    isSelected = selectedBucketKey == bucket.selectionKey(),
                    locale = locale,
                    formatter = formatter,
                    strings = strings,
                    onClick = {
                        onSelect(bucket.selectionKey())
                    },
                )
            }
        }
    }
    val fontScale = LocalDensity.current.fontScale

    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        if (maxWidth < 280.dp || maxWidth < 400.dp && fontScale >= 1.75f) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                donutContent()
                legendContent()
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                donutContent()
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    legendContent()
                }
            }
        }
    }
}

@Composable
private fun CategorySubCategoryLegendItem(
    bucket: SubCategoryBucketUi,
    color: Color,
    isSelected: Boolean,
    locale: Locale,
    formatter: AnalyticsValueFormatter,
    strings: AnalyticsStrings,
    onClick: () -> Unit,
) {
    val label = bucket.fetchLabel(strings = strings)
    val share = formatter.formatPercent(
        value = bucket.share,
        locale = locale,
    )
    val duration = formatter.formatDuration(
        durationMillis = bucket.durationMillis,
        hourSymbol = strings.hourShort,
        minuteSymbol = strings.minuteShort,
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .clip(MaterialTheme.shapes.medium)
            .background(
                color = if (isSelected) {
                    MaterialTheme.colorScheme.surfaceContainer
                } else {
                    Color.Transparent
                },
            )
            .clickable(
                role = Role.Button,
                onClick = onClick,
            )
            .semantics {
                role = Role.Button
                contentDescription = "$label, $share, $duration"
            }
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(
                    color = color,
                    shape = CircleShape,
                ),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.labelMedium,
        )
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = share,
                style = MaterialTheme.typography.labelMedium,
            )
            Text(
                text = duration,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

private fun SubCategoryBucketUi.fetchLabel(
    strings: AnalyticsStrings,
) = when {
    subCategory != null -> subCategory.name.orEmpty()
    isOther -> strings.other
    else -> strings.withoutSubCategory
}

private fun SubCategoryBucketUi.selectionKey(): Long = when {
    isOther -> Long.MIN_VALUE
    subCategory == null -> Long.MIN_VALUE + 1L
    else -> subCategory.id
}
