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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import ru.aleshin.core.ui.theme.TimePlannerRes
import ru.aleshin.core.ui.views.CategoryIconMonogram
import ru.aleshin.core.ui.views.CategoryTextMonogram
import ru.aleshin.core.ui.views.DialogButtons
import ru.aleshin.core.utils.functional.Constants
import ru.aleshin.features.home.api.domains.entities.categories.MainCategory
import ru.aleshin.features.home.api.presentation.mappers.fetchNameByLanguage
import ru.aleshin.features.home.api.presentation.mappers.toIconPainter
import ru.aleshin.features.home.impl.presentation.theme.HomeThemeRes

/**
 * @author Stanislav Aleshin on 16.04.2023.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MainCategoryEditorDialog(
    modifier: Modifier = Modifier,
    editCategory: MainCategory? = null,
    onDismiss: () -> Unit,
    onConfirm: (name: String) -> Unit,
) {
    var isError by rememberSaveable { mutableStateOf(false) }
    val categoryName = editCategory?.fetchNameByLanguage()
    val textRange = TextRange(categoryName?.length ?: 0)
    var mainCategoryNameValue by remember {
        mutableStateOf(TextFieldValue(text = categoryName ?: "", selection = textRange))
    }
    AlertDialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = modifier.width(328.dp).wrapContentHeight(),
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = TimePlannerRes.elevations.levelThree,
        ) {
            Column {
                MainCategoryEditorDialogHeader()
                Divider(Modifier.fillMaxWidth())
                Row(
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 0.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    if (editCategory?.icon != null) {
                        CategoryIconMonogram(
                            icon = checkNotNull(editCategory.icon).toIconPainter(),
                            iconDescription = editCategory.fetchNameByLanguage(),
                            iconColor = MaterialTheme.colorScheme.primary,
                            backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                        )
                    } else {
                        CategoryTextMonogram(
                            text = mainCategoryNameValue.text.firstOrNull()?.toString() ?: "-",
                            textColor = MaterialTheme.colorScheme.primary,
                            backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                        )
                    }
                    CategoryDialogField(
                        modifier = Modifier.fillMaxWidth(),
                        categoryNameValue = mainCategoryNameValue,
                        isError = isError,
                        onNameChange = { nameValue -> mainCategoryNameValue = nameValue },
                    )
                }
                DialogButtons(
                    confirmTitle = when (editCategory != null) {
                        true -> TimePlannerRes.strings.alertDialogOkConfirmTitle
                        false -> HomeThemeRes.strings.dialogCreateTitle
                    },
                    onConfirmClick = {
                        val text = mainCategoryNameValue.text
                        if (text.isNotEmpty() && text.length < Constants.Text.MAX_LENGTH) {
                            onConfirm(mainCategoryNameValue.text)
                        } else {
                            isError = true
                        }
                    },
                    onCancelClick = onDismiss,
                )
            }
        }
    }
}

@Composable
internal fun MainCategoryEditorDialogHeader(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(top = 24.dp, bottom = 12.dp, start = 24.dp, end = 24.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Text(
            text = HomeThemeRes.strings.mainCategoryChooserTitle,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.headlineSmall,
        )
    }
}
