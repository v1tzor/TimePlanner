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
package ru.aleshin.features.settings.impl.presentation.ui.settings.views

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextAlign
import ru.aleshin.core.ui.theme.TimePlannerRes
import ru.aleshin.core.ui.views.*
import ru.aleshin.features.settings.impl.presentation.theme.SettingsThemeRes

/**
 * @author Stanislav Aleshin on 20.02.2023.
 */
@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun SettingsTopAppBar(
    onResetToDefaultClick: () -> Unit,
    onMenuButtonClick: () -> Unit,
) {
    TopAppBar(
        title = {
            TopAppBarTitle(
                text = SettingsThemeRes.strings.settingsTitle,
                textAlign = TextAlign.Center,
            )
        },
        navigationIcon = {
            TopAppBarButton(
                imageVector = Icons.Default.Menu,
                imageDescription = null,
                onButtonClick = onMenuButtonClick,
            )
        },
        actions = {
            TopAppBarMoreActions(
                items = SettingsMoreActions.values(),
                moreIconDescription = SettingsThemeRes.strings.moreIconDesc,
                onItemClick = { action ->
                    when (action) {
                        SettingsMoreActions.RESET_TO_DEFAULT -> onResetToDefaultClick.invoke()
                    }
                },
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
        ),
    )
}

internal enum class SettingsMoreActions : TopAppBarAction {
    RESET_TO_DEFAULT {
        override val title: String @Composable get() = SettingsThemeRes.strings.resetToDefaultTitle
        override val icon: Int @Composable get() = TimePlannerRes.icons.reset
        override val isAlwaysShow: Boolean = false
    },
}

/* ----------------------- Release Preview -----------------------
@Preview
@Composable
internal fun SettingsTopAppBar_Light_Preview() {
    TimePlannerTheme(
        dynamicColor = false,
        themeColorsType = ThemeColorsUiType.LIGHT,
        language = LanguageUiType.RU,
    ) {
        SettingsTheme {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                content = { Box(modifier = Modifier.padding(it)) {} },
                topBar = {
                    SettingsTopAppBar(
                        onResetToDefault = {},
                        onMenuButtonClick = {},
                    )
                },
            )
        }
    }
}

@Preview
@Composable
internal fun SettingsTopAppBar_Dark_Preview() {
    TimePlannerTheme(
        dynamicColor = false,
        themeColorsType = ThemeColorsUiType.DARK,
        language = LanguageUiType.RU,
    ) {
        SettingsTheme {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                content = { Box(modifier = Modifier.padding(it)) {} },
                topBar = {
                    SettingsTopAppBar(
                        onResetToDefault = {},
                        onMenuButtonClick = {},
                    )
                },
            )
        }
    }
}
*/
