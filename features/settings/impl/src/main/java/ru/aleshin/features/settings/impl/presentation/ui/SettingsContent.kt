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
package ru.aleshin.features.settings.impl.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.aleshin.features.settings.api.domain.entities.LanguageType
import ru.aleshin.features.settings.api.domain.entities.ThemeColorsType
import ru.aleshin.features.settings.api.domain.entities.ThemeSettings
import ru.aleshin.features.settings.impl.presentation.theme.SettingsThemeRes
import ru.aleshin.features.settings.impl.presentation.ui.contract.SettingsViewState
import ru.aleshin.features.settings.impl.presentation.ui.views.LanguageChooser
import ru.aleshin.features.settings.impl.presentation.ui.views.ThemeColorsChooser

/**
 * @author Stanislav Aleshin on 17.02.2023.
 */
@Composable
internal fun SettingsContent(
    state: SettingsViewState,
    modifier: Modifier = Modifier,
    onUpdateThemeSettings: (ThemeSettings) -> Unit,
) {
    Column(modifier = modifier.fillMaxSize()) {
        if (state.themeSettings != null) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                MainSettingsSection(
                    themeColors = state.themeSettings.themeColors,
                    languageType = state.themeSettings.language,
                    onThemeColorUpdate = { colorsType ->
                        onUpdateThemeSettings.invoke(state.themeSettings.copy(themeColors = colorsType))
                    },
                    onLanguageChanged = { language ->
                        onUpdateThemeSettings.invoke(state.themeSettings.copy(language = language))
                    },
                )
                Divider(modifier = Modifier.padding(vertical = 8.dp, horizontal = 8.dp))
            }
        }
    }
}

@Composable
internal fun MainSettingsSection(
    modifier: Modifier = Modifier,
    themeColors: ThemeColorsType,
    languageType: LanguageType,
    onThemeColorUpdate: (ThemeColorsType) -> Unit,
    onLanguageChanged: (LanguageType) -> Unit,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = SettingsThemeRes.strings.mainSettingsTitle,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodyLarge,
        )
        ThemeColorsChooser(
            modifier = Modifier.fillMaxWidth(),
            currentThemeColors = themeColors,
            onThemeColorUpdate = onThemeColorUpdate,
        )
        LanguageChooser(
            language = languageType,
            onLanguageChanged = onLanguageChanged,
        )
    }
}

/* ----------------------- Release Preview -----------------------
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
