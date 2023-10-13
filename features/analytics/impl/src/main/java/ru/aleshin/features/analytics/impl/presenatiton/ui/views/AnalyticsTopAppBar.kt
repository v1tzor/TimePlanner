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
package ru.aleshin.features.analytics.impl.presenatiton.ui.views

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import ru.aleshin.core.ui.views.TopAppBarButton
import ru.aleshin.core.ui.views.TopAppBarEmptyButton
import ru.aleshin.core.ui.views.TopAppBarTitle
import ru.aleshin.features.analytics.impl.presenatiton.theme.AnalyticsThemeRes

/**
 * @author Stanislav Aleshin on 30.03.2023.
 */
@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun AnalyticsTopAppBar(
    modifier: Modifier = Modifier,
    onMenuButtonClick: () -> Unit,
) {
    TopAppBar(
        modifier = modifier,
        title = {
            TopAppBarTitle(
                text = AnalyticsThemeRes.strings.topAppBarTitle,
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
            TopAppBarEmptyButton()
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
        ),
    )
}
/* ----------------------- Release Preview -----------------------
@Preview
@Composable
internal fun AnalyticsTopAppBar_Light_Preview() {
    TimePlannerTheme(
        dynamicColor = false,
        themeColorsType = ThemeColorsUiType.LIGHT,
        language = LanguageUiType.RU,
    ) {
        AnalyticsTheme {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                content = { Box(modifier = Modifier.padding(it)) {} },
                topBar = {
                    AnalyticsTopAppBar(onMenuButtonClick = {})
                },
            )
        }
    }
}

@Preview
@Composable
internal fun AnalyticsTopAppBar_Dark_Preview() {
    TimePlannerTheme(
        dynamicColor = false,
        themeColorsType = ThemeColorsUiType.DARK,
        language = LanguageUiType.RU,
    ) {
        AnalyticsTheme {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                content = { Box(modifier = Modifier.padding(it)) {} },
                topBar = {
                    AnalyticsTopAppBar(onMenuButtonClick = {})
                },
            )
        }
    }
}
*/
