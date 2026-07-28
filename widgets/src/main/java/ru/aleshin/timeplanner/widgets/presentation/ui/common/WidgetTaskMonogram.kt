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
package ru.aleshin.timeplanner.widgets.presentation.ui.common

import androidx.compose.runtime.Composable
import androidx.glance.ColorFilter
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.size
import androidx.glance.text.Text
import ru.aleshin.core.domain.entities.categories.DefaultCategoryType
import ru.aleshin.core.presentation.mappers.mapToIcon
import ru.aleshin.timeplanner.core.ui.theme.TimePlannerRes
import ru.aleshin.timeplanner.widgets.presentation.theme.compatCornerBackground
import ru.aleshin.timeplanner.widgets.presentation.theme.tokens.WidgetDimensions
import ru.aleshin.timeplanner.widgets.presentation.theme.tokens.WidgetShapes
import ru.aleshin.timeplanner.widgets.presentation.theme.widgetTypography

/**
 * @author Stanislav Aleshin on 28.07.2026.
 */
@Composable
fun WidgetTaskMonogram(
    modifier: GlanceModifier = GlanceModifier,
    title: String,
    categoryType: DefaultCategoryType?,
    compact: Boolean = false,
) {
    val monogramSize = if (compact) {
        WidgetDimensions.monogramCompact
    } else {
        WidgetDimensions.monogram
    }
    val iconSize = if (compact) {
        WidgetDimensions.iconExtraSmall
    } else {
        WidgetDimensions.iconMedium
    }
    Box(
        modifier = modifier
            .size(monogramSize)
            .compatCornerBackground(GlanceTheme.colors.primaryContainer, WidgetShapes.full),
        contentAlignment = Alignment.Center,
    ) {
        if (categoryType != null) {
            Image(
                modifier = GlanceModifier.size(iconSize),
                provider = ImageProvider(categoryType.mapToIcon(TimePlannerRes.icons)),
                contentDescription = title,
                colorFilter = ColorFilter.tint(GlanceTheme.colors.primary),
            )
        } else {
            Text(
                text = title.firstOrNull()?.uppercase() ?: "•",
                style = if (compact) {
                    GlanceTheme.widgetTypography().caption.copy(
                        color = GlanceTheme.colors.primary,
                    )
                } else {
                    GlanceTheme.widgetTypography().label.copy(
                        color = GlanceTheme.colors.primary,
                    )
                },
            )
        }
    }
}
