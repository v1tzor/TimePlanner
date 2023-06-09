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
 * imitations under the License.
 */
package ru.aleshin.features.editor.impl.presentation.ui.editor.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.aleshin.core.ui.theme.TimePlannerRes
import ru.aleshin.core.ui.views.DialogButtons
import ru.aleshin.features.editor.impl.presentation.theme.EditorThemeRes

/**
 * @author Stanislav Aleshin on 09.05.2023.
 */
@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun TemplateSaveWarningDialog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    onAction: (isSave: Boolean) -> Unit,
) {
    AlertDialog(
        modifier = modifier.width(280.dp),
        onDismissRequest = onDismiss,
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = TimePlannerRes.elevations.levelThree,
        ) {
            Column {
                TemplateWarningDialogHeader(modifier = Modifier.fillMaxWidth())
                Text(
                    modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 16.dp)
                        .fillMaxWidth(),
                    text = EditorThemeRes.strings.saveTemplateWarningTitle,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                )
                DialogButtons(
                    confirmFirstTitle = EditorThemeRes.strings.saveTemplateWarningUnsave,
                    confirmSecondTitle = EditorThemeRes.strings.saveTemplateWarningConfirm,
                    onCancelClick = onDismiss,
                    onConfirmFirstClick = { onAction(false) },
                    onConfirmSecondClick = { onAction(true) },
                )
            }
        }
    }
}

@Composable
internal fun TemplateWarningDialogHeader(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(start = 24.dp, end = 24.dp, top = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary,
        )
        Text(
            text = TimePlannerRes.strings.warningDialogTitle,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.headlineSmall,
        )
    }
}
