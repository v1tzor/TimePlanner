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
package ru.aleshin.features.home.impl.presentation.ui.home.views

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import ru.aleshin.core.domain.entities.settings.HomeViewMode
import ru.aleshin.features.home.impl.presentation.theme.HomeThemeRes

/**
 * @author Stanislav Aleshin on 17.07.2026.
 */
@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun HomeViewTabs(
    modifier: Modifier = Modifier,
    selectedMode: HomeViewMode,
    onModeChange: (HomeViewMode) -> Unit,
) {
    val strings = HomeThemeRes.strings
    val icons = HomeThemeRes.icons

    PrimaryTabRow(
        modifier = modifier,
        selectedTabIndex = selectedMode.ordinal,
        containerColor = MaterialTheme.colorScheme.background,
    ) {
        HomeViewMode.entries.forEach { mode ->
            val title = when (mode) {
                HomeViewMode.AGENDA -> strings.agendaTabTitle
                HomeViewMode.TIMELINE -> strings.timelineTabTitle
            }
            val icon = when (mode) {
                HomeViewMode.AGENDA -> icons.agenda
                HomeViewMode.TIMELINE -> icons.timeline
            }

            Tab(
                selected = selectedMode == mode,
                onClick = { onModeChange(mode) },
                text = { Text(text = title) },
                icon = { Icon(painter = painterResource(icon), contentDescription = title) },
                selectedContentColor = MaterialTheme.colorScheme.primary,
                unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
