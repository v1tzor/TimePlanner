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
package ru.aleshin.timeplanner.widgets.presentation.ui.summary.views

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.text.Text
import androidx.glance.unit.ColorProvider
import ru.aleshin.timeplanner.widgets.presentation.theme.tokens.WidgetDimensions
import ru.aleshin.timeplanner.widgets.presentation.theme.widgetTypography

/**
 * @author Stanislav Aleshin on 28.07.2026.
 */
@Composable
fun SummaryMetric(
    modifier: GlanceModifier,
    value: Int,
    title: String,
    color: ColorProvider,
    compact: Boolean,
    showTitle: Boolean,
    inline: Boolean = false,
) {
    val contentPadding = if (compact) 0.dp else WidgetDimensions.spacingExtraSmall
    if (inline) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = WidgetDimensions.spacingExtraSmall),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = title,
                maxLines = 1,
                style = GlanceTheme.widgetTypography().micro.copy(
                    color = GlanceTheme.colors.onSurfaceVariant,
                ),
            )
            Spacer(GlanceModifier.defaultWeight())
            MetricValue(
                value = value,
                color = color,
                compact = compact,
            )
        }
    } else {
        Column(
            modifier = modifier.padding(horizontal = contentPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            MetricValue(
                value = value,
                color = color,
                compact = compact,
            )
            if (showTitle) {
                Text(
                    text = title,
                    maxLines = 1,
                    style = GlanceTheme.widgetTypography().caption.copy(
                        color = GlanceTheme.colors.onSurfaceVariant,
                    ),
                )
            }
        }
    }
}

@Composable
private fun MetricValue(
    value: Int,
    color: ColorProvider,
    compact: Boolean,
) {
    Text(
        text = value.toString(),
        style = if (compact) {
            GlanceTheme.widgetTypography().label.copy(color = color)
        } else {
            GlanceTheme.widgetTypography().title.copy(color = color)
        },
    )
}
