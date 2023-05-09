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
package ru.aleshin.features.home.impl.presentation.ui.categories

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import kotlinx.coroutines.launch
import ru.aleshin.core.ui.views.Scaffold
import ru.aleshin.core.utils.managers.LocalDrawerManager
import ru.aleshin.core.utils.platform.screen.ScreenContent
import ru.aleshin.features.home.impl.presentation.mapppers.mapToMessage
import ru.aleshin.features.home.impl.presentation.theme.HomeThemeRes
import ru.aleshin.features.home.impl.presentation.ui.categories.contract.CategoriesEffect
import ru.aleshin.features.home.impl.presentation.ui.categories.contract.CategoriesEvent
import ru.aleshin.features.home.impl.presentation.ui.categories.contract.CategoriesViewState
import ru.aleshin.features.home.impl.presentation.ui.categories.screenmodel.rememberCategoriesScreenModel
import ru.aleshin.features.home.impl.presentation.ui.categories.views.CategoriesTopAppBar
import ru.aleshin.features.home.impl.presentation.ui.categories.views.SubCategoryEditorDialog

/**
 * @author Stanislav Aleshin on 05.04.2023.
 */
internal class CategoriesScreen : Screen {

    @Composable
    @OptIn(ExperimentalMaterial3Api::class)
    override fun Content() = ScreenContent(
        screenModel = rememberCategoriesScreenModel(),
        initialState = CategoriesViewState(),
    ) { state ->
        val scope = rememberCoroutineScope()
        val snackbarState = remember { SnackbarHostState() }
        var isShowSubCategoryDialog by remember { mutableStateOf(false) }
        val drawerManager = LocalDrawerManager.current
        val strings = HomeThemeRes.strings

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            content = { paddingValues ->
                CategoriesContent(
                    state = state,
                    modifier = Modifier.padding(paddingValues),
                    onAddMainCategory = { dispatchEvent(CategoriesEvent.AddMainCategory(it)) },
                    onAddSubCategory = { isShowSubCategoryDialog = true },
                    onChangeMainCategory = { dispatchEvent(CategoriesEvent.ChangeMainCategory(it)) },
                    onMainCategoryUpdate = { dispatchEvent(CategoriesEvent.UpdateMainCategory(it)) },
                    onSubCategoryUpdate = { dispatchEvent(CategoriesEvent.UpdateSubCategory(it)) },
                    onMainCategoryDelete = { dispatchEvent(CategoriesEvent.DeleteMainCategory(it)) },
                    onSubCategoryDelete = { dispatchEvent(CategoriesEvent.DeleteSubCategory(it)) },
                )
            },
            topBar = {
                CategoriesTopAppBar(onMenuIconClick = { scope.launch { drawerManager?.openDrawer() } })
            },
            snackbarHost = {
                SnackbarHost(hostState = snackbarState)
            },
        )

        if (isShowSubCategoryDialog) {
            val mainCategory = state.selectedMainCategory ?: return@ScreenContent
            SubCategoryEditorDialog(
                mainCategory = mainCategory,
                onDismiss = { isShowSubCategoryDialog = false },
                onConfirm = { name ->
                    dispatchEvent(CategoriesEvent.AddSubCategory(name, mainCategory))
                    isShowSubCategoryDialog = false
                },
            )
        }

        handleEffect { effect ->
            when (effect) {
                is CategoriesEffect.ShowError -> snackbarState.showSnackbar(
                    message = effect.failure.mapToMessage(strings),
                )
            }
        }
    }
}
