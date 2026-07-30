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
package ru.aleshin.features.settings.impl.presentation.ui.settings.views.sections

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import ru.aleshin.features.settings.impl.presentation.theme.SettingsThemeRes
import ru.aleshin.features.settings.impl.presentation.ui.common.SettingsItemIcon
import ru.aleshin.features.settings.impl.presentation.ui.common.SettingsSection
import ru.aleshin.features.settings.impl.presentation.ui.settings.contract.RestoreBackupContract
import ru.aleshin.features.settings.impl.presentation.ui.settings.contract.SaveBackupContract
import ru.aleshin.features.settings.impl.presentation.ui.settings.contract.launch
import ru.aleshin.timeplanner.core.ui.views.WarningDeleteDialog

/**
 * @author Stanislav Aleshin on 29.07.2026.
 */
@Composable
internal fun DataSection(
    modifier: Modifier = Modifier,
    isLoading: Boolean,
    onRestoreData: (uri: Uri) -> Unit,
    onBackupData: (uri: Uri) -> Unit,
    onClear: () -> Unit,
) {
    SettingsSection(
        modifier = modifier,
        title = SettingsThemeRes.strings.dataSectionHeader,
    ) {
        BackupDataView(
            isLoading = isLoading,
            onBackupData = onBackupData,
            onRestoreData = onRestoreData,
        )
        HorizontalDivider()
        ClearDataView(onClear = onClear)
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
private fun BackupDataView(
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
private fun BackupDataDialog(
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