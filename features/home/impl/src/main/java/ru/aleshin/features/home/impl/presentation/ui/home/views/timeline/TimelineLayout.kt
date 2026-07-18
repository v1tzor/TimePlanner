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

import ru.aleshin.core.utils.functional.Constants
import ru.aleshin.core.utils.functional.TimeRange
import ru.aleshin.features.home.impl.presentation.models.TimelineTimeTaskUi
import java.util.Date
import kotlin.math.abs
import kotlin.math.max

/**
 * @author Stanislav Aleshin on 17.07.2026.
 */
internal object TimelineLayout {

    fun calculate(
        dayTimeRange: TimeRange,
        timeTasks: List<TimelineTimeTaskUi>,
        hourHeight: Float,
        minimumTaskHeight: Float,
        maximumTaskHeight: Float,
        longTaskThreshold: Long,
        longTaskScale: Float,
        minimumFreeTimeHeight: Float,
        freeTimeScale: Float,
        taskSpace: Float,
        verticalPadding: Float,
    ): TimelineLayoutResult {
        val scaleSegments = mutableListOf<TimelineScaleSegment>()
        val taskPositions = mutableListOf<TimelineTaskPosition>()
        var currentTime = dayTimeRange.from.time
        var currentOffset = verticalPadding

        timeTasks.sortedBy { timeTask -> timeTask.visibleTimeRange.from }.forEach { timeTask ->
            val taskStart = max(currentTime, timeTask.visibleTimeRange.from.time)
            if (taskStart > currentTime) {
                currentOffset = addScaleSegment(
                    segments = scaleSegments,
                    timeRange = TimeRange(Date(currentTime), Date(taskStart)),
                    currentOffset = currentOffset,
                    height = calculateFreeTimeHeight(
                        duration = taskStart - currentTime,
                        hourHeight = hourHeight,
                        minimumHeight = minimumFreeTimeHeight,
                        scale = freeTimeScale,
                    ),
                )
            }

            val taskEnd = timeTask.visibleTimeRange.to.time
            if (taskEnd > taskStart) {
                val taskHeight = calculateTaskHeight(
                    duration = taskEnd - taskStart,
                    hourHeight = hourHeight,
                    minimumHeight = minimumTaskHeight,
                    maximumHeight = maximumTaskHeight,
                    longTaskThreshold = longTaskThreshold,
                    longTaskScale = longTaskScale,
                )
                scaleSegments.add(
                    TimelineScaleSegment(
                        timeRange = TimeRange(Date(taskStart), Date(taskEnd)),
                        top = currentOffset,
                        bottom = currentOffset + taskHeight,
                    ),
                )
                taskPositions.add(
                    TimelineTaskPosition(
                        timeTaskId = timeTask.timeTask.key,
                        top = currentOffset + taskSpace / 2f,
                        height = (taskHeight - taskSpace).coerceAtLeast(0f),
                    ),
                )
                currentOffset += taskHeight
                currentTime = taskEnd
            }
        }
        if (currentTime < dayTimeRange.to.time) {
            currentOffset = addScaleSegment(
                segments = scaleSegments,
                timeRange = TimeRange(Date(currentTime), dayTimeRange.to),
                currentOffset = currentOffset,
                height = calculateFreeTimeHeight(
                    duration = dayTimeRange.to.time - currentTime,
                    hourHeight = hourHeight,
                    minimumHeight = minimumFreeTimeHeight,
                    scale = freeTimeScale,
                ),
            )
        }
        val timelineHeight = currentOffset + verticalPadding

        return TimelineLayoutResult(
            scale = TimelineScale(
                dayTimeRange = dayTimeRange,
                height = timelineHeight,
                segments = scaleSegments,
            ),
            taskPositions = taskPositions,
        )
    }

    fun fetchVisibleHourTimes(
        hourTimes: List<Date>,
        boundaryOffsets: List<Float>,
        scale: TimelineScale,
        minimumDistance: Float,
    ): List<Date> {
        val visibleOffsets = mutableListOf<Float>()

        return hourTimes.filter { hourTime ->
            val hourOffset = scale.fetchOffset(hourTime)
            val hasBoundarySpace = boundaryOffsets.all { boundaryOffset ->
                abs(hourOffset - boundaryOffset) >= minimumDistance
            }
            val hasLabelSpace = visibleOffsets.all { visibleOffset ->
                abs(hourOffset - visibleOffset) >= minimumDistance
            }
            (hasBoundarySpace && hasLabelSpace).also { isVisible ->
                if (isVisible) visibleOffsets.add(hourOffset)
            }
        }
    }

    private fun addScaleSegment(
        segments: MutableList<TimelineScaleSegment>,
        timeRange: TimeRange,
        currentOffset: Float,
        height: Float,
    ): Float {
        segments.add(
            TimelineScaleSegment(
                timeRange = timeRange,
                top = currentOffset,
                bottom = currentOffset + height,
            ),
        )
        return currentOffset + height
    }

    private fun calculateTaskHeight(
        duration: Long,
        hourHeight: Float,
        minimumHeight: Float,
        maximumHeight: Float,
        longTaskThreshold: Long,
        longTaskScale: Float,
    ): Float {
        val proportionalHeight = if (duration <= longTaskThreshold) {
            duration.toFloat() / Constants.Date.MILLIS_IN_HOUR * hourHeight
        } else {
            val thresholdHeight = longTaskThreshold.toFloat() /
                Constants.Date.MILLIS_IN_HOUR * hourHeight
            val compressedDuration = duration - longTaskThreshold
            thresholdHeight + compressedDuration.toFloat() /
                Constants.Date.MILLIS_IN_HOUR * hourHeight * longTaskScale
        }
        return proportionalHeight.coerceIn(minimumHeight, maximumHeight)
    }

    private fun calculateFreeTimeHeight(
        duration: Long,
        hourHeight: Float,
        minimumHeight: Float,
        scale: Float,
    ): Float {
        val proportionalHeight = duration.toFloat() / Constants.Date.MILLIS_IN_HOUR * hourHeight * scale
        return proportionalHeight.coerceAtLeast(minimumHeight)
    }
}
