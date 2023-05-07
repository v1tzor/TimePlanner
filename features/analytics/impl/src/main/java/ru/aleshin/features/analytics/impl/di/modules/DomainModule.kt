/*
 * Copyright 2023 Stanislav Aleshin
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
 * imitations under the License.
 */
package ru.aleshin.features.analytics.impl.di.modules

import dagger.Binds
import dagger.Module
import ru.aleshin.features.analytics.impl.domain.common.AnalyticsEitherWrapper
import ru.aleshin.features.analytics.impl.domain.common.AnalyticsErrorHandler
import ru.aleshin.features.analytics.impl.domain.interactors.AnalyticsInteractor

/**
 * @author Stanislav Aleshin on 22.04.2023.
 */
@Module
internal interface DomainModule {

    @Binds
    fun bindAnalyticsInteractor(interactor: AnalyticsInteractor.Base): AnalyticsInteractor

    @Binds
    fun bindAnalyticsEitherWrapper(wrapper: AnalyticsEitherWrapper.Base): AnalyticsEitherWrapper

    @Binds
    fun bindAnalyticsErrorHandler(handler: AnalyticsErrorHandler.Base): AnalyticsErrorHandler
}
