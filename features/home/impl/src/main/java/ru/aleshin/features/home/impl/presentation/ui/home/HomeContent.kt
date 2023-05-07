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
 * imitations under the License.
 */
package ru.aleshin.features.home.impl.presentation.ui.home

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.* // ktlint-disable no-wildcard-imports
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.aleshin.core.ui.theme.material.surfaceOne
import ru.aleshin.core.utils.extensions.endThisDay
import ru.aleshin.core.utils.extensions.isNotZeroDifference
import ru.aleshin.core.utils.extensions.shiftDay
import ru.aleshin.core.utils.functional.Constants
import ru.aleshin.core.utils.functional.TimeRange
import ru.aleshin.features.home.api.domains.entities.schedules.status.TimeTaskStatus
import ru.aleshin.features.home.impl.presentation.models.TimeTaskUi
import ru.aleshin.features.home.impl.presentation.theme.HomeThemeRes
import ru.aleshin.features.home.impl.presentation.ui.home.contract.HomeViewState
import ru.aleshin.features.home.impl.presentation.ui.home.views.*
import ru.aleshin.features.home.impl.presentation.ui.home.views.CompletedTimeTaskItem
import ru.aleshin.features.home.impl.presentation.ui.home.views.DateChooser
import ru.aleshin.features.home.impl.presentation.ui.home.views.PlannedTimeTaskItem
import ru.aleshin.features.home.impl.presentation.ui.home.views.TimeTaskPlaceHolderItem
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
    onTimeTaskAdd: (TimeRange) -> Unit,
    onTimeTaskIncrease: (TimeTaskUi) -> Unit,
    onTimeTaskReduce: (TimeTaskUi) -> Unit,
    onChangeViewStatus: (ViewToggleStatus) -> Unit,
) {
    val listState = rememberLazyListState()
    Column(modifier = modifier.fillMaxSize()) {
        HorizontalProgressBar(isLoading = state.isLoadingContent)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            HomeDataChooser(
                modifier = Modifier.width(202.dp),
                isEnabled = !state.isLoadingContent,
                currentDate = state.currentDate,
                onChangeDate = onChangeDate,
            )
            Spacer(modifier = Modifier.weight(1f))
            ViewToggle(status = state.timeTaskViewStatus, onStatusChange = onChangeViewStatus)
        }
        Box(modifier = Modifier.fillMaxSize()) {
            if (state.dateStatus != null) {
                LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    userScrollEnabled = !state.isLoadingContent,
                ) {
                    if (state.isLoadingContent) {
                        items(Constants.Placeholder.items) { TimeTaskPlaceHolderItem() }
                    } else {
                        items(state.timeTasks, key = { it.key }) { timeTask ->
                            val isCompactView = state.timeTaskViewStatus == ViewToggleStatus.COMPACT
                            val timeTaskIndex = state.timeTasks.indexOf(timeTask)
                            val nextItem = state.timeTasks.getOrNull(timeTaskIndex + 1)

                            TimeTaskViewItem(
                                timeTask = timeTask,
                                onTimeTaskEdit = onTimeTaskEdit,
                                onTimeTaskIncrease = onTimeTaskIncrease,
                                onTimeTaskReduce = onTimeTaskReduce,
                                isCompactView = isCompactView && nextItem != null && timeTask.endTime.isNotZeroDifference(
                                    nextItem.startTime,
                                ),
                            )
                            if (nextItem != null && timeTask.endTime.isNotZeroDifference(nextItem.startTime) && !isCompactView) {
                                val timeRange = TimeRange(timeTask.endTime, nextItem.startTime)
                                val trackColor = when (timeTask.executionStatus) {
                                    TimeTaskStatus.PLANNED -> MaterialTheme.colorScheme.surfaceOne()
                                    TimeTaskStatus.RUNNING -> MaterialTheme.colorScheme.primaryContainer
                                    TimeTaskStatus.COMPLETED -> MaterialTheme.colorScheme.tertiaryContainer
                                }
                                AddTimeTaskViewItem(
                                    onAddTimeTask = { onTimeTaskAdd.invoke(timeRange) },
                                    startTime = timeTask.endTime,
                                    endTime = nextItem.startTime,
                                    indicatorColor = trackColor,
                                )
                            }
                        }
                        item {
                            val startTime = when (state.timeTasks.isEmpty()) {
                                true -> checkNotNull(state.currentDate)
                                false -> state.timeTasks.last().endTime
                            }
                            val endTime = startTime.endThisDay()
                            val timeRange = TimeRange(startTime, endTime)
                            AddTimeTaskViewItem(
                                onAddTimeTask = { onTimeTaskAdd.invoke(timeRange) },
                                startTime = startTime,
                                endTime = endTime,
                            )
                        }
                        item { EmptyItem() }
                    }
                }
            } else if (!state.isLoadingContent) {
                EmptyDateView(
                    modifier = Modifier.align(Alignment.Center),
                    emptyTitle = HomeThemeRes.strings.emptyScheduleTitle,
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
                            modifier = Modifier.padding(start = 4.dp)
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
}

@Composable
internal fun HorizontalProgressBar(
    modifier: Modifier = Modifier,
    isLoading: Boolean,
) = Box(modifier = modifier.animateContentSize().padding(vertical = 4.dp)) {
    if (isLoading) {
        LinearProgressIndicator(modifier = modifier.fillMaxWidth())
    }
}

@Composable
internal fun HomeDataChooser(
    modifier: Modifier = Modifier,
    isEnabled: Boolean,
    currentDate: Date?,
    onChangeDate: (Date) -> Unit,
) {
    val dateFormat = SimpleDateFormat.getDateInstance()
    val isDateDialogShow = rememberSaveable { mutableStateOf(false) }

    DateChooser(
        modifier = modifier,
        isEnabled = isEnabled,
        dateTitle = currentDate?.let { dateFormat.format(it) } ?: "",
        onNextDate = { currentDate?.let { onChangeDate.invoke(it.shiftDay(amount = 1)) } },
        onPreviousDate = { currentDate?.let { onChangeDate.invoke(it.shiftDay(amount = -1)) } },
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
internal fun LazyItemScope.TimeTaskViewItem(
    modifier: Modifier = Modifier,
    timeTask: TimeTaskUi,
    onTimeTaskEdit: (TimeTaskUi) -> Unit,
    onTimeTaskIncrease: (TimeTaskUi) -> Unit,
    onTimeTaskReduce: (TimeTaskUi) -> Unit,
    isCompactView: Boolean,
) {
    when (timeTask.executionStatus) {
        TimeTaskStatus.PLANNED -> {
            PlannedTimeTaskItem(
                modifier = modifier,
                model = timeTask,
                onItemClick = { onTimeTaskEdit.invoke(timeTask) },
                isCompactView = isCompactView,
            )
        }

        TimeTaskStatus.RUNNING -> {
            RunningTimeTaskItem(
                modifier = modifier,
                model = timeTask,
                onMoreButtonClick = { onTimeTaskEdit.invoke(timeTask) },
                onIncreaseTime = { onTimeTaskIncrease.invoke(timeTask) },
                onReduceTime = { onTimeTaskReduce.invoke(timeTask) },
                isCompactView = isCompactView,
            )
        }

        TimeTaskStatus.COMPLETED -> {
            CompletedTimeTaskItem(
                modifier = modifier,
                model = timeTask,
                onItemClick = { onTimeTaskEdit.invoke(timeTask) },
                isCompactView = isCompactView,
            )
        }
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
