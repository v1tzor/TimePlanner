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
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.layout.Box
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.unit.ColorProvider
import ru.aleshin.timeplanner.widgets.presentation.theme.compatCornerBackground
import ru.aleshin.timeplanner.widgets.presentation.theme.tokens.WidgetDimensions
import ru.aleshin.timeplanner.widgets.presentation.theme.tokens.WidgetShapes
import kotlin.math.ceil

/**
 * @author Stanislav Aleshin on 28.07.2026.
 */
@Composable
fun WidgetProgressBar(
    modifier: GlanceModifier = GlanceModifier,
    progress: Float,
    activeColor: ColorProvider = GlanceTheme.colors.primary,
    inactiveColor: ColorProvider = GlanceTheme.colors.surfaceVariant,
    segments: Int = DEFAULT_SEGMENTS,
    compact: Boolean = false,
) {
    val activeSegments = ceil(progress.coerceIn(0f, 1f) * segments).toInt()

    val height = if (compact) {
        WidgetDimensions.progressHeightCompact
    } else {
        WidgetDimensions.progressHeight
    }
    val horizontalPadding = if (compact) {
        WidgetDimensions.spacingHairline
    } else {
        WidgetDimensions.segmentPadding
    }

    Row(modifier.fillMaxWidth()) {
        repeat(segments) { index ->
            Box(
                modifier = GlanceModifier
                    .defaultWeight()
                    .height(height)
                    .padding(horizontal = horizontalPadding),
            ) {
                Box(
                    modifier = GlanceModifier
                        .fillMaxSize()
                        .compatCornerBackground(
                            color = if (index < activeSegments) activeColor else inactiveColor,
                            cornerRadius = WidgetShapes.full,
                        ),
                    content = {},
                )
            }
        }
    }
}

private const val DEFAULT_SEGMENTS = 8
