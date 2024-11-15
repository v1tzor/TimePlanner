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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ru.aleshin.core.ui.views.filterChipSurfaceVariantColors
import ru.aleshin.core.utils.functional.Constants
import ru.aleshin.features.settings.impl.BuildConfig
import ru.aleshin.features.settings.impl.presentation.theme.SettingsThemeRes

/**
 * @author Stanislav Aleshin on 14.09.2023.
 */
@Composable
internal fun AboutAppSection(
    modifier: Modifier = Modifier,
    onOpenGit: () -> Unit,
    onOpenIssues: () -> Unit,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = SettingsThemeRes.strings.aboutAppHeader,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelMedium,
        )
        AboutAppSectionVersion()
        AboutAppSectionDevelopment(
            onOpenGit = onOpenGit,
            onOpenIssues = onOpenIssues,
        )
    }
}

@Composable
internal fun AboutAppSectionVersion(
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceContainer,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            InfoView(
                title = SettingsThemeRes.strings.versionNameTitle,
                text = BuildConfig.VERSION_NAME,
            )
            Spacer(modifier = Modifier.weight(1f))
            InfoView(
                title = SettingsThemeRes.strings.versionCodeTitle,
                text = BuildConfig.VERSION_CODE,
            )
        }
    }
}

@Composable
internal fun AboutAppSectionDevelopment(
    modifier: Modifier = Modifier,
    onOpenGit: () -> Unit,
    onOpenIssues: () -> Unit,
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceContainer,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                InfoView(
                    modifier = Modifier.fillMaxWidth(),
                    title = SettingsThemeRes.strings.developerTitle,
                    spaceInside = true,
                    text = Constants.App.DEVELOPER,
                )
                InfoView(
                    modifier = Modifier.fillMaxWidth(),
                    title = SettingsThemeRes.strings.licenseTitle,
                    spaceInside = true,
                    text = Constants.App.LICENCE,
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    modifier = Modifier.weight(1f),
                    selected = true,
                    onClick = onOpenIssues,
                    label = {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = SettingsThemeRes.strings.askQuestionTitle,
                            textAlign = TextAlign.Center,
                        )
                    },
                    colors = FilterChipDefaults.filterChipSurfaceVariantColors(),
                )
                FilterChip(
                    modifier = Modifier.weight(1f),
                    selected = true,
                    onClick = onOpenGit,
                    label = {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = SettingsThemeRes.strings.githubTitle,
                            textAlign = TextAlign.Center,
                        )
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(SettingsThemeRes.icons.git),
                            contentDescription = null,
                        )
                    },
                    colors = FilterChipDefaults.filterChipSurfaceVariantColors(),
                )
            }
        }
    }
}

@Composable
internal fun InfoView(
    modifier: Modifier = Modifier,
    spaceInside: Boolean = false,
    title: String,
    text: String,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            style = MaterialTheme.typography.titleMedium,
        )
        if (spaceInside) Spacer(modifier = Modifier.weight(1f))
        Text(
            text = text,
            color = MaterialTheme.colorScheme.primary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.titleMedium,
        )
    }
}
