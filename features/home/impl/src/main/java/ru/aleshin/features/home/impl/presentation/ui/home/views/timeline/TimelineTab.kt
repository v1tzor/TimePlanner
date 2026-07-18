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

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import ru.aleshin.core.presentation.models.tasks.TimeTaskUi
import ru.aleshin.core.utils.functional.TimeRange
import ru.aleshin.features.home.impl.presentation.models.TimelineScheduleUi
import ru.aleshin.features.home.impl.presentation.theme.HomeThemeRes
import java.util.Date
import kotlin.math.roundToInt

/**
 * @author Stanislav Aleshin on 17.07.2026.
 */
@Composable
internal fun TimelineTab(
    modifier: Modifier = Modifier,
    schedule: TimelineScheduleUi,
    currentTime: Date?,
    onTimeTaskEdit: (Long) -> Unit,
    onTaskDoneChange: (TimeTaskUi) -> Unit,
    onTimeTaskAdd: (Date, Date) -> Unit,
    onTimeTaskUpdate: (Long, TimeRange) -> Unit,
    onAddClick: () -> Unit,
) {
    val scrollState: ScrollState = rememberScrollState()
    val gestureState = remember(schedule.date) { TimelineGestureState() }
    var viewportHeight by remember { mutableIntStateOf(0) }
    var initialTimeOffset by remember(schedule.date) { mutableFloatStateOf(Float.NaN) }
    var positionedDate by rememberSaveable { mutableStateOf<Long?>(null) }

    LaunchedEffect(schedule.date, initialTimeOffset, viewportHeight, scrollState.maxValue) {
        if (positionedDate != schedule.date.time && !initialTimeOffset.isNaN() && viewportHeight > 0) {
            positionedDate = schedule.date.time
            val targetOffset = (initialTimeOffset - viewportHeight / 2f).roundToInt().coerceIn(0, scrollState.maxValue)
            scrollState.animateScrollTo(
                value = targetOffset,
                animationSpec = tween(durationMillis = INITIAL_SCROLL_DURATION, easing = FastOutSlowInEasing)
            )
        }
    }

    Box(modifier = modifier.fillMaxSize().onSizeChanged { size -> viewportHeight = size.height }) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
        ) {
            TimelineGrid(
                schedule = schedule,
                currentTime = currentTime,
                scrollState = scrollState,
                viewportHeight = viewportHeight,
                gestureState = gestureState,
                onTimeTaskEdit = onTimeTaskEdit,
                onTaskDoneChange = onTaskDoneChange,
                onTimeTaskAdd = onTimeTaskAdd,
                onTimeTaskUpdate = onTimeTaskUpdate,
                onInitialTimePositioned = { offset ->
                    if (initialTimeOffset.isNaN()) initialTimeOffset = offset
                },
            )
        }
        FloatingActionButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 16.dp),
            onClick = onAddClick,
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = HomeThemeRes.strings.addTaskTitle,
            )
        }
    }
}

private const val INITIAL_SCROLL_DURATION = 500
