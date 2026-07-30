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
package ru.aleshin.features.home.impl.presentation.ui.home.views

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import ru.aleshin.core.domain.entities.settings.HomeViewMode
import ru.aleshin.core.domain.entities.settings.ViewToggleStatus
import ru.aleshin.core.utils.extensions.startThisDay
import ru.aleshin.features.home.impl.presentation.theme.HomeThemeRes
import ru.aleshin.timeplanner.core.ui.views.ViewToggle
import java.util.Calendar
import java.util.Date
import java.util.TimeZone

/**
 * @author Stanislav Aleshin on 27.07.2026.
 */
@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun HomeDatePane(
    modifier: Modifier = Modifier,
    selectedDate: Date?,
    selectedMode: HomeViewMode,
    toggleState: ViewToggleStatus,
    onDateChange: (Date) -> Unit,
    onOpenCalendar: () -> Unit,
    onModeChange: (HomeViewMode) -> Unit,
    onToggleChange: (ViewToggleStatus) -> Unit,
) {
    val selectedDateMillis = selectedDate?.toDatePickerMillis()
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDateMillis,
    )

    LaunchedEffect(selectedDateMillis) {
        if (datePickerState.selectedDateMillis != selectedDateMillis) {
            datePickerState.selectedDateMillis = selectedDateMillis
            selectedDateMillis?.let { datePickerState.displayedMonthMillis = it }
        }
    }
    LaunchedEffect(datePickerState.selectedDateMillis) {
        val dateMillis = datePickerState.selectedDateMillis ?: return@LaunchedEffect
        if (dateMillis != selectedDateMillis) {
            onDateChange(Date(dateMillis).startThisDay())
        }
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(
                    insets = WindowInsets.safeDrawing.only(WindowInsetsSides.Vertical + WindowInsetsSides.End)
                ),
        ) {
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier.fillMaxWidth().padding(start = 16.dp, top = 24.dp, end = 16.dp)
            ) {
                HomeViewMode.entries.forEachIndexed { index, mode ->
                    val title = when (mode) {
                        HomeViewMode.AGENDA -> HomeThemeRes.strings.agendaTabTitle
                        HomeViewMode.TIMELINE -> HomeThemeRes.strings.timelineTabTitle
                    }
                    val icon = when (mode) {
                        HomeViewMode.AGENDA -> HomeThemeRes.icons.agenda
                        HomeViewMode.TIMELINE -> HomeThemeRes.icons.timeline
                    }

                    SegmentedButton(
                        modifier = Modifier.weight(1f),
                        selected = selectedMode == mode,
                        onClick = { onModeChange(mode) },
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = HomeViewMode.entries.size,
                        ),
                        icon = {
                            Icon(
                                painter = painterResource(icon),
                                contentDescription = null,
                            )
                        },
                        label = { Text(text = title) },
                    )
                }
            }
            DatePicker(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                state = datePickerState,
                title = null,
                headline = null,
                showModeToggle = false,
                colors = DatePickerDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                ),
            )
            Spacer(modifier = Modifier.weight(1f))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 16.dp,
                        top = 8.dp,
                        end = 8.dp,
                        bottom = 12.dp,
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                HomeDateChooser(
                    modifier = Modifier.weight(1f),
                    selectedDate = selectedDate,
                    onDateChange = onDateChange,
                    onOpenCalendar = onOpenCalendar,
                )
                AnimatedVisibility(
                    visible = selectedMode == HomeViewMode.AGENDA,
                    enter = fadeIn() + expandHorizontally(expandFrom = Alignment.End),
                    exit = fadeOut() + shrinkHorizontally(shrinkTowards = Alignment.End),
                ) {
                    ViewToggle(
                        status = toggleState,
                        onStatusChange = onToggleChange,
                    )
                }
            }
        }
    }
}

private fun Date.toDatePickerMillis(): Long {
    val localCalendar = Calendar.getInstance().apply { time = this@toDatePickerMillis }
    return Calendar.getInstance(DATE_PICKER_TIME_ZONE).apply {
        clear()
        set(
            localCalendar[Calendar.YEAR],
            localCalendar[Calendar.MONTH],
            localCalendar[Calendar.DAY_OF_MONTH],
        )
    }.timeInMillis
}

private val DATE_PICKER_TIME_ZONE: TimeZone = TimeZone.getTimeZone("UTC")
