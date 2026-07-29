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

import kotlinx.coroutines.flow.first
import ru.aleshin.core.domain.common.GoalProgressManager
import ru.aleshin.core.domain.entities.goals.GoalHistory
import ru.aleshin.core.domain.repository.GoalHistoryRepository
import ru.aleshin.core.domain.repository.GoalRepository
import ru.aleshin.core.domain.repository.TimeTaskRepository
import ru.aleshin.core.utils.functional.DomainResult
import ru.aleshin.core.utils.functional.UnitDomainResult
import ru.aleshin.core.utils.managers.DateManager
import ru.aleshin.features.overview.impl.domain.common.OverviewEitherWrapper
import ru.aleshin.features.overview.impl.domain.entities.OverviewFailures
import java.util.TimeZone
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 28.07.2026.
 */
internal interface GoalsHistoryInteractor {

    suspend fun syncCompletedGoals(): UnitDomainResult<OverviewFailures>
    suspend fun fetchHistoryPage(
        beforePeriodEnd: Long?,
        beforeId: Long?,
    ): DomainResult<OverviewFailures, List<GoalHistory>>

    class Base @Inject constructor(
        private val goalRepository: GoalRepository,
        private val goalHistoryRepository: GoalHistoryRepository,
        private val timeTaskRepository: TimeTaskRepository,
        private val progressManager: GoalProgressManager,
        private val dateManager: DateManager,
        private val eitherWrapper: OverviewEitherWrapper,
    ) : GoalsHistoryInteractor {

        override suspend fun syncCompletedGoals() = eitherWrapper.wrap {
            val goals = goalRepository.fetchAllGoals().first()
            if (goals.isEmpty()) return@wrap

            val currentDate = dateManager.fetchCurrentDate()
            val timeZone = TimeZone.getDefault()
            val latestHistory = goalHistoryRepository.fetchLatestGoalsHistory().associateBy { history ->
                history.goalId
            }
            val completedGoals = goals.filter { goal ->
                progressManager.isDeadlinePassed(goal, currentDate, timeZone) &&
                    latestHistory[goal.id]?.isAchieved != true
            }
            if (completedGoals.isEmpty()) return@wrap

            val sourceRange = checkNotNull(
                progressManager.fetchTaskSourceRange(completedGoals, currentDate, timeZone),
            )
            val tasks = timeTaskRepository.fetchTimeTasksByScheduleDateRange(sourceRange).first()
            val history = progressManager.calculateHistory(
                goals = completedGoals,
                tasks = tasks,
                currentDate = currentDate,
                timeZone = timeZone,
            ).filter { snapshot ->
                val latestSnapshot = latestHistory[snapshot.goalId]
                latestSnapshot == null || (
                    !latestSnapshot.isAchieved && snapshot.isAchieved
                )
            }

            if (history.isNotEmpty()) goalHistoryRepository.addGoalsHistory(history)
        }

        override suspend fun fetchHistoryPage(
            beforePeriodEnd: Long?,
            beforeId: Long?,
        ) = eitherWrapper.wrap {
            goalHistoryRepository.fetchGoalHistoryPage(
                beforePeriodEnd = beforePeriodEnd,
                beforeId = beforeId,
                pageSize = HISTORY_PAGE_SIZE,
            )
        }
    }
}

internal const val HISTORY_PAGE_SIZE = 30
