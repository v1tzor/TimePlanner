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

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import ru.aleshin.core.ui.theme.TimePlannerRes
import ru.aleshin.core.ui.views.BaseSelectorBottomSheet
import ru.aleshin.core.ui.views.SelectorAddItemView
import ru.aleshin.core.ui.views.SelectorNotSelectedItemView
import ru.aleshin.core.ui.views.SelectorSwipeItemView
import ru.aleshin.core.ui.views.SelectorTextField
import ru.aleshin.core.ui.views.SwipeToDismissBackground
import ru.aleshin.core.utils.functional.Constants
import ru.aleshin.features.editor.impl.presentation.models.categories.MainCategoryUi
import ru.aleshin.features.editor.impl.presentation.models.categories.SubCategoryUi
import ru.aleshin.features.editor.impl.presentation.theme.EditorThemeRes

/**
 * @author Stanislav Aleshin on 26.02.2023.
 */
@Composable
internal fun SubCategoryChooser(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    mainCategory: MainCategoryUi?,
    allSubCategories: List<SubCategoryUi>,
    currentSubCategory: SubCategoryUi?,
    onChangeSubCategory: (SubCategoryUi?) -> Unit,
    onEditSubCategory: (SubCategoryUi) -> Unit,
    onAddSubCategory: (String) -> Unit,
) {
    var openSubCategorySelectorSheet by rememberSaveable { mutableStateOf(false) }
    Surface(
        enabled = enabled,
        onClick = { openSubCategorySelectorSheet = true },
        modifier = modifier.sizeIn(minHeight = 68.dp),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
        Row(
            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp, start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(id = EditorThemeRes.icons.subCategory),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
            )
            Column(modifier = Modifier.weight(1f).animateContentSize()) {
                val mainTitle = if (mainCategory != null) currentSubCategory?.name else ""
                Text(
                    text = EditorThemeRes.strings.subCategoryChooserTitle,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelMedium,
                )
                Text(
                    text = mainTitle ?: EditorThemeRes.strings.categoryNotSelectedTitle,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleMedium,
                )
            }

            val icon = when (currentSubCategory != null) {
                true -> EditorThemeRes.icons.showDialog
                false -> EditorThemeRes.icons.add
            }
            val tint = when (mainCategory != null) {
                true -> MaterialTheme.colorScheme.onSurface
                false -> MaterialTheme.colorScheme.surfaceVariant
            }
            Icon(
                modifier = Modifier.animateContentSize(),
                painter = painterResource(icon),
                contentDescription = EditorThemeRes.strings.chooseCategoryTitle,
                tint = tint,
            )
        }
    }
    if (openSubCategorySelectorSheet) {
        SubCategorySelectorBottomSheet(
            initCategory = currentSubCategory,
            mainCategory = mainCategory,
            allSubCategories = allSubCategories,
            onDismiss = { openSubCategorySelectorSheet = false },
            onAddCategory = onAddSubCategory,
            onEditSubCategory = onEditSubCategory,
            onChooseSubCategory = { onChangeSubCategory(it); openSubCategorySelectorSheet = false },
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun SubCategorySelectorBottomSheet(
    modifier: Modifier = Modifier,
    initCategory: SubCategoryUi?,
    mainCategory: MainCategoryUi?,
    allSubCategories: List<SubCategoryUi>,
    onDismiss: () -> Unit,
    onChooseSubCategory: (SubCategoryUi?) -> Unit,
    onEditSubCategory: (SubCategoryUi) -> Unit,
    onAddCategory: (String) -> Unit,
) {
    var selectedSubCategory by rememberSaveable { mutableStateOf(initCategory) }
    var searchQuery by rememberSaveable { mutableStateOf<String?>(null) }
    var isEdited by remember { mutableStateOf(false) }
    var editableSubCategory by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val searchedSubCategory = remember(searchQuery, allSubCategories) {
        allSubCategories.filter { category ->
            searchQuery == null || (category.name?.contains(searchQuery ?: "", true) == true)
        }
    }

    BaseSelectorBottomSheet(
        modifier = modifier,
        selected = selectedSubCategory,
        items = searchedSubCategory,
        header = EditorThemeRes.strings.subCategoryChooserTitle,
        title = EditorThemeRes.strings.subCategoryDialogMainCategoryFormat.format(
            mainCategory?.fetchName() ?: EditorThemeRes.strings.categoryNotSelectedTitle,
        ),
        itemView = { subCategory ->
            val dismissState = rememberSwipeToDismissBoxState(
                confirmValueChange = { dismissBoxValue ->
                    when (dismissBoxValue) {
                        SwipeToDismissBoxValue.StartToEnd -> Unit
                        SwipeToDismissBoxValue.EndToStart -> onEditSubCategory(subCategory)
                        SwipeToDismissBoxValue.Settled -> Unit
                    }
                    return@rememberSwipeToDismissBoxState false
                },
                positionalThreshold = { it * .60f },
            )
            SelectorSwipeItemView(
                onClick = { selectedSubCategory = subCategory },
                state = dismissState,
                selected = subCategory.id == selectedSubCategory?.id,
                title = subCategory.name ?: TimePlannerRes.strings.categoryEmptyTitle,
                label = subCategory.description,
                enableDismissFromStartToEnd = false,
                backgroundContent = {
                    SwipeToDismissBackground(
                        dismissState = dismissState,
                        endToStartContent = {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                            )
                        },
                        endToStartColor = MaterialTheme.colorScheme.tertiaryContainer,
                    )
                },
            )
        },
        addItemView = if (searchQuery == null) {
            {
                AnimatedContent(targetState = isEdited, label = "subCategory") { edit ->
                    if (edit) {
                        SelectorTextField(
                            modifier = Modifier.focusRequester(focusRequester),
                            value = editableSubCategory,
                            onValueChange = {
                                if (it.length < Constants.Text.MAX_LENGTH) {
                                    editableSubCategory = it
                                }
                            },
                            onDismiss = {
                                editableSubCategory = ""
                                isEdited = false
                            },
                            maxLines = 1,
                            onConfirm = {
                                onAddCategory(editableSubCategory)
                                editableSubCategory = ""
                                isEdited = false
                            }
                        )
                        SideEffect { focusRequester.requestFocus() }
                    } else {
                        SelectorAddItemView(
                            text = EditorThemeRes.strings.subCategoryDialogAddedTitle,
                            enabled = mainCategory?.id != 0 && mainCategory != null,
                            onClick = { isEdited = true },
                        )
                    }
                }
            }
        } else {
            null
        },
        notSelectedItem = if (searchQuery == null) {
            {
                SelectorNotSelectedItemView(
                    text = EditorThemeRes.strings.categoryNotSelectedTitle,
                    selected = selectedSubCategory == null,
                    onClick = { selectedSubCategory = null },
                )
            }
        } else {
            null
        },
        searchBar = {
            SearchBar(
                inputField = {
                    val focusManager = LocalFocusManager.current
                    val searchInteractionSource = remember { MutableInteractionSource() }
                    val isFocus = searchInteractionSource.collectIsFocusedAsState().value

                    SearchBarDefaults.InputField(
                        query = searchQuery ?: "",
                        onQueryChange = { searchQuery = it.takeIf { it.isNotBlank() } },
                        onSearch = {
                            searchQuery = it.takeIf { it.isNotBlank() }
                            focusManager.clearFocus()
                        },
                        expanded = false,
                        onExpandedChange = {},
                        enabled = true,
                        placeholder = {
                            Text(text = EditorThemeRes.strings.chooseCategoryTitle)
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = EditorThemeRes.strings.topAppBarBackIconDesc,
                                tint = MaterialTheme.colorScheme.onSurface,
                            )
                        },
                        trailingIcon = {
                            AnimatedVisibility(
                                visible = isFocus,
                                enter = fadeIn() + scaleIn(),
                                exit = fadeOut() + scaleOut(),
                            ) {
                                IconButton(
                                    onClick = {
                                        focusManager.clearFocus()
                                        searchQuery = null
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error,
                                    )
                                }
                            }
                        },
                        interactionSource = searchInteractionSource,
                    )
                },
                expanded = false,
                onExpandedChange = {},
                modifier = Modifier.fillMaxWidth(),
                windowInsets = WindowInsets(0.dp),
                content = {},
            )
        },
        onDismissRequest = onDismiss,
        onConfirm = onChooseSubCategory,
    )
}

/* ----------------------- Release Preview -----------------------
@Composable
@Preview(showBackground = true)
private fun SubCategoryDialogChooser_Preview() {
    TimePlannerTheme(
        dynamicColor = false,
        themeColorsType = ThemeColorsUiType.DARK,
    ) {
        EditorTheme {
            SubCategoryDialogChooser(
                onCloseDialog = { },
                mainCategory = MainCategory(englishName = "Work", name = "Работа"),
                allSubCategories = emptyList(),
                initCategory = null,
                onChooseSubCategory = {},
                onAddSubCategory = {},
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun SubCategoryChooser_Preview() {
    TimePlannerTheme(dynamicColor = false, themeColorsType = ThemeColorsUiType.DARK) {
        EditorTheme {
            val category = rememberSaveable { mutableStateOf<SubCategory?>(null) }
            SubCategoryChooser(
                modifier = Modifier.fillMaxWidth(),
                mainCategory = MainCategory(englishName = "Work", name = "Работа"),
                allSubCategories = emptyList(),
                currentSubCategory = category.value,
                onSubCategoryChoose = {},
                onAddSubCategory = {},
            )
        }
    }
}
*/
