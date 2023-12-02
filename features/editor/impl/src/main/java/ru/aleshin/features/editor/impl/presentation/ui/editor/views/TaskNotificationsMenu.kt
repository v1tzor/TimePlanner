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

import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.DropdownMenu
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import ru.aleshin.core.ui.views.CheckedMenuItem
import ru.aleshin.features.editor.impl.presentation.models.editmodel.TaskNotificationsUi
import ru.aleshin.features.editor.impl.presentation.theme.EditorThemeRes

/**
 * @author Stanislav Aleshin on 10.11.2023.
 */
@Composable
internal fun TaskNotificationsMenu(
    modifier: Modifier = Modifier,
    isExpanded: Boolean,
    taskNotification: TaskNotificationsUi,
    onDismiss: () -> Unit,
    onUpdate: (TaskNotificationsUi) -> Unit,
) {
    DropdownMenu(
        expanded = isExpanded,
        onDismissRequest = onDismiss,
        modifier = modifier.sizeIn(maxHeight = 200.dp),
        offset = DpOffset(0.dp, 6.dp),
    ) {
        CheckedMenuItem(
            text = EditorThemeRes.strings.fifteenMinutesBeforeTitle,
            check = taskNotification.fifteenMinutesBefore,
            onCheckedChange = { onUpdate(taskNotification.copy(fifteenMinutesBefore = it)) },
        )
        CheckedMenuItem(
            text = EditorThemeRes.strings.oneHourBeforeTitle,
            check = taskNotification.oneHourBefore,
            onCheckedChange = { onUpdate(taskNotification.copy(oneHourBefore = it)) },
        )
        CheckedMenuItem(
            text = EditorThemeRes.strings.threeHourBeforeTitle,
            check = taskNotification.threeHourBefore,
            onCheckedChange = { onUpdate(taskNotification.copy(threeHourBefore = it)) },
        )
        CheckedMenuItem(
            text = EditorThemeRes.strings.oneDayBeforeTitle,
            check = taskNotification.oneDayBefore,
            onCheckedChange = { onUpdate(taskNotification.copy(oneDayBefore = it)) },
        )
        CheckedMenuItem(
            text = EditorThemeRes.strings.oneWeekBeforeTitle,
            check = taskNotification.oneWeekBefore,
            onCheckedChange = { onUpdate(taskNotification.copy(oneWeekBefore = it)) },
        )
        CheckedMenuItem(
            text = EditorThemeRes.strings.beforeEndTitle,
            check = taskNotification.beforeEnd,
            onCheckedChange = { onUpdate(taskNotification.copy(beforeEnd = it)) },
        )
    }
}
