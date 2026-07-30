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

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import ru.aleshin.features.editor.impl.presentation.theme.EditorThemeRes
import ru.aleshin.timeplanner.core.ui.views.TopAppBarButton
import ru.aleshin.timeplanner.core.ui.views.TopAppBarEmptyButton
import ru.aleshin.timeplanner.core.ui.views.TopAppBarTitle

/**
 * @author Stanislav Aleshin on 28.07.2026.
 */
@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun GoalEditorTopAppBar(
    modifier: Modifier = Modifier,
    isCompact: Boolean,
    isEditing: Boolean,
    onBackClick: () -> Unit,
) {
    TopAppBar(
        modifier = modifier,
        title = {
            TopAppBarTitle(
                text = if (isEditing) {
                    EditorThemeRes.strings.editGoalTitle
                } else {
                    EditorThemeRes.strings.createGoalTitle
                },
                textAlign = if (isCompact) TextAlign.Center else TextAlign.Start,
            )
        },
        navigationIcon = {
            TopAppBarButton(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                imageDescription = EditorThemeRes.strings.topAppBarBackIconDesc,
                onButtonClick = onBackClick,
            )
        },
        actions = {
            if (isCompact) TopAppBarEmptyButton()
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
        ),
    )
}
