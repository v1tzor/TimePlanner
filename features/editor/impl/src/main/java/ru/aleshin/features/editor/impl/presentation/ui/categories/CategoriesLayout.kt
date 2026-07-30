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

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ru.aleshin.core.presentation.models.categories.MainCategoryUi
import ru.aleshin.core.presentation.models.categories.SubCategoryUi
import ru.aleshin.features.editor.impl.presentation.theme.tokens.EditorLayoutDefaults
import ru.aleshin.features.editor.impl.presentation.ui.categories.contract.CategoriesEvent
import ru.aleshin.features.editor.impl.presentation.ui.categories.contract.CategoriesState
import ru.aleshin.features.editor.impl.presentation.ui.categories.views.MainCategoriesHorizontalList
import ru.aleshin.features.editor.impl.presentation.ui.categories.views.MainCategoriesPaneHeader
import ru.aleshin.features.editor.impl.presentation.ui.categories.views.MainCategoriesVerticalList
import ru.aleshin.features.editor.impl.presentation.ui.categories.views.SubCategoriesList
import ru.aleshin.features.editor.impl.presentation.ui.categories.views.SubCategoriesPaneHeader
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
    onAddMainCategory: () -> Unit,
    onAddSubCategory: () -> Unit,
    onEvent: (CategoriesEvent) -> Unit,
) {
    val mainPaneScrollState = rememberScrollState()
    val detailPaneScrollState = rememberScrollState()
    val selectedCategoryDetails = remember(state.categories, state.selectedMainCategory) {
        state.categories.find { details ->
            details.mainCategory == state.selectedMainCategory
        }
    }
    val subCategories = remember(selectedCategoryDetails) {
        selectedCategoryDetails?.subCategories.orEmpty()
    }
    val mainCategories = remember(state.categories) {
        state.categories.map { details -> details.mainCategory }
    }

    when {
        adaptiveLayoutInfo.useCategoriesListDetailLayout -> CategoriesListDetailLayout(
            modifier = modifier,
            adaptiveLayoutInfo = adaptiveLayoutInfo,
            mainCategories = mainCategories,
            selectedMainCategory = state.selectedMainCategory,
            subCategories = subCategories,
            mainPaneScrollState = mainPaneScrollState,
            detailPaneScrollState = detailPaneScrollState,
            onAddMainCategory = onAddMainCategory,
            onAddSubCategory = onAddSubCategory,
            onEvent = onEvent,
        )
        else -> CategoriesSinglePaneLayout(
            modifier = modifier,
            mainCategories = mainCategories,
            selectedMainCategory = state.selectedMainCategory,
            subCategories = subCategories,
            scrollState = mainPaneScrollState,
            maxContentWidth = if (adaptiveLayoutInfo.isMediumWidth) {
                EditorLayoutDefaults.MediumContentMaxWidth
            } else {
                null
            },
            onAddMainCategory = onAddMainCategory,
            onAddSubCategory = onAddSubCategory,
            onEvent = onEvent,
        )
    }
}

@Composable
private fun CategoriesSinglePaneLayout(
    modifier: Modifier = Modifier,
    mainCategories: List<MainCategoryUi>,
    selectedMainCategory: MainCategoryUi?,
    subCategories: List<SubCategoryUi>,
    maxContentWidth: Dp?,
    scrollState: ScrollState,
    onAddMainCategory: () -> Unit,
    onAddSubCategory: () -> Unit,
    onEvent: (CategoriesEvent) -> Unit,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter,
    ) {
        Column(
            modifier = if (maxContentWidth != null) {
                Modifier
                    .widthIn(max = maxContentWidth)
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            } else {
                Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            },
        ) {
            MainCategoriesPaneHeader(
                modifier = Modifier
                    .padding(top = 8.dp, bottom = 4.dp)
                    .fillMaxWidth(),
                onRestoreDefaultCategories = {
                    onEvent(CategoriesEvent.RestoreDefaultCategories)
                },
            )
            MainCategoriesHorizontalList(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 12.dp),
                mainCategories = mainCategories,
                selectedCategory = selectedMainCategory,
                onSelectCategory = { category ->
                    onEvent(CategoriesEvent.ChangeMainCategory(category))
                },
                onUpdateCategory = { category ->
                    onEvent(CategoriesEvent.UpdateMainCategory(category))
                },
                onDeleteCategory = { category ->
                    onEvent(CategoriesEvent.DeleteMainCategory(category))
                },
                onAddCategory = onAddMainCategory,
            )
            SubCategoriesPaneHeader(
                modifier = Modifier
                    .padding(top = 12.dp, bottom = 8.dp)
                    .fillMaxWidth(),
            )
            SubCategoriesList(
                mainCategory = selectedMainCategory,
                subCategories = subCategories,
                onCategoryUpdate = { category ->
                    onEvent(CategoriesEvent.UpdateSubCategory(category))
                },
                onCategoryDelete = { category ->
                    onEvent(CategoriesEvent.DeleteSubCategory(category))
                },
                onAddSubCategory = onAddSubCategory,
            )
        }
    }
}

@Composable
private fun CategoriesListDetailLayout(
    modifier: Modifier = Modifier,
    adaptiveLayoutInfo: AdaptiveLayoutInfo,
    mainCategories: List<MainCategoryUi>,
    selectedMainCategory: MainCategoryUi?,
    subCategories: List<SubCategoryUi>,
    mainPaneScrollState: ScrollState,
    detailPaneScrollState: ScrollState,
    onAddMainCategory: () -> Unit,
    onAddSubCategory: () -> Unit,
    onEvent: (CategoriesEvent) -> Unit,
) {
    AdaptiveListDetailPaneScaffold(
        modifier = modifier.fillMaxSize(),
        adaptiveLayoutInfo = adaptiveLayoutInfo,
        listPane = {
            CategoriesListPane(
                modifier = Modifier.fillMaxSize(),
                mainCategories = mainCategories,
                selectedMainCategory = selectedMainCategory,
                scrollState = mainPaneScrollState,
                onAddMainCategory = onAddMainCategory,
                onEvent = onEvent,
            )
        },
        detailPane = {
            CategoriesDetailPane(
                modifier = Modifier.fillMaxSize(),
                selectedMainCategory = selectedMainCategory,
                subCategories = subCategories,
                scrollState = detailPaneScrollState,
                onAddSubCategory = onAddSubCategory,
                onEvent = onEvent,
            )
        },
    )
}

@Composable
private fun CategoriesListPane(
    modifier: Modifier = Modifier,
    mainCategories: List<MainCategoryUi>,
    selectedMainCategory: MainCategoryUi?,
    scrollState: ScrollState,
    onAddMainCategory: () -> Unit,
    onEvent: (CategoriesEvent) -> Unit,
) {
    Column(
        modifier = modifier.verticalScroll(scrollState),
    ) {
        MainCategoriesPaneHeader(
            modifier = Modifier
                .padding(top = 8.dp, bottom = 4.dp)
                .fillMaxWidth(),
            onRestoreDefaultCategories = {
                onEvent(CategoriesEvent.RestoreDefaultCategories)
            },
        )
        MainCategoriesVerticalList(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 16.dp,
                    top = 8.dp,
                    end = 16.dp,
                    bottom = 24.dp,
                ),
            mainCategories = mainCategories,
            selectedCategory = selectedMainCategory,
            onSelectCategory = { category ->
                onEvent(CategoriesEvent.ChangeMainCategory(category))
            },
            onUpdateCategory = { category ->
                onEvent(CategoriesEvent.UpdateMainCategory(category))
            },
            onDeleteCategory = { category ->
                onEvent(CategoriesEvent.DeleteMainCategory(category))
            },
            onAddCategory = onAddMainCategory,
        )
    }
}

@Composable
private fun CategoriesDetailPane(
    modifier: Modifier = Modifier,
    selectedMainCategory: MainCategoryUi?,
    subCategories: List<SubCategoryUi>,
    scrollState: ScrollState,
    onAddSubCategory: () -> Unit,
    onEvent: (CategoriesEvent) -> Unit,
) {
    Column(
        modifier = modifier.verticalScroll(scrollState),
    ) {
        SubCategoriesPaneHeader(
            modifier = Modifier
                .padding(top = 12.dp, bottom = 8.dp)
                .fillMaxWidth(),
        )
        SubCategoriesList(
            mainCategory = selectedMainCategory,
            subCategories = subCategories,
            onCategoryUpdate = { category ->
                onEvent(CategoriesEvent.UpdateSubCategory(category))
            },
            onCategoryDelete = { category ->
                onEvent(CategoriesEvent.DeleteSubCategory(category))
            },
            onAddSubCategory = onAddSubCategory,
        )
    }
}

private val AdaptiveLayoutInfo.useCategoriesListDetailLayout: Boolean
    get() = useExpandedLayout || isBookPosture
