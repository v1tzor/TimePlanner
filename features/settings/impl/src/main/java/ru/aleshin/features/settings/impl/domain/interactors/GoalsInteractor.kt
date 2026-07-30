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

import kotlinx.coroutines.flow.first
import ru.aleshin.core.domain.entities.goals.Goal
import ru.aleshin.core.domain.repository.GoalRepository
import ru.aleshin.core.utils.functional.DomainResult
import ru.aleshin.core.utils.functional.UnitDomainResult
import ru.aleshin.features.settings.impl.domain.common.SettingsEitherWrapper
import ru.aleshin.features.settings.impl.domain.entities.SettingsFailures
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 28.07.2026.
 */
internal interface GoalsInteractor {

    suspend fun fetchAllGoals(): DomainResult<SettingsFailures, List<Goal>>
    suspend fun addOrUpdateGoals(goals: List<Goal>): UnitDomainResult<SettingsFailures>
    suspend fun deleteAllGoals(): DomainResult<SettingsFailures, List<Goal>>

    class Base @Inject constructor(
        private val goalRepository: GoalRepository,
        private val eitherWrapper: SettingsEitherWrapper,
    ) : GoalsInteractor {

        override suspend fun fetchAllGoals() = eitherWrapper.wrap {
            goalRepository.fetchAllGoals().first()
        }

        override suspend fun addOrUpdateGoals(goals: List<Goal>) = eitherWrapper.wrap {
            goalRepository.addOrUpdateGoals(goals)
        }

        override suspend fun deleteAllGoals() = eitherWrapper.wrap {
            goalRepository.deleteAllGoals()
        }
    }
}
