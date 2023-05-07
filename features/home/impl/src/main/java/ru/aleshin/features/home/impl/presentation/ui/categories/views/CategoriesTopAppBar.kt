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

import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import ru.aleshin.core.ui.views.TopAppBarButton
import ru.aleshin.core.ui.views.TopAppBarEmptyButton
import ru.aleshin.core.ui.views.TopAppBarTitle
import ru.aleshin.features.home.impl.presentation.theme.HomeThemeRes

/**
 * @author Stanislav Aleshin on 08.04.2023.
 */
@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun CategoriesTopAppBar(
    modifier: Modifier = Modifier,
    onMenuIconClick: () -> Unit,
) {
    TopAppBar(
        modifier = modifier.background(MaterialTheme.colorScheme.background),
        title = {
            TopAppBarTitle(
                text = HomeThemeRes.strings.topAppBarCategoriesTitle,
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
internal fun CategoriesTopAppBar_Light_Preview() {
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
                    CategoriesTopAppBar(onMenuIconClick = {})
                },
            )
        }
    }
}

@Preview
@Composable
internal fun CategoriesTopAppBar_Dark_Preview() {
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
                    CategoriesTopAppBar(onMenuIconClick = {})
                },
            )
        }
    }
}
*/
