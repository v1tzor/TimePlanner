/*
 * Copyright 2023 Stanislav Aleshin
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
package ru.aleshin.features.home.impl.presentation.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.* // ktlint-disable no-wildcard-imports
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import ru.aleshin.core.ui.theme.material.surfaceOne
import ru.aleshin.core.ui.views.ViewToggle
import ru.aleshin.core.ui.views.ViewToggleStatus
import ru.aleshin.core.utils.extensions.endThisDay
import ru.aleshin.core.utils.extensions.isNotZeroDifference
import ru.aleshin.core.utils.extensions.shiftDay
import ru.aleshin.features.home.api.domain.entities.schedules.DailyScheduleStatus
import ru.aleshin.features.home.api.domain.entities.schedules.TimeTaskStatus
import ru.aleshin.features.home.impl.presentation.models.schedules.TimeTaskUi
import ru.aleshin.features.home.impl.presentation.theme.HomeThemeRes
import ru.aleshin.features.home.impl.presentation.ui.home.contract.HomeViewState
import ru.aleshin.features.home.impl.presentation.ui.home.views.*
import ru.aleshin.features.home.impl.presentation.ui.home.views.CompletedTimeTaskItem
import ru.aleshin.features.home.impl.presentation.ui.home.views.DateChooser
import ru.aleshin.features.home.impl.presentation.ui.home.views.PlannedTimeTaskItem
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author Stanislav Aleshin on 18.02.2023.
 */
@Composable
internal fun HomeContent(
    state: HomeViewState,
    modifier: Modifier = Modifier,
    onChangeDate: (Date) -> Unit,
    onCreateSchedule: () -> Unit,
    onTimeTaskEdit: (TimeTaskUi) -> Unit,
    onTaskDoneChange: (TimeTaskUi) -> Unit,
    onTimeTaskAdd: (startTime: Date, endTime: Date) -> Unit,
    onTimeTaskIncrease: (TimeTaskUi) -> Unit,
    onTimeTaskReduce: (TimeTaskUi) -> Unit,
    onChangeToggleStatus: (ViewToggleStatus) -> Unit,
) {
    Column(modifier = modifier.fillMaxSize()) {
        HorizontalProgressBar(isLoading = state.isLoading)
        DateChooserSection(
            isEnabled = !state.isLoading,
            currentDate = state.currentDate,
            toggleState = state.taskViewStatus,
            onChangeDate = onChangeDate,
            onChangeToggleStatus = onChangeToggleStatus,
        )
        TimeTasksSection(
            isLoadingContent = state.isLoading,
            currentDate = state.currentDate,
            dateStatus = state.dateStatus,
            timeTasks = state.timeTasks,
            timeTaskViewStatus = state.taskViewStatus,
            onCreateSchedule = onCreateSchedule,
            onTimeTaskEdit = onTimeTaskEdit,
            onTaskDoneChange = onTaskDoneChange,
            onTimeTaskAdd = onTimeTaskAdd,
            onTimeTaskIncrease = onTimeTaskIncrease,
            onTimeTaskReduce = onTimeTaskReduce,
        )
    }
}

@Composable
internal fun DateChooserSection(
    modifier: Modifier = Modifier,
    isEnabled: Boolean,
    currentDate: Date?,
    toggleState: ViewToggleStatus,
    onChangeDate: (Date) -> Unit,
    onChangeToggleStatus: (ViewToggleStatus) -> Unit,
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(start = 16.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        HomeDateChooser(
            modifier = Modifier.width(202.dp),
            isEnabled = isEnabled,
            currentDate = currentDate,
            onChangeDate = onChangeDate,
        )
        Spacer(modifier = Modifier.weight(1f))
        ViewToggle(
            status = toggleState, 
            onStatusChange = onChangeToggleStatus,
        )
    }
}

@Composable
internal fun HomeDateChooser(
    modifier: Modifier = Modifier,
    isEnabled: Boolean,
    currentDate: Date?,
    onChangeDate: (Date) -> Unit,
) {
    val dateFormat = SimpleDateFormat("EEE, MMM d", Locale.getDefault())
    val isDateDialogShow = rememberSaveable { mutableStateOf(false) }

    DateChooser(
        modifier = modifier,
        enabled = isEnabled,
        dateTitle = currentDate?.let { dateFormat.format(it) } ?: "",
        onNext = { currentDate?.let { onChangeDate.invoke(it.shiftDay(amount = 1)) } },
        onPrevious = { currentDate?.let { onChangeDate.invoke(it.shiftDay(amount = -1)) } },
        onChooseDate = { isDateDialogShow.value = true },
    )

    HomeDatePicker(
        isOpenDialog = isDateDialogShow.value,
        onDismiss = { isDateDialogShow.value = false },
        onSelectedDate = {
            isDateDialogShow.value = false
            onChangeDate.invoke(it)
        },
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun TimeTasksSection(
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
    isLoadingContent: Boolean,
    dateStatus: DailyScheduleStatus?,
    currentDate: Date?,
    timeTasks: List<TimeTaskUi>,
    timeTaskViewStatus: ViewToggleStatus,
    onCreateSchedule: () -> Unit,
    onTimeTaskEdit: (TimeTaskUi) -> Unit,
    onTaskDoneChange: (TimeTaskUi) -> Unit,
    onTimeTaskAdd: (startTime: Date, endTime: Date) -> Unit,
    onTimeTaskIncrease: (TimeTaskUi) -> Unit,
    onTimeTaskReduce: (TimeTaskUi) -> Unit,
) = Box(modifier = modifier.fillMaxSize()) {
    if (dateStatus != null) {
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            userScrollEnabled = !isLoadingContent,
        ) {
            if (!isLoadingContent) {
                items(timeTasks, key = { it.key }) { timeTask ->
                    val isCompactView = timeTaskViewStatus == ViewToggleStatus.COMPACT
                    val timeTaskIndex = timeTasks.indexOf(timeTask)
                    val nextItem = timeTasks.getOrNull(timeTaskIndex + 1)

                    TimeTaskViewItem(
                        modifier = Modifier.animateItemPlacement(
                            animationSpec = spring(
                                stiffness = Spring.StiffnessMedium,
                                visibilityThreshold = IntOffset.VisibilityThreshold,
                            ),
                        ),
                        timeTask = timeTask,
                        onEdit = onTimeTaskEdit,
                        onIncrease = onTimeTaskIncrease,
                        onReduce = onTimeTaskReduce,
                        onDoneChange = onTaskDoneChange,
                        isCompactView = isCompactView && nextItem != null && timeTask.endTime.isNotZeroDifference(
                            nextItem.startTime,
                        ),
                    )
                    AnimatedVisibility(
                        enter = fadeIn() + slideInVertically(),
                        exit = shrinkVertically() + fadeOut(),
                        visible = nextItem != null && 
                            timeTask.endTime.isNotZeroDifference(nextItem.startTime) && 
                            !isCompactView,
                    ) {
                        val trackColor = when (timeTask.executionStatus) {
                            TimeTaskStatus.PLANNED -> MaterialTheme.colorScheme.surfaceOne()
                            TimeTaskStatus.RUNNING -> MaterialTheme.colorScheme.primaryContainer
                            TimeTaskStatus.COMPLETED -> MaterialTheme.colorScheme.tertiaryContainer
                        }
                        if (nextItem != null) {
                            AddTimeTaskViewItem(
                                modifier = Modifier.animateItemPlacement(),
                                onAddClick = { onTimeTaskAdd.invoke(timeTask.endTime, nextItem.startTime) },
                                startTime = timeTask.endTime,
                                endTime = nextItem.startTime,
                                indicatorColor = trackColor,
                            )
                        }
                    }
                }
                item {
                    val startTime = when (timeTasks.isEmpty()) {
                        true -> checkNotNull(currentDate)
                        false -> timeTasks.last().endTime
                    }
                    val endTime = startTime.endThisDay()
                    AddTimeTaskViewItem(
                        modifier = Modifier.animateItemPlacement(),
                        onAddClick = { onTimeTaskAdd(startTime, endTime) },
                        startTime = startTime,
                        endTime = endTime,
                    )
                }
                item { EmptyItem() }
            }
        }
    } else if (!isLoadingContent) {
        EmptyDateView(
            modifier = Modifier.align(Alignment.Center),
            emptyTitle = HomeThemeRes.strings.emptyScheduleTitle,
            subTitle = null,
        ) {
            OutlinedButton(
                onClick = onCreateSchedule,
                modifier = Modifier.width(185.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.secondary,
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                contentPadding = PaddingValues(horizontal = 4.dp),
            ) {
                Icon(
                    modifier = Modifier.size(18.dp).align(Alignment.CenterVertically),
                    imageVector = Icons.Default.Add,
                    contentDescription = HomeThemeRes.strings.createScheduleDesc,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    modifier = Modifier.padding(start = 4.dp).align(Alignment.CenterVertically),
                    text = HomeThemeRes.strings.createScheduleTitle,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

@Composable
internal fun LazyItemScope.TimeTaskViewItem(
    modifier: Modifier = Modifier,
    timeTask: TimeTaskUi,
    onEdit: (TimeTaskUi) -> Unit,
    onIncrease: (TimeTaskUi) -> Unit,
    onReduce: (TimeTaskUi) -> Unit,
    onDoneChange: (TimeTaskUi) -> Unit,
    isCompactView: Boolean,
) {
    when (timeTask.executionStatus) {
        TimeTaskStatus.PLANNED -> {
            PlannedTimeTaskItem(
                modifier = modifier,
                model = timeTask,
                onItemClick = { onEdit.invoke(timeTask) },
                isCompactView = isCompactView,
            )
        }
        TimeTaskStatus.RUNNING -> {
            RunningTimeTaskItem(
                modifier = modifier,
                model = timeTask,
                onMoreButtonClick = { onEdit.invoke(timeTask) },
                onIncreaseTime = { onIncrease.invoke(timeTask) },
                onReduceTime = { onReduce.invoke(timeTask) },
                isCompactView = isCompactView,
            )
        }
        TimeTaskStatus.COMPLETED -> {
            CompletedTimeTaskItem(
                modifier = modifier,
                model = timeTask,
                onItemClick = { onEdit.invoke(timeTask) },
                onDoneChange = { onDoneChange.invoke(timeTask) },
                isCompactView = isCompactView,
            )
        }
    }
}

@Composable
internal fun HorizontalProgressBar(
    modifier: Modifier = Modifier,
    isLoading: Boolean,
) = Box(
    modifier = modifier.animateContentSize().padding(vertical = 4.dp),
) {
    if (isLoading) {
        LinearProgressIndicator(modifier = modifier.fillMaxWidth())
    }
}

/* ----------------------- Release Preview -----------------------
@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun HomeContent_Preview() {
    TimePlannerTheme(
        dynamicColor = false,
        themeColorsType = ThemeColorsUiType.LIGHT,
        language = LanguageUiType.RU,
    ) {
        HomeTheme {
            Box(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
                HomeContent(
                    state = HomeViewState(dateStatus = null),
                    onChangeDate = {},
                    onTimeTaskEdit = {},
                    onTimeTaskAdd = {},
                    onTimeTaskIncrease = {},
                    onCreateSchedule = {},
                    onTimeTaskReduce = {},
                    onChangeViewStatus = {},
                )
            }
        }
    }
}
*/
