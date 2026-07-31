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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.aleshin.core.domain.entities.goals.GoalScopeType
import ru.aleshin.core.presentation.models.categories.MainCategoryDetailsUi
import ru.aleshin.core.presentation.models.categories.MainCategoryUi
import ru.aleshin.core.presentation.models.categories.SubCategoryUi
import ru.aleshin.features.editor.impl.presentation.models.goals.GoalEditUi
import ru.aleshin.features.editor.impl.presentation.models.goals.GoalSegmentedItemUi
import ru.aleshin.features.editor.impl.presentation.theme.EditorThemeRes
import ru.aleshin.features.editor.impl.presentation.ui.goal.validators.GoalValidationError
import ru.aleshin.features.editor.impl.presentation.ui.task.views.MainCategoryChooser
import ru.aleshin.features.editor.impl.presentation.ui.task.views.SegmentedParametersChooser
import ru.aleshin.features.editor.impl.presentation.ui.task.views.SubCategoryChooser

/**
 * @author Stanislav Aleshin on 28.07.2026.
 */
@Composable
internal fun GoalScopeSection(
    modifier: Modifier = Modifier,
    goal: GoalEditUi,
    categories: List<MainCategoryDetailsUi>,
    errors: Set<GoalValidationError>,
    onScopeChange: (GoalScopeType) -> Unit,
    onMainCategoryChange: (MainCategoryUi) -> Unit,
    onSubCategoryChange: (SubCategoryUi?) -> Unit,
) {
    val strings = EditorThemeRes.strings
    val scopeItems = remember(strings) {
        arrayOf(
            GoalSegmentedItemUi(
                value = GoalScopeType.ALL,
                text = strings.goalAllTasksTitle,
            ),
            GoalSegmentedItemUi(
                value = GoalScopeType.MAIN_CATEGORY,
                text = strings.goalCategoryScopeTitle,
            ),
            GoalSegmentedItemUi(
                value = GoalScopeType.SUB_CATEGORY,
                text = strings.goalSubCategoryScopeTitle,
            )
        )
    }
    val isScopeError = GoalValidationError.SCOPE in errors
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        SegmentedParametersChooser(
            parameters = scopeItems,
            selected = scopeItems.first { item -> item.value == goal.scopeType },
            title = EditorThemeRes.strings.goalScopeTitle,
            onChangeSelected = { item -> onScopeChange(item.value) },
        )
        AnimatedVisibility(visible = goal.scopeType != GoalScopeType.ALL) {
            MainCategoryChooser(
                modifier = Modifier.fillMaxWidth(),
                isError = isScopeError && goal.mainCategory == null,
                currentCategory = goal.mainCategory,
                allCategories = remember(categories) {
                    categories.map { details -> details.mainCategory }
                },
                onChangeCategory = onMainCategoryChange,
            )
        }
        AnimatedVisibility(
            visible = goal.scopeType == GoalScopeType.SUB_CATEGORY && goal.mainCategory != null,
        ) {
            val mainCategory = goal.mainCategory
            if (mainCategory != null) {
                val subCategories = remember(categories) {
                    categories.firstOrNull { details ->
                        details.mainCategory.id == mainCategory.id
                    }?.subCategories.orEmpty()
                }
                SubCategoryChooser(
                    modifier = Modifier.fillMaxWidth(),
                    isError = isScopeError && goal.subCategory == null,
                    mainCategory = mainCategory,
                    allSubCategories = subCategories,
                    currentSubCategory = goal.subCategory,
                    onChangeSubCategory = onSubCategoryChange,
                )
            }
        }
        AnimatedVisibility(visible = isScopeError) {
            Text(
                text = EditorThemeRes.strings.goalScopeError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}
