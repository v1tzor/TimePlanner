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
package ru.aleshin.features.settings.impl.presentation.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.aleshin.core.ui.theme.TimePlannerRes
import ru.aleshin.core.ui.theme.material.ThemeColorsUiType
import ru.aleshin.core.ui.theme.tokens.LanguageUiType
import ru.aleshin.core.ui.views.WarningDeleteDialog
import ru.aleshin.features.settings.api.domain.entities.LanguageType
import ru.aleshin.features.settings.api.domain.entities.ThemeColorsType
import ru.aleshin.features.settings.api.domain.entities.ThemeSettings
import ru.aleshin.features.settings.impl.presentation.models.ThemeSettingsUi
import ru.aleshin.features.settings.impl.presentation.theme.SettingsThemeRes
import ru.aleshin.features.settings.impl.presentation.ui.contract.RestoreBackupContract
import ru.aleshin.features.settings.impl.presentation.ui.contract.SaveBackupContract
import ru.aleshin.features.settings.impl.presentation.ui.contract.SettingsViewState
import ru.aleshin.features.settings.impl.presentation.ui.contract.launch
import ru.aleshin.features.settings.impl.presentation.ui.views.DynamicColorChooser
import ru.aleshin.features.settings.impl.presentation.ui.views.LanguageChooser
import ru.aleshin.features.settings.impl.presentation.ui.views.ThemeColorsChooser

/**
 * @author Stanislav Aleshin on 17.02.2023.
 */
@Composable
internal fun SettingsContent(
    state: SettingsViewState,
    modifier: Modifier = Modifier,
    onClearData: () -> Unit,
    onRestoreData: (uri: Uri) -> Unit,
    onBackupData: (uri: Uri) -> Unit,
    onUpdateThemeSettings: (ThemeSettingsUi) -> Unit,
) {
    val scrollState = rememberScrollState()
    Column(modifier = modifier.fillMaxSize().verticalScroll(scrollState)) {
        if (state.themeSettings != null) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                MainSettingsSection(
                    themeColors = state.themeSettings.themeColors,
                    languageType = state.themeSettings.language,
                    dynamicColorEnabled = state.themeSettings.isDynamicColorEnable,
                    onThemeColorUpdate = { colorsType ->
                        onUpdateThemeSettings(state.themeSettings.copy(themeColors = colorsType))
                    },
                    onLanguageChanged = { language ->
                        onUpdateThemeSettings(state.themeSettings.copy(language = language))
                    },
                    onEnableDynamicColorsChanged = {
                        onUpdateThemeSettings(state.themeSettings.copy(isDynamicColorEnable = it))
                    },
                )
                Divider(modifier = Modifier.padding(top = 8.dp, bottom = 0.dp))
            }
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                DataSettingsSection(
                    onClear = onClearData,
                    isLoading = state.isBackupLoading,
                    onBackupData = onBackupData,
                    onRestoreData = onRestoreData,
                )
            }
        }
    }
}

@Composable
internal fun MainSettingsSection(
    modifier: Modifier = Modifier,
    themeColors: ThemeColorsUiType,
    languageType: LanguageUiType,
    dynamicColorEnabled: Boolean,
    onThemeColorUpdate: (ThemeColorsUiType) -> Unit,
    onLanguageChanged: (LanguageUiType) -> Unit,
    onEnableDynamicColorsChanged: (Boolean) -> Unit,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = SettingsThemeRes.strings.mainSettingsTitle,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelMedium,
        )
        ThemeColorsChooser(
            modifier = Modifier.fillMaxWidth(),
            currentThemeColors = themeColors,
            onThemeColorUpdate = onThemeColorUpdate,
        )
        DynamicColorChooser(
            dynamicColorEnabled = dynamicColorEnabled,
            onEnabledChanged = onEnableDynamicColorsChanged,
        )
        LanguageChooser(
            language = languageType,
            onLanguageChanged = onLanguageChanged,
        )
    }
}

@Composable
internal fun DataSettingsSection(
    modifier: Modifier = Modifier,
    isLoading: Boolean,
    onRestoreData: (uri: Uri) -> Unit,
    onBackupData: (uri: Uri) -> Unit,
    onClear: () -> Unit,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = SettingsThemeRes.strings.mainSettingsClearDataTitle,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelMedium,
        )
        ClearDataView(onClear = onClear)
        BackupDataView(
            onBackupData = onBackupData,
            onRestoreData = onRestoreData,
            isLoading = isLoading,
        )
    }
}

@Composable
internal fun ClearDataView(
    modifier: Modifier = Modifier,
    onClear: () -> Unit,
) {
    var isOpenDialog by rememberSaveable { mutableStateOf(false) }
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        tonalElevation = TimePlannerRes.elevations.levelTwo,
    ) {
        Row(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = SettingsThemeRes.strings.clearDataTitle,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium,
            )
            Button(onClick = { isOpenDialog = true }) {
                Text(text = SettingsThemeRes.strings.clearDataButtonTitle)
            }
        }
    }
    if (isOpenDialog) {
        WarningDeleteDialog(
            text = SettingsThemeRes.strings.clearDataWarning,
            onDismiss = { isOpenDialog = false },
            onAction = {
                isOpenDialog = false
                onClear()
            },
        )
    }
}

@Composable
internal fun BackupDataView(
    modifier: Modifier = Modifier,
    isLoading: Boolean,
    onRestoreData: (uri: Uri) -> Unit,
    onBackupData: (uri: Uri) -> Unit,
) {
    val restoreBackupLauncher = rememberLauncherForActivityResult(RestoreBackupContract) { uri ->
        if (uri != null) onRestoreData(uri)
    }
    val saveBackupLauncher = rememberLauncherForActivityResult(SaveBackupContract) { uri ->
        if (uri != null) onBackupData(uri)
    }
    Surface(
        modifier = modifier.fillMaxWidth().animateContentSize(),
        shape = MaterialTheme.shapes.medium,
        tonalElevation = TimePlannerRes.elevations.levelTwo,
    ) {
        Row(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            Text(
                text = SettingsThemeRes.strings.backupDataTitle,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium,
            )
            if (!isLoading) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Button(
                        modifier = Modifier.height(40.dp).fillMaxWidth(),
                        onClick = { saveBackupLauncher.launch() },
                        content = { Text(text = SettingsThemeRes.strings.backupDataButtonTitle) },
                    )
                    Button(
                        modifier = Modifier.height(40.dp).fillMaxWidth(),
                        onClick = { restoreBackupLauncher.launch() },
                        content = { Text(text = SettingsThemeRes.strings.restoreDataButtonTitle) },
                    )
                }
            } else {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterEnd) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

/* ----------------------- Release Preview-----------------------
@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun SettingsContent_Preview() {
    TimePlannerTheme(
        dynamicColor = false,
        themeColorsType = ThemeColorsUiType.LIGHT,
        language = LanguageUiType.RU,
    ) {
        SettingsTheme {
            val state = SettingsViewState(
                themeSettings = ThemeSettings(LanguageType.RU, ThemeColorsType.LIGHT),
            )
            Box(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
                SettingsContent(
                    state = state,
                    onUpdateThemeSettings = {},
                )
            }
        }
    }
}*/
