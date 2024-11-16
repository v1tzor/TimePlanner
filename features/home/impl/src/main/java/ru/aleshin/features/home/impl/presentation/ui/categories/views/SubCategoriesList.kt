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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.aleshin.core.domain.entities.categories.DefaultCategoryType
import ru.aleshin.core.ui.theme.TimePlannerRes
import ru.aleshin.core.utils.extensions.alphaByEnabled
import ru.aleshin.features.home.impl.presentation.models.categories.MainCategoryUi
import ru.aleshin.features.home.impl.presentation.models.categories.SubCategoryUi
import ru.aleshin.features.home.impl.presentation.theme.HomeThemeRes

/**
 * @author Stanislav Aleshin on 16.04.2023.
 */
@Composable
internal fun SubCategoriesList(
    modifier: Modifier = Modifier,
    mainCategory: MainCategoryUi?,
    subCategories: List<SubCategoryUi>,
    onCategoryUpdate: (SubCategoryUi) -> Unit,
    onCategoryDelete: (SubCategoryUi) -> Unit,
    onAddSubCategory: () -> Unit,
) {
    if (mainCategory != null) {
        Column(
            modifier = modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp).animateContentSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            if (subCategories.isNotEmpty()) {
                subCategories.forEach { subCategory ->
                    SubCategoryViewItem(
                        mainCategory = mainCategory,
                        subCategory = subCategory,
                        onChange = onCategoryUpdate,
                        onDelete = onCategoryDelete,
                    )
                }
            }
            SubCategoryAddItem(
                enabled = mainCategory.defaultType != DefaultCategoryType.EMPTY,
                onClick = onAddSubCategory,
            )
            Spacer(modifier = Modifier.height(50.dp))
        }
    }
}

@Composable
internal fun SubCategoryViewItem(
    modifier: Modifier = Modifier,
    mainCategory: MainCategoryUi,
    subCategory: SubCategoryUi,
    onChange: (SubCategoryUi) -> Unit,
    onDelete: (SubCategoryUi) -> Unit,
) {
    var isEditable by rememberSaveable { mutableStateOf(false) }
    Surface(
        onClick = { isEditable = !isEditable },
        modifier = modifier.animateContentSize(),
        enabled = true,
        shape = MaterialTheme.shapes.large,
        color = when (isEditable) {
            true -> MaterialTheme.colorScheme.secondaryContainer
            false -> MaterialTheme.colorScheme.surfaceContainerLow
        },
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 8.dp, top = 12.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = Modifier.align(Alignment.CenterVertically),
                text = subCategory.name ?: TimePlannerRes.strings.categoryEmptyTitle,
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
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        modifier = modifier.animateContentSize().alphaByEnabled(enabled),
        enabled = enabled,
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceContainer,
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
