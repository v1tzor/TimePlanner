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
package ru.aleshin.features.analytics.impl.domain.interactors

import kotlinx.coroutines.flow.combine
import ru.aleshin.core.domain.repository.MainCategoryRepository
import ru.aleshin.core.domain.repository.TimeTaskRepository
import ru.aleshin.core.utils.functional.FlowDomainResult
import ru.aleshin.core.utils.functional.TimeRange
import ru.aleshin.core.utils.managers.DateManager
import ru.aleshin.features.analytics.impl.domain.common.AnalyticsEitherWrapper
import ru.aleshin.features.analytics.impl.domain.common.AnalyticsTaskClassifier
import ru.aleshin.features.analytics.impl.domain.common.CategoryAnalyticsCalculator
import ru.aleshin.features.analytics.impl.domain.entities.AnalyticsFailure
import ru.aleshin.features.analytics.impl.domain.entities.AnalyticsRangeSelection
import ru.aleshin.features.analytics.impl.domain.entities.CategoryAnalytics
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 21.07.2026.
 */
internal interface CategoryAnalyticsInteractor {

    suspend fun fetchCategoryAnalytics(
        mainCategoryId: Long,
        selection: AnalyticsRangeSelection,
    ): FlowDomainResult<AnalyticsFailure, CategoryAnalytics>

    class Base @Inject constructor(
        private val timeTaskRepository: TimeTaskRepository,
        private val mainCategoryRepository: MainCategoryRepository,
        private val dateManager: DateManager,
        private val taskClassifier: AnalyticsTaskClassifier,
        private val categoryCalculator: CategoryAnalyticsCalculator,
        private val eitherWrapper: AnalyticsEitherWrapper,
    ) : CategoryAnalyticsInteractor {

        override suspend fun fetchCategoryAnalytics(
            mainCategoryId: Long,
            selection: AnalyticsRangeSelection,
        ) = eitherWrapper.wrapFlow {
            val unionRange = TimeRange(selection.ranges.comparison.from, selection.ranges.current.to)

            timeTaskRepository.fetchTimeTasksByScheduleDateRange(unionRange).combine(
                flow = mainCategoryRepository.fetchAllCategoriesDetails(),
            ) { tasks, categories ->
                val now = dateManager.fetchCurrentDate()
                val currentTasks = taskClassifier.prepare(tasks, selection.ranges.current, now, selection.timeZone)
                val comparisonTasks = taskClassifier.prepare(tasks, selection.ranges.comparison, now, selection.timeZone)

                categoryCalculator.calculate(
                    category = categories.find { it.category.id == mainCategoryId },
                    currentPlanTasks = currentTasks,
                    comparisonPlanTasks = comparisonTasks,
                    currentCivilRange = selection.civilRange,
                    locale = selection.locale,
                    timeZone = selection.timeZone,
                )
            }
        }
    }
}
