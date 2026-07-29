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
package ru.aleshin.features.editor.impl.presentation.ui.goal.validators

import ru.aleshin.core.domain.entities.goals.GoalScopeType
import ru.aleshin.core.utils.extensions.startThisDay
import ru.aleshin.features.editor.impl.presentation.models.goals.GoalEditUi
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 28.07.2026.
 */
internal interface GoalValidator {

    fun validate(goal: GoalEditUi): Set<GoalValidationError>

    class Base @Inject constructor() : GoalValidator {

        override fun validate(goal: GoalEditUi): Set<GoalValidationError> {
            return buildSet {
                if (goal.title.isBlank()) add(GoalValidationError.TITLE)
                if (goal.targetValue.toLongOrNull()?.let { value -> value > 0L } != true) {
                    add(GoalValidationError.TARGET)
                }
                if (goal.deadline.startThisDay().before(goal.createdAt.startThisDay())) {
                    add(GoalValidationError.DEADLINE)
                }
                val isScopeValid = when (goal.scopeType) {
                    GoalScopeType.ALL -> true
                    GoalScopeType.MAIN_CATEGORY -> goal.mainCategory != null
                    GoalScopeType.SUB_CATEGORY -> {
                        goal.mainCategory != null && goal.subCategory != null
                    }
                }
                if (!isScopeValid) add(GoalValidationError.SCOPE)
            }
        }
    }
}
