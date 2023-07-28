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
package ru.aleshin.features.home.impl.presentation.ui.home.views

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import ru.aleshin.core.ui.views.TopAppBarButton
import ru.aleshin.core.ui.views.TopAppBarTitle
import ru.aleshin.features.home.impl.presentation.theme.HomeThemeRes

/**
 * @author Stanislav Aleshin on 20.02.2023.
 */
@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun HomeTopAppBar(
    onMenuIconClick: () -> Unit,
    onCalendarIconClick: () -> Unit,
    onGoToToday: () -> Unit,
) {
    TopAppBar(
        title = {
            TopAppBarTitle(
                text = HomeThemeRes.strings.topAppBarHomeTitle,
                textAlign = TextAlign.Center,
            )
        },
        navigationIcon = {
            TopAppBarButton(
                imageVector = Icons.Default.Menu,
                imageDescription = HomeThemeRes.strings.topAppBarMenuIconDesc,
                onButtonClick = onMenuIconClick,
            )
        },
        actions = {
            TopAppBarButton(
                imagePainter = painterResource(HomeThemeRes.icons.calendar),
                imageDescription = HomeThemeRes.strings.topAppBarCalendarIconDesc,
                onButtonClick = onGoToToday,
                onLongButtonClick = onCalendarIconClick,
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
        ),
    )
}

/* ----------------------- Release Preview -----------------------
@Preview
@Composable
internal fun HomeTopAppBar_Light_Preview() {
    TimePlannerTheme(
        dynamicColor = false,
        themeColorsType = ThemeColorsUiType.LIGHT,
        language = LanguageUiType.RU,
    ) {
        HomeTheme {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                content = { Box(modifier = Modifier.padding(it)) {} },
                topBar = {
                    HomeTopAppBar(onMenuIconClick = {}, onCalendarIconClick = {})
                },
            )
        }
    }
}

@Preview
@Composable
internal fun HomeTopAppBar_Dark_Preview() {
    TimePlannerTheme(
        dynamicColor = false,
        themeColorsType = ThemeColorsUiType.DARK,
        language = LanguageUiType.RU,
    ) {
        HomeTheme {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                content = { Box(modifier = Modifier.padding(it)) {} },
                topBar = {
                    HomeTopAppBar(onMenuIconClick = {}, onCalendarIconClick = {})
                },
            )
        }
    }
}*/
