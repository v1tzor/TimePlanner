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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import ru.aleshin.core.domain.entities.categories.DefaultCategoryType
import ru.aleshin.core.ui.mappers.mapToIconPainter
import ru.aleshin.core.ui.theme.TimePlannerRes
import ru.aleshin.core.ui.views.BaseSelectorBottomSheet
import ru.aleshin.core.ui.views.CategoryIconMonogram
import ru.aleshin.core.ui.views.CategoryTextMonogram
import ru.aleshin.core.ui.views.SelectorSwipeItemView
import ru.aleshin.core.ui.views.SwipeToDismissBackground
import ru.aleshin.features.editor.impl.presentation.models.categories.MainCategoryUi
import ru.aleshin.features.editor.impl.presentation.theme.EditorThemeRes

/**
 * @author Stanislav Aleshin on 25.02.2023.
 */
@Composable
internal fun MainCategoryChooser(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isError: Boolean = false,
    allCategories: List<MainCategoryUi>,
    currentCategory: MainCategoryUi?,
    onEditCategory: (MainCategoryUi) -> Unit,
    onChangeCategory: (MainCategoryUi) -> Unit,
) {
    var openSubCategorySelectorSheet by rememberSaveable { mutableStateOf(false) }
    val categoryIcon = currentCategory?.defaultType?.mapToIconPainter()
    val categoryName = currentCategory?.fetchName()
    
    Surface(
        onClick = { openSubCategorySelectorSheet = true },
        modifier = modifier.height(68.dp),
        enabled = currentCategory != null && enabled,
        shape = MaterialTheme.shapes.medium,
//        color = when (isError) {
//            true -> MaterialTheme.colorScheme.errorContainer
//            false -> MaterialTheme.colorScheme.surface
//        },
        color = when (isError) {
            true -> MaterialTheme.colorScheme.errorContainer
            false -> MaterialTheme.colorScheme.surfaceContainerLow
        },
        // tonalElevation = if (!isError) TimePlannerRes.elevations.levelOne else 0.dp,
        border = when (isError) {
            true -> BorderStroke(1.5.dp, MaterialTheme.colorScheme.error)
            false -> null
        },
    ) {
        Row(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val categoryNameColor = when (currentCategory != null) {
                true -> MaterialTheme.colorScheme.onSurface
                false -> MaterialTheme.colorScheme.onSurfaceVariant
            }
            if (currentCategory != null && categoryIcon == null) {
                CategoryTextMonogram(
                    text = categoryName?.first()?.toString() ?: "",
                    textColor = MaterialTheme.colorScheme.primary,
                    backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                )
            } else {
                CategoryIconMonogram(
                    icon = categoryIcon ?: DefaultCategoryType.EMPTY.mapToIconPainter(),
                    iconDescription = categoryName,
                    iconColor = when (isError) {
                        true -> MaterialTheme.colorScheme.errorContainer
                        false -> MaterialTheme.colorScheme.primary
                    },
                    backgroundColor = when (isError) {
                        true -> MaterialTheme.colorScheme.error
                        false -> MaterialTheme.colorScheme.primaryContainer
                    },
                )
            }
            Column(modifier = Modifier.weight(1f).animateContentSize()) {
                Text(
                    text = EditorThemeRes.strings.mainCategoryChooserTitle,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelMedium,
                )
                Text(
                    text = categoryName ?: EditorThemeRes.strings.categoryNotSelectedTitle,
                    color = categoryNameColor,
                    style = MaterialTheme.typography.titleMedium,
                )
            }
            Icon(
                painter = painterResource(EditorThemeRes.icons.showDialog),
                contentDescription = EditorThemeRes.strings.chooseCategoryTitle,
                tint = categoryNameColor,
            )
        }
    }
    if (openSubCategorySelectorSheet && currentCategory != null) {
        MainCategorySelectorBottomSheet(
            initCategory = currentCategory,
            allCategories = allCategories,
            onDismiss = { openSubCategorySelectorSheet = false },
            onEditCategory = onEditCategory,
            onChooseCategory = {
                onChangeCategory(it)
                openSubCategorySelectorSheet = false
            },
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun MainCategorySelectorBottomSheet(
    modifier: Modifier = Modifier,
    allCategories: List<MainCategoryUi>,
    initCategory: MainCategoryUi,
    onDismiss: () -> Unit,
    onEditCategory: (MainCategoryUi) -> Unit,
    onChooseCategory: (MainCategoryUi) -> Unit,
) {
    val coreStrings = TimePlannerRes.strings
    var selectedCategory by rememberSaveable { mutableStateOf(initCategory) }
    var searchQuery by rememberSaveable { mutableStateOf<String?>(null) }
    val searchedCategory = remember(searchQuery, allCategories) {
        allCategories.filter { category ->
            searchQuery == null || (category.fetchName(coreStrings)?.contains(searchQuery ?: "", true) == true)
        }
    }

    BaseSelectorBottomSheet(
        modifier = modifier,
        selected = selectedCategory,
        items = searchedCategory,
        header = EditorThemeRes.strings.mainCategoryChooserTitle,
        title = null,
        itemView = { category ->
            val isSelected = category.id == selectedCategory.id
            val density = LocalDensity.current
            val dismissState = remember(category) {
                SwipeToDismissBoxState(
                    initialValue = SwipeToDismissBoxValue.Settled,
                    density = density,
                    confirmValueChange = { dismissBoxValue ->
                        when (dismissBoxValue) {
                            SwipeToDismissBoxValue.StartToEnd -> Unit
                            SwipeToDismissBoxValue.EndToStart -> onEditCategory(category)
                            SwipeToDismissBoxValue.Settled -> Unit
                        }
                        false
                    },
                    positionalThreshold = { it * .60f }
                )
            }
            SelectorSwipeItemView(
                onClick = { selectedCategory = category },
                state = dismissState,
                selected = isSelected,
                title = category.fetchName() ?: "*",
                label = null,
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
                leadingIcon = {
                    val title = category.fetchName() ?: "*"
                    val icon = category.defaultType?.mapToIconPainter()
                    if (icon != null) {
                        CategoryIconMonogram(
                            icon = icon,
                            iconColor = MaterialTheme.colorScheme.onPrimary,
                            backgroundColor = MaterialTheme.colorScheme.primary,
                            iconDescription = title,
                        )
                    } else {
                        CategoryTextMonogram(
                            text = title.first().toString(),
                            textColor = MaterialTheme.colorScheme.onPrimary,
                            backgroundColor = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            )
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
        onConfirm = { category -> if (category != null) onChooseCategory(category) },
    )
}

/* ----------------------- Release Preview -----------------------
@Composable
@Preview(showBackground = true)
private fun MainCategoryChooser_Preview() {
    TimePlannerTheme(
        dynamicColor = false,
        themeColorsType = ThemeColorsUiType.DARK,
        language = LanguageUiType.RU,
    ) {
        EditorTheme {
            val category = rememberSaveable { mutableStateOf<MainCategory?>(null) }
            MainCategoryChooser(
                currentCategory = category.value,
                allMainCategories = listOf(),
                onCategoryChoose = { category.value = it },
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun MainCategoryChooser_Enabled_Preview() {
    TimePlannerTheme(
        dynamicColor = false,
        themeColorsType = ThemeColorsUiType.DARK,
        language = LanguageUiType.RU,
    ) {
        EditorTheme {
            val category = rememberSaveable { mutableStateOf(MainCategory(name = "Работа")) }
            MainCategoryChooser(
                currentCategory = category.value,
                allMainCategories = listOf(category.value),
                onCategoryChoose = { category.value = it },
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun MainCategoryDialogChooser_Preview() {
    TimePlannerTheme(
        dynamicColor = false,
        themeColorsType = ThemeColorsUiType.DARK,
    ) {
        EditorTheme {
            val category = MainCategory(name = "Работа")
            MainCategoryDialogChooser(
                onCloseDialog = {},
                allMainCategories = listOf(category),
                initCategory = category,
                onChooseCategory = {},
            )
        }
    }
}
*/
