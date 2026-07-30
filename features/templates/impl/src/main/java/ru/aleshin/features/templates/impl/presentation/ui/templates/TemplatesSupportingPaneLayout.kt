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
package ru.aleshin.features.templates.impl.presentation.ui.templates

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.aleshin.features.templates.impl.presentation.theme.TemplatesThemeRes
import ru.aleshin.features.templates.impl.presentation.theme.tokens.TemplatesLayoutDefaults
import ru.aleshin.features.templates.impl.presentation.ui.templates.contract.TemplatesEvent
import ru.aleshin.features.templates.impl.presentation.ui.templates.contract.TemplatesState
import ru.aleshin.features.templates.impl.presentation.ui.templates.views.TemplatesGridPlaceholder
import ru.aleshin.features.templates.impl.presentation.ui.templates.views.sections.TemplatesMonthlyPatternSection
import ru.aleshin.features.templates.impl.presentation.ui.templates.views.sections.TemplatesMonthlyPatternSectionPlaceholder
import ru.aleshin.timeplanner.core.ui.views.AdaptiveLayoutInfo
import ru.aleshin.timeplanner.core.ui.views.AdaptiveSupportingPaneScaffold
import ru.aleshin.timeplanner.core.ui.views.animations.AnimatedLoadingContent

/**
 * @author Stanislav Aleshin on 30.07.2026.
 */
@Composable
internal fun TemplatesSupportingPaneLayout(
    modifier: Modifier = Modifier,
    state: TemplatesState,
    adaptiveLayoutInfo: AdaptiveLayoutInfo,
    onCreateTemplate: () -> Unit,
    onEvent: (TemplatesEvent) -> Unit,
) {
    val showPaneExpansionDragHandle = !adaptiveLayoutInfo.isBookPosture && !adaptiveLayoutInfo.isTabletopPosture

    AdaptiveSupportingPaneScaffold(
        modifier = modifier,
        adaptiveLayoutInfo = adaptiveLayoutInfo,
        mainPaneMinWidth = TemplatesLayoutDefaults.MainPaneMinWidth,
        supportingPaneMinWidth = TemplatesLayoutDefaults.SupportingPaneMinWidth,
        supportingPaneMaxWidth = TemplatesLayoutDefaults.SupportingPaneMaxWidth,
        supportingPanePreferredWidth = TemplatesLayoutDefaults.SupportingPanePreferredWidth,
        useTwoPanesOnMediumWidth = true,
        showPaneExpansionDragHandle = showPaneExpansionDragHandle,
        mainPane = {
            TemplatesMainPane(
                modifier = Modifier.fillMaxSize(),
                state = state,
                onCreateTemplate = onCreateTemplate,
                onEvent = onEvent,
            )
        },
        supportingPane = {
            TemplatesPatternPane(
                modifier = Modifier.fillMaxSize(),
                state = state,
                onEvent = onEvent,
            )
        },
    )
}

@Composable
private fun TemplatesMainPane(
    modifier: Modifier = Modifier,
    state: TemplatesState,
    onCreateTemplate: () -> Unit,
    onEvent: (TemplatesEvent) -> Unit,
) {
    val templatesData = state.templatesData

    Box(modifier = modifier) {
        AnimatedLoadingContent(
            modifier = Modifier.fillMaxSize(),
            isLoading = templatesData == null,
            targetValue = templatesData,
            label = "TemplatesMainPane",
        ) { data ->
            if (data != null) {
                TemplatesGrid(
                    modifier = Modifier.fillMaxSize(),
                    state = state,
                    templatesData = data,
                    columns = GridCells.Adaptive(TemplatesLayoutDefaults.TemplateCardMinWidth),
                    contentPadding = PaddingValues(start = 16.dp, top = 12.dp, end = 16.dp, bottom = 88.dp),
                    horizontalSpacing = 16.dp,
                    showPattern = false,
                    onEvent = onEvent,
                )
            } else {
                TemplatesGridPlaceholder(
                    modifier = Modifier.fillMaxSize(),
                    columns = GridCells.Adaptive(TemplatesLayoutDefaults.TemplateCardMinWidth),
                    contentPadding = PaddingValues(start = 16.dp, top = 12.dp, end = 16.dp, bottom = 88.dp),
                    horizontalSpacing = 16.dp,
                    showPattern = false,
                )
            }
        }
        FloatingActionButton(
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
            onClick = onCreateTemplate,
        ) {
            Text(
                text = TemplatesThemeRes.strings.addTemplatesFabTitle,
                style = MaterialTheme.typography.titleLarge,
            )
        }
    }
}

@Composable
private fun TemplatesPatternPane(
    modifier: Modifier = Modifier,
    state: TemplatesState,
    onEvent: (TemplatesEvent) -> Unit,
) {
    val templatesData = state.templatesData

    Column(
        modifier = modifier
            .windowInsetsPadding(
                insets = WindowInsets.safeDrawing.only(WindowInsetsSides.End + WindowInsetsSides.Bottom),
            )
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
    ) {
        AnimatedLoadingContent(
            modifier = Modifier.fillMaxWidth(),
            isLoading = templatesData == null,
            targetValue = templatesData,
            label = "TemplatesPatternPane",
        ) { data ->
            if (data != null) {
                TemplatesMonthlyPatternSection(
                    patternFilter = state.patternFilter,
                    monthPattern = data.monthPattern,
                    onChangePatternFilter = { filter ->
                        onEvent(TemplatesEvent.UpdatedPatternFilter(filter))
                    },
                )
            } else {
                TemplatesMonthlyPatternSectionPlaceholder()
            }
        }
    }
}
