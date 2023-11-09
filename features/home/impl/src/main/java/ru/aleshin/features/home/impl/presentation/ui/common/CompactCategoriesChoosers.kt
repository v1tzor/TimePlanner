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
package ru.aleshin.features.home.impl.presentation.ui.common

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import ru.aleshin.core.ui.theme.TimePlannerRes
import ru.aleshin.core.ui.views.ExpandedIcon
import ru.aleshin.features.home.api.presentation.mappers.mapToIconPainter
import ru.aleshin.features.home.impl.presentation.models.categories.CategoriesUi
import ru.aleshin.features.home.impl.presentation.models.categories.MainCategoryUi
import ru.aleshin.features.home.impl.presentation.models.categories.SubCategoryUi
import ru.aleshin.features.home.impl.presentation.theme.HomeThemeRes

/**
 * @author Stanislav Aleshin on 02.11.2023.
 */
@Composable
internal fun CompactCategoryChooser(
    modifier: Modifier = Modifier,
    allCategories: List<CategoriesUi>,
    selectedCategory: MainCategoryUi,
    onCategoryChange: (MainCategoryUi) -> Unit,
) {
    var isCategoryMenuOpen by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed: Boolean by interactionSource.collectIsPressedAsState()

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Icon(
            painter = painterResource(id = HomeThemeRes.icons.category),
            contentDescription = HomeThemeRes.strings.mainCategoryLabel,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = selectedCategory.fetchName() ?: "*",
            onValueChange = {},
            readOnly = true,
            label = { Text(text = HomeThemeRes.strings.mainCategoryLabel) },
            trailingIcon = { ExpandedIcon(isExpanded = isCategoryMenuOpen) },
            interactionSource = interactionSource,
        )
        Box(contentAlignment = Alignment.TopEnd) {
            MainCategoriesChooseMenu(
                isExpanded = isCategoryMenuOpen,
                mainCategories = allCategories.map { it.mainCategory },
                onDismiss = { isCategoryMenuOpen = false },
                onChoose = { mainCategory ->
                    isCategoryMenuOpen = false
                    onCategoryChange(mainCategory)
                },
            )
        }
    }

    LaunchedEffect(key1 = isPressed) {
        if (isPressed) {
            isCategoryMenuOpen = !isCategoryMenuOpen
        }
    }
}

@Composable
internal fun MainCategoriesChooseMenu(
    modifier: Modifier = Modifier,
    isExpanded: Boolean,
    mainCategories: List<MainCategoryUi>,
    onDismiss: () -> Unit,
    onChoose: (MainCategoryUi) -> Unit,
) {
    DropdownMenu(
        expanded = isExpanded,
        onDismissRequest = onDismiss,
        modifier = modifier.sizeIn(maxHeight = 200.dp),
        offset = DpOffset(0.dp, 6.dp),
    ) {
        mainCategories.forEach { category ->
            DropdownMenuItem(
                onClick = { onChoose(category) },
                leadingIcon = {
                    if (category.defaultType != null) {
                        Icon(
                            modifier = Modifier.size(18.dp),
                            painter = category.defaultType.mapToIconPainter(),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    } else {
                        Text(
                            text = category.customName?.first()?.uppercaseChar()?.toString() ?: "*",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.titleLarge,
                        )
                    }
                },
                text = {
                    Text(
                        text = category.fetchName() ?: "*",
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleMedium,
                    )
                },
            )
        }
    }
}

@Composable
internal fun CompactSubCategoryChooser(
    modifier: Modifier = Modifier,
    allCategories: List<CategoriesUi>,
    selectedMainCategory: MainCategoryUi,
    selectedSubCategory: SubCategoryUi?,
    onSubCategoryChange: (SubCategoryUi?) -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    var isSubCategoryMenuOpen by remember { mutableStateOf(false) }
    val isPressed: Boolean by interactionSource.collectIsPressedAsState()
    val subCategories = allCategories.find { it.mainCategory == selectedMainCategory }?.subCategories ?: emptyList()

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Icon(
            painter = painterResource(id = HomeThemeRes.icons.subCategory),
            contentDescription = HomeThemeRes.strings.subCategoryLabel,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = selectedSubCategory?.name ?: HomeThemeRes.strings.subCategoryEmptyTitle,
            onValueChange = {},
            readOnly = true,
            label = { Text(text = HomeThemeRes.strings.subCategoryLabel) },
            trailingIcon = { ExpandedIcon(isExpanded = isSubCategoryMenuOpen) },
            interactionSource = interactionSource,
        )
        Box(contentAlignment = Alignment.TopEnd) {
            SubCategoriesChooseMenu(
                isExpanded = isSubCategoryMenuOpen,
                subCategories = subCategories.toMutableList().apply { add(SubCategoryUi()) },
                onDismiss = { isSubCategoryMenuOpen = false },
                onChoose = { subCategory ->
                    isSubCategoryMenuOpen = false
                    onSubCategoryChange(subCategory)
                },
            )
        }
    }

    LaunchedEffect(key1 = isPressed) {
        if (isPressed) {
            isSubCategoryMenuOpen = !isSubCategoryMenuOpen
        }
    }
}

@Composable
internal fun SubCategoriesChooseMenu(
    modifier: Modifier = Modifier,
    isExpanded: Boolean,
    subCategories: List<SubCategoryUi>,
    onDismiss: () -> Unit,
    onChoose: (SubCategoryUi?) -> Unit,
) {
    DropdownMenu(
        expanded = isExpanded,
        onDismissRequest = onDismiss,
        modifier = modifier.sizeIn(maxHeight = 200.dp),
        offset = DpOffset(0.dp, 6.dp),
    ) {
        subCategories.forEach { subCategory ->
            DropdownMenuItem(
                onClick = { if (subCategory.id == 0) onChoose(null) else onChoose(subCategory) },
                text = {
                    Text(
                        text = subCategory.name ?: TimePlannerRes.strings.categoryEmptyTitle,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleMedium,
                    )
                },
            )
        }
    }
}
