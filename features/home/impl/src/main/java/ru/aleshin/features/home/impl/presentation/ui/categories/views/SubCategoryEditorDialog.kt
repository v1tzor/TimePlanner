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

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import ru.aleshin.core.ui.theme.TimePlannerRes
import ru.aleshin.core.ui.views.DialogButtons
import ru.aleshin.features.home.api.domains.entities.categories.MainCategory
import ru.aleshin.features.home.api.domains.entities.categories.SubCategory
import ru.aleshin.features.home.api.presentation.mappers.fetchNameByLanguage
import ru.aleshin.features.home.impl.presentation.theme.HomeThemeRes

/**
 * @author Stanislav Aleshin on 10.04.2023.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SubCategoryEditorDialog(
    modifier: Modifier = Modifier,
    mainCategory: MainCategory,
    editSubCategory: SubCategory? = null,
    onDismiss: () -> Unit,
    onConfirm: (name: String) -> Unit,
) {
    var isError by rememberSaveable { mutableStateOf(false) }
    val subCategoryName = editSubCategory?.fetchNameByLanguage()
    val textRange = TextRange(subCategoryName?.length ?: 0)
    var subCategoryNameValue by remember {
        mutableStateOf(TextFieldValue(text = subCategoryName ?: "", selection = textRange))
    }
    AlertDialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = modifier.width(328.dp).wrapContentHeight(),
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = TimePlannerRes.elevations.levelThree,
        ) {
            Column {
                SubCategoryEditorDialogHeader(mainCategory = mainCategory)
                Divider(Modifier.fillMaxWidth())
                CategoryDialogField(
                    modifier = Modifier.fillMaxWidth().padding(
                        top = 18.dp,
                        bottom = 8.dp,
                        start = 24.dp,
                        end = 24.dp,
                    ),
                    categoryNameValue = subCategoryNameValue,
                    isError = isError,
                    onNameChange = { nameValue -> subCategoryNameValue = nameValue },
                )
                DialogButtons(
                    confirmTitle = when (editSubCategory != null) {
                        true -> TimePlannerRes.strings.alertDialogOkConfirmTitle
                        false -> HomeThemeRes.strings.dialogCreateTitle
                    },
                    onConfirmClick = {
                        val text = subCategoryNameValue.text
                        if (text.isNotEmpty() && text.length < 100) {
                            onConfirm(subCategoryNameValue.text)
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
internal fun SubCategoryEditorDialogHeader(
    modifier: Modifier = Modifier,
    mainCategory: MainCategory,
) {
    Column(
        modifier = modifier.padding(top = 24.dp, bottom = 12.dp, start = 24.dp, end = 12.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        val mainCategoryName = mainCategory.fetchNameByLanguage()
        Text(
            text = HomeThemeRes.strings.subCategoryChooserTitle,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.headlineSmall,
        )
        Text(
            text = HomeThemeRes.strings.subCategoryDialogMainCategoryFormat.format(mainCategoryName),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
internal fun CategoryDialogField(
    modifier: Modifier = Modifier,
    isError: Boolean,
    categoryNameValue: TextFieldValue,
    onNameChange: (TextFieldValue) -> Unit,
) {
    val requester = remember { FocusRequester() }
    Box(modifier = modifier) {
        OutlinedTextField(
            modifier = Modifier.focusRequester(requester),
            value = categoryNameValue,
            onValueChange = onNameChange,
            singleLine = true,
            isError = isError,
            label = { Text(text = HomeThemeRes.strings.categoryFieldLabel) },
        )
    }
    LaunchedEffect(
        key1 = Unit,
        block = { requester.requestFocus() },
    )
}
