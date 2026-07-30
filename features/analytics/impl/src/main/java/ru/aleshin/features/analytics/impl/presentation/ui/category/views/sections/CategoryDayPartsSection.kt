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
package ru.aleshin.features.analytics.impl.presentation.ui.category.views.sections

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import ru.aleshin.core.utils.charts.CategoryColorsDefaults
import ru.aleshin.features.analytics.impl.presentation.models.category.CategoryDayPartCellUi
import ru.aleshin.features.analytics.impl.presentation.models.category.CategoryDayPartSummaryUi
import ru.aleshin.features.analytics.impl.presentation.theme.AnalyticsThemeRes
import ru.aleshin.features.analytics.impl.presentation.ui.common.views.CategoryDayPartHeatmap
import ru.aleshin.features.analytics.impl.presentation.utils.fetchAnalyticsLocale
import ru.aleshin.timeplanner.core.ui.theme.TimePlannerRes

/**
 * @author Stanislav Aleshin on 23.07.2026.
 */
@Composable
internal fun CategoryDayPartsSection(
    modifier: Modifier = Modifier,
    categoryId: Long,
    cells: List<CategoryDayPartCellUi>,
    summaries: List<CategoryDayPartSummaryUi>,
) {
    CategorySection(
        modifier = modifier,
        title = AnalyticsThemeRes.strings.dayPartsTitle,
    ) {
        CategoryDayPartsContent(
            categoryId = categoryId,
            cells = cells,
            summaries = summaries,
        )
    }
}

@Composable
internal fun CategoryExpandedDayPartsSection(
    modifier: Modifier = Modifier,
    categoryId: Long,
    cells: List<CategoryDayPartCellUi>,
    summaries: List<CategoryDayPartSummaryUi>,
) {
    CategoryExpandedSection(
        modifier = modifier,
        title = AnalyticsThemeRes.strings.dayPartsTitle,
    ) {
        CategoryDayPartsContent(
            categoryId = categoryId,
            cells = cells,
            summaries = summaries,
        )
    }
}

@Composable
private fun CategoryDayPartsContent(
    categoryId: Long,
    cells: List<CategoryDayPartCellUi>,
    summaries: List<CategoryDayPartSummaryUi>,
) {
    val language = TimePlannerRes.language
    val locale = remember(language) { language.fetchAnalyticsLocale() }

    CategoryDayPartHeatmap(
        modifier = Modifier.fillMaxWidth(),
        cells = cells,
        summaries = summaries,
        categoryColor = CategoryColorsDefaults.fetchColor(categoryId = categoryId),
        locale = locale,
        strings = AnalyticsThemeRes.strings,
    )
}
