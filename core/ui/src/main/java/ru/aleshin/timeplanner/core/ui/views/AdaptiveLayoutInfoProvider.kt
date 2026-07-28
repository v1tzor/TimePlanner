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

package ru.aleshin.timeplanner.core.ui.views

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.toSize

/**
 * @author Stanislav Aleshin on 26.07.2026.
 */
@Composable
@OptIn(ExperimentalMaterial3AdaptiveApi::class)
fun rememberAdaptiveLayoutInfo(): AdaptiveLayoutInfo {
    val adaptiveInfo = currentWindowAdaptiveInfo(supportLargeAndXLargeWidth = true)
    val density = LocalDensity.current
    val windowInfo = LocalWindowInfo.current
    val windowSize = with(density) {
        windowInfo.containerSize.toSize().toDpSize()
    }
    return remember(
        adaptiveInfo.windowSizeClass,
        adaptiveInfo.windowPosture,
        windowSize,
    ) {
        AdaptiveLayoutInfo(
            windowSizeClass = adaptiveInfo.windowSizeClass,
            windowSize = windowSize,
            posture = adaptiveInfo.windowPosture,
        )
    }
}
