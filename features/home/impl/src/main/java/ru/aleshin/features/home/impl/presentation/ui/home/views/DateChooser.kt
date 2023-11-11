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
package ru.aleshin.features.home.impl.presentation.ui.home.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ru.aleshin.core.ui.theme.TimePlannerRes
import ru.aleshin.core.utils.extensions.mapToDate
import ru.aleshin.core.utils.extensions.startThisDay
import ru.aleshin.features.home.impl.presentation.theme.HomeThemeRes
import java.util.*

/**
 * @author Stanislav Aleshin on 22.02.2023.
 */
@Composable
internal fun DateChooser(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    dateTitle: String,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onChooseDate: () -> Unit,
) {
    Surface(
        modifier = modifier.height(36.dp),
        shape = MaterialTheme.shapes.large,
        tonalElevation = TimePlannerRes.elevations.levelOne,
    ) {
        Row(modifier = Modifier.fillMaxHeight(), verticalAlignment = Alignment.CenterVertically) {
            DateChooserIcon(
                enabled = enabled, 
                icon = painterResource(HomeThemeRes.icons.previousDate), 
                description = HomeThemeRes.strings.previousDateIconDesc,
                onClick = onPrevious,
            )
            DateChooserContent(
                modifier = Modifier.weight(1f),
                enabled = enabled,
                dateTitle = dateTitle,
                onClick = onChooseDate,
            )
            DateChooserIcon(
                enabled = enabled,
                icon = painterResource(HomeThemeRes.icons.nextDate),
                description = HomeThemeRes.strings.nextDateIconDesc,
                onClick = onNext,
            )
        }
    }
}

@Composable
internal fun DateChooserIcon(
    modifier: Modifier = Modifier,
    enabled: Boolean,
    icon: Painter,
    description: String?,
    onClick: () -> Unit,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier.size(36.dp),
        enabled = enabled,
    ) {
        Icon(
            modifier = Modifier.size(12.dp).graphicsLayer(alpha = if (enabled) 1f else 0.5f),
            painter = icon,
            contentDescription = description,
            tint = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
internal fun DateChooserContent(
    modifier: Modifier = Modifier,
    enabled: Boolean,
    dateTitle: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier.fillMaxHeight().clip(MaterialTheme.shapes.medium).clickable(
            enabled = enabled,
            onClick = onClick, 
        ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 12.dp).graphicsLayer(
                alpha = if (enabled) 1f else 0.5f,
            ),
            text = dateTitle,
            textAlign = TextAlign.Center,
            maxLines = 1,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleMedium,
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun HomeDatePicker(
    modifier: Modifier = Modifier,
    isOpenDialog: Boolean,
    onDismiss: () -> Unit,
    onSelectedDate: (Date) -> Unit,
) {
    if (isOpenDialog) {
        val datePickerState = rememberDatePickerState()
        val confirmEnabled by remember {
            derivedStateOf { datePickerState.selectedDateMillis != null }
        }
        DatePickerDialog(
            modifier = modifier,
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(
                    onClick = {
                        val dateMillis = datePickerState.selectedDateMillis
                        val date = dateMillis?.mapToDate() ?: return@TextButton
                        onSelectedDate.invoke(date.startThisDay())
                    },
                    enabled = confirmEnabled,
                ) {
                    Text(text = TimePlannerRes.strings.alertDialogSelectConfirmTitle)
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text(text = TimePlannerRes.strings.alertDialogDismissTitle)
                }
            },
        ) {
            DatePicker(
                state = datePickerState,
                title = {
                    Text(
                        modifier = Modifier.padding(start = 24.dp, top = 24.dp),
                        text = HomeThemeRes.strings.dateDialogPickerTitle,
                    )
                },
                headline = {
                    Text(
                        modifier = Modifier.padding(start = 24.dp),
                        text = HomeThemeRes.strings.dateDialogPickerHeadline,
                    )
                },
            )
        }
    }
}

/* ----------------------- Release Preview -----------------------
@Composable
@Preview(showBackground = true)
internal fun DateChooser_Preview() {
    TimePlannerTheme(dynamicColor = false, themeColorsType = ThemeColorsUiType.LIGHT) {
        HomeTheme {
            DateChooser(
                modifier = Modifier.sizeIn(minWidth = 200.dp).width(150.dp),
                dateTitle = "Feb 23, 2023",
                onChooseDate = {},
                onNextDate = {},
                onPreviousDate = {},
            )
        }
    }
}*/
