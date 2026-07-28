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
package ru.aleshin.timeplanner.widgets.di.modules

import dagger.Binds
import dagger.Module
import ru.aleshin.timeplanner.widgets.domain.common.WidgetEitherWrapper
import ru.aleshin.timeplanner.widgets.domain.common.WidgetErrorHandler
import ru.aleshin.timeplanner.widgets.domain.interactors.WidgetsInteractor
import ru.aleshin.timeplanner.widgets.domain.managers.DailySummaryCalculator
import ru.aleshin.timeplanner.widgets.domain.managers.DeadlineTasksCalculator
import ru.aleshin.timeplanner.widgets.domain.managers.WeekOverviewCalculator

/**
 * @author Stanislav Aleshin on 28.07.2026.
 */
@Module
internal interface DomainModule {

    @Binds
    fun bindWidgetsInteractor(interactor: WidgetsInteractor.Base): WidgetsInteractor

    @Binds
    fun bindWeekOverviewCalculator(calculator: WeekOverviewCalculator.Base): WeekOverviewCalculator

    @Binds
    fun bindDailySummaryCalculator(calculator: DailySummaryCalculator.Base): DailySummaryCalculator

    @Binds
    fun bindDeadlineTasksCalculator(calculator: DeadlineTasksCalculator.Base): DeadlineTasksCalculator

    @Binds
    fun bindWidgetEitherWrapper(wrapper: WidgetEitherWrapper.Base): WidgetEitherWrapper

    @Binds
    fun bindWidgetErrorHandler(handler: WidgetErrorHandler.Base): WidgetErrorHandler
}
