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
package ru.aleshin.features.home.impl.presentation.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import ru.aleshin.core.domain.entities.schedules.DailyScheduleStatus
import ru.aleshin.core.domain.entities.settings.ViewToggleStatus
import ru.aleshin.core.domain.entities.tasks.TimeTaskStatus
import ru.aleshin.core.presentation.models.tasks.TimeTaskDetailsUi
import ru.aleshin.core.utils.architecture.store.compose.handleEffects
import ru.aleshin.core.utils.architecture.store.compose.stateAsState
import ru.aleshin.core.utils.extensions.endThisDay
import ru.aleshin.core.utils.extensions.isCurrentDay
import ru.aleshin.core.utils.extensions.shiftDay
import ru.aleshin.features.home.impl.presentation.mapppers.mapToMessage
import ru.aleshin.features.home.impl.presentation.theme.HomeThemeRes
import ru.aleshin.features.home.impl.presentation.ui.home.contract.HomeEffect
import ru.aleshin.features.home.impl.presentation.ui.home.contract.HomeEvent
import ru.aleshin.features.home.impl.presentation.ui.home.contract.HomeState
import ru.aleshin.features.home.impl.presentation.ui.home.store.HomeComponent
import ru.aleshin.features.home.impl.presentation.ui.home.views.AddTimeTaskViewItem
import ru.aleshin.features.home.impl.presentation.ui.home.views.CompletedTimeTaskItem
import ru.aleshin.features.home.impl.presentation.ui.home.views.DateChooser
import ru.aleshin.features.home.impl.presentation.ui.home.views.EmptyDateView
import ru.aleshin.features.home.impl.presentation.ui.home.views.EmptyItem
import ru.aleshin.features.home.impl.presentation.ui.home.views.HomeDatePicker
import ru.aleshin.features.home.impl.presentation.ui.home.views.HomeTopAppBar
import ru.aleshin.features.home.impl.presentation.ui.home.views.PlannedTimeTaskItem
import ru.aleshin.features.home.impl.presentation.ui.home.views.RunningTimeTaskItem
import ru.aleshin.timeplanner.core.ui.theme.TimePlannerRes
import ru.aleshin.timeplanner.core.ui.theme.topSide
import ru.aleshin.timeplanner.core.ui.views.ErrorSnackbar
import ru.aleshin.timeplanner.core.ui.views.ViewToggle
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * @author Stanislav Aleshin on 18.02.2023.
 */
@Composable
internal fun HomeContent(
    homeComponent: HomeComponent,
    modifier: Modifier = Modifier,
) {
    val store = homeComponent.store
    val state by store.stateAsState()
    val snackbarState = remember { SnackbarHostState() }
    var isDateDialogShow by rememberSaveable { mutableStateOf(false) }
    val strings = HomeThemeRes.strings

    Scaffold(
        modifier = modifier,
        content = { paddingValues ->
            BaseHomeContent(
                state = state,
                modifier = Modifier.padding(paddingValues),
                onTimeTaskEdit = { store.dispatchEvent(HomeEvent.PressEditTimeTaskButton(it)) },
                onTaskDoneChange = { store.dispatchEvent(HomeEvent.ChangeTaskDoneStateButton(it)) },
                onTimeTaskAdd = { start, end ->
                    store.dispatchEvent(HomeEvent.PressAddTimeTaskButton(start, end))
                },
                onCreateSchedule = { store.dispatchEvent(HomeEvent.CreateSchedule) },
                onTimeTaskIncrease = { store.dispatchEvent(HomeEvent.TimeTaskShiftUp(it)) },
                onTimeTaskReduce = { store.dispatchEvent(HomeEvent.TimeTaskShiftDown(it)) },
            )
        },
        topBar = {
            HomeTopAppBar(
                calendarIconBehavior = state.calendarButtonBehavior,
                onSettingsIconClick = { store.dispatchEvent(HomeEvent.PressSettingsButton) },
                onOpenCalendar = { isDateDialogShow = true },
                onGoToToday = { store.dispatchEvent(HomeEvent.SelectedCurrentDate) },
            )
        },
        bottomBar = {
            DateChooserSection(
                selectedDate = state.selectedDate,
                toggleState = state.taskViewStatus,
                onChangeDate = { date -> store.dispatchEvent(HomeEvent.LoadSchedule(date)) },
                onChangeToggleStatus = { store.dispatchEvent(HomeEvent.PressViewToggleButton(it)) }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarState) { snackbarData ->
                ErrorSnackbar(snackbarData)
            }
        },
    )

    HomeDatePicker(
        isOpenDialog = isDateDialogShow,
        onDismiss = { isDateDialogShow = false },
        onSelectedDate = {
            isDateDialogShow = false
            store.dispatchEvent(HomeEvent.LoadSchedule(it))
        },
    )

    store.handleEffects { effect ->
        when (effect) {
            is HomeEffect.ShowError -> snackbarState.showSnackbar(
                message = effect.failures.mapToMessage(strings),
                withDismissAction = true,
            )
        }
    }
}

@Composable
private fun BaseHomeContent(
    state: HomeState,
    modifier: Modifier = Modifier,
    onCreateSchedule: () -> Unit,
    onTimeTaskEdit: (TimeTaskDetailsUi) -> Unit,
    onTaskDoneChange: (TimeTaskDetailsUi) -> Unit,
    onTimeTaskAdd: (startTime: Date, endTime: Date) -> Unit,
    onTimeTaskIncrease: (TimeTaskDetailsUi) -> Unit,
    onTimeTaskReduce: (TimeTaskDetailsUi) -> Unit,
) {
    Column(modifier = modifier.fillMaxSize()) {
        TimeTasksSection(
            modifier = Modifier.weight(1f),
            selectedDate = state.selectedDate,
            dateStatus = state.schedule?.dateStatus,
            timeTasks = state.schedule?.timeTasks ?: emptyList(),
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
    selectedDate: Date?,
    toggleState: ViewToggleStatus,
    onChangeDate: (Date) -> Unit,
    onChangeToggleStatus: (ViewToggleStatus) -> Unit,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge.topSide,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
        Row(
            modifier = Modifier.padding(start = 16.dp, end = 8.dp, top = 12.dp, bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            HomeDateChooser(
                modifier = Modifier.width(202.dp),
                selectedDate = selectedDate,
                onChangeDate = onChangeDate,
            )
            Spacer(modifier = Modifier.weight(1f))
            ViewToggle(
                status = toggleState,
                onStatusChange = onChangeToggleStatus,
            )
        }
    }
}

@Composable
internal fun HomeDateChooser(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    selectedDate: Date?,
    onChangeDate: (Date) -> Unit,
) {
    val dateFormat = remember { SimpleDateFormat("EEE, d MMM", Locale.getDefault()) }
    val isDateDialogShow = rememberSaveable { mutableStateOf(false) }

    DateChooser(
        modifier = modifier,
        enabled = enabled,
        dateTitle = remember(selectedDate) {
            selectedDate?.let { dateFormat.format(it) } ?: ""
        },
        onNext = { selectedDate?.let { onChangeDate.invoke(it.shiftDay(amount = 1)) } },
        onPrevious = { selectedDate?.let { onChangeDate.invoke(it.shiftDay(amount = -1)) } },
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

@Composable
@OptIn(ExperimentalFoundationApi::class)
internal fun TimeTasksSection(
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
    dateStatus: DailyScheduleStatus?,
    selectedDate: Date?,
    timeTasks: List<TimeTaskDetailsUi>,
    timeTaskViewStatus: ViewToggleStatus,
    onCreateSchedule: () -> Unit,
    onTimeTaskEdit: (TimeTaskDetailsUi) -> Unit,
    onTaskDoneChange: (TimeTaskDetailsUi) -> Unit,
    onTimeTaskAdd: (startTime: Date, endTime: Date) -> Unit,
    onTimeTaskIncrease: (TimeTaskDetailsUi) -> Unit,
    onTimeTaskReduce: (TimeTaskDetailsUi) -> Unit,
) = AnimatedVisibility(
    visible = selectedDate != null,
    enter = fadeIn() + scaleIn(initialScale = 0.9f),
    exit = fadeOut(),
) {
    val isCompactView = timeTaskViewStatus == ViewToggleStatus.COMPACT
    var isScrolled by rememberSaveable { mutableStateOf(false) }
    val visibleFirstAdd = remember(timeTasks, selectedDate, isCompactView) {
        timeTasks.isNotEmpty() && timeTasks.first().startTime > selectedDate && !isCompactView
    }
    if (dateStatus != null) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = listState,
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            if (visibleFirstAdd) {
                item(key = "visibleFirstAdd:$selectedDate") {
                    val startTime = checkNotNull(selectedDate)
                    val endTime = remember(timeTasks) { timeTasks[0].startTime }

                    AddTimeTaskViewItem(
                        modifier = Modifier.animateItem(
                            placementSpec = spring(
                                stiffness = Spring.StiffnessMedium,
                                visibilityThreshold = IntOffset.VisibilityThreshold,
                            ),
                        ),
                        onAddClick = { onTimeTaskAdd(startTime, endTime) },
                        startTime = startTime,
                        endTime = endTime,
                    )
                }
            }
            items(timeTasks, key = { it.key }) { timeTask ->
                val timeTaskIndex = remember(timeTasks) {
                    timeTasks.indexOf(timeTask)
                }
                val nextItem = remember(timeTasks, timeTaskIndex) {
                    timeTasks.getOrNull(timeTaskIndex + 1)
                }

                TimeTaskViewItem(
                    modifier = Modifier.animateItem(
                        placementSpec = spring(
                            stiffness = Spring.StiffnessMedium,
                            visibilityThreshold = IntOffset.VisibilityThreshold,
                        ),
                    ),
                    timeTask = timeTask,
                    onEdit = onTimeTaskEdit,
                    onIncrease = onTimeTaskIncrease,
                    onReduce = onTimeTaskReduce,
                    onDoneChange = onTaskDoneChange,
                    isCompactView = remember(timeTask, nextItem, isCompactView) {
                        isCompactView && nextItem != null && timeTask.endTime < nextItem.startTime
                    }
                )
                AnimatedVisibility(
                    enter = fadeIn() + slideInVertically(),
                    exit = shrinkVertically() + fadeOut(),
                    visible = remember(nextItem, isCompactView, timeTask) {
                        nextItem != null && timeTask.endTime < nextItem.startTime && !isCompactView
                    }
                ) {
                    val trackColor = when (timeTask.executionStatus) {
                        TimeTaskStatus.PLANNED -> MaterialTheme.colorScheme.surfaceContainerLow
                        TimeTaskStatus.RUNNING -> MaterialTheme.colorScheme.primaryContainer
                        TimeTaskStatus.COMPLETED -> MaterialTheme.colorScheme.tertiaryContainer
                    }
                    if (nextItem != null) {
                        AddTimeTaskViewItem(
                            modifier = Modifier.animateItem(
                                placementSpec = spring(
                                    stiffness = Spring.StiffnessMedium,
                                    visibilityThreshold = IntOffset.VisibilityThreshold,
                                ),
                            ),
                            onAddClick = {
                                onTimeTaskAdd.invoke(timeTask.endTime, nextItem.startTime)
                            },
                            startTime = timeTask.endTime,
                            endTime = nextItem.startTime,
                            indicatorColor = trackColor,
                        )
                    }
                }
            }
            item(key = "lastAdd:$selectedDate") {
                val startTime = remember(timeTasks, selectedDate) {
                    when (timeTasks.isEmpty()) {
                        true -> checkNotNull(selectedDate)
                        false -> timeTasks.last().endTime
                    }
                }
                val endTime = remember(startTime) {
                    startTime.endThisDay()
                }
                val enabled = remember(timeTasks, selectedDate) {
                    timeTasks.isEmpty() || timeTasks.last().endTime.isCurrentDay(selectedDate!!)
                }

                AddTimeTaskViewItem(
                    modifier = Modifier.animateItem(
                        placementSpec = spring(
                            stiffness = Spring.StiffnessMedium,
                            visibilityThreshold = IntOffset.VisibilityThreshold,
                        ),
                    ),
                    enabled = enabled,
                    onAddClick = { onTimeTaskAdd(startTime, endTime) },
                    startTime = startTime,
                    endTime = endTime,
                )
            }
            item { EmptyItem() }
        }

        LaunchedEffect(Unit) {
            val runningTask = timeTasks.find { it.executionStatus == TimeTaskStatus.RUNNING }
            if (runningTask != null && !isScrolled) {
                val index = timeTasks.indexOf(runningTask) + if (visibleFirstAdd) 1 else 0
                listState.animateScrollToItem(index)
                isScrolled = true
            }
        }
    } else if (selectedDate != null) {
        Box(modifier = modifier.fillMaxSize()) {
            EmptyDateView(
                modifier = Modifier.align(Alignment.Center),
                emptyTitle = TimePlannerRes.strings.emptyScheduleTitle,
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
                        modifier = Modifier
                            .size(18.dp)
                            .align(Alignment.CenterVertically),
                        imageVector = Icons.Default.Add,
                        contentDescription = HomeThemeRes.strings.createScheduleDesc,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        modifier = Modifier
                            .padding(start = 4.dp)
                            .align(Alignment.CenterVertically),
                        text = HomeThemeRes.strings.createScheduleTitle,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
    }
}

@Composable
internal fun LazyItemScope.TimeTaskViewItem(
    modifier: Modifier = Modifier,
    timeTask: TimeTaskDetailsUi,
    onEdit: (TimeTaskDetailsUi) -> Unit,
    onIncrease: (TimeTaskDetailsUi) -> Unit,
    onReduce: (TimeTaskDetailsUi) -> Unit,
    onDoneChange: (TimeTaskDetailsUi) -> Unit,
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
