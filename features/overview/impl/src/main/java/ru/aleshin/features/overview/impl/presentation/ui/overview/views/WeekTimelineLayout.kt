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
package ru.aleshin.features.overview.impl.presentation.ui.overview.views

import ru.aleshin.core.presentation.models.tasks.TimeTaskUi
import ru.aleshin.core.utils.extensions.shiftDay
import ru.aleshin.core.utils.extensions.startThisDay
import ru.aleshin.features.overview.impl.presentation.models.overview.TimelineTaskPositionUi
import java.util.Date
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

/**
 * @author Stanislav Aleshin on 16.07.2026.
 */
internal object WeekTimelineLayout {

    fun calculateHeight(
        timeTasks: List<TimeTaskUi>,
        date: Date,
        minimumTimelineHeight: Float,
        minimumTaskHeight: Float,
        tasksSpace: Float,
    ): Float {
        val taskIntervals = timeTasks.fetchTaskIntervals(date)
        if (taskIntervals.isEmpty()) return minimumTimelineHeight
        if (taskIntervals.canPlaceTasks(minimumTimelineHeight, minimumTaskHeight, tasksSpace)) {
            return minimumTimelineHeight
        }

        var minimumHeight = minimumTimelineHeight
        var maximumHeight = minimumTimelineHeight * 2f
        var searchStep = 0

        while (
            !taskIntervals.canPlaceTasks(maximumHeight, minimumTaskHeight, tasksSpace) &&
            searchStep < HEIGHT_SEARCH_STEPS
        ) {
            minimumHeight = maximumHeight
            maximumHeight *= 2f
            searchStep++
        }
        repeat(HEIGHT_CALCULATION_STEPS) {
            val height = (minimumHeight + maximumHeight) / 2f
            if (taskIntervals.canPlaceTasks(height, minimumTaskHeight, tasksSpace)) {
                maximumHeight = height
            } else {
                minimumHeight = height
            }
        }

        return ceil(maximumHeight)
    }

    fun calculatePositions(
        timeTasks: List<TimeTaskUi>,
        date: Date,
        timelineHeight: Float,
        minimumTaskHeight: Float,
        tasksSpace: Float,
    ): List<TimelineTaskPositionUi> {
        if (timelineHeight <= 0f) return emptyList()

        val taskIntervals = timeTasks.fetchTaskIntervals(date)
        if (taskIntervals.isEmpty()) return emptyList()

        val taskHeights = FloatArray(taskIntervals.size) { index ->
            val interval = taskIntervals[index].second
            max(
                minimumTaskHeight,
                (interval.endInclusive - interval.start) * timelineHeight - tasksSpace,
            )
        }
        val taskOffsets = FloatArray(taskIntervals.size)
        val preferredOffsets = FloatArray(taskIntervals.size)
        var accumulatedHeight = 0f

        taskIntervals.forEachIndexed { index, (_, interval) ->
            val intervalHeight = (interval.endInclusive - interval.start) * timelineHeight
            val preferredTop = interval.start * timelineHeight + (intervalHeight - taskHeights[index]) / 2f

            taskOffsets[index] = accumulatedHeight
            preferredOffsets[index] = preferredTop - accumulatedHeight
            accumulatedHeight += taskHeights[index] + tasksSpace
        }

        val requiredHeight = accumulatedHeight - tasksSpace
        val maximumOffset = (timelineHeight - requiredHeight).coerceAtLeast(0f)
        val resolvedOffsets = preferredOffsets.resolveCollisions(
            minimumValue = 0f,
            maximumValue = maximumOffset,
        )

        return taskIntervals.mapIndexed { index, (task, _) ->
            TimelineTaskPositionUi(
                task = task,
                top = resolvedOffsets[index] + taskOffsets[index],
                height = min(taskHeights[index], timelineHeight),
            )
        }
    }

    private fun List<Pair<TimeTaskUi, ClosedFloatingPointRange<Float>>>.canPlaceTasks(
        timelineHeight: Float,
        minimumTaskHeight: Float,
        tasksSpace: Float,
    ): Boolean {
        val taskHeights = FloatArray(size) { index ->
            val interval = this[index].second
            max(
                minimumTaskHeight,
                (interval.endInclusive - interval.start) * timelineHeight - tasksSpace,
            )
        }
        val preferredOffsets = FloatArray(size)
        var accumulatedHeight = 0f

        forEachIndexed { index, (_, interval) ->
            val intervalHeight = (interval.endInclusive - interval.start) * timelineHeight
            val preferredTop = interval.start * timelineHeight + (intervalHeight - taskHeights[index]) / 2f

            preferredOffsets[index] = preferredTop - accumulatedHeight
            accumulatedHeight += taskHeights[index] + tasksSpace
        }

        val requiredHeight = accumulatedHeight - tasksSpace
        if (requiredHeight > timelineHeight) return false

        val resolvedOffsets = preferredOffsets.resolveCollisions(
            minimumValue = Float.NEGATIVE_INFINITY,
            maximumValue = Float.POSITIVE_INFINITY,
        )
        val maximumOffset = timelineHeight - requiredHeight
        val startsAtDayBeginning = first().second.start <= TIMELINE_BOUNDARY_TOLERANCE
        val endsAtDayEnding = last().second.endInclusive >= 1f - TIMELINE_BOUNDARY_TOLERANCE
        val fitsAtBeginning = startsAtDayBeginning || resolvedOffsets.first() >= 0f
        val fitsAtEnding = endsAtDayEnding || resolvedOffsets.last() <= maximumOffset

        return fitsAtBeginning && fitsAtEnding
    }

    private fun List<TimeTaskUi>.fetchTaskIntervals(
        date: Date,
    ): List<Pair<TimeTaskUi, ClosedFloatingPointRange<Float>>> {
        val dayStart = date.startThisDay()
        val dayEnd = dayStart.shiftDay(1)
        val dayDuration = dayEnd.time - dayStart.time

        return mapNotNull { task ->
            val taskStart = max(task.timeRanges.from.time, dayStart.time)
            val taskEnd = min(task.timeRanges.to.time, dayEnd.time)
            if (taskStart >= taskEnd) return@mapNotNull null

            val startFraction = (taskStart - dayStart.time) / dayDuration.toFloat()
            val endFraction = (taskEnd - dayStart.time) / dayDuration.toFloat()
            task to startFraction.coerceIn(0f, 1f)..endFraction.coerceIn(0f, 1f)
        }.sortedBy { (_, interval) -> interval.start }
    }

    private fun FloatArray.resolveCollisions(
        minimumValue: Float,
        maximumValue: Float,
    ): FloatArray {
        val blockValues = FloatArray(size)
        val blockSizes = IntArray(size)
        var blocksCount = 0

        forEach { preferredValue ->
            blockValues[blocksCount] = preferredValue
            blockSizes[blocksCount] = 1
            blocksCount++

            while (blocksCount > 1 && blockValues[blocksCount - 2] > blockValues[blocksCount - 1]) {
                val previousBlock = blocksCount - 2
                val currentBlock = blocksCount - 1
                val mergedSize = blockSizes[previousBlock] + blockSizes[currentBlock]

                blockValues[previousBlock] = (
                    blockValues[previousBlock] * blockSizes[previousBlock] +
                        blockValues[currentBlock] * blockSizes[currentBlock]
                    ) / mergedSize
                blockSizes[previousBlock] = mergedSize
                blocksCount--
            }
        }

        val resolvedValues = FloatArray(size)
        var resolvedIndex = 0

        repeat(blocksCount) { blockIndex ->
            val value = blockValues[blockIndex].coerceIn(minimumValue, maximumValue)
            repeat(blockSizes[blockIndex]) {
                resolvedValues[resolvedIndex++] = value
            }
        }

        return resolvedValues
    }
}

private const val HEIGHT_SEARCH_STEPS = 12
private const val HEIGHT_CALCULATION_STEPS = 16
private const val TIMELINE_BOUNDARY_TOLERANCE = 0.0001f
