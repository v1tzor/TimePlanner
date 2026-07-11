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
package ru.aleshin.features.home.impl.presentation.ui.categories

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import ru.aleshin.timeplanner.core.ui.views.Scaffold
import ru.aleshin.core.utils.architecture.store.compose.handleEffects
import ru.aleshin.core.utils.architecture.store.compose.stateAsState
import ru.aleshin.core.utils.managers.LocalDrawerManager
import ru.aleshin.features.home.impl.presentation.mapppers.mapToMessage
import ru.aleshin.core.presentation.models.categories.MainCategoryUi
import ru.aleshin.core.presentation.models.categories.SubCategoryUi
import ru.aleshin.features.home.impl.presentation.theme.HomeThemeRes
import ru.aleshin.features.home.impl.presentation.ui.categories.contract.CategoriesEffect
import ru.aleshin.features.home.impl.presentation.ui.categories.contract.CategoriesEvent
import ru.aleshin.features.home.impl.presentation.ui.categories.contract.CategoriesState
import ru.aleshin.features.home.impl.presentation.ui.categories.store.CategoriesComponent
import ru.aleshin.features.home.impl.presentation.ui.categories.views.CategoriesTopAppBar
import ru.aleshin.features.home.impl.presentation.ui.categories.views.MainCategoriesHorizontalList
import ru.aleshin.features.home.impl.presentation.ui.categories.views.MainCategoryEditorDialog
import ru.aleshin.features.home.impl.presentation.ui.categories.views.SubCategoriesList
import ru.aleshin.features.home.impl.presentation.ui.categories.views.SubCategoryEditorDialog

/**
 * @author Stanislav Aleshin on 05.04.2023.
 */
@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun CategoriesContent(
    categoriesComponent: CategoriesComponent,
    modifier: Modifier = Modifier,
) {
    val store = categoriesComponent.store
    val state by store.stateAsState()
    val scope = rememberCoroutineScope()
    val snackbarState = remember { SnackbarHostState() }
    var isShowSubCategoryDialog by rememberSaveable { mutableStateOf(false) }
    val drawerManager = LocalDrawerManager.current
    val strings = HomeThemeRes.strings

    Scaffold(
        modifier = modifier.fillMaxSize(),
        content = { paddingValues ->
            BaseCategoriesContent(
                state = state,
                modifier = Modifier.padding(paddingValues),
                onAddMainCategory = { store.dispatchEvent(CategoriesEvent.AddMainCategory(it)) },
                onAddSubCategory = { isShowSubCategoryDialog = true },
                onChangeMainCategory = { store.dispatchEvent(CategoriesEvent.ChangeMainCategory(it)) },
                onMainCategoryUpdate = { store.dispatchEvent(CategoriesEvent.UpdateMainCategory(it)) },
                onSubCategoryUpdate = { store.dispatchEvent(CategoriesEvent.UpdateSubCategory(it)) },
                onMainCategoryDelete = { store.dispatchEvent(CategoriesEvent.DeleteMainCategory(it)) },
                onSubCategoryDelete = { store.dispatchEvent(CategoriesEvent.DeleteSubCategory(it)) },
                onRestoreDefaultCategories = { store.dispatchEvent(CategoriesEvent.RestoreDefaultCategories) },
            )
        },
        topBar = {
            CategoriesTopAppBar(
                onMenuIconClick = { scope.launch { drawerManager?.openDrawer() } },
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarState)
        },
    )

    val selectedMainCategory = state.selectedMainCategory
    if (isShowSubCategoryDialog && selectedMainCategory != null) {
        SubCategoryEditorDialog(
            mainCategory = selectedMainCategory,
            onDismiss = { isShowSubCategoryDialog = false },
            onConfirm = { name ->
                store.dispatchEvent(CategoriesEvent.AddSubCategory(name, selectedMainCategory))
                isShowSubCategoryDialog = false
            },
        )
    }

    store.handleEffects { effect ->
        when (effect) {
            is CategoriesEffect.ShowError -> snackbarState.showSnackbar(
                message = effect.failure.mapToMessage(strings),
            )
        }
    }
}

@Composable
private fun BaseCategoriesContent(
    state: CategoriesState,
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
        val categories = remember(state.categories, state.selectedMainCategory) {
            state.categories.find { it.mainCategory == state.selectedMainCategory }
        }
        val subCategories = remember(categories) {
            categories?.subCategories ?: emptyList()
        }

        MainCategoriesHeader(
            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp).fillMaxWidth(),
            onRestoreDefaultCategories = onRestoreDefaultCategories,
        )
        MainCategoriesHorizontalList(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 12.dp),
            mainCategories = remember(state.categories) {
                state.categories.map { it.mainCategory }
            },
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
    var isOpenParametersMenu by rememberSaveable { mutableStateOf(false) }

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
        shape = MaterialTheme.shapes.large,
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
