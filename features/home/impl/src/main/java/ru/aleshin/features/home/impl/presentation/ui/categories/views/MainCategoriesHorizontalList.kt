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
package ru.aleshin.features.home.impl.presentation.ui.categories.views

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import ru.aleshin.core.domain.entities.categories.DefaultCategoryType
import ru.aleshin.core.ui.mappers.mapToIconPainter
import ru.aleshin.core.ui.theme.TimePlannerRes
import ru.aleshin.core.ui.views.WarningDeleteDialog
import ru.aleshin.features.home.impl.presentation.models.categories.MainCategoryUi
import ru.aleshin.features.home.impl.presentation.theme.HomeThemeRes

/**
 * @author Stanislav Aleshin on 15.04.2023.
 */
@Composable
internal fun MainCategoriesHorizontalList(
    modifier: Modifier = Modifier,
    gridState: LazyGridState = rememberLazyGridState(),
    mainCategories: List<MainCategoryUi>,
    selectedCategory: MainCategoryUi?,
    onSelectCategory: (MainCategoryUi) -> Unit,
    onUpdateCategory: (MainCategoryUi) -> Unit,
    onDeleteCategory: (MainCategoryUi) -> Unit,
    onAddCategory: () -> Unit,
) {
    LazyHorizontalGrid(
        rows = GridCells.Fixed(2),
        modifier = modifier.height(236.dp).animateContentSize(),
        state = gridState,
        contentPadding = PaddingValues(start = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(
            items = mainCategories,
            key = { category -> category.id },
        ) { category ->
            MainCategoryItem(
                isSelected = category == selectedCategory,
                category = category,
                onSelected = { onSelectCategory(category) },
                onDelete = { onDeleteCategory(category) },
                onUpdate = {
                    onUpdateCategory(category.copy(customName = it))
                },
            )
        }
        item {
            MainCategoryAddItem(onClick = onAddCategory)
        }
    }
    LaunchedEffect(key1 = selectedCategory) {
        val index = mainCategories.indexOf(selectedCategory)
        if (index != -1) gridState.animateScrollToItem(index = index)
    }
}

@Composable
internal fun MainCategoryItem(
    modifier: Modifier = Modifier,
    isSelected: Boolean,
    category: MainCategoryUi,
    onSelected: () -> Unit,
    onDelete: () -> Unit,
    onUpdate: (name: String) -> Unit,
) {
    var isExpanded by remember { mutableStateOf(false) }
    var isCreatorDialogOpen by rememberSaveable { mutableStateOf(false) }
    var isWarningDeleteDialogOpen by rememberSaveable { mutableStateOf(false) }

    Surface(
        onClick = onSelected,
        modifier = modifier.size(width = 180.dp, height = 80.dp),
        shape = MaterialTheme.shapes.small,
        color = when (isSelected) {
            true -> MaterialTheme.colorScheme.primaryContainer
            false -> MaterialTheme.colorScheme.surface
        },
        tonalElevation = when (isSelected) {
            true -> TimePlannerRes.elevations.levelZero
            false -> TimePlannerRes.elevations.levelOne
        },
    ) {
        Row(
            modifier = Modifier
                .padding(top = 12.dp, bottom = 12.dp, start = 12.dp, end = 4.dp)
                .animateContentSize(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                MainCategoryItemLeading(
                    icon = category.defaultType?.mapToIconPainter(),
                    name = category.fetchName() ?: "*",
                    isSelected = isSelected,
                )
                Spacer(modifier = Modifier.weight(1f))
                Column {
                    Text(
                        text = HomeThemeRes.strings.nameCategoryTitle,
                        color = when (isSelected) {
                            true -> MaterialTheme.colorScheme.onPrimaryContainer
                            false -> MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        style = MaterialTheme.typography.labelMedium,
                    )
                    Text(
                        text = category.fetchName() ?: "*",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = when (isSelected) {
                            true -> MaterialTheme.colorScheme.onPrimaryContainer
                            false -> MaterialTheme.colorScheme.onSurface
                        },
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }
            if (isSelected) {
                IconButton(modifier = Modifier.size(36.dp), onClick = { isExpanded = true }) {
                    Icon(
                        modifier = modifier.size(24.dp),
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopEnd) {
            MainCategoriesOptionMenu(
                modifier = Modifier.width(180.dp),
                isExpanded = isExpanded,
                isChangeable = category.defaultType != DefaultCategoryType.EMPTY,
                onUpdateClick = {
                    isCreatorDialogOpen = true
                    isExpanded = false
                },
                onDeleteClick = {
                    isExpanded = false
                    isWarningDeleteDialogOpen = true
                },
                onDismiss = { isExpanded = false },
            )
        }
    }
    if (isCreatorDialogOpen) {
        MainCategoryEditorDialog(
            editCategory = category,
            onDismiss = { isCreatorDialogOpen = false },
            onConfirm = { name ->
                onUpdate(name)
                isCreatorDialogOpen = false
            },
        )
    }
    if (isWarningDeleteDialogOpen) {
        WarningDeleteDialog(
            text = HomeThemeRes.strings.warningDeleteCategoryText,
            onDismiss = { isWarningDeleteDialogOpen = false },
            onAction = {
                onDelete()
                isWarningDeleteDialogOpen = false
            },
        )
    }
}

@Composable
internal fun MainCategoryAddItem(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Surface(
        modifier = modifier.size(180.dp, 100.dp),
        tonalElevation = TimePlannerRes.elevations.levelOne,
        shape = MaterialTheme.shapes.small,
        onClick = onClick,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = HomeThemeRes.strings.addCategoryTitle,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.labelLarge,
                )
                Icon(
                    modifier = Modifier.size(18.dp),
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}

@Composable
internal fun MainCategoryItemLeading(
    modifier: Modifier = Modifier,
    icon: Painter?,
    name: String,
    isSelected: Boolean,
) {
    if (icon != null) {
        Icon(
            modifier = modifier.size(32.dp),
            painter = icon,
            contentDescription = name,
            tint = when (isSelected) {
                true -> MaterialTheme.colorScheme.onPrimaryContainer
                false -> MaterialTheme.colorScheme.primary
            },
        )
    } else {
        Text(
            modifier = modifier,
            text = name.first().toString(),
            color = when (isSelected) {
                true -> MaterialTheme.colorScheme.onPrimaryContainer
                false -> MaterialTheme.colorScheme.primary
            },
            style = MaterialTheme.typography.headlineMedium,
        )
    }
}

@Composable
internal fun MainCategoriesOptionMenu(
    modifier: Modifier = Modifier,
    isExpanded: Boolean,
    isChangeable: Boolean,
    onUpdateClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onDismiss: () -> Unit,
) {
    DropdownMenu(
        expanded = isExpanded,
        onDismissRequest = onDismiss,
        modifier = modifier,
        offset = DpOffset(0.dp, 4.dp),
    ) {
        DropdownMenuItem(
            modifier = Modifier.alpha(if (isChangeable) 1f else 0.5f),
            onClick = onUpdateClick,
            enabled = isChangeable,
            text = {
                Text(
                    text = HomeThemeRes.strings.updateCategoryTitle,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleMedium,
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = HomeThemeRes.strings.updateCategoryTitle,
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            },
        )
        val deleteContentColor = when (isChangeable) {
            true -> MaterialTheme.colorScheme.onSurface
            false -> MaterialTheme.colorScheme.onSurfaceVariant
        }
        DropdownMenuItem(
            modifier = Modifier.alpha(if (isChangeable) 1f else 0.5f),
            enabled = isChangeable,
            onClick = onDeleteClick,
            text = {
                Text(
                    text = HomeThemeRes.strings.deleteCategoryTitle,
                    color = deleteContentColor,
                    style = MaterialTheme.typography.titleMedium,
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = HomeThemeRes.strings.deleteCategoryTitle,
                    tint = deleteContentColor,
                )
            },
        )
    }
}

/* ----------------------- Release Preview -----------------------
@Preview
@Composable
private fun MainCategoryList_Preview() {
    TimePlannerTheme(
        dynamicColor = false,
        themeColorsType = ThemeColorsUiType.DARK,
        language = LanguageUiType.RU,
    ) {
        HomeTheme {
            MainCategoriesHorizontalList(
                modifier = Modifier.fillMaxWidth(),
                mainCategories = listOf(MainCategory.empty(), MainCategory(name = "Работа")),
                selectedCategory = null,
                onSelectCategory = {},
                onUpdateCategory = {},
                onDeleteCategory = {},
            )
        }
    }
}*/
