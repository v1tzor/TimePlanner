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
package ru.aleshin.features.home.impl.presentation.ui.overview.views

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import ru.aleshin.core.ui.theme.TimePlannerRes
import ru.aleshin.core.ui.views.DialogButtons
import ru.aleshin.core.ui.views.ExpandedIcon
import ru.aleshin.core.utils.extensions.shiftDay
import ru.aleshin.core.utils.functional.Constants
import ru.aleshin.features.home.impl.presentation.theme.HomeThemeRes
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * @author Stanislav Aleshin on 04.11.2023.
 */
@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun TaskDateChooserDialog(
    modifier: Modifier = Modifier,
    daysCount: Int = Constants.Date.DAYS_IN_WEEK * 2,
    onDismiss: () -> Unit,
    onConfirm: (Date) -> Unit,
) {
    var selectedDate by remember { mutableStateOf<Date?>(null) }
    val dateList = remember {
        mutableStateListOf<Date>().apply {
            for (shiftAmount in 0..daysCount) add(Date().shiftDay(shiftAmount))
        }
    }

    AlertDialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = modifier.width(328.dp).wrapContentHeight(),
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = TimePlannerRes.elevations.levelThree,
        ) {
            Column {
                TaskDateChooserDialogHeader()
                Divider(Modifier.fillMaxWidth())
                Column(
                    modifier = Modifier.height(160.dp).padding(start = 16.dp, end = 16.dp, top = 16.dp),
                ) {
                    DayChooser(
                        days = dateList,
                        selected = selectedDate,
                        onSelected = { selectedDate = it },
                    )
                    DialogButtons(
                        isConfirmEnabled = selectedDate != null,
                        confirmTitle = TimePlannerRes.strings.alertDialogOkConfirmTitle,
                        onConfirmClick = { selectedDate?.let { onConfirm(it) } },
                        onCancelClick = onDismiss,
                    )
                }
            }
        }
    }
}

@Composable
internal fun TaskDateChooserDialogHeader(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(top = 24.dp, bottom = 12.dp, start = 24.dp, end = 24.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Text(
            text = HomeThemeRes.strings.taskDateChooserHeader,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.headlineSmall,
        )
    }
}

@Composable
internal fun DayChooser(
    modifier: Modifier = Modifier,
    days: List<Date>,
    selected: Date?,
    onSelected: (Date?) -> Unit,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    val dateFormat = SimpleDateFormat("EEE, MMM d, ''yy", Locale.getDefault())
    val isPressed by interactionSource.collectIsPressedAsState()
    var isDayChooseMenuOpen by remember { mutableStateOf(false) }

    OutlinedTextField(
        modifier = modifier.fillMaxWidth(),
        value = selected?.let { dateFormat.format(it) } ?: "",
        onValueChange = {},
        readOnly = true,
        label = { Text(text = HomeThemeRes.strings.taskDateChooserFieldLabel) },
        trailingIcon = { ExpandedIcon(isExpanded = isDayChooseMenuOpen) },
        interactionSource = interactionSource,
    )
    Box(contentAlignment = Alignment.TopEnd) {
        DayChooseMenu(
            isExpanded = isDayChooseMenuOpen,
            days = days,
            onDismiss = { isDayChooseMenuOpen = false },
            onChoose = { mainCategory ->
                isDayChooseMenuOpen = false
                onSelected(mainCategory)
            },
        )
    }
    LaunchedEffect(key1 = isPressed) {
        if (isPressed) {
            isDayChooseMenuOpen = !isDayChooseMenuOpen
        }
    }
}

@Composable
internal fun DayChooseMenu(
    modifier: Modifier = Modifier,
    isExpanded: Boolean,
    days: List<Date>,
    onDismiss: () -> Unit,
    onChoose: (Date?) -> Unit,
) {
    val dateFormat = SimpleDateFormat("EEE, MMM d, ''yy", Locale.getDefault())
    DropdownMenu(
        expanded = isExpanded,
        onDismissRequest = onDismiss,
        modifier = modifier.sizeIn(maxHeight = 200.dp),
        offset = DpOffset(0.dp, 6.dp),
    ) {
        days.forEach { day ->
            DropdownMenuItem(
                onClick = { onChoose(day) },
                text = {
                    Text(
                        text = dateFormat.format(day),
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleMedium,
                    )
                },
            )
        }
    }
}
