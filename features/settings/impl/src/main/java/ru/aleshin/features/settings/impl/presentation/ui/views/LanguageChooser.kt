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
package ru.aleshin.features.settings.impl.presentation.ui.views

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ru.aleshin.core.ui.theme.TimePlannerRes
import ru.aleshin.core.ui.views.DialogButtons
import ru.aleshin.features.settings.api.domain.entities.LanguageType
import ru.aleshin.features.settings.impl.presentation.mappers.toLanguageName
import ru.aleshin.features.settings.impl.presentation.theme.SettingsThemeRes

/**
 * @author Stanislav Aleshin on 21.03.2023.
 */
@Composable
fun LanguageChooser(
    modifier: Modifier = Modifier,
    language: LanguageType,
    onLanguageChanged: (LanguageType) -> Unit,
) {
    var isOpenDialog by rememberSaveable { mutableStateOf(false) }
    Surface(
        onClick = { isOpenDialog = true },
        modifier = modifier.height(56.dp),
        shape = MaterialTheme.shapes.medium,
        tonalElevation = TimePlannerRes.elevations.levelTwo,
    ) {
        Row(
            modifier = Modifier.padding(vertical = 16.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = SettingsThemeRes.strings.mainSettingsLanguageTitle,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                modifier = Modifier.weight(1f),
                text = language.toLanguageName(),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.End,
                style = MaterialTheme.typography.titleMedium,
            )
        }
    }
    LanguageDialogChooser(
        openDialog = isOpenDialog,
        initialLanguage = language,
        onCloseDialog = { isOpenDialog = false },
        onLanguageChoose = { languageType ->
            isOpenDialog = false
            onLanguageChanged(languageType)
        },
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun LanguageDialogChooser(
    modifier: Modifier = Modifier,
    openDialog: Boolean,
    initialLanguage: LanguageType,
    onCloseDialog: () -> Unit,
    onLanguageChoose: (LanguageType) -> Unit,
) {
    if (openDialog) {
        val initPosition = LanguageType.values().indexOf(initialLanguage)
        val listState = rememberLazyListState(initPosition)
        var selectedLanguage by rememberSaveable { mutableStateOf(initialLanguage) }

        AlertDialog(onDismissRequest = onCloseDialog) {
            Surface(
                modifier = modifier.width(280.dp).wrapContentHeight(),
                shape = MaterialTheme.shapes.extraLarge,
                tonalElevation = TimePlannerRes.elevations.levelThree,
            ) {
                Column {
                    Box(
                        modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 12.dp),
                    ) {
                        Text(
                            text = SettingsThemeRes.strings.mainSettingsLanguageTitle,
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.headlineSmall,
                        )
                    }
                    LazyColumn(modifier = Modifier.height(300.dp), state = listState) {
                        items(LanguageType.values()) { language ->
                            LanguageDialogItem(
                                modifier = Modifier.fillMaxWidth(),
                                title = language.toLanguageName(),
                                selected = selectedLanguage == language,
                                onSelectChange = { selectedLanguage = language },
                            )
                        }
                    }
                    DialogButtons(
                        onCancelClick = onCloseDialog,
                        onConfirmClick = { onLanguageChoose.invoke(selectedLanguage) },
                    )
                }
            }
        }
    }
}

@Composable
internal fun LanguageDialogItem(
    modifier: Modifier = Modifier,
    title: String,
    selected: Boolean,
    onSelectChange: () -> Unit,
) {
    Column {
        Row(
            modifier = modifier
                .padding(vertical = 8.dp, horizontal = 8.dp)
                .clip(MaterialTheme.shapes.medium)
                .clickable(onClick = onSelectChange)
                .padding(start = 8.dp, end = 16.dp)
                .requiredHeight(56.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 8.dp).weight(1f),
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
