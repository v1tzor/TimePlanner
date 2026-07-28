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

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import ru.aleshin.core.utils.architecture.store.compose.handleEffects
import ru.aleshin.core.utils.architecture.store.compose.stateAsState
import ru.aleshin.features.editor.impl.presentation.ui.categories.contract.CategoriesEffect
import ru.aleshin.features.editor.impl.presentation.ui.categories.contract.CategoriesEvent
import ru.aleshin.features.editor.impl.presentation.ui.categories.store.CategoriesComponent
import ru.aleshin.features.editor.impl.presentation.ui.categories.views.CategoriesTopAppBar
import ru.aleshin.features.editor.impl.presentation.ui.categories.views.SubCategoryEditorDialog
import ru.aleshin.timeplanner.core.ui.views.AdaptiveLayoutInfo
import ru.aleshin.timeplanner.core.ui.views.Scaffold
import ru.aleshin.timeplanner.core.ui.views.rememberAdaptiveLayoutInfo

/**
 * @author Stanislav Aleshin on 05.04.2023.
 */
@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun CategoriesContent(
    modifier: Modifier = Modifier,
    categoriesComponent: CategoriesComponent,
    adaptiveLayoutInfo: AdaptiveLayoutInfo = rememberAdaptiveLayoutInfo(),
) {
    val store = categoriesComponent.store
    val state by store.stateAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var isSubCategoryDialogOpen by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CategoriesTopAppBar(
                isCompact = adaptiveLayoutInfo.isCompactWidth,
                onBackIconClick = {
                    store.dispatchEvent(CategoriesEvent.PressBackIcon)
                },
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
    ) { contentPadding ->
        CategoriesLayout(
            modifier = Modifier.fillMaxSize().padding(contentPadding),
            state = state,
            adaptiveLayoutInfo = adaptiveLayoutInfo,
            onEvent = store::dispatchEvent,
            onAddSubCategory = { isSubCategoryDialogOpen = true },
        )
    }

    val selectedMainCategory = state.selectedMainCategory
    if (isSubCategoryDialogOpen && selectedMainCategory != null) {
        SubCategoryEditorDialog(
            mainCategory = selectedMainCategory,
            onDismiss = {
                isSubCategoryDialogOpen = false
            },
            onConfirm = { name ->
                store.dispatchEvent(CategoriesEvent.AddSubCategory(name, selectedMainCategory))
                isSubCategoryDialogOpen = false
            },
        )
    }

    store.handleEffects { effect ->
        when (effect) {
            is CategoriesEffect.ShowError -> snackbarHostState.showSnackbar(
                message = effect.failure.toString(),
            )
        }
    }
}
