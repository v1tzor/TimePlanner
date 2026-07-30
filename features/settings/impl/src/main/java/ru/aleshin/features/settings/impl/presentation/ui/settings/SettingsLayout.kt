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
package ru.aleshin.features.settings.impl.presentation.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ru.aleshin.core.utils.extensions.openNetworkUri
import ru.aleshin.core.utils.functional.Constants
import ru.aleshin.features.settings.impl.presentation.theme.tokens.SettingsLayoutDefaults
import ru.aleshin.features.settings.impl.presentation.ui.settings.contract.SettingsEvent
import ru.aleshin.features.settings.impl.presentation.ui.settings.contract.SettingsState
import ru.aleshin.features.settings.impl.presentation.ui.settings.views.sections.AboutAppSection
import ru.aleshin.features.settings.impl.presentation.ui.settings.views.sections.AppearanceSection
import ru.aleshin.features.settings.impl.presentation.ui.settings.views.sections.DataSection
import ru.aleshin.features.settings.impl.presentation.ui.settings.views.sections.DonateButton
import ru.aleshin.features.settings.impl.presentation.ui.settings.views.sections.InterfaceSection
import ru.aleshin.features.settings.impl.presentation.ui.settings.views.sections.SecureSection
import ru.aleshin.timeplanner.core.ui.theme.tokens.AdaptiveLayoutDefaults
import ru.aleshin.timeplanner.core.ui.views.AdaptiveLayoutInfo
import ru.aleshin.timeplanner.core.ui.views.AdaptiveSupportingPaneScaffold

/**
 * @author Stanislav Aleshin on 29.07.2026.
 */
@Composable
internal fun SettingsLayout(
    modifier: Modifier = Modifier,
    state: SettingsState,
    adaptiveLayoutInfo: AdaptiveLayoutInfo,
    onEvent: (SettingsEvent) -> Unit,
) {
    when {
        adaptiveLayoutInfo.isBookPosture -> SettingsBookLayout(
            modifier = modifier,
            state = state,
            adaptiveLayoutInfo = adaptiveLayoutInfo,
            onEvent = onEvent,
        )
        adaptiveLayoutInfo.useExpandedLayout -> SettingsExpandedLayout(
            modifier = modifier,
            state = state,
            onEvent = onEvent,
        )
        else -> SettingsSinglePaneLayout(
            modifier = modifier,
            state = state,
            maxContentWidth = if (adaptiveLayoutInfo.isMediumWidth) {
                AdaptiveLayoutDefaults.SettingsContentMaxWidth
            } else {
                SettingsLayoutDefaults.CompactContentMaxWidth
            },
            onEvent = onEvent,
        )
    }
}

@Composable
private fun SettingsSinglePaneLayout(
    modifier: Modifier = Modifier,
    state: SettingsState,
    maxContentWidth: Dp,
    onEvent: (SettingsEvent) -> Unit,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter,
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = maxContentWidth)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = AdaptiveLayoutDefaults.CompactHorizontalPadding),
        ) {
            SettingsPrimarySections(
                state = state,
                onEvent = onEvent,
            )
            Spacer(modifier = Modifier.height(24.dp))
            SettingsSecondarySections(
                state = state,
                onEvent = onEvent,
            )
            Spacer(modifier = Modifier.height(SettingsLayoutDefaults.ContentBottomPadding))
        }
    }
}

@Composable
private fun SettingsExpandedLayout(
    modifier: Modifier = Modifier,
    state: SettingsState,
    onEvent: (SettingsEvent) -> Unit,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter,
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = AdaptiveLayoutDefaults.ExpandedContentMaxWidth)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = AdaptiveLayoutDefaults.ExpandedHorizontalPadding),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(AdaptiveLayoutDefaults.PaneSpacing),
            ) {
                SettingsPrimarySections(
                    modifier = Modifier.weight(1f),
                    state = state,
                    onEvent = onEvent,
                )
                SettingsSecondarySections(
                    modifier = Modifier.weight(1f),
                    state = state,
                    onEvent = onEvent,
                )
            }
            Spacer(modifier = Modifier.height(SettingsLayoutDefaults.ContentBottomPadding))
        }
    }
}

@Composable
private fun SettingsBookLayout(
    modifier: Modifier = Modifier,
    state: SettingsState,
    adaptiveLayoutInfo: AdaptiveLayoutInfo,
    onEvent: (SettingsEvent) -> Unit,
) {
    AdaptiveSupportingPaneScaffold(
        modifier = modifier.fillMaxSize(),
        adaptiveLayoutInfo = adaptiveLayoutInfo,
        mainPane = {
            SettingsBookPane {
                SettingsPrimarySections(
                    state = state,
                    onEvent = onEvent,
                )
            }
        },
        supportingPane = {
            SettingsBookPane {
                SettingsSecondarySections(
                    state = state,
                    onEvent = onEvent,
                )
            }
        },
    )
}

@Composable
private fun SettingsBookPane(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = AdaptiveLayoutDefaults.CompactHorizontalPadding),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        content()
        Spacer(modifier = Modifier.height(SettingsLayoutDefaults.ContentBottomPadding))
    }
}

@Composable
private fun SettingsPrimarySections(
    modifier: Modifier = Modifier,
    state: SettingsState,
    onEvent: (SettingsEvent) -> Unit,
) {
    val themeSettings = state.themeSettings ?: return
    val tasksSettings = state.tasksSettings ?: return

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(AdaptiveLayoutDefaults.SpaceExtraLarge),
    ) {
        AppearanceSection(
            modifier = Modifier.padding(top = AdaptiveLayoutDefaults.CompactHorizontalPadding),
            languageType = themeSettings.language,
            themeColors = themeSettings.themeColors,
            colorsType = themeSettings.colorsType,
            dynamicColor = themeSettings.isDynamicColorEnable,
            onThemeColorUpdate = { themeColors ->
                onEvent(SettingsEvent.ChangedThemeSettings(themeSettings.copy(themeColors = themeColors)))
            },
            onLanguageChange = { language ->
                onEvent(SettingsEvent.ChangedThemeSettings(themeSettings.copy(language = language)))
            },
            onColorsTypeUpdate = { colorsType ->
                onEvent(SettingsEvent.ChangedThemeSettings(themeSettings.copy(colorsType = colorsType)))
            },
            onDynamicColorsChange = { isEnabled ->
                onEvent(SettingsEvent.ChangedThemeSettings(themeSettings.copy(isDynamicColorEnable = isEnabled)))
            },
        )
        InterfaceSection(
            calendarButtonBehavior = tasksSettings.calendarButtonBehavior,
            onUpdateCalendarBehavior = { behavior ->
                onEvent(SettingsEvent.ChangedTasksSettings(tasksSettings.copy(calendarButtonBehavior = behavior)))
            },
        )
    }
}

@Composable
private fun SettingsSecondarySections(
    modifier: Modifier = Modifier,
    state: SettingsState,
    onEvent: (SettingsEvent) -> Unit,
) {
    val tasksSettings = state.tasksSettings ?: return
    val context = LocalContext.current

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(AdaptiveLayoutDefaults.SpaceExtraLarge),
    ) {
        SecureSection(
            secureMode = tasksSettings.secureMode,
            onUpdateSecureMode = { secureMode ->
                onEvent(SettingsEvent.ChangedTasksSettings(tasksSettings.copy(secureMode = secureMode)))
            },
        )
        DataSection(
            isLoading = state.isBackupLoading,
            onClear = { onEvent(SettingsEvent.PressClearDataButton) },
            onBackupData = { uri -> onEvent(SettingsEvent.PressSaveBackupData(uri)) },
            onRestoreData = { uri -> onEvent(SettingsEvent.PressRestoreBackupData(uri)) },
        )
        AboutAppSection(
            onOpenGit = { context.openNetworkUri(Constants.App.GITHUB_URI) },
            onOpenIssues = { context.openNetworkUri(Constants.App.ISSUES_URI) },
        )
        DonateButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = { onEvent(SettingsEvent.PressDonateButton) },
        )
    }
}
