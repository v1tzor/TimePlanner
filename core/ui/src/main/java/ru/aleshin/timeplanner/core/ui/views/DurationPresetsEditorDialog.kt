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
package ru.aleshin.timeplanner.core.ui.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.aleshin.timeplanner.core.ui.theme.TimePlannerRes
import ru.aleshin.core.utils.extensions.minutesToMillis
import ru.aleshin.core.utils.extensions.toHorses
import ru.aleshin.core.utils.extensions.toMinutes
import ru.aleshin.core.utils.extensions.toMinutesInHours
import ru.aleshin.core.utils.extensions.toStringOrEmpty
import ru.aleshin.core.utils.functional.Constants

/**
 * @author Stanislav Aleshin on 02.07.2026.
 */
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun DurationPresetsEditorDialog(
    modifier: Modifier = Modifier,
    headerTitle: String,
    presets: List<Long>,
    onDismissRequest: () -> Unit,
    onConfirmPresets: (List<Long>) -> Unit,
) {
    val defaultPresets = defaultDurationPresets()
    var presetItems by rememberSaveable(presets) {
        mutableStateOf(presets.normalizeDurationPresets().ifEmpty { defaultPresets })
    }
    var editingPreset by rememberSaveable { mutableStateOf<Long?>(null) }
    var hours by rememberSaveable { mutableStateOf<Int?>(0) }
    var minutes by rememberSaveable { mutableStateOf<Int?>(15) }
    val draftDuration = createDuration(hours, minutes)
    val itemsWithoutEditing = presetItems.filterNot { it == editingPreset }
    val canSaveDraft = draftDuration != null && itemsWithoutEditing.none { it == draftDuration }

    BasicAlertDialog(onDismissRequest = onDismissRequest) {
        Surface(
            modifier = modifier.width(320.dp),
            color = MaterialTheme.colorScheme.surfaceContainer,
            shape = MaterialTheme.shapes.extraLarge,
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.End,
            ) {
                TimePickerHeader(title = headerTitle)
                DurationPickerHourMinuteSelector(
                    hours = hours.toStringOrEmpty(),
                    minutes = minutes.toStringOrEmpty(),
                    isEnableSupportText = true,
                    isRequestInitialFocus = false,
                    onMinutesChanges = { value ->
                        if (value.isEmpty()) {
                            hours = null
                        } else if (value.toIntOrNull() != null && value.length <= 2) {
                            hours = value.toIntOrNull()
                        }
                    },
                    onHoursChanges = { value ->
                        if (value.isEmpty()) {
                            minutes = null
                        } else if (value.toIntOrNull() != null && value.length <= 2) {
                            minutes = value.toIntOrNull()
                        }
                    },
                )
                Row(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(
                        modifier = Modifier.size(36.dp),
                        enabled = canSaveDraft,
                        onClick = {
                            if (draftDuration != null) {
                                presetItems = (itemsWithoutEditing + draftDuration).normalizeDurationPresets()
                                editingPreset = null
                            }
                        },
                    ) {
                        Icon(
                            imageVector = if (editingPreset == null) Icons.Filled.Add else Icons.Filled.Check,
                            contentDescription = null,
                        )
                    }
                    IconButton(
                        modifier = Modifier.size(36.dp),
                        onClick = {
                            presetItems = defaultPresets
                            editingPreset = null
                        },
                    ) {
                        Icon(imageVector = Icons.Filled.Refresh, contentDescription = null)
                    }
                    Spacer(modifier = Modifier.weight(1f))
                }
                LazyColumn(
                    modifier = Modifier.heightIn(max = 184.dp),
                    contentPadding = PaddingValues(horizontal = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(presetItems) { preset ->
                        DurationPresetItem(
                            preset = preset,
                            onEdit = {
                                editingPreset = preset
                                hours = preset.toHorses().toInt()
                                minutes = preset.toMinutesInHours().toInt()
                            },
                            onDelete = {
                                presetItems = presetItems.filterNot { it == preset }
                                if (editingPreset == preset) editingPreset = null
                            },
                        )
                    }
                }
                Row(
                    modifier = Modifier.padding(bottom = 20.dp, start = 16.dp, end = 24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(onClick = onDismissRequest) {
                        Text(text = TimePlannerRes.strings.cancelTitle)
                    }
                    TextButton(
                        enabled = presetItems.isNotEmpty(),
                        onClick = { onConfirmPresets(presetItems.normalizeDurationPresets()) },
                    ) {
                        Text(text = TimePlannerRes.strings.okConfirmTitle)
                    }
                }
            }
        }
    }
}

@Composable
private fun DurationPresetItem(
    modifier: Modifier = Modifier,
    preset: Long,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceContainerHighest,
        shape = MaterialTheme.shapes.large,
    ) {
        Row(
            modifier = Modifier.padding(start = 16.dp, end = 8.dp, top = 4.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = preset.toMinutesAndHoursTitle(),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge,
            )
            IconButton(modifier = Modifier.size(32.dp), onClick = onEdit) {
                Icon(imageVector = Icons.Filled.Edit, contentDescription = null)
            }
            IconButton(modifier = Modifier.size(32.dp), onClick = onDelete) {
                Icon(imageVector = Icons.Filled.Delete, contentDescription = null)
            }
        }
    }
}

private fun List<Long>.normalizeDurationPresets(): List<Long> {
    return map { it.toMinutes() }
        .filter { it in MIN_PRESET_MINUTES..MAX_PRESET_MINUTES }
        .distinct()
        .sorted()
        .map { it.toInt().minutesToMillis() }
}

private fun createDuration(hours: Int?, minutes: Int?): Long? {
    val totalMinutes = (hours ?: return null) * Constants.Date.MINUTES_IN_HOUR + (minutes ?: return null)
    return totalMinutes
        .takeIf { it in MIN_PRESET_MINUTES..MAX_PRESET_MINUTES }
        ?.toInt()
        ?.minutesToMillis()
}

private fun defaultDurationPresets(): List<Long> {
    return Constants.Date.DEFAULT_DURATION_PRESETS.split(",").map { it.toInt().minutesToMillis() }
}

private const val MIN_PRESET_MINUTES = 1L
private const val MAX_PRESET_MINUTES = 1440L
