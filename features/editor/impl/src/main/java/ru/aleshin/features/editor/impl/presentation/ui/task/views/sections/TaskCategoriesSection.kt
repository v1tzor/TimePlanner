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
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import ru.aleshin.core.presentation.models.categories.MainCategoryDetailsUi
import ru.aleshin.core.presentation.models.categories.MainCategoryUi
import ru.aleshin.core.presentation.models.categories.SubCategoryUi
import ru.aleshin.core.utils.functional.Constants
import ru.aleshin.features.editor.impl.presentation.theme.EditorThemeRes
import ru.aleshin.features.editor.impl.presentation.ui.task.views.MainCategoryChooser
import ru.aleshin.features.editor.impl.presentation.ui.task.views.SubCategoryChooser
import ru.aleshin.timeplanner.core.ui.views.CustomLargeTextField

/**
 * @author Stanislav Aleshin on 30.07.2026.
 */
@Composable
internal fun TaskCategoriesSection(
    modifier: Modifier = Modifier,
    mainCategory: MainCategoryUi?,
    subCategory: SubCategoryUi?,
    allCategories: List<MainCategoryDetailsUi>,
    note: String?,
    isEnabled: Boolean = true,
    isError: Boolean,
    showNote: Boolean,
    onEditCategory: (MainCategoryUi) -> Unit,
    onEditSubCategory: (SubCategoryUi) -> Unit,
    onCategoriesChange: (MainCategoryUi, SubCategoryUi?) -> Unit,
    onAddCategory: () -> Unit,
    onAddSubCategory: (String) -> Unit,
    onNoteChange: (String?) -> Unit,
) {
    val mainCategories = remember(allCategories) {
        allCategories.map { details -> details.mainCategory }
    }
    val selectedCategoryDetails = remember(allCategories, mainCategory) {
        allCategories.find { details -> details.mainCategory == mainCategory }
    }

    Column(
        modifier = modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(
            modifier = Modifier.animateContentSize(),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            MainCategoryChooser(
                modifier = Modifier.fillMaxWidth(),
                enabled = isEnabled,
                isError = isError,
                currentCategory = mainCategory,
                allCategories = mainCategories,
                onEditCategory = onEditCategory,
                onAddCategory = onAddCategory,
                onChangeCategory = { category ->
                    onCategoriesChange(category, null)
                },
            )
            AnimatedVisibility(visible = isError) {
                Text(
                    text = EditorThemeRes.strings.categoryValidateError,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.error,
                )
            }
        }
        SubCategoryChooser(
            modifier = Modifier.fillMaxWidth(),
            enabled = isEnabled,
            mainCategory = mainCategory,
            allSubCategories = selectedCategoryDetails?.subCategories.orEmpty(),
            currentSubCategory = subCategory,
            onAddSubCategory = onAddSubCategory,
            onEditSubCategory = onEditSubCategory,
            onChangeSubCategory = { category ->
                if (mainCategory != null) {
                    onCategoriesChange(mainCategory, category)
                }
            },
        )
        if (showNote) {
            TaskNoteField(
                note = note,
                onNoteChange = onNoteChange,
            )
        }
    }
}

@Composable
internal fun TaskNoteField(
    modifier: Modifier = Modifier,
    note: String?,
    enabled: Boolean = true,
    onNoteChange: (String?) -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val interactionSource = remember { MutableInteractionSource() }
    var editableNote by remember {
        mutableStateOf(TextFieldValue(text = note.orEmpty()))
    }

    LaunchedEffect(note) {
        val updatedNote = note.orEmpty()
        if (editableNote.text != updatedNote) {
            editableNote = TextFieldValue(text = updatedNote)
        }
    }

    CustomLargeTextField(
        modifier = modifier,
        enabled = enabled,
        text = editableNote,
        onTextChange = { value ->
            if (value.text.length <= Constants.Text.MAX_NOTE_LENGTH) {
                editableNote = value
                onNoteChange(value.text.ifEmpty { null })
            }
        },
        label = { Text(text = EditorThemeRes.strings.noteLabel) },
        placeholder = { Text(text = EditorThemeRes.strings.notePlaceholder) },
        leadingIcon = {
            Icon(
                painter = painterResource(EditorThemeRes.icons.notesField),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
            )
        },
        maxLines = 4,
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
        interactionSource = interactionSource,
    )
}
