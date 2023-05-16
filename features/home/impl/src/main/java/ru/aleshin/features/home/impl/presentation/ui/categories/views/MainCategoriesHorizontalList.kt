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
package ru.aleshin.features.home.impl.presentation.ui.categories.views

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import ru.aleshin.core.ui.theme.TimePlannerRes
import ru.aleshin.core.ui.theme.tokens.TimePlannerLanguage
import ru.aleshin.core.ui.views.WarningDeleteDialog
import ru.aleshin.features.home.api.domains.entities.categories.MainCategory
import ru.aleshin.features.home.api.presentation.mappers.fetchNameByLanguage
import ru.aleshin.features.home.api.presentation.mappers.toIconPainter
import ru.aleshin.features.home.impl.presentation.theme.HomeThemeRes

/**
 * @author Stanislav Aleshin on 15.04.2023.
 */
@Composable
internal fun MainCategoriesHorizontalList(
    modifier: Modifier = Modifier,
    mainCategories: List<MainCategory>,
    selectedCategory: MainCategory?,
    onSelectCategory: (MainCategory) -> Unit,
    onUpdateCategory: (MainCategory) -> Unit,
    onDeleteCategory: (MainCategory) -> Unit,
) {
    val gridState = rememberLazyGridState()
    val language = TimePlannerRes.language
    LazyHorizontalGrid(
        rows = GridCells.Fixed(3),
        modifier = modifier.height(216.dp).animateContentSize(),
        state = gridState,
        contentPadding = PaddingValues(start = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        items(
            items = mainCategories,
            key = { category -> category.id },
        ) { category ->
            MainCategoryItem(
                modifier = Modifier.sizeIn(maxHeight = 56.dp),
                isSelected = category == selectedCategory,
                category = category,
                onSelected = { onSelectCategory(category) },
                onDelete = { onDeleteCategory(category) },
                onUpdate = {
                    val languageCategory = when (language) {
                        TimePlannerLanguage.RU -> category.copy(name = it)
                        TimePlannerLanguage.EN -> category.copy(englishName = it)
                    }
                    onUpdateCategory(languageCategory)
                },
            )
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
    category: MainCategory,
    onSelected: () -> Unit,
    onDelete: () -> Unit,
    onUpdate: (name: String) -> Unit,
) {
    var isExpanded by remember { mutableStateOf(false) }
    var isCreatorDialogOpen by rememberSaveable { mutableStateOf(false) }
    var isWarningDeleteDialogOpen by rememberSaveable { mutableStateOf(false) }

    Surface(
        onClick = onSelected,
        modifier = modifier.size(width = 250.dp, height = 56.dp),
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = TimePlannerRes.elevations.levelOne,
        border = when (isSelected) {
            true -> BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
            false -> null
        },
    ) {
        Row(
            modifier = Modifier
                .padding(top = 10.dp, bottom = 10.dp, start = 12.dp, end = 4.dp)
                .animateContentSize(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(modifier = Modifier.width(32.dp).fillMaxHeight()) {
                MainCategoryItemLeading(
                    modifier = Modifier.align(Alignment.Center),
                    icon = category.icon?.toIconPainter(),
                    name = category.fetchNameByLanguage(),
                )
            }
            Text(
                text = category.fetchNameByLanguage(),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.labelLarge,
            )
            Spacer(Modifier.weight(1f))
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
                modifier = Modifier.width(250.dp),
                isExpanded = isExpanded,
                isDeleteEnabled = !category.isNotDeleted,
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
internal fun MainCategoryItemLeading(
    modifier: Modifier = Modifier,
    icon: Painter?,
    name: String,
) {
    if (icon != null) {
        Icon(
            modifier = modifier.size(32.dp),
            painter = icon,
            contentDescription = name,
            tint = MaterialTheme.colorScheme.primary,
        )
    } else {
        Text(
            modifier = modifier,
            text = name.first().toString(),
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.headlineMedium,
        )
    }
}

@Composable
internal fun MainCategoriesOptionMenu(
    modifier: Modifier = Modifier,
    isExpanded: Boolean,
    isDeleteEnabled: Boolean,
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
            onClick = onUpdateClick,
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
        val deleteContentColor = when (isDeleteEnabled) {
            true -> MaterialTheme.colorScheme.onSurface
            false -> MaterialTheme.colorScheme.onSurfaceVariant
        }
        DropdownMenuItem(
            modifier = Modifier.alpha(if (isDeleteEnabled) 1f else 0.5f),
            enabled = isDeleteEnabled,
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
