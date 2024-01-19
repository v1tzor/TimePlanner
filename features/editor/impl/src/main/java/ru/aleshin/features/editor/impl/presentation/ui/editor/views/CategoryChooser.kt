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

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import ru.aleshin.core.domain.entities.categories.DefaultCategoryType
import ru.aleshin.core.ui.mappers.mapToIconPainter
import ru.aleshin.core.ui.theme.TimePlannerRes
import ru.aleshin.core.ui.views.CategoryIconMonogram
import ru.aleshin.core.ui.views.CategoryTextMonogram
import ru.aleshin.core.ui.views.DialogButtons
import ru.aleshin.features.editor.impl.presentation.models.categories.MainCategoryUi
import ru.aleshin.features.editor.impl.presentation.theme.EditorThemeRes

/**
 * @author Stanislav Aleshin on 25.02.2023.
 */
@Composable
internal fun MainCategoryChooser(
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    allCategories: List<MainCategoryUi>,
    currentCategory: MainCategoryUi?,
    onChange: (MainCategoryUi) -> Unit,
) {
    val openDialog = rememberSaveable { mutableStateOf(false) }
    val categoryIcon = currentCategory?.defaultType?.mapToIconPainter()
    val categoryName = currentCategory?.fetchName()
    
    Surface(
        onClick = { openDialog.value = true },
        modifier = modifier.height(68.dp),
        enabled = currentCategory != null && enabled,
        shape = MaterialTheme.shapes.medium,
        color = when (isError) {
            true -> MaterialTheme.colorScheme.errorContainer
            false -> MaterialTheme.colorScheme.surface
        },
        tonalElevation = if (!isError) TimePlannerRes.elevations.levelOne else 0.dp,
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
                contentDescription = EditorThemeRes.strings.mainCategoryChooserExpandedIconDesc,
                tint = categoryNameColor,
            )
        }
    }
    if (openDialog.value && currentCategory != null) {
        MainCategoryDialogChooser(
            initCategory = currentCategory,
            allCategories = allCategories,
            onDismiss = { openDialog.value = false },
            onChooseCategory = {
                onChange(it)
                openDialog.value = false
            },
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun MainCategoryDialogChooser(
    modifier: Modifier = Modifier,
    allCategories: List<MainCategoryUi>,
    initCategory: MainCategoryUi,
    onDismiss: () -> Unit,
    onChooseCategory: (MainCategoryUi) -> Unit,
) {
    val initPosition = allCategories.indexOf(initCategory).let { if (it == -1) 0 else it }
    val listState = rememberLazyListState(initPosition)
    var selectedCategory by rememberSaveable { mutableStateOf(initCategory) }

    AlertDialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = modifier.width(280.dp).wrapContentHeight(),
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = TimePlannerRes.elevations.levelThree,
        ) {
            Column {
                Box(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = EditorThemeRes.strings.mainCategoryChooserTitle,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.headlineSmall,
                    )
                }
                LazyColumn(modifier = Modifier.height(300.dp), state = listState) {
                    items(allCategories) { category ->
                        MainCategoryDialogItem(
                            modifier = Modifier.fillMaxWidth(),
                            selected = selectedCategory == category,
                            title = category.fetchName() ?: "*",
                            icon = category.defaultType?.mapToIconPainter(),
                            onSelectedChange = { selectedCategory = category },
                        )
                    }
                }
                DialogButtons(
                    onCancelClick = onDismiss,
                    onConfirmClick = { onChooseCategory.invoke(selectedCategory) },
                )
            }
        }
    }
}

@Composable
internal fun MainCategoryDialogItem(
    modifier: Modifier = Modifier,
    selected: Boolean,
    title: String,
    icon: Painter?,
    onSelectedChange: () -> Unit,
) {
    Column {
        Row(
            modifier = modifier
                .padding(vertical = 8.dp, horizontal = 8.dp)
                .clip(MaterialTheme.shapes.medium)
                .clickable(onClick = onSelectedChange)
                .padding(start = 8.dp, end = 16.dp)
                .requiredHeight(56.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (icon != null) {
                CategoryIconMonogram(
                    icon = icon,
                    iconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                    iconDescription = title,
                )
            } else {
                CategoryTextMonogram(
                    text = title.first().toString(),
                    textColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                )
            }
            Text(
                modifier = Modifier.weight(1f),
                text = title,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge,
            )
            RadioButton(selected = selected, onClick = null)
        }
        Divider(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            color = MaterialTheme.colorScheme.outlineVariant,
        )
    }
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
