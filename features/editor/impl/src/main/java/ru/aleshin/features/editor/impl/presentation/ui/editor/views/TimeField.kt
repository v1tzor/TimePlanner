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
package ru.aleshin.features.editor.impl.presentation.ui.editor.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import ru.aleshin.core.ui.theme.TimePlannerRes
import ru.aleshin.core.ui.views.DurationPickerDialog
import ru.aleshin.core.ui.views.TimePickerDialog
import ru.aleshin.core.ui.views.toMinutesAndHoursTitle
import ru.aleshin.features.editor.impl.presentation.theme.EditorThemeRes
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author Stanislav Aleshin on 25.02.2023.
 */
@Composable
internal fun BaseTimeField(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    currentTime: Date,
    isError: Boolean,
    label: String,
    leadingIcon: Int,
    onChangeTime: (Date) -> Unit,
) {
    val timeFormat = SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT)
    var openDialog by rememberSaveable { mutableStateOf(false) }

    Surface(
        enabled = enabled,
        onClick = { openDialog = true },
        modifier = modifier.height(56.dp).widthIn(min = 120.dp),
        shape = MaterialTheme.shapes.medium,
        tonalElevation = TimePlannerRes.elevations.levelOne,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(leadingIcon),
                contentDescription = label,
                tint = when (isError) {
                    true -> MaterialTheme.colorScheme.error
                    false -> MaterialTheme.colorScheme.onSurfaceVariant
                },
            )
            Column {
                val textColor = when (isError) {
                    true -> MaterialTheme.colorScheme.error
                    false -> MaterialTheme.colorScheme.onSurface
                }
                Text(
                    text = label,
                    color = textColor,
                    style = MaterialTheme.typography.bodySmall,
                )
                Text(
                    text = timeFormat.format(currentTime),
                    color = textColor,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
    if (openDialog) {
        TimePickerDialog(
            headerTitle = EditorThemeRes.strings.timePickerHeader,
            initTime = currentTime,
            onDismissRequest = { openDialog = false },
            onSelectedTime = {
                onChangeTime(it)
                openDialog = false
            },
        )
    }
}

@Composable
internal fun StartTimeField(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    currentTime: Date,
    isError: Boolean,
    onChangeTime: (Date) -> Unit,
) = BaseTimeField(
    enabled = enabled,
    modifier = modifier,
    currentTime = currentTime,
    isError = isError,
    label = EditorThemeRes.strings.timeFieldStartLabel,
    leadingIcon = EditorThemeRes.icons.startTime,
    onChangeTime = onChangeTime,
)

@Composable
internal fun EndTimeField(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    currentTime: Date,
    isError: Boolean,
    onChangeTime: (Date) -> Unit,
) = BaseTimeField(
    enabled = enabled,
    modifier = modifier,
    currentTime = currentTime,
    isError = isError,
    label = EditorThemeRes.strings.timeFieldEndLabel,
    leadingIcon = EditorThemeRes.icons.endTime,
    onChangeTime = onChangeTime,
)

@Composable
internal fun DurationTitle(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    duration: Long,
    startTime: Date,
    isError: Boolean = false,
    onChangeDuration: (Long) -> Unit,
) {
    var isOpenDurationDialog by remember { mutableStateOf(false) }
    val correctDuration = if (duration < 0L) 0L else duration
    val titleColor = when (isError) {
        true -> MaterialTheme.colorScheme.error
        false -> MaterialTheme.colorScheme.onSurface
    }
    Box(
        modifier = modifier.clip(MaterialTheme.shapes.small).clickable(enabled) {
            isOpenDurationDialog = true
        },
    ) {
        Text(
            modifier = Modifier.padding(4.dp),
            text = correctDuration.toMinutesAndHoursTitle(),
            style = MaterialTheme.typography.bodyLarge,
            maxLines = 1,
            color = titleColor,
        )
    }
    if (isOpenDurationDialog) {
        DurationPickerDialog(
            headerTitle = EditorThemeRes.strings.durationPickerTitle,
            duration = duration,
            startTime = startTime,
            onDismissRequest = { isOpenDurationDialog = false },
            onSelectedTime = {
                onChangeDuration(it)
                isOpenDurationDialog = false
            },
        )
    }
}

/* ----------------------- Release Preview -----------------------
@Composable
@Preview(showBackground = true)
private fun StartTimeField_Preview() {
    TimePlannerTheme(dynamicColor = false, themeColorsType = ThemeColorsUiType.LIGHT) {
        EditorTheme {
            Box(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
                StartTimeField(
                    modifier = Modifier.padding(12.dp),
                    currentTime = Calendar.getInstance().time,
                    isError = false,
                    onChangeTime = {},
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun StartTimeField_Dark_Preview() {
    TimePlannerTheme(dynamicColor = false, themeColorsType = ThemeColorsUiType.DARK) {
        EditorTheme {
            Box(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
                val time = remember { mutableStateOf<Date>(Calendar.getInstance().time) }
                StartTimeField(
                    modifier = Modifier.padding(12.dp),
                    currentTime = time.value,
                    isError = true,
                    onChangeTime = {},
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun TimePickerDialog_Dark_Preview() {
    TimePlannerTheme(dynamicColor = false, themeColorsType = ThemeColorsUiType.DARK) {
        EditorTheme {
            Box(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
                TimePickerDialog(
                    headerTitle = EditorThemeRes.strings.timePickerHeader,
                    initTime = Calendar.getInstance().time,
                    onDismissRequest = {},
                    onSelectedTime = {},
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun TimePickerDialog_Light_Preview() {
    TimePlannerTheme(dynamicColor = false, themeColorsType = ThemeColorsUiType.LIGHT) {
        EditorTheme {
            Box(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
                TimePickerDialog(
                    headerTitle = EditorThemeRes.strings.timePickerHeader,
                    initTime = Calendar.getInstance().time,
                    onDismissRequest = {},
                    onSelectedTime = {},
                )
            }
        }
    }
}
*/
