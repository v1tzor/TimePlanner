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
package ru.aleshin.timeplanner.widgets.presentation.ui.deadlines.views

import androidx.compose.runtime.Composable
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.Text
import androidx.glance.unit.ColorProvider
import ru.aleshin.timeplanner.widgets.R
import ru.aleshin.timeplanner.widgets.presentation.ui.deadlines.state.DeadlinesWidgetStateUi
import ru.aleshin.timeplanner.widgets.presentation.utils.WidgetSizeClass
import ru.aleshin.timeplanner.widgets.presentation.theme.compatCornerBackground
import ru.aleshin.timeplanner.widgets.presentation.theme.widgetString
import ru.aleshin.timeplanner.widgets.presentation.theme.tokens.WidgetDimensions
import ru.aleshin.timeplanner.widgets.presentation.theme.tokens.WidgetShapes
import ru.aleshin.timeplanner.widgets.presentation.theme.widgetTypography

/**
 * @author Stanislav Aleshin on 28.07.2026.
 */
@Composable
fun DeadlineCounters(
    state: DeadlinesWidgetStateUi,
    sizeClass: WidgetSizeClass,
) {
    val compact = sizeClass.width == WidgetSizeClass.Width.COMPACT ||
        sizeClass.height == WidgetSizeClass.Height.COMPACT
    val spacing = if (compact) {
        WidgetDimensions.spacingExtraSmall
    } else {
        WidgetDimensions.spacingSmall
    }
    Row(
        modifier = GlanceModifier
            .fillMaxWidth()
            .padding(bottom = spacing),
    ) {
        DeadlineCounter(
            modifier = GlanceModifier.defaultWeight(),
            value = state.overdueCount,
            title = widgetString(
                if (compact) R.string.widget_overdue_short else R.string.widget_overdue,
            ),
            color = GlanceTheme.colors.error,
            container = GlanceTheme.colors.errorContainer,
            compact = compact,
        )
        Spacer(GlanceModifier.width(spacing))
        DeadlineCounter(
            modifier = GlanceModifier.defaultWeight(),
            value = state.todayCount,
            title = widgetString(
                if (compact) R.string.widget_today_short else R.string.widget_today,
            ),
            color = GlanceTheme.colors.primary,
            container = GlanceTheme.colors.primaryContainer,
            compact = compact,
        )
        Spacer(GlanceModifier.width(spacing))
        DeadlineCounter(
            modifier = GlanceModifier.defaultWeight(),
            value = state.upcomingCount,
            title = widgetString(
                if (compact) R.string.widget_upcoming_short else R.string.widget_upcoming,
            ),
            color = GlanceTheme.colors.onSurfaceVariant,
            container = GlanceTheme.colors.surfaceVariant,
            compact = compact,
        )
    }
}

@Composable
private fun DeadlineCounter(
    modifier: GlanceModifier,
    value: Int,
    title: String,
    color: ColorProvider,
    container: ColorProvider,
    compact: Boolean,
) {
    val padding = if (compact) {
        WidgetDimensions.spacingExtraSmall
    } else {
        WidgetDimensions.spacingSmall
    }
    Column(
        modifier = modifier
            .compatCornerBackground(container, WidgetShapes.medium)
            .padding(padding),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = value.toString(),
            style = GlanceTheme.widgetTypography().title.copy(color = color),
        )
        Text(
            text = title,
            maxLines = 1,
            style = if (compact) {
                GlanceTheme.widgetTypography().micro.copy(color = color)
            } else {
                GlanceTheme.widgetTypography().caption.copy(color = color)
            },
        )
    }
}
