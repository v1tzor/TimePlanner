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
package ru.aleshin.features.overview.impl.presentation.ui.overview.views

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import ru.aleshin.features.overview.impl.presentation.theme.OverviewThemeRes
import ru.aleshin.timeplanner.core.ui.views.TopAppBarTitle

/**
 * @author Stanislav Aleshin on 30.07.2026.
 */
@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun OverviewTopAppBar(
    modifier: Modifier = Modifier,
    isCompact: Boolean,
) {
    if (isCompact) {
        CenterAlignedTopAppBar(
            modifier = modifier,
            title = {
                TopAppBarTitle(
                    text = OverviewThemeRes.strings.topAppBarOverviewTitle,
                    textAlign = TextAlign.Center
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background,
            ),
        )
    } else {
        Spacer(modifier = modifier.statusBarsPadding())
    }
}
