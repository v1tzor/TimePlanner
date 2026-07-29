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
package ru.aleshin.features.editor.impl.presentation.ui.goal.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import ru.aleshin.features.editor.impl.presentation.models.goals.GoalEditUi
import ru.aleshin.features.editor.impl.presentation.theme.EditorThemeRes
import ru.aleshin.features.editor.impl.presentation.ui.goal.validators.GoalValidationError
import ru.aleshin.timeplanner.core.ui.views.CustomLargeTextField

/**
 * @author Stanislav Aleshin on 28.07.2026.
 */
@Composable
internal fun GoalNameSection(
    modifier: Modifier = Modifier,
    goal: GoalEditUi,
    errors: Set<GoalValidationError>,
    onTitleChange: (String) -> Unit,
) {
    val strings = EditorThemeRes.goalStrings
    val isError = GoalValidationError.TITLE in errors
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        CustomLargeTextField(
            modifier = Modifier.fillMaxWidth(),
            text = goal.title,
            onTextChange = onTitleChange,
            label = { Text(strings.titleLabel) },
            placeholder = { Text(strings.titlePlaceholder) },
            leadingIcon = {
                Icon(
                    painter = painterResource(EditorThemeRes.icons.notesField),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            },
            isError = isError,
            singleLine = true,
        )
        if (isError) {
            Text(
                text = strings.titleError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelSmall,
            )
        }
    }
}
