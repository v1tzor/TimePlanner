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

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ru.aleshin.core.ui.theme.TimePlannerRes
import ru.aleshin.core.ui.views.DialogButtons
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
    onChangeCategory: (SubCategoryUi?) -> Unit,
    onAddSubCategory: (String) -> Unit,
) {
    val openDialog = rememberSaveable { mutableStateOf(false) }
    Surface(
        enabled = enabled,
        onClick = { openDialog.value = true },
        modifier = modifier.sizeIn(minHeight = 68.dp),
        shape = MaterialTheme.shapes.medium,
        tonalElevation = TimePlannerRes.elevations.levelOne,
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
                contentDescription = EditorThemeRes.strings.mainCategoryChooserExpandedIconDesc,
                tint = tint,
            )
        }
    }
    if (openDialog.value) {
        SubCategoryDialogChooser(
            initCategory = currentSubCategory,
            mainCategory = mainCategory,
            allSubCategories = allSubCategories,
            onDismiss = { openDialog.value = false },
            onAddCategory = { onAddSubCategory(it) },
            onChooseSubCategory = { onChangeCategory(it); openDialog.value = false },
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun SubCategoryDialogChooser(
    modifier: Modifier = Modifier,
    initCategory: SubCategoryUi?,
    mainCategory: MainCategoryUi?,
    allSubCategories: List<SubCategoryUi>,
    onDismiss: () -> Unit,
    onChooseSubCategory: (SubCategoryUi?) -> Unit,
    onAddCategory: (String) -> Unit,
) {
    val initItem = initCategory?.let { allSubCategories.find { it.id == initCategory.id } }
    val initPosition = initItem?.let { allSubCategories.indexOf(it) } ?: 0
    val listState = rememberLazyListState(initPosition)
    var selectedSubCategory by rememberSaveable { mutableStateOf(initCategory) }

    AlertDialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = modifier.width(280.dp).wrapContentHeight(),
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = TimePlannerRes.elevations.levelThree,
        ) {
            Column {
                Column(
                    modifier = Modifier.padding(
                        top = 24.dp,
                        bottom = 8.dp,
                        start = 24.dp,
                        end = 24.dp,
                    ),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    Text(
                        text = EditorThemeRes.strings.subCategoryChooserTitle,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.headlineSmall,
                    )
                    Text(
                        text = EditorThemeRes.strings.subCategoryDialogMainCategoryFormat.format(
                            mainCategory?.fetchName() ?: EditorThemeRes.strings.categoryNotSelectedTitle,
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
                LazyColumn(modifier = Modifier.height(300.dp), state = listState) {
                    item {
                        SubCategoryDialogItem(
                            selected = selectedSubCategory == null,
                            title = EditorThemeRes.strings.categoryNotSelectedTitle,
                            description = null,
                            onSelectedChange = { selectedSubCategory = null },
                        )
                    }
                    items(allSubCategories) { subCategory ->
                        SubCategoryDialogItem(
                            selected = selectedSubCategory == subCategory,
                            title = subCategory.name ?: TimePlannerRes.strings.categoryEmptyTitle,
                            description = subCategory.description,
                            onSelectedChange = { selectedSubCategory = subCategory },
                        )
                    }
                    item {
                        if (mainCategory?.id != 0 && mainCategory != null) {
                            AddCategoriesDialogItem(
                                modifier = Modifier.fillMaxWidth(),
                                onAddCategory = onAddCategory,
                            )
                        }
                    }
                }
                DialogButtons(
                    onCancelClick = onDismiss,
                    onConfirmClick = { onChooseSubCategory.invoke(selectedSubCategory) },
                )
            }
        }
    }
}

@Composable
internal fun SubCategoryDialogItem(
    modifier: Modifier = Modifier,
    selected: Boolean,
    title: String,
    description: String?,
    onSelectedChange: () -> Unit,
) {
    Column {
        Row(
            modifier = modifier
                .padding(vertical = 8.dp, horizontal = 16.dp)
                .clip(MaterialTheme.shapes.medium)
                .clickable(onClick = onSelectedChange)
                .padding(start = 8.dp, end = 16.dp)
                .sizeIn(minHeight = 56.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            RadioButton(selected = selected, onClick = null)
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.Start,
            ) {
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.onSurface,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 4,
                    style = MaterialTheme.typography.bodyLarge,
                )
                if (description != null) {
                    Text(
                        text = description,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
        Divider(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            color = MaterialTheme.colorScheme.outlineVariant,
        )
    }
}

@Composable
internal fun AddCategoriesDialogItem(
    modifier: Modifier = Modifier,
    onAddCategory: (String) -> Unit,
) {
    var isSubCategoryEdited by remember { mutableStateOf(false) }
    var subCategoryEditedName by remember { mutableStateOf("") }

    Column {
        Row(
            modifier = modifier
                .padding(vertical = 4.dp, horizontal = 24.dp).height(48.dp)
                .clip(MaterialTheme.shapes.medium)
                .clickable(onClick = { isSubCategoryEdited = true }),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                modifier = Modifier.size(24.dp),
                imageVector = when (isSubCategoryEdited) {
                    true -> Icons.Default.Edit
                    false -> Icons.Default.Add
                },
                contentDescription = EditorThemeRes.strings.subCategoryDialogAddedTitle,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            if (!isSubCategoryEdited) {
                Text(
                    text = EditorThemeRes.strings.subCategoryDialogAddedTitle,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyLarge,
                )
            } else {
                val focusRequester = remember { FocusRequester() }
                BasicTextField(
                    modifier = Modifier.weight(1f).padding(vertical = 4.dp).focusRequester(focusRequester),
                    value = subCategoryEditedName,
                    onValueChange = { subCategoryEditedName = it },
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                    ),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
                )
                IconButton(onClick = { isSubCategoryEdited = false; subCategoryEditedName = "" }) {
                    Icon(
                        modifier = Modifier.size(18.dp),
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                }

                LaunchedEffect(key1 = isSubCategoryEdited) {
                    when (isSubCategoryEdited) {
                        true -> focusRequester.requestFocus()
                        false -> focusRequester.freeFocus()
                    }
                }
            }
        }
        if (isSubCategoryEdited) {
            Button(
                onClick = { if (subCategoryEditedName.isNotEmpty()) onAddCategory(subCategoryEditedName) },
                modifier = Modifier.fillMaxWidth().padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = 4.dp,
                    bottom = 8.dp,
                ),
            ) {
                Text(text = EditorThemeRes.strings.subCategoryDialogAddedTitle)
            }
        }
        Divider(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            color = MaterialTheme.colorScheme.outlineVariant,
        )
    }
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
