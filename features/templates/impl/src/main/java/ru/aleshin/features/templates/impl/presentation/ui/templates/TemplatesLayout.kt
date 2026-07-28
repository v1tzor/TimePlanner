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

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import ru.aleshin.features.templates.impl.presentation.models.TemplatesDataUi
import ru.aleshin.features.templates.impl.presentation.theme.tokens.TemplatesLayoutDefaults
import ru.aleshin.features.templates.impl.presentation.ui.templates.contract.TemplatesEvent
import ru.aleshin.features.templates.impl.presentation.ui.templates.contract.TemplatesState
import ru.aleshin.features.templates.impl.presentation.ui.templates.views.TemplatesMonthlyPatternPane
import ru.aleshin.features.templates.impl.presentation.ui.templates.views.TemplatesMonthlyPatternPanePlaceholder
import ru.aleshin.timeplanner.core.ui.views.AdaptiveLayoutInfo
import ru.aleshin.timeplanner.core.ui.views.AdaptiveSupportingPaneScaffold

/**
 * @author Stanislav Aleshin on 28.07.2026.
 */
@Composable
internal fun TemplatesLayout(
    modifier: Modifier = Modifier,
    state: TemplatesState,
    adaptiveLayoutInfo: AdaptiveLayoutInfo,
    layoutMode: TemplatesLayoutMode,
    onCreateTemplate: () -> Unit,
    onEvent: (TemplatesEvent) -> Unit,
) {
    val templatesData = state.templatesData

    AnimatedContent(
        modifier = modifier,
        targetState = templatesData == null,
        contentAlignment = Alignment.TopCenter,
        label = "TemplatesContent",
        transitionSpec = {
            fadeIn(
                animationSpec = tween(
                    durationMillis = CONTENT_ENTER_DURATION,
                    delayMillis = CONTENT_ENTER_DELAY,
                ),
            ).togetherWith(
                fadeOut(
                    animationSpec = tween(
                        durationMillis = CONTENT_EXIT_DURATION,
                    ),
                ),
            )
        },
    ) { isLoading ->
        when (layoutMode) {
            TemplatesLayoutMode.COMPACT -> TemplatesCompactLayout(
                modifier = Modifier.fillMaxSize(),
                isLoading = isLoading,
                state = state,
                templatesData = templatesData,
                onEvent = onEvent,
            )
            TemplatesLayoutMode.SUPPORTING -> TemplatesSupportingLayout(
                modifier = Modifier.fillMaxSize(),
                isLoading = isLoading,
                state = state,
                templatesData = templatesData,
                adaptiveLayoutInfo = adaptiveLayoutInfo,
                onCreateTemplate = onCreateTemplate,
                onEvent = onEvent,
            )
        }
    }
}

@Composable
private fun TemplatesCompactLayout(
    modifier: Modifier = Modifier,
    isLoading: Boolean,
    state: TemplatesState,
    templatesData: TemplatesDataUi?,
    onEvent: (TemplatesEvent) -> Unit,
) {
    if (isLoading || templatesData == null) {
        TemplatesCompactGridPlaceholder(
            modifier = modifier,
        )
    } else {
        TemplatesCompactGrid(
            modifier = modifier,
            state = state,
            templatesData = templatesData,
            onEvent = onEvent,
        )
    }
}

@Composable
private fun TemplatesSupportingLayout(
    modifier: Modifier = Modifier,
    isLoading: Boolean,
    state: TemplatesState,
    templatesData: TemplatesDataUi?,
    adaptiveLayoutInfo: AdaptiveLayoutInfo,
    onCreateTemplate: () -> Unit,
    onEvent: (TemplatesEvent) -> Unit,
) {
    val showPaneExpansionDragHandle = !adaptiveLayoutInfo.isBookPosture &&
        !adaptiveLayoutInfo.isTabletopPosture

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
            if (isLoading || templatesData == null) {
                TemplatesMainPanePlaceholder(
                    modifier = Modifier.fillMaxSize(),
                    onCreateTemplate = onCreateTemplate,
                )
            } else {
                TemplatesMainPane(
                    modifier = Modifier.fillMaxSize(),
                    state = state,
                    templatesData = templatesData,
                    onCreateTemplate = onCreateTemplate,
                    onEvent = onEvent,
                )
            }
        },
        supportingPane = {
            if (isLoading || templatesData == null) {
                TemplatesMonthlyPatternPanePlaceholder(
                    modifier = Modifier.fillMaxSize(),
                )
            } else {
                TemplatesMonthlyPatternPane(
                    modifier = Modifier.fillMaxSize(),
                    patternFilter = state.patternFilter,
                    monthPattern = templatesData.monthPattern,
                    onChangePatternFilter = { filter ->
                        onEvent(TemplatesEvent.UpdatedPatternFilter(filter))
                    },
                )
            }
        },
    )
}

private const val CONTENT_ENTER_DURATION = 600
private const val CONTENT_ENTER_DELAY = 90
private const val CONTENT_EXIT_DURATION = 300
