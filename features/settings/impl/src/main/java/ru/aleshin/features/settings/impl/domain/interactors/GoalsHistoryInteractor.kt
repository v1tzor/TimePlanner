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
package ru.aleshin.features.settings.impl.domain.interactors

import ru.aleshin.core.domain.entities.goals.GoalHistory
import ru.aleshin.core.domain.repository.GoalHistoryRepository
import ru.aleshin.core.utils.functional.DomainResult
import ru.aleshin.core.utils.functional.UnitDomainResult
import ru.aleshin.features.settings.impl.domain.common.SettingsEitherWrapper
import ru.aleshin.features.settings.impl.domain.entities.SettingsFailures
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 29.07.2026.
 */
internal interface GoalsHistoryInteractor {

    suspend fun fetchAllGoalsHistory(): DomainResult<SettingsFailures, List<GoalHistory>>
    suspend fun addGoalsHistory(history: List<GoalHistory>): UnitDomainResult<SettingsFailures>
    suspend fun deleteAllGoalsHistory(): DomainResult<SettingsFailures, List<GoalHistory>>

    class Base @Inject constructor(
        private val goalHistoryRepository: GoalHistoryRepository,
        private val eitherWrapper: SettingsEitherWrapper,
    ) : GoalsHistoryInteractor {

        override suspend fun fetchAllGoalsHistory() = eitherWrapper.wrap {
            goalHistoryRepository.fetchAllGoalsHistory()
        }

        override suspend fun addGoalsHistory(
            history: List<GoalHistory>,
        ) = eitherWrapper.wrap {
            goalHistoryRepository.addGoalsHistory(history)
        }

        override suspend fun deleteAllGoalsHistory() = eitherWrapper.wrap {
            goalHistoryRepository.deleteAllGoalsHistory()
        }
    }
}
