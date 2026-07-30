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
package ru.aleshin.features.editor.impl.presentation.ui.task.views

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Badge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import ru.aleshin.features.editor.impl.presentation.theme.EditorThemeRes
import ru.aleshin.timeplanner.core.ui.theme.TimePlannerRes
import ru.aleshin.timeplanner.core.ui.views.TopAppBarButton
import ru.aleshin.timeplanner.core.ui.views.TopAppBarEmptyButton
import ru.aleshin.timeplanner.core.ui.views.TopAppBarTitle

/**
 * @author Stanislav Aleshin on 25.02.2023.
 */
@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun EditorTopAppBar(
    modifier: Modifier = Modifier,
    isCompact: Boolean,
    actionsEnabled: Boolean = true,
    countUndefinedTasks: Int,
    onBackIconClick: () -> Unit,
    onOpenUndefinedTasks: () -> Unit,
    onDeleteActionClick: () -> Unit,
    onTemplatesActionClick: () -> Unit,
) {
    TopAppBar(
        modifier = modifier,
        title = {
            TopAppBarTitle(
                text = EditorThemeRes.strings.topAppBarEditorTitle,
                textAlign = if (isCompact) TextAlign.Center else TextAlign.Start,
            )
        },
        navigationIcon = {
            TopAppBarButton(
                imageVector = Icons.Default.ArrowBack,
                imageDescription = EditorThemeRes.strings.topAppBarBackIconDesc,
                onButtonClick = onBackIconClick,
            )
            if (isCompact) {
                TopAppBarEmptyButton()
            }
        },
        actions = {
            if (actionsEnabled) {
                TopAppBarButton(
                    imagePainter = painterResource(id = TimePlannerRes.icons.plannedTask),
                    imageDescription = null,
                    onButtonClick = onOpenUndefinedTasks,
                    badge = if (countUndefinedTasks > 0) {
                        {
                            Badge {
                                Text(text = countUndefinedTasks.toString())
                            }
                        }
                    } else {
                        null
                    },
                )
                TopAppBarButton(
                    imagePainter = painterResource(id = EditorThemeRes.icons.templates),
                    imageDescription = EditorThemeRes.strings.topAppBarTemplatesTitle,
                    onButtonClick = onTemplatesActionClick,
                )
                TopAppBarButton(
                    imagePainter = painterResource(id = EditorThemeRes.icons.delete),
                    imageDescription = EditorThemeRes.strings.topAppBarDeleteTitle,
                    onButtonClick = onDeleteActionClick,
                )
            } else {
                TopAppBarEmptyButton()
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
        ),
    )
}