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
package ru.aleshin.features.editor.impl.presentation.ui.goal.views.sections

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import ru.aleshin.core.domain.entities.goals.GoalDirection
import ru.aleshin.core.domain.entities.goals.GoalMetric
import ru.aleshin.features.editor.impl.presentation.models.goals.GoalEditUi
import ru.aleshin.features.editor.impl.presentation.models.goals.GoalSegmentedItemUi
import ru.aleshin.features.editor.impl.presentation.theme.EditorThemeRes
import ru.aleshin.features.editor.impl.presentation.ui.goal.validators.GoalValidationError
import ru.aleshin.features.editor.impl.presentation.ui.task.views.SegmentedParametersChooser
import ru.aleshin.timeplanner.core.ui.views.CustomLargeTextField

/**
 * @author Stanislav Aleshin on 28.07.2026.
 */
@Composable
internal fun GoalMetricSection(
    modifier: Modifier = Modifier,
    goal: GoalEditUi,
    errors: Set<GoalValidationError>,
    onMetricChange: (GoalMetric) -> Unit,
    onDirectionChange: (GoalDirection) -> Unit,
    onTargetValueChange: (String) -> Unit,
) {
    val metricItems = arrayOf(
        GoalSegmentedItemUi(
            value = GoalMetric.DURATION,
            text = EditorThemeRes.strings.goalDurationMetricTitle,
        ),
        GoalSegmentedItemUi(
            value = GoalMetric.TASK_COUNT,
            text = EditorThemeRes.strings.goalTaskCountMetricTitle,
        ),
    )
    val directionItems = arrayOf(
        GoalSegmentedItemUi(
            value = GoalDirection.AT_LEAST,
            text = EditorThemeRes.strings.goalAtLeastTitle,
        ),
        GoalSegmentedItemUi(
            value = GoalDirection.AT_MOST,
            text = EditorThemeRes.strings.goalAtMostTitle,
        ),
    )
    val isTargetError = GoalValidationError.TARGET in errors
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        SegmentedParametersChooser(
            parameters = metricItems,
            selected = metricItems.first { item -> item.value == goal.metric },
            title = EditorThemeRes.strings.goalMetricTitle,
            onChangeSelected = { item -> onMetricChange(item.value) },
        )
        SegmentedParametersChooser(
            parameters = directionItems,
            selected = directionItems.first { item -> item.value == goal.direction },
            title = EditorThemeRes.strings.goalDirectionTitle,
            onChangeSelected = { item -> onDirectionChange(item.value) },
        )
        CustomLargeTextField(
            modifier = Modifier.fillMaxWidth(),
            text = goal.targetValue,
            onTextChange = onTargetValueChange,
            label = {
                Text(
                    if (goal.metric == GoalMetric.DURATION) {
                        EditorThemeRes.strings.goalTargetMinutesLabel
                    } else {
                        EditorThemeRes.strings.goalTargetTasksLabel
                    }
                )
            },
            leadingIcon = {
                Icon(
                    painter = painterResource(EditorThemeRes.icons.statistics),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = isTargetError,
            singleLine = true,
        )
        AnimatedVisibility(visible = isTargetError) {
            Text(
                text = EditorThemeRes.strings.goalTargetError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelSmall,
            )
        }
    }
}
