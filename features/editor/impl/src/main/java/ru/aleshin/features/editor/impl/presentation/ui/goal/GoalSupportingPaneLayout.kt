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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.aleshin.features.editor.impl.presentation.ui.goal.contract.GoalEvent
import ru.aleshin.features.editor.impl.presentation.ui.goal.contract.GoalState
import ru.aleshin.features.editor.impl.presentation.ui.goal.views.sections.GoalActionButtonsSection
import ru.aleshin.features.editor.impl.presentation.ui.goal.views.sections.GoalDeadlineSection
import ru.aleshin.features.editor.impl.presentation.ui.goal.views.sections.GoalMetricSection
import ru.aleshin.features.editor.impl.presentation.ui.goal.views.sections.GoalNameSection
import ru.aleshin.features.editor.impl.presentation.ui.goal.views.sections.GoalScopeSection
import ru.aleshin.timeplanner.core.ui.views.AdaptiveLayoutInfo
import ru.aleshin.timeplanner.core.ui.views.AdaptiveSupportingPaneScaffold

/**
 * @author Stanislav Aleshin on 30.07.2026.
 */
@Composable
internal fun GoalSupportingPaneLayout(
    modifier: Modifier = Modifier,
    state: GoalState,
    adaptiveLayoutInfo: AdaptiveLayoutInfo,
    mainScrollState: ScrollState,
    supportingScrollState: ScrollState,
    onEvent: (GoalEvent) -> Unit,
) {
    AdaptiveSupportingPaneScaffold(
        modifier = modifier.fillMaxSize(),
        adaptiveLayoutInfo = adaptiveLayoutInfo,
        mainPane = {
            GoalFormPane(
                modifier = Modifier.fillMaxSize(),
                state = state,
                scrollState = mainScrollState,
                onEvent = onEvent,
            )
        },
        supportingPane = {
            GoalParametersPane(
                modifier = Modifier.fillMaxSize(),
                state = state,
                scrollState = supportingScrollState,
                onEvent = onEvent,
            )
        },
    )
}

@Composable
internal fun GoalFormPane(
    modifier: Modifier = Modifier,
    state: GoalState,
    scrollState: ScrollState,
    onEvent: (GoalEvent) -> Unit,
) {
    val editModel = state.editModel
    if (editModel != null) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            GoalNameSection(
                goal = editModel,
                errors = state.validationErrors,
                onTitleChange = { title ->
                    onEvent(GoalEvent.ChangeTitle(title))
                },
            )
            GoalScopeSection(
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
        }
    } else {
        Box(modifier = modifier.fillMaxSize())
    }
}

@Composable
internal fun GoalParametersPane(
    modifier: Modifier = Modifier,
    state: GoalState,
    scrollState: ScrollState,
    onEvent: (GoalEvent) -> Unit,
) {
    val editModel = state.editModel
    if (editModel != null) {
        Column(modifier = modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .padding(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                GoalMetricSection(
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
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
                GoalDeadlineSection(
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
        Box(modifier = modifier.fillMaxSize())
    }
}
