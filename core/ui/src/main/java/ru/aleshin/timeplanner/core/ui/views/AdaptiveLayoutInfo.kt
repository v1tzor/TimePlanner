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

import androidx.compose.material3.adaptive.Posture
import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.DpSize
import androidx.window.core.layout.WindowSizeClass

/**
 * @author Stanislav Aleshin on 26.07.2026.
 */
@Immutable
data class AdaptiveLayoutInfo(
    val windowSizeClass: WindowSizeClass,
    val windowSize: DpSize,
    val posture: Posture,
) {

    val isCompactWidth: Boolean
        get() = !windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)

    val isMediumWidth: Boolean
        get() = windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND) &&
            !windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_EXPANDED_LOWER_BOUND)

    val isExpandedWidth: Boolean
        get() = windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_EXPANDED_LOWER_BOUND) &&
            !windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_LARGE_LOWER_BOUND)

    val isLargeWidth: Boolean
        get() = windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_LARGE_LOWER_BOUND) &&
            !windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_EXTRA_LARGE_LOWER_BOUND)

    val isExtraLargeWidth: Boolean
        get() = windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_EXTRA_LARGE_LOWER_BOUND)

    val isCompactHeight: Boolean
        get() = !windowSizeClass.isHeightAtLeastBreakpoint(WindowSizeClass.HEIGHT_DP_MEDIUM_LOWER_BOUND)

    val isMediumHeight: Boolean
        get() = windowSizeClass.isHeightAtLeastBreakpoint(WindowSizeClass.HEIGHT_DP_MEDIUM_LOWER_BOUND) &&
            !windowSizeClass.isHeightAtLeastBreakpoint(WindowSizeClass.HEIGHT_DP_EXPANDED_LOWER_BOUND)

    val isExpandedHeight: Boolean
        get() = windowSizeClass.isHeightAtLeastBreakpoint(WindowSizeClass.HEIGHT_DP_EXPANDED_LOWER_BOUND)

    val isBookPosture: Boolean
        get() = posture.hingeList.any { hinge ->
            hinge.isVertical && (hinge.isSeparating || hinge.isOccluding)
        }

    val isTabletopPosture: Boolean
        get() = posture.isTabletop

    val useNavigationRail: Boolean
        get() = !isCompactWidth && !isCompactHeight && !isTabletopPosture

    val useExpandedLayout: Boolean
        get() = (isExpandedWidth || isLargeWidth || isExtraLargeWidth) &&
            !isCompactHeight &&
            !isTabletopPosture

    val useLargeLayout: Boolean
        get() = (isLargeWidth || isExtraLargeWidth) &&
                !isCompactHeight &&
                !isTabletopPosture

    companion object {
        const val WIDTH_DP_EXPANDED_LOWER_BOUND = WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND
        const val WIDTH_DP_LARGE_LOWER_BOUND = 1200
        const val WIDTH_DP_EXTRA_LARGE_LOWER_BOUND = 1600
    }
}
