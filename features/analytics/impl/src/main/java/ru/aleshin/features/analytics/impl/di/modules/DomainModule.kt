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
package ru.aleshin.features.analytics.impl.di.modules

import dagger.Binds
import dagger.Module
import ru.aleshin.core.utils.di.FeatureScope
import ru.aleshin.features.analytics.impl.domain.common.AnalyticsBucketCalculator
import ru.aleshin.features.analytics.impl.domain.common.AnalyticsEitherWrapper
import ru.aleshin.features.analytics.impl.domain.common.AnalyticsErrorHandler
import ru.aleshin.features.analytics.impl.domain.common.AnalyticsIntervalSplitter
import ru.aleshin.features.analytics.impl.domain.common.AnalyticsOverviewCalculator
import ru.aleshin.features.analytics.impl.domain.common.AnalyticsRangeCalculator
import ru.aleshin.features.analytics.impl.domain.common.AnalyticsTaskClassifier
import ru.aleshin.features.analytics.impl.domain.common.CategoryAnalyticsCalculator
import ru.aleshin.features.analytics.impl.domain.interactors.AnalyticsOverviewInteractor
import ru.aleshin.features.analytics.impl.domain.interactors.AnalyticsRangeInteractor
import ru.aleshin.features.analytics.impl.domain.interactors.CategoryAnalyticsInteractor

/**
 * @author Stanislav Aleshin on 22.04.2023.
 */
@Module
internal interface DomainModule {

    @Binds
    @FeatureScope
    fun bindAnalyticsRangeInteractor(interactor: AnalyticsRangeInteractor.Base): AnalyticsRangeInteractor

    @Binds
    fun bindAnalyticsOverviewInteractor(interactor: AnalyticsOverviewInteractor.Base): AnalyticsOverviewInteractor

    @Binds
    fun bindCategoryAnalyticsInteractor(interactor: CategoryAnalyticsInteractor.Base): CategoryAnalyticsInteractor

    @Binds
    fun bindAnalyticsEitherWrapper(wrapper: AnalyticsEitherWrapper.Base): AnalyticsEitherWrapper

    @Binds
    fun bindAnalyticsErrorHandler(handler: AnalyticsErrorHandler.Base): AnalyticsErrorHandler

    @Binds
    fun bindAnalyticsRangeCalculator(calculator: AnalyticsRangeCalculator.Base): AnalyticsRangeCalculator

    @Binds
    fun bindAnalyticsTaskClassifier(classifier: AnalyticsTaskClassifier.Base): AnalyticsTaskClassifier

    @Binds
    fun bindAnalyticsIntervalSplitter(splitter: AnalyticsIntervalSplitter.Base): AnalyticsIntervalSplitter

    @Binds
    fun bindAnalyticsBucketCalculator(calculator: AnalyticsBucketCalculator.Base): AnalyticsBucketCalculator

    @Binds
    fun bindAnalyticsOverviewCalculator(calculator: AnalyticsOverviewCalculator.Base): AnalyticsOverviewCalculator

    @Binds
    fun bindCategoryAnalyticsCalculator(calculator: CategoryAnalyticsCalculator.Base): CategoryAnalyticsCalculator
}
