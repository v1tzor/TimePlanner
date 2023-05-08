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
package ru.aleshin.features.home.impl.presentation.ui.templates.views

import androidx.compose.foundation.background
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
import ru.aleshin.features.home.impl.presentation.theme.HomeThemeRes

/**
 * @author Stanislav Aleshin on 08.05.2023.
 */
@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun TemplatesTopAppBar(
    modifier: Modifier = Modifier,
    onMenuIconClick: () -> Unit,
) {
    TopAppBar(
        modifier = modifier.background(MaterialTheme.colorScheme.background),
        title = {
            TopAppBarTitle(
                text = HomeThemeRes.strings.topAppBarTemplatesTitle,
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
