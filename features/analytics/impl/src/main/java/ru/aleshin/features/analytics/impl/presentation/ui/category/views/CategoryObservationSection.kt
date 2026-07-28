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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ru.aleshin.features.analytics.impl.domain.entities.CategoryObservationType
import ru.aleshin.features.analytics.impl.presentation.models.category.CategoryObservationUi
import ru.aleshin.features.analytics.impl.presentation.theme.AnalyticsThemeRes
import ru.aleshin.features.analytics.impl.presentation.theme.tokens.AnalyticsStrings
import ru.aleshin.features.analytics.impl.presentation.utils.AnalyticsValueFormatter
import ru.aleshin.features.analytics.impl.presentation.utils.fetchAnalyticsLocale
import ru.aleshin.features.analytics.impl.presentation.utils.formatAnalyticsCivilDate
import ru.aleshin.features.analytics.impl.presentation.utils.rememberAnalyticsValueFormatter
import ru.aleshin.timeplanner.core.ui.theme.TimePlannerRes
import java.util.Locale

/**
 * @author Stanislav Aleshin on 23.07.2026.
 */
@Composable
internal fun CategoryObservationSection(
    modifier: Modifier = Modifier,
    observation: CategoryObservationUi,
    categoryColor: Color,
) {
    val strings = AnalyticsThemeRes.strings
    val language = TimePlannerRes.language
    val locale = remember(language) { language.fetchAnalyticsLocale() }
    val formatter = rememberAnalyticsValueFormatter()
    val text = observation.formatText(
        formatter = formatter,
        locale = locale,
        strings = strings,
    )

    CategorySection(
        title = strings.observationTitle,
        modifier = modifier,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = categoryColor.copy(alpha = 0.16f),
                        shape = MaterialTheme.shapes.medium,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = categoryColor,
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = text,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

private fun CategoryObservationUi.formatText(
    formatter: AnalyticsValueFormatter,
    locale: Locale,
    strings: AnalyticsStrings,
) = when (type) {
    CategoryObservationType.COMPLETION_DROP -> {
        strings.completionDropObservationFormat.format(
            formatter.formatPercent(
                value = valuePercent,
                locale = locale,
            )
        )
    }
    CategoryObservationType.BUSIEST_DAY -> {
        strings.busiestDayObservationFormat.format(
            day?.formatAnalyticsCivilDate(
                pattern = DAY_PATTERN,
                locale = locale,
            ).orEmpty(),
            formatter.formatPercent(
                value = valuePercent,
                locale = locale,
            ),
        )
    }
    CategoryObservationType.DOMINANT_SUBCATEGORY -> {
        strings.dominantSubCategoryObservationFormat.format(
            subCategory?.name ?: strings.withoutSubCategory,
            formatter.formatPercent(
                value = valuePercent,
                locale = locale,
            ),
        )
    }
}

private const val DAY_PATTERN = "EEEE"
