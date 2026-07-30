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
package ru.aleshin.features.editor.impl.domain.interactors

import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import ru.aleshin.core.domain.entities.goals.Goal
import ru.aleshin.core.domain.entities.goals.GoalDirection
import ru.aleshin.core.domain.entities.goals.GoalMetric
import ru.aleshin.core.domain.entities.goals.GoalScopeType
import ru.aleshin.core.domain.repository.GoalRepository
import ru.aleshin.core.domain.repository.MainCategoryRepository
import ru.aleshin.core.utils.extensions.shiftDay
import ru.aleshin.core.utils.functional.FlowDomainResult
import ru.aleshin.core.utils.functional.UnitDomainResult
import ru.aleshin.core.utils.managers.DateManager
import ru.aleshin.features.editor.impl.domain.common.EditorEitherWrapper
import ru.aleshin.features.editor.impl.domain.entites.EditorFailures
import ru.aleshin.features.editor.impl.domain.entites.GoalEditorData
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 28.07.2026.
 */
internal interface GoalInteractor {

    suspend fun fetchGoalEditorData(goalId: Long?): FlowDomainResult<EditorFailures, GoalEditorData>
    suspend fun saveGoal(goal: Goal): UnitDomainResult<EditorFailures>

    class Base @Inject constructor(
        private val goalRepository: GoalRepository,
        private val mainCategoryRepository: MainCategoryRepository,
        private val dateManager: DateManager,
        private val eitherWrapper: EditorEitherWrapper,
    ) : GoalInteractor {

        private companion object {
            const val DEFAULT_DURATION_TARGET = 5L * 60L * 60L * 1000L
            const val DEFAULT_DEADLINE_DAYS = 7
        }

        override suspend fun fetchGoalEditorData(goalId: Long?) = eitherWrapper.wrapFlow {
            val goalFlow = if (goalId != null) {
                goalRepository.fetchGoalById(goalId)
            } else {
                flowOf(null)
            }
            combine(
                flow = goalFlow,
                flow2 = mainCategoryRepository.fetchAllCategoriesDetails(),
            ) { goal, categories ->
                GoalEditorData(
                    goal = goal ?: createDefaultGoal(),
                    categories = categories.sortedBy { details -> details.category.id != 0L },
                )
            }
        }

        override suspend fun saveGoal(goal: Goal) = eitherWrapper.wrapUnit {
            goalRepository.addOrUpdateGoal(goal)
        }

        private fun createDefaultGoal(): Goal {
            val createdAt = dateManager.fetchCurrentDate()
            return Goal(
                title = "",
                scopeType = GoalScopeType.ALL,
                metric = GoalMetric.DURATION,
                direction = GoalDirection.AT_LEAST,
                targetValue = DEFAULT_DURATION_TARGET,
                createdAt = createdAt,
                deadline = dateManager.fetchBeginningCurrentDay().shiftDay(DEFAULT_DEADLINE_DAYS - 1),
            )
        }
    }
}
