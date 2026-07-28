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

import android.os.Build
import androidx.compose.runtime.Composable
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalSize
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.unit.ColorProvider
import ru.aleshin.timeplanner.widgets.presentation.theme.compatCornerBackground
import ru.aleshin.timeplanner.widgets.presentation.theme.tokens.WidgetDimensions
import ru.aleshin.timeplanner.widgets.presentation.theme.tokens.WidgetShapes
import ru.aleshin.timeplanner.widgets.presentation.utils.WidgetSizeClass

/**
 * @author Stanislav Aleshin on 28.07.2026.
 */
@Composable
fun WidgetScaffold(
    modifier: GlanceModifier = GlanceModifier,
    backgroundColor: ColorProvider = GlanceTheme.colors.background,
    header: @Composable (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val sizeClass = WidgetSizeClass.fetch(LocalSize.current)
    val contentPadding = when (sizeClass.width) {
        WidgetSizeClass.Width.COMPACT -> WidgetDimensions.contentPaddingCompact
        WidgetSizeClass.Width.MEDIUM -> WidgetDimensions.contentPadding
        WidgetSizeClass.Width.EXPANDED -> WidgetDimensions.contentPaddingExpanded
    }
    Box(
        modifier = modifier
            .fillMaxSize()
            .applyWidgetBackground(backgroundColor)
            .appWidgetBackground(),
    ) {
        Column(GlanceModifier.fillMaxSize()) {
            header?.invoke()
            Box(
                modifier = GlanceModifier
                    .padding(horizontal = contentPadding)
                    .padding(bottom = contentPadding)
                    .defaultWeight(),
                content = content,
            )
        }
    }
}

@Composable
private fun GlanceModifier.applyWidgetBackground(
    color: ColorProvider,
): GlanceModifier {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        cornerRadius(android.R.dimen.system_app_widget_background_radius).background(color)
    } else {
        compatCornerBackground(color, WidgetShapes.extraLarge)
    }
}
