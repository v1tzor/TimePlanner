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
package ru.aleshin.features.editor.impl.presentation.ui.task.views.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.aleshin.core.utils.extensions.changeDay
import ru.aleshin.core.utils.extensions.fetchHourOfDay
import ru.aleshin.core.utils.extensions.shiftDay
import ru.aleshin.core.utils.extensions.shiftMillis
import ru.aleshin.core.utils.functional.TimeRange
import ru.aleshin.features.editor.impl.presentation.ui.task.views.DurationTitle
import ru.aleshin.features.editor.impl.presentation.ui.task.views.EndTimeField
import ru.aleshin.features.editor.impl.presentation.ui.task.views.StartTimeField
import ru.aleshin.features.editor.impl.presentation.ui.task.views.TimeRangeSlider
import java.util.Date

/**
 * @author Stanislav Aleshin on 30.07.2026.
 */
@Composable
internal fun TaskDateTimeSection(
    modifier: Modifier = Modifier,
    scheduleDate: Date,
    timeRange: TimeRange,
    duration: Long,
    durationPresets: List<Long>?,
    isEnabled: Boolean = true,
    isError: Boolean,
    onTimeRangeChange: (TimeRange) -> Unit,
    onDurationPresetsChange: (List<Long>) -> Unit,
) {
    Column(
        modifier = modifier.padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            StartTimeField(
                modifier = Modifier.weight(1f),
                enabled = isEnabled,
                currentTime = timeRange.from,
                isError = isError,
                onChangeTime = { startTime ->
                    val endDate = if (startTime.fetchHourOfDay() <= timeRange.to.fetchHourOfDay()) {
                        scheduleDate
                    } else {
                        scheduleDate.shiftDay(1)
                    }
                    val updatedTimeRange = timeRange.copy(
                        from = startTime,
                        to = timeRange.to.changeDay(endDate),
                    )
                    onTimeRangeChange(updatedTimeRange)
                },
            )
            EndTimeField(
                modifier = Modifier.weight(1f),
                enabled = isEnabled,
                currentTime = timeRange.to,
                isError = isError,
                onChangeTime = { endTime ->
                    val endDate = if (timeRange.from.fetchHourOfDay() <= endTime.fetchHourOfDay()) {
                        scheduleDate
                    } else {
                        scheduleDate.shiftDay(1)
                    }
                    val updatedTimeRange = timeRange.copy(to = endTime.changeDay(endDate))
                    onTimeRangeChange(updatedTimeRange)
                },
            )
            DurationTitle(
                enabled = isEnabled,
                duration = duration,
                startTime = timeRange.from,
                durationPresets = durationPresets,
                isError = isError,
                onChangeDuration = { selectedDuration ->
                    val endTime = timeRange.from.shiftMillis(selectedDuration.toInt())
                    onTimeRangeChange(timeRange.copy(to = endTime))
                },
                onDurationPresetsChange = onDurationPresetsChange,
            )
        }
        TimeRangeSlider(
            enabled = isEnabled,
            isError = isError,
            scheduleDate = scheduleDate,
            timeRange = timeRange,
            onTimeRangeChange = onTimeRangeChange,
        )
    }
}
