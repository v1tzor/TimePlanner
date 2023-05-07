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
package ru.aleshin.features.analytics.impl.domain.common

import ru.aleshin.core.utils.handlers.ErrorHandler
import ru.aleshin.features.analytics.impl.domain.entities.AnalyticsFailure
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 22.04.2023.
 */
internal interface AnalyticsErrorHandler : ErrorHandler<AnalyticsFailure> {

    class Base @Inject constructor() : AnalyticsErrorHandler {
        override fun handle(throwable: Throwable) = when (throwable) {
            else -> AnalyticsFailure.OtherError(throwable)
        }
    }
}
