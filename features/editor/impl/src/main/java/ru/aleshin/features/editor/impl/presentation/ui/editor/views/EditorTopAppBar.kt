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
package ru.aleshin.features.editor.impl.presentation.ui.editor.views

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextAlign
import ru.aleshin.core.ui.views.*
import ru.aleshin.features.editor.impl.presentation.theme.EditorThemeRes

/**
 * @author Stanislav Aleshin on 25.02.2023.
 */
@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun EditorTopAppBar(
    onBackIconClick: () -> Unit,
    onDeleteActionClick: () -> Unit,
    onTemplatesActionClick: () -> Unit,
) {
    TopAppBar(
        title = {
            TopAppBarTitle(
                text = EditorThemeRes.strings.topAppBarEditorTitle,
                textAlign = TextAlign.Center,
            )
        },
        navigationIcon = {
            TopAppBarButton(
                imageVector = Icons.Default.ArrowBack,
                imageDescription = EditorThemeRes.strings.topAppBarBackIconDesc,
                onButtonClick = onBackIconClick,
            )
        },
        actions = {
            TopAppBarMoreActions(
                items = EditorTopAppBarActions.values(),
                onItemClick = {
                    when (it) {
                        EditorTopAppBarActions.TEMPLATES -> onTemplatesActionClick.invoke()
                        EditorTopAppBarActions.DELETE -> onDeleteActionClick.invoke()
                    }
                },
                moreIconDescription = EditorThemeRes.strings.topAppBarMoreIconDesc,
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
        ),
    )
}

internal enum class EditorTopAppBarActions : TopAppBarAction {
    TEMPLATES {
        override val title: String @Composable get() = EditorThemeRes.strings.topAppBarTemplatesTitle
        override val icon: Int? @Composable get() = null
        override val isAlwaysShow: Boolean = false
    },
    DELETE {
        override val title: String @Composable get() = EditorThemeRes.strings.topAppBarDeleteTitle
        override val icon: Int? @Composable get() = null
        override val isAlwaysShow: Boolean = false
    },
}

/* ----------------------- Release Preview -----------------------
@Preview
@Composable
internal fun EditorTopAppBar_Light_Preview() {
    TimePlannerTheme(
        dynamicColor = false,
        themeColorsType = ThemeColorsUiType.LIGHT,
        language = LanguageUiType.RU,
    ) {
        EditorTheme {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                content = { Box(modifier = Modifier.padding(it)) {} },
                topBar = {
                    EditorTopAppBar(
                        onBackIconClick = {},
                        onTemplatesActionClick = {},
                        onDeleteActionClick = {},
                    )
                },
            )
        }
    }
}

@Preview
@Composable
internal fun EditorTopAppBar_Dark_Preview() {
    TimePlannerTheme(
        dynamicColor = false,
        themeColorsType = ThemeColorsUiType.DARK,
        language = LanguageUiType.RU,
    ) {
        EditorTheme {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                content = { Box(modifier = Modifier.padding(it)) {} },
                topBar = {
                    EditorTopAppBar(
                        onBackIconClick = {},
                        onTemplatesActionClick = {},
                        onDeleteActionClick = {},
                    )
                },
            )
        }
    }
}
*/
