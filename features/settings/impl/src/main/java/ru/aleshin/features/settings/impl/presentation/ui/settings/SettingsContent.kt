/*
 * Copyright 2026 Stanislav Aleshin
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
package ru.aleshin.features.settings.impl.presentation.ui.settings

import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import ru.aleshin.core.domain.entities.settings.CalendarButtonBehavior
import ru.aleshin.core.utils.architecture.store.compose.handleEffects
import ru.aleshin.core.utils.architecture.store.compose.stateAsState
import ru.aleshin.core.utils.extensions.openNetworkUri
import ru.aleshin.core.utils.functional.Constants
import ru.aleshin.features.settings.impl.presentation.mappers.mapToMessage
import ru.aleshin.features.settings.impl.presentation.models.TasksSettingsUi
import ru.aleshin.features.settings.impl.presentation.models.ThemeSettingsUi
import ru.aleshin.features.settings.impl.presentation.theme.SettingsThemeRes
import ru.aleshin.features.settings.impl.presentation.ui.settings.contract.RestoreBackupContract
import ru.aleshin.features.settings.impl.presentation.ui.settings.contract.SaveBackupContract
import ru.aleshin.features.settings.impl.presentation.ui.settings.contract.SettingsEffect
import ru.aleshin.features.settings.impl.presentation.ui.settings.contract.SettingsEvent
import ru.aleshin.features.settings.impl.presentation.ui.settings.contract.SettingsState
import ru.aleshin.features.settings.impl.presentation.ui.settings.contract.launch
import ru.aleshin.features.settings.impl.presentation.ui.settings.screensmodel.SettingsComponent
import ru.aleshin.features.settings.impl.presentation.ui.settings.views.AboutAppSection
import ru.aleshin.features.settings.impl.presentation.ui.settings.views.CalendarButtonBehaviorChooser
import ru.aleshin.features.settings.impl.presentation.ui.settings.views.ColorsTypeChooser
import ru.aleshin.features.settings.impl.presentation.ui.settings.views.DonateButton
import ru.aleshin.features.settings.impl.presentation.ui.settings.views.DynamicColorChooser
import ru.aleshin.features.settings.impl.presentation.ui.settings.views.LanguageChooser
import ru.aleshin.features.settings.impl.presentation.ui.settings.views.SettingsItemDivider
import ru.aleshin.features.settings.impl.presentation.ui.settings.views.SettingsItemIcon
import ru.aleshin.features.settings.impl.presentation.ui.settings.views.SettingsTopAppBar
import ru.aleshin.features.settings.impl.presentation.ui.settings.views.ThemeColorsChooser
import ru.aleshin.timeplanner.core.ui.theme.material.ColorsUiType
import ru.aleshin.timeplanner.core.ui.theme.material.ThemeUiType
import ru.aleshin.timeplanner.core.ui.theme.tokens.LanguageUiType
import ru.aleshin.timeplanner.core.ui.views.WarningDeleteDialog

/**
 * @author Stanislav Aleshin on 17.02.2023.
 */
@Composable
internal fun SettingsContent(
    settingsComponent: SettingsComponent,
    modifier: Modifier = Modifier,
) {
    val store = settingsComponent.store
    val state by store.stateAsState()
    val strings = SettingsThemeRes.strings
    val snackbarState = remember { SnackbarHostState() }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        content = { paddingValues ->
            BaseSettingsContent(
                state = state,
                modifier = Modifier.padding(paddingValues),
                onBackupData = { store.dispatchEvent(SettingsEvent.PressSaveBackupData(it)) },
                onRestoreData = { store.dispatchEvent(SettingsEvent.PressRestoreBackupData(it)) },
                onClearData = { store.dispatchEvent(SettingsEvent.PressClearDataButton) },
                onDonateButtonClick = { store.dispatchEvent(SettingsEvent.PressDonateButton) },
                onUpdateThemeSettings = { themeSettings ->
                    store.dispatchEvent(SettingsEvent.ChangedThemeSettings(themeSettings))
                },
                onUpdateTasksSettings = { tasksSettings ->
                    store.dispatchEvent(SettingsEvent.ChangedTasksSettings(tasksSettings))
                },
            )
        },
        topBar = {
            SettingsTopAppBar(
                onResetToDefaultClick = { store.dispatchEvent(SettingsEvent.PressResetButton) },
                onBackIconClick = { store.dispatchEvent(SettingsEvent.PressBackIcon) }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarState)
        },
    )

    store.handleEffects { effect ->
        when (effect) {
            is SettingsEffect.ShowError -> snackbarState.showSnackbar(
                message = effect.failures.mapToMessage(strings),
                withDismissAction = true,
            )
        }
    }
}

@Composable
private fun BaseSettingsContent(
    state: SettingsState,
    modifier: Modifier = Modifier,
    onClearData: () -> Unit,
    onRestoreData: (uri: Uri) -> Unit,
    onBackupData: (uri: Uri) -> Unit,
    onUpdateThemeSettings: (ThemeSettingsUi) -> Unit,
    onUpdateTasksSettings: (TasksSettingsUi) -> Unit,
    onDonateButtonClick: () -> Unit,
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        Column(
            modifier = Modifier
                .widthIn(max = 600.dp)
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            if (state.themeSettings != null && state.tasksSettings != null) {
                MainSettingsSection(
                    modifier = Modifier.padding(top = 16.dp),
                    languageType = state.themeSettings.language,
                    themeColors = state.themeSettings.themeColors,
                    colorsType = state.themeSettings.colorsType,
                    dynamicColor = state.themeSettings.isDynamicColorEnable,
                    onThemeColorUpdate = { colorsType ->
                        onUpdateThemeSettings(state.themeSettings.copy(themeColors = colorsType))
                    },
                    onLanguageChange = { language ->
                        onUpdateThemeSettings(state.themeSettings.copy(language = language))
                    },
                    onColorsTypeUpdate = { colorsType ->
                        onUpdateThemeSettings(state.themeSettings.copy(colorsType = colorsType))
                    },
                    onDynamicColorsChange = {
                        onUpdateThemeSettings(state.themeSettings.copy(isDynamicColorEnable = it))
                    },
                )
                InterfaceSettingsSection(
                    calendarButtonBehavior = state.tasksSettings.calendarButtonBehavior,
                    onUpdateCalendarBehavior = {
                        onUpdateTasksSettings(state.tasksSettings.copy(calendarButtonBehavior = it))
                    },
                )
                SecureSettingsSection(
                    secureMode = state.tasksSettings.secureMode,
                    onUpdateSecureMode = {
                        onUpdateTasksSettings(state.tasksSettings.copy(secureMode = it))
                    },
                )
                DataSettingsSection(
                    onClear = onClearData,
                    isLoading = state.isBackupLoading,
                    onBackupData = onBackupData,
                    onRestoreData = onRestoreData,
                )
                AboutAppSection(
                    onOpenGit = { context.openNetworkUri(Constants.App.GITHUB_URI) },
                    onOpenIssues = { context.openNetworkUri(Constants.App.ISSUES_URI) },
                )
                DonateButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onDonateButtonClick,
                )
            }
            Spacer(modifier = Modifier.height(90.dp))
        }
    }
}

@Composable
internal fun MainSettingsSection(
    modifier: Modifier = Modifier,
    languageType: LanguageUiType,
    themeColors: ThemeUiType,
    colorsType: ColorsUiType,
    dynamicColor: Boolean,
    onLanguageChange: (LanguageUiType) -> Unit,
    onThemeColorUpdate: (ThemeUiType) -> Unit,
    onColorsTypeUpdate: (ColorsUiType) -> Unit,
    onDynamicColorsChange: (Boolean) -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = SettingsThemeRes.strings.mainSettingsTitle,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleMedium,
        )
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.surfaceContainerLow,
        ) {
            Column {
                ThemeColorsChooser(
                    modifier = Modifier.fillMaxWidth(),
                    themeColors = themeColors,
                    onThemeColorUpdate = onThemeColorUpdate,
                )
                SettingsItemDivider()
                ColorsTypeChooser(
                    modifier = Modifier.fillMaxWidth(),
                    colorsType = colorsType,
                    onChoose = onColorsTypeUpdate,
                )
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    SettingsItemDivider()
                    DynamicColorChooser(
                        dynamicColor = dynamicColor,
                        onChange = onDynamicColorsChange,
                    )
                }
                SettingsItemDivider()
                LanguageChooser(
                    language = languageType,
                    onLanguageChanged = onLanguageChange,
                )
            }
        }
    }
}

@Composable
internal fun InterfaceSettingsSection(
    modifier: Modifier = Modifier,
    calendarButtonBehavior: CalendarButtonBehavior,
    onUpdateCalendarBehavior: (CalendarButtonBehavior) -> Unit,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = SettingsThemeRes.strings.interfaceSectionHeader,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleMedium,
        )
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.surfaceContainerLow,
        ) {
            CalendarButtonBehaviorChooser(
                calendarButtonBehavior = calendarButtonBehavior,
                onUpdateCalendarBehavior = onUpdateCalendarBehavior,
            )
        }
    }
}

@Composable
internal fun SecureSettingsSection(
    modifier: Modifier = Modifier,
    secureMode: Boolean,
    onUpdateSecureMode: (Boolean) -> Unit,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = SettingsThemeRes.strings.secureSectionHeader,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleMedium,
        )
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.surfaceContainerLow,
        ) {
            Row(
                modifier = Modifier.padding(vertical = 16.dp, horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                SettingsItemIcon(
                    icon = SettingsThemeRes.icons.lock,
                    contentDescription = null,
                )
                Text(
                    modifier = Modifier.padding(start = 16.dp).weight(1f),
                    text = SettingsThemeRes.strings.secureModeTitle,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleMedium,
                )
                Switch(checked = secureMode, onCheckedChange = onUpdateSecureMode)
            }
        }
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
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = SettingsThemeRes.strings.dataSectionHeader,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleMedium,
        )
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.surfaceContainerLow,
        ) {
            Column {
                BackupDataView(
                    isLoading = isLoading,
                    onBackupData = onBackupData,
                    onRestoreData = onRestoreData,
                )
                SettingsItemDivider()
                ClearDataView(onClear = onClear)
            }
        }
    }
}

@Composable
internal fun ClearDataView(
    modifier: Modifier = Modifier,
    onClear: () -> Unit,
) {
    var isOpenDialog by rememberSaveable { mutableStateOf(false) }
    Surface(
        onClick = { isOpenDialog = true },
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
        Row(
            modifier = Modifier.padding(vertical = 16.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SettingsItemIcon(
                icon = SettingsThemeRes.icons.delete,
                contentDescription = null,
                iconColor = MaterialTheme.colorScheme.error,
                containerColor = MaterialTheme.colorScheme.errorContainer,
            )
            Text(
                modifier = Modifier.padding(start = 16.dp).weight(1f),
                text = SettingsThemeRes.strings.clearDataTitle,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.titleMedium,
            )
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(SettingsThemeRes.icons.chevronRight),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
            )
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
    var isOpenDialog by rememberSaveable { mutableStateOf(false) }
    Surface(
        onClick = { isOpenDialog = true },
        modifier = modifier.fillMaxWidth().animateContentSize(),
        enabled = !isLoading,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
        Row(
            modifier = Modifier.padding(vertical = 16.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SettingsItemIcon(
                icon = SettingsThemeRes.icons.cloudUpload,
                contentDescription = null,
            )
            Text(
                modifier = Modifier.padding(start = 16.dp).weight(1f),
                text = SettingsThemeRes.strings.backupDataTitle,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium,
            )
            if (!isLoading) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(SettingsThemeRes.icons.chevronRight),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                CircularProgressIndicator(modifier = Modifier.size(32.dp))
            }
        }
    }
    BackupDataDialog(
        openDialog = isOpenDialog,
        onCloseDialog = { isOpenDialog = false },
        onBackupData = {
            isOpenDialog = false
            saveBackupLauncher.launch()
        },
        onRestoreData = {
            isOpenDialog = false
            restoreBackupLauncher.launch()
        },
    )
}


@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun BackupDataDialog(
    modifier: Modifier = Modifier,
    openDialog: Boolean,
    onCloseDialog: () -> Unit,
    onBackupData: () -> Unit,
    onRestoreData: () -> Unit,
) {
    if (openDialog) {
        BasicAlertDialog(onDismissRequest = onCloseDialog) {
            Surface(
                modifier = modifier.width(280.dp),
                shape = MaterialTheme.shapes.extraLarge,
                color = MaterialTheme.colorScheme.surfaceContainer,
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(
                        text = SettingsThemeRes.strings.backupDataTitle,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.headlineSmall,
                    )
                    FilledTonalButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = onBackupData,
                    ) {
                        Icon(
                            painter = painterResource(SettingsThemeRes.icons.cloudUpload),
                            contentDescription = null,
                        )
                        Text(
                            modifier = Modifier.padding(start = 8.dp),
                            text = SettingsThemeRes.strings.backupDataButtonTitle,
                        )
                    }
                    FilledTonalButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = onRestoreData,
                    ) {
                        Icon(
                            painter = painterResource(SettingsThemeRes.icons.cloudDownload),
                            contentDescription = null,
                        )
                        Text(
                            modifier = Modifier.padding(start = 8.dp),
                            text = SettingsThemeRes.strings.restoreDataButtonTitle,
                        )
                    }
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
