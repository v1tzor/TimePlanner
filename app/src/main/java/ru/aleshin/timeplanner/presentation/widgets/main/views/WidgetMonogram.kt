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
package ru.aleshin.timeplanner.presentation.widgets.main.views

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import androidx.glance.ColorFilter
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.Text
import androidx.glance.unit.ColorProvider
import ru.aleshin.core.ui.theme.material.badgePriorityMax
import ru.aleshin.core.ui.theme.material.badgePriorityMedium
import ru.aleshin.core.ui.views.MonogramPriority
import ru.aleshin.timeplanner.presentation.widgets.compatCornerBackground
import ru.aleshin.timeplanner.presentation.widgets.typography

/**
 * @author Stanislav Aleshin on 28.04.2024.
 */
@Composable
fun CategoryWidgetIconMonogram(
    modifier: GlanceModifier = GlanceModifier,
    icon: ImageProvider,
    iconDescription: String?,
    iconColor: ColorProvider,
    priority: MonogramPriority = MonogramPriority.STANDARD,
    backgroundColor: ColorProvider,
) = Box(
    modifier = modifier.size(28.dp),
    contentAlignment = Alignment.Center,
) {
    Box(
        modifier = GlanceModifier.fillMaxSize().compatCornerBackground(backgroundColor, 100),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            modifier = GlanceModifier.size(18.dp),
            provider = icon,
            contentDescription = iconDescription,
            colorFilter = ColorFilter.tint(iconColor),
        )
    }
    if (priority != MonogramPriority.STANDARD) {
        Box(
            modifier = GlanceModifier.fillMaxSize(),
            contentAlignment = Alignment.TopEnd,
        ) {
            WidgetMonogramBadge(
                color = ColorProvider(
                    color = if (priority == MonogramPriority.MEDIUM) badgePriorityMedium else badgePriorityMax
                ),
            )
        }
    }
}

@Composable
fun CategoryWidgetTextMonogram(
    modifier: GlanceModifier = GlanceModifier,
    text: String,
    textColor: ColorProvider,
    backgroundColor: ColorProvider,
    priority: MonogramPriority = MonogramPriority.STANDARD,
) = Box(
    modifier = modifier.size(40.dp),
    contentAlignment = Alignment.Center,
) {
    Box(
        modifier = GlanceModifier.fillMaxSize().compatCornerBackground(backgroundColor, 100),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text.toUpperCase(Locale.current),
            style = GlanceTheme.typography().titleMedium.copy(
                color = textColor,
            ),
        )
    }
    if (priority != MonogramPriority.STANDARD) {
        Box(
            modifier = GlanceModifier.fillMaxSize(),
            contentAlignment = Alignment.TopEnd,
        ) {
            WidgetMonogramBadge(
                color = ColorProvider(
                    color = if (priority == MonogramPriority.MEDIUM) badgePriorityMedium else badgePriorityMax
                ),
            )
        }
    }
}

@Composable
fun WidgetMonogramBadge(
    modifier: GlanceModifier = GlanceModifier,
    color: ColorProvider = ColorProvider(badgePriorityMax),
) {
    Box(
        modifier = modifier
            .padding(vertical = 4.dp, horizontal = 0.dp)
            .size(8.dp)
            .compatCornerBackground(color, 100),
        content = {},
    )
}