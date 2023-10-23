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
package ru.aleshin.features.home.impl.presentation.ui.categories

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import ru.aleshin.features.home.impl.presentation.models.categories.MainCategoryUi
import ru.aleshin.features.home.impl.presentation.models.categories.SubCategoryUi
import ru.aleshin.features.home.impl.presentation.theme.HomeThemeRes
import ru.aleshin.features.home.impl.presentation.ui.categories.contract.CategoriesViewState
import ru.aleshin.features.home.impl.presentation.ui.categories.views.MainCategoriesHorizontalList
import ru.aleshin.features.home.impl.presentation.ui.categories.views.MainCategoryEditorDialog
import ru.aleshin.features.home.impl.presentation.ui.categories.views.SubCategoriesList

/**
 * @author Stanislav Aleshin on 05.04.2023.
 */
@Composable
internal fun CategoriesContent(
    state: CategoriesViewState,
    modifier: Modifier = Modifier,
    onAddMainCategory: (name: String) -> Unit,
    onAddSubCategory: () -> Unit,
    onChangeMainCategory: (MainCategoryUi) -> Unit,
    onSubCategoryUpdate: (SubCategoryUi) -> Unit,
    onMainCategoryUpdate: (MainCategoryUi) -> Unit,
    onMainCategoryDelete: (MainCategoryUi) -> Unit,
    onSubCategoryDelete: (SubCategoryUi) -> Unit,
    onRestoreDefaultCategories: () -> Unit,
) {
    var isMainCategoryCreatorOpen by rememberSaveable { mutableStateOf(false) }
    val scrollableState = rememberScrollState()

    Column(modifier = modifier.fillMaxSize().verticalScroll(scrollableState)) {
        val categories = state.categories.find { it.mainCategory == state.selectedMainCategory }
        val subCategories = categories?.subCategories ?: emptyList()

        MainCategoriesHeader(
            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp).fillMaxWidth(),
            onRestoreDefaultCategories = onRestoreDefaultCategories,
        )
        MainCategoriesHorizontalList(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 12.dp),
            mainCategories = state.categories.map { it.mainCategory },
            selectedCategory = state.selectedMainCategory,
            onSelectCategory = onChangeMainCategory,
            onUpdateCategory = onMainCategoryUpdate,
            onDeleteCategory = onMainCategoryDelete,
            onAddCategory = { isMainCategoryCreatorOpen = true },
        )
        SubCategoriesHeader(
            modifier = Modifier.padding(top = 12.dp, bottom = 8.dp).fillMaxWidth(),
        )
        SubCategoriesList(
            modifier = Modifier.height(250.dp),
            mainCategory = state.selectedMainCategory,
            subCategories = subCategories,
            onCategoryUpdate = onSubCategoryUpdate,
            onCategoryDelete = onSubCategoryDelete,
            onAddSubCategory = onAddSubCategory,
        )
    }
    if (isMainCategoryCreatorOpen) {
        MainCategoryEditorDialog(
            onDismiss = { isMainCategoryCreatorOpen = false },
            onConfirm = { name ->
                onAddMainCategory(name)
                isMainCategoryCreatorOpen = false
            },
        )
    }
}

@Composable
internal fun MainCategoriesHeader(
    modifier: Modifier = Modifier,
    onRestoreDefaultCategories: () -> Unit,
) {
    var isOpenParametersMenu by remember { mutableStateOf(false) }

    Row(
        modifier = modifier.padding(start = 16.dp, end = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier.padding(end = 8.dp),
            text = HomeThemeRes.strings.mainCategoryTitle,
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
        offset = DpOffset(0.dp, 4.dp),
    ) {
        DropdownMenuItem(
            onClick = onRestoreDefaultCategories,
            text = {
                Text(
                    text = HomeThemeRes.strings.restoreDefaultCategories,
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
            text = HomeThemeRes.strings.subCategoryTitle,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleMedium,
        )
    }
}

/* ----------------------- Release Preview -----------------------
@Composable
@Preview(showBackground = true, showSystemUi = true)
private fun CategoriesContent_Preview() {
    TimePlannerTheme(
        dynamicColor = false,
        themeColorsType = ThemeColorsUiType.DARK,
        language = LanguageUiType.RU,
    ) {
        HomeTheme {
            CategoriesContent(
                state = CategoriesViewState(),
                onAddMainCategory = {},
                onChangeMainCategory = {},
                onMainCategoryUpdate = {},
                onMainCategoryDelete = {},
                onSubCategoryUpdate = {},
                onSubCategoryDelete = {},
            )
        }
    }
}
*/
