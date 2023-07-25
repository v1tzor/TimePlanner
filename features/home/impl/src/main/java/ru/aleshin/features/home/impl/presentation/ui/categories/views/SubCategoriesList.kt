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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.aleshin.core.ui.theme.TimePlannerRes
import ru.aleshin.features.home.api.domains.entities.categories.MainCategory
import ru.aleshin.features.home.api.domains.entities.categories.SubCategory
import ru.aleshin.features.home.api.presentation.mappers.fetchNameByLanguage
import ru.aleshin.features.home.impl.presentation.theme.HomeThemeRes

/**
 * @author Stanislav Aleshin on 16.04.2023.
 */
@Composable
internal fun SubCategoriesList(
    modifier: Modifier = Modifier,
    mainCategory: MainCategory?,
    subCategories: List<SubCategory>,
    onCategoryUpdate: (SubCategory) -> Unit,
    onCategoryDelete: (SubCategory) -> Unit,
    onAddSubCategory: () -> Unit,
) {
    val listState = rememberLazyListState()
    if (mainCategory != null) {
        LazyColumn(
            modifier = modifier.padding(top = 8.dp).animateContentSize(),
            state = listState,
            contentPadding = PaddingValues(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            if (subCategories.isNotEmpty()) {
                items(subCategories) { subCategory ->
                    SubCategoryViewItem(
                        mainCategory = mainCategory,
                        subCategory = subCategory,
                        onChange = onCategoryUpdate,
                        onDelete = onCategoryDelete,
                    )
                }
            }
            item {
                SubCategoryAddItem(onClick = onAddSubCategory)
            }
        }
    }
}

@Composable
internal fun SubCategoryViewItem(
    modifier: Modifier = Modifier,
    mainCategory: MainCategory,
    subCategory: SubCategory,
    onChange: (SubCategory) -> Unit,
    onDelete: (SubCategory) -> Unit,
) {
    var isEditable by remember { mutableStateOf(false) }
    Surface(
        onClick = { isEditable = !isEditable },
        modifier = modifier.animateContentSize(),
        enabled = true,
        shape = MaterialTheme.shapes.large,
        color = when (isEditable) {
            true -> MaterialTheme.colorScheme.secondaryContainer
            false -> MaterialTheme.colorScheme.surface
        },
        tonalElevation = when (isEditable) {
            true -> TimePlannerRes.elevations.levelZero
            false -> TimePlannerRes.elevations.levelTwo
        },
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 8.dp, top = 12.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = Modifier.align(Alignment.CenterVertically),
                text = subCategory.fetchNameByLanguage(),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(modifier = Modifier.weight(1f))
            IconButton(
                modifier = Modifier.size(36.dp),
                onClick = { onDelete(subCategory) },
            ) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
    if (isEditable) {
        SubCategoryEditorDialog(
            mainCategory = mainCategory,
            editSubCategory = subCategory,
            onDismiss = { isEditable = false },
            onConfirm = { name ->
                onChange(subCategory.copy(name = name))
                isEditable = false
            },
        )
    }
}

@Composable
internal fun SubCategoryAddItem(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        modifier = modifier.animateContentSize(),
        enabled = true,
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = TimePlannerRes.elevations.levelOne,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 8.dp, top = 4.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = HomeThemeRes.strings.addCategoryTitle,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelLarge,
            )
            IconButton(
                modifier = Modifier.size(36.dp),
                onClick = onClick,
            ) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}
