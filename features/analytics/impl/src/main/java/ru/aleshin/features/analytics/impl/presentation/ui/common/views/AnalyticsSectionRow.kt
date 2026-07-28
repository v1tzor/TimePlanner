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
package ru.aleshin.features.analytics.impl.presentation.ui.common.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import ru.aleshin.timeplanner.core.ui.theme.tokens.AdaptiveLayoutDefaults

/**
 * @author Stanislav Aleshin on 27.07.2026.
 */
@Composable
internal fun AnalyticsSectionRow(
    modifier: Modifier = Modifier,
    height: Dp? = null,
    content: @Composable RowScope.() -> Unit,
) {
    val fontScale = LocalDensity.current.fontScale
    val accessibilityScale = 1f + (fontScale - 1f).coerceAtLeast(0f) * 0.5f

    Row(
        modifier = modifier.then(
            other = if (height != null && height != Dp.Unspecified) {
                Modifier.height(height * accessibilityScale)
            } else {
                Modifier.height(IntrinsicSize.Min)
            }
        ),
        horizontalArrangement = Arrangement.spacedBy(AdaptiveLayoutDefaults.GridSpacing),
        content = content,
    )
}
