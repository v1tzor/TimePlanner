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

/**
 * @author Stanislav Aleshin on 27.07.2026.
 */
internal class PaneExpansionDragLimiter(
    private val firstPaneMinWidth: Float,
    private val secondPaneMinWidth: Float,
    private val firstPaneMaxWidth: Float = Float.POSITIVE_INFINITY,
    private val secondPaneMaxWidth: Float = Float.POSITIVE_INFINITY,
) {

    private var dividerOffset = Float.NaN
    private var scaffoldWidth = Float.NaN

    fun updateLayout(
        dividerOffset: Float,
        scaffoldWidth: Float,
    ) {
        this.dividerOffset = dividerOffset
        this.scaffoldWidth = scaffoldWidth
    }

    fun consumeDragDelta(delta: Float): Float {
        if (!canResize()) return 0f

        val minimumDividerOffset = maxOf(
            firstPaneMinWidth,
            scaffoldWidth - secondPaneMaxWidth,
        )
        val maximumDividerOffset = minOf(
            firstPaneMaxWidth,
            scaffoldWidth - secondPaneMinWidth,
        )
        if (minimumDividerOffset > maximumDividerOffset) return 0f

        val limitedOffset = (dividerOffset + delta).coerceIn(
            minimumValue = minimumDividerOffset,
            maximumValue = maximumDividerOffset,
        )
        val remainingDelta = limitedOffset - dividerOffset
        dividerOffset = limitedOffset
        return remainingDelta
    }

    private fun canResize(): Boolean {
        return dividerOffset.isFinite() &&
            scaffoldWidth.isFinite() &&
            scaffoldWidth >= firstPaneMinWidth + secondPaneMinWidth &&
            firstPaneMinWidth <= firstPaneMaxWidth &&
            secondPaneMinWidth <= secondPaneMaxWidth
    }
}
