/*
 * Copyright 2023 Stanislav Aleshin
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
package ru.aleshin.timeplanner.presentation.widgets.common

import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.unit.ColorProvider
import ru.aleshin.timeplanner.presentation.widgets.compatCornerBackground

/**
 * @author Stanislav Aleshin on 14.11.2024.
 */
@Composable
fun CompatScaffold(
    modifier: GlanceModifier = GlanceModifier,
    titleBar: @Composable (() -> Unit)? = null,
    backgroundColor: ColorProvider = GlanceTheme.colors.widgetBackground,
    horizontalPadding: Dp = 12.dp,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .applyWidgetCornerBackground(backgroundColor)
            .appWidgetBackground()
    ) {
        Column(GlanceModifier.fillMaxSize()) {
            titleBar?.invoke()
            Box(
                modifier = GlanceModifier.padding(horizontal = horizontalPadding).defaultWeight(),
                content = content
            )
        }
    }
}

@Composable
fun GlanceModifier.applyWidgetCornerBackground(
    background: ColorProvider = GlanceTheme.colors.background,
) = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
    this.cornerRadius(android.R.dimen.system_app_widget_background_radius)
        .background(background)
} else {
    this.compatCornerBackground(background, 24)
}
