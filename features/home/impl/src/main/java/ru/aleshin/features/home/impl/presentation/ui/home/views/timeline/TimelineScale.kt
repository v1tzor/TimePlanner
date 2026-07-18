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
package ru.aleshin.features.home.impl.presentation.ui.home.views.timeline

import ru.aleshin.core.utils.functional.TimeRange
import java.util.Date

/**
 * @author Stanislav Aleshin on 17.07.2026.
 */
internal class TimelineScale(
    val dayTimeRange: TimeRange,
    val height: Float,
    private val segments: List<TimelineScaleSegment>,
) {

    fun fetchOffset(time: Date): Float {
        val targetTime = time.time.coerceIn(dayTimeRange.from.time, dayTimeRange.to.time)
        val segment = segments.firstOrNull { scaleSegment ->
            targetTime <= scaleSegment.timeRange.to.time
        } ?: return height
        val duration = segment.timeRange.to.time - segment.timeRange.from.time
        if (duration <= 0L) return segment.top

        val progress = (targetTime - segment.timeRange.from.time) / duration.toFloat()
        return segment.top + (segment.bottom - segment.top) * progress.coerceIn(0f, 1f)
    }

    fun fetchTime(offset: Float): Date {
        val targetOffset = offset.coerceIn(0f, height)
        val segment = segments.firstOrNull { scaleSegment ->
            targetOffset <= scaleSegment.bottom
        } ?: return dayTimeRange.to
        val segmentHeight = segment.bottom - segment.top
        if (segmentHeight <= 0f) return segment.timeRange.from

        val progress = (targetOffset - segment.top) / segmentHeight
        val duration = segment.timeRange.to.time - segment.timeRange.from.time
        return Date(
            segment.timeRange.from.time + (duration * progress.coerceIn(0f, 1f)).toLong(),
        )
    }
}
