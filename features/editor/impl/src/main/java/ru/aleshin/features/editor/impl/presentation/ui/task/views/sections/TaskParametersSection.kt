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
package ru.aleshin.features.editor.impl.presentation.ui.task.views.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import ru.aleshin.features.editor.impl.presentation.mappers.convertToItem
import ru.aleshin.features.editor.impl.presentation.mappers.convertToModel
import ru.aleshin.features.editor.impl.presentation.models.tasks.TaskPriorityItemUi
import ru.aleshin.features.editor.impl.presentation.models.tasks.TimeTaskEditParametersUi
import ru.aleshin.features.editor.impl.presentation.theme.EditorThemeRes
import ru.aleshin.features.editor.impl.presentation.ui.task.views.ParameterChooser
import ru.aleshin.features.editor.impl.presentation.ui.task.views.SegmentedParametersChooser
import ru.aleshin.features.editor.impl.presentation.ui.task.views.TaskNotificationsMenu

/**
 * @author Stanislav Aleshin on 30.07.2026.
 */
@Composable
internal fun TaskParametersSection(
    modifier: Modifier = Modifier,
    parameters: TimeTaskEditParametersUi,
    isEnabled: Boolean = true,
    onParametersChange: (TimeTaskEditParametersUi) -> Unit,
) {
    var isNotificationsMenuOpen by remember { mutableStateOf(false) }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        SegmentedParametersChooser(
            enabled = isEnabled,
            parameters = TaskPriorityItemUi.entries.toTypedArray(),
            selected = parameters.priority.convertToItem(),
            leadingIcon = painterResource(EditorThemeRes.icons.priority),
            title = EditorThemeRes.strings.priorityParameterTitle,
            onChangeSelected = { priority ->
                val updatedParameters = parameters.copy(priority = priority.convertToModel())
                onParametersChange(updatedParameters)
            },
        )
        ParameterChooser(
            enabled = isEnabled,
            selected = parameters.isEnableNotification,
            leadingIcon = painterResource(EditorThemeRes.icons.notifications),
            title = EditorThemeRes.strings.notifyParameterTitle,
            description = EditorThemeRes.strings.notifyParameterDesc,
            optionsButton = if (parameters.isEnableNotification) {
                {
                    Box {
                        IconButton(
                            modifier = Modifier.size(32.dp),
                            enabled = isEnabled,
                            onClick = { isNotificationsMenuOpen = true },
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            ),
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = null,
                            )
                        }
                        TaskNotificationsMenu(
                            isExpanded = isNotificationsMenuOpen,
                            taskNotification = parameters.taskNotifications,
                            onDismiss = { isNotificationsMenuOpen = false },
                            onUpdate = { notifications ->
                                val updatedParameters = parameters.copy(
                                    taskNotifications = notifications,
                                )
                                onParametersChange(updatedParameters)
                            },
                        )
                    }
                }
            } else {
                null
            },
            onChangeSelected = { isNotificationEnabled ->
                val updatedParameters = parameters.copy(
                    isEnableNotification = isNotificationEnabled,
                )
                onParametersChange(updatedParameters)
            },
        )
        ParameterChooser(
            enabled = isEnabled,
            selected = parameters.isConsiderInStatistics,
            leadingIcon = painterResource(EditorThemeRes.icons.statistics),
            title = EditorThemeRes.strings.statisticsParameterTitle,
            description = EditorThemeRes.strings.statisticsParameterDesc,
            onChangeSelected = { isStatisticsEnabled ->
                val updatedParameters = parameters.copy(
                    isConsiderInStatistics = isStatisticsEnabled,
                )
                onParametersChange(updatedParameters)
            },
        )
    }
}
