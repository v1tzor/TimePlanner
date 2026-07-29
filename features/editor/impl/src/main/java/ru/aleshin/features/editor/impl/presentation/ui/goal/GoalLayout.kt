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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.aleshin.features.editor.impl.presentation.ui.goal.contract.GoalEvent
import ru.aleshin.features.editor.impl.presentation.ui.goal.contract.GoalState
import ru.aleshin.features.editor.impl.presentation.ui.goal.views.GoalMetricSection
import ru.aleshin.features.editor.impl.presentation.ui.goal.views.GoalNameSection
import ru.aleshin.features.editor.impl.presentation.ui.goal.views.GoalDeadlineSection
import ru.aleshin.features.editor.impl.presentation.ui.goal.views.GoalScopeSection
import ru.aleshin.features.editor.impl.presentation.ui.task.ActionButtonsSection
import ru.aleshin.timeplanner.core.ui.theme.tokens.AdaptiveLayoutDefaults
import ru.aleshin.timeplanner.core.ui.views.AdaptiveLayoutInfo
import ru.aleshin.timeplanner.core.ui.views.PlaceholderBox

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
    when {
        adaptiveLayoutInfo.isCompactWidth -> GoalCompactLayout(
            modifier = modifier,
            state = state,
            onEvent = onEvent,
        )
        adaptiveLayoutInfo.isMediumWidth -> GoalMediumLayout(
            modifier = modifier,
            state = state,
            onEvent = onEvent,
        )
        else -> GoalExpandedLayout(
            modifier = modifier,
            state = state,
            onEvent = onEvent,
        )
    }
}

@Composable
private fun GoalCompactLayout(
    modifier: Modifier,
    state: GoalState,
    onEvent: (GoalEvent) -> Unit,
) {
    GoalEditorList(
        modifier = modifier,
        state = state,
        onEvent = onEvent,
    )
}

@Composable
private fun GoalMediumLayout(
    modifier: Modifier,
    state: GoalState,
    onEvent: (GoalEvent) -> Unit,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter,
    ) {
        GoalEditorList(
            modifier = Modifier
                .widthIn(max = AdaptiveLayoutDefaults.MediumContentMaxWidth)
                .fillMaxWidth(),
            state = state,
            onEvent = onEvent,
        )
    }
}

@Composable
private fun GoalExpandedLayout(
    modifier: Modifier,
    state: GoalState,
    onEvent: (GoalEvent) -> Unit,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter,
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = AdaptiveLayoutDefaults.EditorContentMaxWidth)
                .padding(horizontal = AdaptiveLayoutDefaults.ExpandedHorizontalPadding),
            horizontalArrangement = Arrangement.spacedBy(AdaptiveLayoutDefaults.PaneSpacing),
        ) {
            GoalIdentityPane(
                modifier = Modifier.weight(1.2f).fillMaxSize(),
                state = state,
                onEvent = onEvent,
            )
            GoalProgressPane(
                modifier = Modifier.weight(1f).fillMaxSize(),
                state = state,
                onEvent = onEvent,
            )
        }
    }
}

@Composable
private fun GoalIdentityPane(
    modifier: Modifier,
    state: GoalState,
    onEvent: (GoalEvent) -> Unit,
) {
    val editModel = state.editModel
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        if (state.isLoading || editModel == null) {
            items(3) {
                PlaceholderBox(
                    modifier = Modifier.fillMaxWidth().height(88.dp),
                    shape = MaterialTheme.shapes.medium,
                )
            }
        } else {
            item(key = "name") {
                GoalNameSection(
                    goal = editModel,
                    errors = state.validationErrors,
                    onTitleChange = { title -> onEvent(GoalEvent.ChangeTitle(title)) },
                )
            }
            item(key = "scope") {
                GoalScopeSection(
                    goal = editModel,
                    categories = state.categories,
                    errors = state.validationErrors,
                    onScopeChange = { scope -> onEvent(GoalEvent.ChangeScope(scope)) },
                    onMainCategoryChange = { category ->
                        onEvent(GoalEvent.ChangeMainCategory(category))
                    },
                    onSubCategoryChange = { subCategory ->
                        onEvent(GoalEvent.ChangeSubCategory(subCategory))
                    },
                )
            }
        }
    }
}

@Composable
private fun GoalProgressPane(
    modifier: Modifier,
    state: GoalState,
    onEvent: (GoalEvent) -> Unit,
) {
    val editModel = state.editModel
    Column(modifier = modifier) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (state.isLoading || editModel == null) {
                items(4) {
                    PlaceholderBox(
                        modifier = Modifier.fillMaxWidth().height(88.dp),
                        shape = MaterialTheme.shapes.medium,
                    )
                }
            } else {
                item(key = "metric") {
                    GoalMetricSection(
                        goal = editModel,
                        errors = state.validationErrors,
                        onMetricChange = { metric -> onEvent(GoalEvent.ChangeMetric(metric)) },
                        onDirectionChange = { direction ->
                            onEvent(GoalEvent.ChangeDirection(direction))
                        },
                        onTargetValueChange = { target ->
                            onEvent(GoalEvent.ChangeTargetValue(target))
                        },
                    )
                }
                item(key = "metricDivider") {
                    HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                }
                item(key = "deadline") {
                    GoalDeadlineSection(
                        goal = editModel,
                        errors = state.validationErrors,
                        onDeadlineChange = { date ->
                            onEvent(GoalEvent.ChangeDeadline(date))
                        },
                    )
                }
            }
        }
        if (editModel != null) {
            ActionButtonsSection(
                isCreateMode = true,
                isTemplate = false,
                onUnlinkTemplate = {},
                onControlTemplate = {},
                onCreateTemplate = {},
                onCancelClick = { onEvent(GoalEvent.PressBack) },
                onSaveClick = { onEvent(GoalEvent.PressSave) },
            )
        }
    }
}

@Composable
private fun GoalEditorList(
    modifier: Modifier,
    state: GoalState,
    onEvent: (GoalEvent) -> Unit,
) {
    val editModel = state.editModel
    Column(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (state.isLoading || editModel == null) {
                items(5) {
                    PlaceholderBox(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(88.dp)
                            .padding(horizontal = 16.dp),
                        shape = MaterialTheme.shapes.medium,
                    )
                }
            } else {
                item(key = "name") {
                    GoalNameSection(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        goal = editModel,
                        errors = state.validationErrors,
                        onTitleChange = { title -> onEvent(GoalEvent.ChangeTitle(title)) },
                    )
                }
                item(key = "scope") {
                    GoalScopeSection(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        goal = editModel,
                        categories = state.categories,
                        errors = state.validationErrors,
                        onScopeChange = { scope -> onEvent(GoalEvent.ChangeScope(scope)) },
                        onMainCategoryChange = { category ->
                            onEvent(GoalEvent.ChangeMainCategory(category))
                        },
                        onSubCategoryChange = { subCategory ->
                            onEvent(GoalEvent.ChangeSubCategory(subCategory))
                        },
                    )
                }
                item(key = "scopeDivider") {
                    HorizontalDivider(Modifier.padding(horizontal = 32.dp))
                }
                item(key = "metric") {
                    GoalMetricSection(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        goal = editModel,
                        errors = state.validationErrors,
                        onMetricChange = { metric -> onEvent(GoalEvent.ChangeMetric(metric)) },
                        onDirectionChange = { direction ->
                            onEvent(GoalEvent.ChangeDirection(direction))
                        },
                        onTargetValueChange = { target ->
                            onEvent(GoalEvent.ChangeTargetValue(target))
                        },
                    )
                }
                item(key = "metricDivider") {
                    HorizontalDivider(Modifier.padding(horizontal = 32.dp))
                }
                item(key = "deadline") {
                    GoalDeadlineSection(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        goal = editModel,
                        errors = state.validationErrors,
                        onDeadlineChange = { date ->
                            onEvent(GoalEvent.ChangeDeadline(date))
                        },
                    )
                }
            }
        }
        if (editModel != null) {
            ActionButtonsSection(
                isCreateMode = true,
                isTemplate = false,
                onUnlinkTemplate = {},
                onControlTemplate = {},
                onCreateTemplate = {},
                onCancelClick = { onEvent(GoalEvent.PressBack) },
                onSaveClick = { onEvent(GoalEvent.PressSave) },
            )
        }
    }
}
