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
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.aleshin.core.domain.entities.settings.HomeViewMode
import ru.aleshin.core.domain.entities.settings.ViewToggleStatus
import ru.aleshin.core.utils.extensions.shiftDay
import ru.aleshin.features.home.impl.presentation.ui.home.HomeLayoutMode
import ru.aleshin.timeplanner.core.ui.theme.topSide
import ru.aleshin.timeplanner.core.ui.views.ViewToggle
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * @author Stanislav Aleshin on 26.07.2026.
 */
@Composable
internal fun HomeBottomBar(
    modifier: Modifier = Modifier,
    layoutMode: HomeLayoutMode,
    selectedDate: Date?,
    toggleState: ViewToggleStatus,
    viewMode: HomeViewMode,
    onChangeDate: (Date) -> Unit,
    onViewToggleChange: (ViewToggleStatus) -> Unit,
) {
    if (layoutMode == HomeLayoutMode.COMPACT || layoutMode == HomeLayoutMode.MEDIUM) {
        DateChooserSection(
            modifier = modifier,
            selectedDate = selectedDate,
            toggleState = toggleState,
            isToggleVisible = viewMode == HomeViewMode.AGENDA,
            onChangeDate = onChangeDate,
            onChangeToggleStatus = onViewToggleChange,
        )
    }
}

@Composable
internal fun DateChooserSection(
    modifier: Modifier = Modifier,
    selectedDate: Date?,
    toggleState: ViewToggleStatus,
    isToggleVisible: Boolean = true,
    onChangeDate: (Date) -> Unit,
    onChangeToggleStatus: (ViewToggleStatus) -> Unit,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge.topSide,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
        Row(
            modifier = Modifier.padding(start = 16.dp, end = 8.dp, top = 12.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            HomeDateChooser(
                modifier = Modifier.width(202.dp),
                selectedDate = selectedDate,
                onChangeDate = onChangeDate,
            )
            Spacer(modifier = Modifier.weight(1f))
            AnimatedVisibility(
                visible = isToggleVisible,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut(),
            ) {
                ViewToggle(
                    status = toggleState,
                    onStatusChange = onChangeToggleStatus,
                )
            }
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