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
import androidx.compose.ui.unit.dp
import androidx.glance.ColorFilter
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalSize
import androidx.glance.action.Action
import androidx.glance.action.clickable
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.Text
import ru.aleshin.timeplanner.core.ui.theme.TimePlannerRes
import ru.aleshin.timeplanner.widgets.presentation.theme.compatCornerBackground
import ru.aleshin.timeplanner.widgets.presentation.theme.tokens.WidgetDimensions
import ru.aleshin.timeplanner.widgets.presentation.theme.tokens.WidgetShapes
import ru.aleshin.timeplanner.widgets.presentation.theme.widgetTypography
import ru.aleshin.timeplanner.widgets.presentation.utils.WidgetSizeClass
import ru.aleshin.timeplanner.widgets.presentation.utils.WidgetSizeClass.Width

/**
 * @author Stanislav Aleshin on 28.07.2026.
 */
@Composable
fun WidgetHeader(
    modifier: GlanceModifier = GlanceModifier,
    title: String?,
    titleAction: Action?,
    actionIcon: ImageProvider?,
    actionDescription: String?,
    action: Action?,
    secondaryActionIcon: ImageProvider? = null,
    secondaryActionDescription: String? = null,
    secondaryAction: Action? = null,
) {
    val sizeClass = WidgetSizeClass.fetch(LocalSize.current)
    val isCompact = sizeClass.width == Width.COMPACT

    val showSecondaryAction = secondaryActionIcon != null && secondaryAction != null && (
        sizeClass.width == Width.EXPANDED || (title == null && sizeClass.width == Width.MEDIUM)
    )
    val horizontalPadding = if (isCompact) 0.dp else WidgetDimensions.spacingExtraSmall
    val iconSize = if (isCompact) WidgetDimensions.iconCompact else WidgetDimensions.icon

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(WidgetDimensions.headerHeight)
            .padding(horizontal = horizontalPadding),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = GlanceModifier
                .size(WidgetDimensions.touchTarget)
                .let { if (titleAction != null) it.clickable(titleAction) else it },
            contentAlignment = Alignment.Center,
        ) {
            Image(
                modifier = GlanceModifier.size(iconSize),
                provider = ImageProvider(TimePlannerRes.icons.logoCircular),
                contentDescription = null,
            )
        }
        if (title != null && !isCompact) {
            Text(
                modifier = GlanceModifier.let { if (titleAction != null) it.clickable(titleAction) else it },
                text = title,
                maxLines = 1,
                style = GlanceTheme.widgetTypography().title.copy(
                    color = GlanceTheme.colors.onBackground,
                ),
            )
        }
        Spacer(GlanceModifier.defaultWeight())
        if (showSecondaryAction) {
            Box(
                modifier = GlanceModifier
                    .size(WidgetDimensions.touchTarget)
                    .compatCornerBackground(GlanceTheme.colors.background, WidgetShapes.full)
                    .clickable(secondaryAction),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    modifier = GlanceModifier.size(iconSize),
                    provider = secondaryActionIcon,
                    contentDescription = secondaryActionDescription,
                    colorFilter = ColorFilter.tint(GlanceTheme.colors.onBackground),
                )
            }
        }
        if (actionIcon != null && action != null) {
            Box(
                modifier = GlanceModifier
                    .size(WidgetDimensions.touchTarget)
                    .compatCornerBackground(GlanceTheme.colors.background, WidgetShapes.full)
                    .clickable(action),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    modifier = GlanceModifier.size(iconSize),
                    provider = actionIcon,
                    contentDescription = actionDescription,
                    colorFilter = ColorFilter.tint(GlanceTheme.colors.onBackground),
                )
            }
        }
    }
}
