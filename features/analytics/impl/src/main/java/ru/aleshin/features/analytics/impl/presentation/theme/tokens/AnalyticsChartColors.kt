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
package ru.aleshin.features.analytics.impl.presentation.theme.tokens

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/**
 * @author Stanislav Aleshin on 21.07.2026.
 */
@Immutable
internal data class AnalyticsChartColors(
    val heatmapLevels: List<Color>,
)

internal fun ColorScheme.fetchAnalyticsChartColors() = AnalyticsChartColors(
    heatmapLevels = listOf(
        surfaceContainerHighest,
        primaryContainer.copy(alpha = 0.55f),
        primaryContainer,
        primary.copy(alpha = 0.72f),
        primary,
    ),
)

internal fun ColorScheme.fetchCategoryHeatmapLevels(categoryColor: Color) = listOf(
    surfaceContainerHighest,
    categoryColor.copy(alpha = 0.25f),
    categoryColor.copy(alpha = 0.45f),
    categoryColor.copy(alpha = 0.70f),
    categoryColor,
)

internal fun ColorScheme.fetchCategoryChartPalette(categoryColor: Color) = listOf(
    categoryColor,
    primary,
    tertiary,
    secondary,
    onSurfaceVariant,
)
