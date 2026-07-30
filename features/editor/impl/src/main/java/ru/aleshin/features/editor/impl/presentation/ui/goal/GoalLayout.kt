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
package ru.aleshin.features.editor.impl.presentation.ui.goal

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ru.aleshin.features.editor.impl.presentation.theme.tokens.EditorLayoutDefaults
import ru.aleshin.features.editor.impl.presentation.ui.goal.contract.GoalEvent
import ru.aleshin.features.editor.impl.presentation.ui.goal.contract.GoalState
import ru.aleshin.features.editor.impl.presentation.ui.goal.views.sections.GoalActionButtonsSection
import ru.aleshin.features.editor.impl.presentation.ui.goal.views.sections.GoalDeadlineSection
import ru.aleshin.features.editor.impl.presentation.ui.goal.views.sections.GoalMetricSection
import ru.aleshin.features.editor.impl.presentation.ui.goal.views.sections.GoalNameSection
import ru.aleshin.features.editor.impl.presentation.ui.goal.views.sections.GoalScopeSection
import ru.aleshin.timeplanner.core.ui.views.AdaptiveLayoutInfo

/**
 * @author Stanislav Aleshin on 28.07.2026.
 */
@Composable
internal fun GoalLayout(
    modifier: Modifier = Modifier,
    state: GoalState,
    adaptiveLayoutInfo: AdaptiveLayoutInfo,
    onEvent: (GoalEvent) -> Unit,
) {
    val mainScrollState = rememberScrollState()
    val supportingScrollState = rememberScrollState()

    when {
        adaptiveLayoutInfo.isBookPosture || adaptiveLayoutInfo.isTabletopPosture -> GoalSupportingPaneLayout(
            modifier = modifier,
            state = state,
            adaptiveLayoutInfo = adaptiveLayoutInfo,
            mainScrollState = mainScrollState,
            supportingScrollState = supportingScrollState,
            onEvent = onEvent,
        )
        adaptiveLayoutInfo.useExpandedLayout -> GoalExpandedLayout(
            modifier = modifier,
            state = state,
            mainScrollState = mainScrollState,
            supportingScrollState = supportingScrollState,
            onEvent = onEvent,
        )
        else -> GoalSinglePaneLayout(
            modifier = modifier,
            state = state,
            scrollState = mainScrollState,
            maxContentWidth = if (adaptiveLayoutInfo.isMediumWidth) {
                EditorLayoutDefaults.MediumContentMaxWidth
            } else {
                null
            },
            onEvent = onEvent,
        )
    }
}

@Composable
private fun GoalSinglePaneLayout(
    modifier: Modifier = Modifier,
    state: GoalState,
    scrollState: ScrollState,
    maxContentWidth: Dp?,
    onEvent: (GoalEvent) -> Unit,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter,
    ) {
        val editModel = state.editModel
        if (editModel != null) {
            val contentModifier = if (maxContentWidth != null) {
                Modifier
                    .widthIn(max = maxContentWidth)
                    .fillMaxSize()
            } else {
                Modifier.fillMaxSize()
            }
            Column(modifier = contentModifier) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(scrollState)
                        .padding(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    GoalNameSection(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        goal = editModel,
                        errors = state.validationErrors,
                        onTitleChange = { title ->
                            onEvent(GoalEvent.ChangeTitle(title))
                        },
                    )
                    GoalScopeSection(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        goal = editModel,
                        categories = state.categories,
                        errors = state.validationErrors,
                        onScopeChange = { scope ->
                            onEvent(GoalEvent.ChangeScope(scope))
                        },
                        onMainCategoryChange = { category ->
                            onEvent(GoalEvent.ChangeMainCategory(category))
                        },
                        onSubCategoryChange = { subCategory ->
                            onEvent(GoalEvent.ChangeSubCategory(subCategory))
                        },
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 32.dp),
                    )
                    GoalMetricSection(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        goal = editModel,
                        errors = state.validationErrors,
                        onMetricChange = { metric ->
                            onEvent(GoalEvent.ChangeMetric(metric))
                        },
                        onDirectionChange = { direction ->
                            onEvent(GoalEvent.ChangeDirection(direction))
                        },
                        onTargetValueChange = { target ->
                            onEvent(GoalEvent.ChangeTargetValue(target))
                        },
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 32.dp),
                    )
                    GoalDeadlineSection(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        goal = editModel,
                        errors = state.validationErrors,
                        onDeadlineChange = { deadline ->
                            onEvent(GoalEvent.ChangeDeadline(deadline))
                        },
                    )
                }
                GoalActionButtonsSection(
                    onCancelClick = { onEvent(GoalEvent.PressBack) },
                    onSaveClick = { onEvent(GoalEvent.PressSave) },
                )
            }
        } else {
            Box(modifier = Modifier.fillMaxSize())
        }
    }
}

@Composable
private fun GoalExpandedLayout(
    modifier: Modifier = Modifier,
    state: GoalState,
    mainScrollState: ScrollState,
    supportingScrollState: ScrollState,
    onEvent: (GoalEvent) -> Unit,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter,
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = EditorLayoutDefaults.ExpandedContentMaxWidth)
                .padding(horizontal = 32.dp),
            horizontalArrangement = Arrangement.spacedBy(EditorLayoutDefaults.PaneSpacing),
        ) {
            GoalFormPane(
                modifier = Modifier
                    .weight(1.2f)
                    .fillMaxSize(),
                state = state,
                scrollState = mainScrollState,
                onEvent = onEvent,
            )
            GoalParametersPane(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(),
                state = state,
                scrollState = supportingScrollState,
                onEvent = onEvent,
            )
        }
    }
}
