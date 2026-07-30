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
package ru.aleshin.features.editor.impl.presentation.ui.goal.views.sections

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
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
    val isError = GoalValidationError.TITLE in errors
    val interactionSource = remember { MutableInteractionSource() }
    val focusManager = LocalFocusManager.current

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        CustomLargeTextField(
            modifier = Modifier.fillMaxWidth(),
            text = goal.title,
            onTextChange = onTitleChange,
            label = { Text(text = EditorThemeRes.strings.goalTitleLabel) },
            placeholder = {
                Text(text = EditorThemeRes.strings.goalTitlePlaceholder)
            },
            leadingIcon = {
                Icon(
                    painter = painterResource(EditorThemeRes.icons.notesField),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            },
            trailingIcon = if (interactionSource.collectIsFocusedAsState().value) {
                {
                    IconButton(
                        modifier = Modifier.size(32.dp),
                        onClick = focusManager::clearFocus,
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }
            } else {
                null
            },
            isError = isError,
            singleLine = true,
            interactionSource = interactionSource,
        )
        AnimatedVisibility(visible = isError) {
            Text(
                text = EditorThemeRes.strings.goalTitleError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelSmall,
            )
        }
    }
}
