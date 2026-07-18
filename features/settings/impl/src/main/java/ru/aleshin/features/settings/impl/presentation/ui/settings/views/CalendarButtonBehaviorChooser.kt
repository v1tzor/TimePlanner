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
package ru.aleshin.features.settings.impl.presentation.ui.settings.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import ru.aleshin.core.domain.entities.settings.CalendarButtonBehavior
import ru.aleshin.core.domain.entities.settings.CalendarButtonBehavior.OPEN_CALENDAR
import ru.aleshin.core.domain.entities.settings.CalendarButtonBehavior.SET_CURRENT_DATE
import ru.aleshin.features.settings.impl.presentation.theme.SettingsThemeRes
import ru.aleshin.timeplanner.core.ui.views.DialogButtons
import ru.aleshin.timeplanner.core.ui.views.SegmentedButtonItem

/**
 * @author Stanislav Aleshin on 23.10.2023.
 */
@Composable
internal fun CalendarButtonBehaviorChooser(
    modifier: Modifier = Modifier,
    calendarButtonBehavior: CalendarButtonBehavior,
    onUpdateCalendarBehavior: (CalendarButtonBehavior) -> Unit,
) {
    var isOpenDialog by rememberSaveable { mutableStateOf(false) }
    Surface(
        onClick = { isOpenDialog = true },
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
        Row(
            modifier = Modifier.padding(vertical = 16.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SettingsItemIcon(
                icon = SettingsThemeRes.icons.calendar,
                contentDescription = null,
            )
            Column(modifier = Modifier.padding(start = 16.dp).weight(1f)) {
                Text(
                    text = SettingsThemeRes.strings.calendarButtonBehaviorTitle,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = calendarButtonBehavior.toSegmentedItem().title,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            Icon(
                modifier = Modifier.padding(start = 8.dp).size(24.dp),
                painter = painterResource(SettingsThemeRes.icons.chevronRight),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
    CalendarButtonBehaviorDialog(
        openDialog = isOpenDialog,
        initialBehavior = calendarButtonBehavior,
        onCloseDialog = { isOpenDialog = false },
        onBehaviorChoose = { behavior ->
            isOpenDialog = false
            onUpdateCalendarBehavior(behavior)
        },
    )
}

/**
 * @author Stanislav Aleshin on 18.07.2026.
 */
@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun CalendarButtonBehaviorDialog(
    modifier: Modifier = Modifier,
    openDialog: Boolean,
    initialBehavior: CalendarButtonBehavior,
    onCloseDialog: () -> Unit,
    onBehaviorChoose: (CalendarButtonBehavior) -> Unit,
) {
    if (openDialog) {
        var selectedBehavior by rememberSaveable(initialBehavior) { mutableStateOf(initialBehavior) }

        BasicAlertDialog(onDismissRequest = onCloseDialog) {
            Surface(
                modifier = modifier.width(280.dp).wrapContentHeight(),
                shape = MaterialTheme.shapes.extraLarge,
                color = MaterialTheme.colorScheme.surfaceContainer,
            ) {
                Column {
                    Box(
                        modifier = Modifier.padding(
                            start = 24.dp,
                            end = 24.dp,
                            top = 24.dp,
                            bottom = 12.dp,
                        ),
                    ) {
                        Text(
                            text = SettingsThemeRes.strings.calendarButtonBehaviorTitle,
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.headlineSmall,
                        )
                    }
                    CalendarButtonBehaviorSegmentedItems.entries.forEachIndexed { index, item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp)
                                .clip(MaterialTheme.shapes.medium)
                                .clickable { selectedBehavior = item.toCalendarButtonBehavior() }
                                .padding(start = 16.dp, end = 8.dp)
                                .requiredHeight(56.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                modifier = Modifier.weight(1f),
                                text = item.title,
                                color = MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.bodyLarge,
                            )
                            RadioButton(
                                selected = selectedBehavior == item.toCalendarButtonBehavior(),
                                onClick = null,
                            )
                        }
                        if (index != CalendarButtonBehaviorSegmentedItems.entries.lastIndex) {
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                color = MaterialTheme.colorScheme.outlineVariant,
                            )
                        }
                    }
                    DialogButtons(
                        onCancelClick = onCloseDialog,
                        onConfirmClick = { onBehaviorChoose(selectedBehavior) },
                    )
                }
            }
        }
    }
}

internal enum class CalendarButtonBehaviorSegmentedItems : SegmentedButtonItem {
    OPEN_CALENDAR {
        override val title: String @Composable get() = SettingsThemeRes.strings.selectDayCalendarBehavior
    },
    SET_CURRENT_DATE {
        override val title: String @Composable get() = SettingsThemeRes.strings.currentDayCalendarBehavior
    },
}

internal fun CalendarButtonBehavior.toSegmentedItem() = when (this) {
    OPEN_CALENDAR -> CalendarButtonBehaviorSegmentedItems.OPEN_CALENDAR
    SET_CURRENT_DATE -> CalendarButtonBehaviorSegmentedItems.SET_CURRENT_DATE
}

internal fun CalendarButtonBehaviorSegmentedItems.toCalendarButtonBehavior() = when (this) {
    CalendarButtonBehaviorSegmentedItems.OPEN_CALENDAR -> OPEN_CALENDAR
    CalendarButtonBehaviorSegmentedItems.SET_CURRENT_DATE -> SET_CURRENT_DATE
}
