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
package ru.aleshin.features.overview.impl.domain.interactors

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import ru.aleshin.core.domain.common.GoalProgressManager
import ru.aleshin.core.domain.entities.goals.Goal
import ru.aleshin.core.domain.entities.goals.GoalDetails
import ru.aleshin.core.domain.entities.goals.GoalProgress
import ru.aleshin.core.domain.repository.GoalRepository
import ru.aleshin.core.domain.repository.TimeTaskRepository
import ru.aleshin.core.utils.functional.FlowDomainResult
import ru.aleshin.core.utils.functional.UnitDomainResult
import ru.aleshin.core.utils.managers.DateManager
import ru.aleshin.features.overview.impl.domain.common.OverviewEitherWrapper
import ru.aleshin.features.overview.impl.domain.entities.OverviewFailures
import java.util.TimeZone
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 28.07.2026.
 */
internal interface GoalsInteractor {

    suspend fun fetchGoalsProgress(): FlowDomainResult<OverviewFailures, List<GoalProgress>>
    suspend fun fetchGoalDetails(goalId: Long): FlowDomainResult<OverviewFailures, GoalDetails?>
    suspend fun deleteGoalById(goalId: Long): UnitDomainResult<OverviewFailures>
    suspend fun restoreGoal(goal: Goal): UnitDomainResult<OverviewFailures>

    @OptIn(ExperimentalCoroutinesApi::class)
    class Base @Inject constructor(
        private val goalRepository: GoalRepository,
        private val timeTaskRepository: TimeTaskRepository,
        private val progressManager: GoalProgressManager,
        private val dateManager: DateManager,
        private val eitherWrapper: OverviewEitherWrapper,
    ) : GoalsInteractor {

        override suspend fun fetchGoalsProgress() = eitherWrapper.wrapFlow {
            goalRepository.fetchAllGoals().combine(dateManager.fetchMinuteTicker()) { goals, currentDate ->
                goals to currentDate
            }.flatMapLatest { (goals, currentDate) ->
                if (goals.isEmpty()) return@flatMapLatest flowOf(emptyList())

                val timeZone = TimeZone.getDefault()
                val taskRange = checkNotNull(
                    progressManager.fetchTaskSourceRange(goals, currentDate, timeZone),
                )
                timeTaskRepository.fetchTimeTasksByScheduleDateRange(taskRange).map { tasks ->
                    progressManager.calculate(
                        goals = goals,
                        tasks = tasks,
                        currentDate = currentDate,
                        timeZone = timeZone,
                    )
                }
            }.distinctUntilChanged()
        }

        override suspend fun fetchGoalDetails(goalId: Long) = eitherWrapper.wrapFlow {
            goalRepository.fetchGoalById(goalId).combine(dateManager.fetchMinuteTicker()) { goal, currentDate ->
                goal to currentDate
            }.flatMapLatest { (goal, currentDate) ->
                if (goal == null) return@flatMapLatest flowOf(null)

                val timeZone = TimeZone.getDefault()
                val taskRange = checkNotNull(
                    progressManager.fetchTaskSourceRange(listOf(goal), currentDate, timeZone),
                )

                timeTaskRepository.fetchTimeTasksByScheduleDateRange(taskRange).map { tasks ->
                    progressManager.calculateDetails(
                        goal = goal,
                        tasks = tasks,
                        currentDate = currentDate,
                        timeZone = timeZone,
                    )
                }
            }.distinctUntilChanged()
        }

        override suspend fun deleteGoalById(goalId: Long) = eitherWrapper.wrap {
            goalRepository.deleteGoalById(goalId)
        }

        override suspend fun restoreGoal(goal: Goal) = eitherWrapper.wrapUnit {
            goalRepository.addOrUpdateGoal(goal)
        }
    }
}
