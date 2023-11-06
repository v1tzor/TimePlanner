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
package ru.aleshin.features.home.impl.presentation.ui.overview.views

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import ru.aleshin.core.ui.theme.TimePlannerRes
import ru.aleshin.core.ui.views.DialogButtons
import ru.aleshin.core.utils.extensions.generateUniqueKey
import ru.aleshin.core.utils.functional.Constants
import ru.aleshin.features.home.impl.presentation.models.categories.CategoriesUi
import ru.aleshin.features.home.impl.presentation.models.categories.MainCategoryUi
import ru.aleshin.features.home.impl.presentation.models.schedules.UndefinedTaskUi
import ru.aleshin.features.home.impl.presentation.theme.HomeThemeRes
import ru.aleshin.features.home.impl.presentation.ui.common.CompactCategoryChooser
import ru.aleshin.features.home.impl.presentation.ui.common.CompactImportanceChooser
import ru.aleshin.features.home.impl.presentation.ui.common.CompactSubCategoryChooser
import java.util.Date

/**
 * @author Stanislav Aleshin on 02.11.2023.
 */
@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun UndefinedTaskEditorDialog(
    modifier: Modifier = Modifier,
    categories: List<CategoriesUi>,
    model: UndefinedTaskUi?,
    onDismiss: () -> Unit,
    onConfirm: (UndefinedTaskUi) -> Unit,
) {
    val scrollState = rememberScrollState()
    var mainCategory by remember { mutableStateOf(model?.mainCategory ?: MainCategoryUi()) }
    var subCategory by remember { mutableStateOf(model?.subCategory) }
    var note by remember { mutableStateOf(model?.note) }
    var deadline by remember { mutableStateOf(model?.deadline) }
    var isImportance by remember { mutableStateOf(model?.isImportant ?: false) }

    AlertDialog(
        onDismissRequest = onDismiss,
    ) {
        Surface(
            modifier = modifier.width(328.dp).wrapContentHeight(),
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = TimePlannerRes.elevations.levelThree,
        ) {
            Column {
                UndefinedTaskEditorDialogHeader()
                Divider(Modifier.fillMaxWidth())
                Column(
                    modifier = Modifier
                        .height(360.dp)
                        .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 0.dp)
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    CompactCategoryChooser(
                        allCategories = categories,
                        selectedCategory = mainCategory,
                        onCategoryChange = { mainCategory = it; subCategory = null },
                    )
                    CompactSubCategoryChooser(
                        allCategories = categories,
                        selectedMainCategory = mainCategory,
                        selectedSubCategory = subCategory,
                        onSubCategoryChange = { subCategory = it },
                    )
                    NoteCompactTextField(
                        note = note,
                        onNoteChange = { note = it },
                    )
                    DeadlineChooser(
                        deadline = deadline,
                        onChooseDeadline = { deadline = it },
                    )
                    CompactImportanceChooser(
                        isImportance = isImportance,
                        onImportanceChange = { isImportance = it },
                    )
                }

                val isEnabled = mainCategory.id != 0
                DialogButtons(
                    isConfirmEnabled = isEnabled,
                    confirmTitle = when (model != null) {
                        true -> TimePlannerRes.strings.alertDialogOkConfirmTitle
                        false -> HomeThemeRes.strings.dialogCreateTitle
                    },
                    onConfirmClick = {
                        if (isEnabled) {
                            val task = UndefinedTaskUi(
                                id = model?.id ?: generateUniqueKey(),
                                createdAt = Date(),
                                mainCategory = mainCategory,
                                subCategory = subCategory,
                                isImportant = isImportance,
                                deadline = deadline,
                                note = note,
                            )
                            onConfirm(task)
                        }
                    },
                    onCancelClick = onDismiss,
                )
            }
        }
    }
}

@Composable
internal fun UndefinedTaskEditorDialogHeader(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(top = 24.dp, bottom = 12.dp, start = 24.dp, end = 24.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Text(
            text = HomeThemeRes.strings.addTaskTitle,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.headlineSmall,
        )
    }
}

@Composable
internal fun NoteCompactTextField(
    modifier: Modifier = Modifier,
    note: String?,
    onNoteChange: (String?) -> Unit,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    val focusManager = LocalFocusManager.current
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Icon(
            painter = painterResource(id = HomeThemeRes.icons.subCategory),
            contentDescription = HomeThemeRes.strings.subCategoryLabel,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        OutlinedTextField(
            modifier = Modifier.weight(1f),
            value = note ?: "",
            onValueChange = {
                if (it.length <= Constants.Text.MAX_NOTE_LENGTH) onNoteChange(it.ifEmpty { null })
            },
            readOnly = true,
            label = { Text(text = HomeThemeRes.strings.noteTitle) },
            trailingIcon = if (interactionSource.collectIsFocusedAsState().value) { {
                IconButton(
                    modifier = Modifier.size(32.dp),
                    onClick = { focusManager.clearFocus(); },
                ) {
                    Icon(
                        imageVector = Icons.Outlined.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                }
            } } else {
                null
            },
            interactionSource = interactionSource,
            colors = OutlinedTextFieldDefaults.colors(
                cursorColor = MaterialTheme.colorScheme.onSurface,
            ),
        )
    }
}
