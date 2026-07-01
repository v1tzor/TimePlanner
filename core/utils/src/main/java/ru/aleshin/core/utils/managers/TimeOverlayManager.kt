/*
 * Copyright 2025 Stanislav Aleshin
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
package ru.aleshin.core.utils.managers

import ru.aleshin.core.utils.functional.TimeRange
import java.util.Date
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 26.03.2023.
 */
interface TimeOverlayManager {

    fun isOverlay(current: TimeRange, allTimeRanges: List<TimeRange>): TimeOverlayResult

    class Base @Inject constructor() : TimeOverlayManager {

        override fun isOverlay(
            current: TimeRange,
            allTimeRanges: List<TimeRange>,
        ): TimeOverlayResult {
            var leftBorder: Date? = null
            var rightBorder: Date? = null
            var startError = false
            var endError = false
            allTimeRanges.forEach { newRange ->
                val isOverlay = current.from < newRange.to && current.to > newRange.from
                if (!isOverlay) return@forEach

                if (newRange.to > current.from && newRange.to <= current.to && (leftBorder == null || newRange.to > leftBorder)) {
                    leftBorder = newRange.to
                    startError = true
                }
                if (newRange.from >= current.from && newRange.from < current.to && (rightBorder == null || newRange.from > rightBorder)) {
                    rightBorder = newRange.from
                    endError = true
                }
                if (current.from >= newRange.from && current.from < newRange.to) {
                    startError = true
                }
                if (current.to > newRange.from && current.to <= newRange.to) {
                    endError = true
                }
            }
            if (leftBorder != null && rightBorder != null) {
                if (leftBorder.time > rightBorder.time) {
                    rightBorder = current.to
                } else if (rightBorder.time > current.to.time) {
                    rightBorder = current.to
                }
            }
            return TimeOverlayResult(
                isOverlay = startError || endError,
                leftTimeBorder = leftBorder,
                rightTimeBorder = rightBorder,
            )
        }
    }
}

data class TimeOverlayResult(
    val isOverlay: Boolean,
    val leftTimeBorder: Date?,
    val rightTimeBorder: Date?,
)

data class TimeOverlayException(val startOverlay: Date?, val endOverlay: Date?) : Exception()
