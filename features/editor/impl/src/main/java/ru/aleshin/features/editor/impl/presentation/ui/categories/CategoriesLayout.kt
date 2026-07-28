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
package ru.aleshin.features.editor.impl.presentation.ui.categories

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import ru.aleshin.core.presentation.models.categories.MainCategoryUi
import ru.aleshin.core.presentation.models.categories.SubCategoryUi
import ru.aleshin.features.editor.impl.presentation.theme.EditorThemeRes
import ru.aleshin.features.editor.impl.presentation.ui.categories.contract.CategoriesEvent
import ru.aleshin.features.editor.impl.presentation.ui.categories.contract.CategoriesState
import ru.aleshin.features.editor.impl.presentation.ui.categories.views.MainCategoriesHorizontalList
import ru.aleshin.features.editor.impl.presentation.ui.categories.views.MainCategoriesVerticalList
import ru.aleshin.features.editor.impl.presentation.ui.categories.views.MainCategoryEditorDialog
import ru.aleshin.features.editor.impl.presentation.ui.categories.views.SubCategoriesList
import ru.aleshin.timeplanner.core.ui.theme.tokens.AdaptiveLayoutDefaults
import ru.aleshin.timeplanner.core.ui.views.AdaptiveLayoutInfo
import ru.aleshin.timeplanner.core.ui.views.AdaptiveListDetailPaneScaffold

/**
 * @author Stanislav Aleshin on 05.04.2023.
 */
@Composable
internal fun CategoriesLayout(
    modifier: Modifier = Modifier,
    state: CategoriesState,
    adaptiveLayoutInfo: AdaptiveLayoutInfo,
    onEvent: (CategoriesEvent) -> Unit,
    onAddSubCategory: () -> Unit,
) {
    var isMainCategoryCreatorOpen by rememberSaveable { mutableStateOf(false) }
    val mainPaneScrollState = rememberScrollState()
    val detailPaneScrollState = rememberScrollState()
    val categories = remember(state.categories, state.selectedMainCategory) {
        state.categories.find { it.mainCategory == state.selectedMainCategory }
    }
    val subCategories = remember(categories) {
        categories?.subCategories ?: emptyList()
    }
    val mainCategories = remember(state.categories) {
        state.categories.map { it.mainCategory }
    }
    val useListDetailPane = adaptiveLayoutInfo.useExpandedLayout ||
        adaptiveLayoutInfo.isBookPosture

    if (useListDetailPane) {
        AdaptiveListDetailPaneScaffold(
            adaptiveLayoutInfo = adaptiveLayoutInfo,
            modifier = modifier.fillMaxSize(),
            listPane = {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(mainPaneScrollState),
                ) {
                    MainCategoriesPane(
                        useVerticalLayout = true,
                        mainCategories = mainCategories,
                        selectedCategory = state.selectedMainCategory,
                        onRestoreDefaultCategories = {
                            onEvent(CategoriesEvent.RestoreDefaultCategories)
                        },
                        onChangeMainCategory = { category ->
                            onEvent(CategoriesEvent.ChangeMainCategory(category))
                        },
                        onMainCategoryUpdate = { category ->
                            onEvent(CategoriesEvent.UpdateMainCategory(category))
                        },
                        onMainCategoryDelete = { category ->
                            onEvent(CategoriesEvent.DeleteMainCategory(category))
                        },
                        onAddCategory = { isMainCategoryCreatorOpen = true },
                    )
                }
            },
            detailPane = {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(detailPaneScrollState),
                ) {
                    SubCategoriesPane(
                        selectedMainCategory = state.selectedMainCategory,
                        subCategories = subCategories,
                        onSubCategoryUpdate = { category ->
                            onEvent(CategoriesEvent.UpdateSubCategory(category))
                        },
                        onSubCategoryDelete = { category ->
                            onEvent(CategoriesEvent.DeleteSubCategory(category))
                        },
                        onAddSubCategory = onAddSubCategory,
                    )
                }
            },
        )
    } else {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter,
        ) {
            Column(
                modifier = Modifier
                    .then(
                        if (adaptiveLayoutInfo.isMediumWidth) {
                            Modifier.widthIn(max = AdaptiveLayoutDefaults.MediumContentMaxWidth)
                        } else {
                            Modifier
                        }
                    )
                    .fillMaxSize()
                    .verticalScroll(mainPaneScrollState),
            ) {
                MainCategoriesPane(
                    useVerticalLayout = false,
                    mainCategories = mainCategories,
                    selectedCategory = state.selectedMainCategory,
                    onRestoreDefaultCategories = {
                        onEvent(CategoriesEvent.RestoreDefaultCategories)
                    },
                    onChangeMainCategory = { category ->
                        onEvent(CategoriesEvent.ChangeMainCategory(category))
                    },
                    onMainCategoryUpdate = { category ->
                        onEvent(CategoriesEvent.UpdateMainCategory(category))
                    },
                    onMainCategoryDelete = { category ->
                        onEvent(CategoriesEvent.DeleteMainCategory(category))
                    },
                    onAddCategory = { isMainCategoryCreatorOpen = true },
                )
                SubCategoriesPane(
                    selectedMainCategory = state.selectedMainCategory,
                    subCategories = subCategories,
                    onSubCategoryUpdate = { category ->
                        onEvent(CategoriesEvent.UpdateSubCategory(category))
                    },
                    onSubCategoryDelete = { category ->
                        onEvent(CategoriesEvent.DeleteSubCategory(category))
                    },
                    onAddSubCategory = onAddSubCategory,
                )
            }
        }
    }
    if (isMainCategoryCreatorOpen) {
        MainCategoryEditorDialog(
            onDismiss = { isMainCategoryCreatorOpen = false },
            onConfirm = { name ->
                onEvent(CategoriesEvent.AddMainCategory(name))
                isMainCategoryCreatorOpen = false
            },
        )
    }
}

@Composable
private fun MainCategoriesPane(
    useVerticalLayout: Boolean,
    mainCategories: List<MainCategoryUi>,
    selectedCategory: MainCategoryUi?,
    onRestoreDefaultCategories: () -> Unit,
    onChangeMainCategory: (MainCategoryUi) -> Unit,
    onMainCategoryUpdate: (MainCategoryUi) -> Unit,
    onMainCategoryDelete: (MainCategoryUi) -> Unit,
    onAddCategory: () -> Unit,
) {
    MainCategoriesHeader(
        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp).fillMaxWidth(),
        onRestoreDefaultCategories = onRestoreDefaultCategories,
    )
    if (useVerticalLayout) {
        MainCategoriesVerticalList(
            modifier = Modifier.fillMaxWidth().padding(
                start = 16.dp,
                top = 8.dp,
                end = 16.dp,
                bottom = 24.dp,
            ),
            mainCategories = mainCategories,
            selectedCategory = selectedCategory,
            onSelectCategory = onChangeMainCategory,
            onUpdateCategory = onMainCategoryUpdate,
            onDeleteCategory = onMainCategoryDelete,
            onAddCategory = onAddCategory,
        )
    } else {
        MainCategoriesHorizontalList(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 12.dp),
            mainCategories = mainCategories,
            selectedCategory = selectedCategory,
            onSelectCategory = onChangeMainCategory,
            onUpdateCategory = onMainCategoryUpdate,
            onDeleteCategory = onMainCategoryDelete,
            onAddCategory = onAddCategory,
        )
    }
}

@Composable
private fun SubCategoriesPane(
    selectedMainCategory: MainCategoryUi?,
    subCategories: List<SubCategoryUi>,
    onSubCategoryUpdate: (SubCategoryUi) -> Unit,
    onSubCategoryDelete: (SubCategoryUi) -> Unit,
    onAddSubCategory: () -> Unit,
) {
    SubCategoriesHeader(
        modifier = Modifier.padding(top = 12.dp, bottom = 8.dp).fillMaxWidth(),
    )
    SubCategoriesList(
        mainCategory = selectedMainCategory,
        subCategories = subCategories,
        onCategoryUpdate = onSubCategoryUpdate,
        onCategoryDelete = onSubCategoryDelete,
        onAddSubCategory = onAddSubCategory,
    )
}

@Composable
internal fun MainCategoriesHeader(
    modifier: Modifier = Modifier,
    onRestoreDefaultCategories: () -> Unit,
) {
    var isOpenParametersMenu by rememberSaveable { mutableStateOf(false) }

    Row(
        modifier = modifier.padding(start = 16.dp, end = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier.padding(end = 8.dp),
            text = EditorThemeRes.strings.mainCategoryTitle,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleMedium,
        )
        Spacer(modifier = Modifier.weight(1f))
        IconButton(
            modifier = Modifier.size(24.dp),
            onClick = { isOpenParametersMenu = true },
        ) {
            Icon(
                modifier = Modifier.size(18.dp),
                imageVector = Icons.Default.MoreVert,
                contentDescription = null,
            )
            CategoriesParametersMenu(
                expanded = isOpenParametersMenu,
                onDismiss = { isOpenParametersMenu = false },
                onRestoreDefaultCategories = {
                    onRestoreDefaultCategories()
                    isOpenParametersMenu = false
                },
            )
        }
    }
}

@Composable
internal fun CategoriesParametersMenu(
    modifier: Modifier = Modifier,
    expanded: Boolean,
    onDismiss: () -> Unit,
    onRestoreDefaultCategories: () -> Unit,
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss,
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        offset = DpOffset(0.dp, 4.dp),
    ) {
        DropdownMenuItem(
            onClick = onRestoreDefaultCategories,
            text = {
                Text(
                    text = EditorThemeRes.strings.restoreDefaultCategories,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleMedium,
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            },
        )
    }
}

@Composable
internal fun SubCategoriesHeader(
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = EditorThemeRes.strings.subCategoryTitle,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleMedium,
        )
    }
}
