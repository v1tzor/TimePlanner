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
package ru.aleshin.features.analytics.impl.presentation.ui.analytics.views

import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import ru.aleshin.features.analytics.impl.presentation.theme.AnalyticsThemeRes

/**
 * @author Stanislav Aleshin on 30.03.2023.
 */
@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun AnalyticsTopAppBar(
    modifier: Modifier = Modifier,
    isCompact: Boolean,
) {
    if (isCompact) {
        CenterAlignedTopAppBar(
            modifier = modifier,
            title = {
                AnalyticsTopAppBarTitle()
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background,
            ),
        )
    } else {
        TopAppBar(
            modifier = modifier,
            title = {
                AnalyticsTopAppBarTitle()
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background,
            ),
        )
    }
}

@Composable
private fun AnalyticsTopAppBarTitle(
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier,
        text = AnalyticsThemeRes.strings.topAppBarTitle,
        color = MaterialTheme.colorScheme.onSurface,
        fontWeight = FontWeight.Bold,
        style = MaterialTheme.typography.titleLarge,
    )
}
