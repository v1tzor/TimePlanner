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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import ru.aleshin.features.editor.impl.presentation.theme.EditorThemeRes

/**
 * @author Stanislav Aleshin on 30.07.2026.
 */
@Composable
internal fun TaskActionButtonsSection(
    modifier: Modifier = Modifier,
    isCreateMode: Boolean,
    isLinkedToTemplate: Boolean,
    onUnlinkTemplate: () -> Unit,
    onCreateTemplate: () -> Unit,
    onCancelClick: () -> Unit,
    onSaveClick: () -> Unit,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.BottomStart,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            FilledTonalButton(
                onClick = onCancelClick,
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                ),
            ) {
                Text(text = EditorThemeRes.strings.cancelButtonTitle)
            }
            Button(onClick = onSaveClick) {
                Text(text = EditorThemeRes.strings.saveTaskButtonTitle)
            }
            Spacer(modifier = Modifier.weight(1f))
            TaskTemplateActions(
                isCreateMode = isCreateMode,
                isLinkedToTemplate = isLinkedToTemplate,
                onUnlink = onUnlinkTemplate,
                onCreateTemplate = onCreateTemplate,
            )
        }
    }
}

@Composable
private fun TaskTemplateActions(
    modifier: Modifier = Modifier,
    isCreateMode: Boolean,
    isLinkedToTemplate: Boolean,
    onUnlink: () -> Unit,
    onCreateTemplate: () -> Unit,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AnimatedVisibility(visible = isLinkedToTemplate) {
            IconButton(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(40.dp),
                    ),
                onClick = onUnlink,
            ) {
                Icon(
                    painter = painterResource(EditorThemeRes.icons.unlink),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                )
            }
        }
        AnimatedVisibility(visible = !isCreateMode && !isLinkedToTemplate) {
            IconButton(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = CircleShape,
                    ),
                onClick = onCreateTemplate,
            ) {
                Icon(
                    painter = painterResource(EditorThemeRes.icons.unFavorite),
                    contentDescription = EditorThemeRes.strings.templateIconDesc,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        }
    }
}
