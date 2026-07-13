/*
 * Copyright 2026 Stanislav Aleshin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package ru.aleshin.features.overview.impl.di.modules

import dagger.Binds
import dagger.Module
import ru.aleshin.features.overview.impl.domain.common.OverviewEitherWrapper
import ru.aleshin.features.overview.impl.domain.common.OverviewErrorHandler
import ru.aleshin.features.overview.impl.domain.interactors.MainCategoriesInteractor
import ru.aleshin.features.overview.impl.domain.interactors.ScheduleInteractor
import ru.aleshin.features.overview.impl.domain.interactors.ShareTextInteractor
import ru.aleshin.features.overview.impl.domain.interactors.UndefinedTasksInteractor

/** @author Stanislav Aleshin on 11.07.2026. */
@Module
internal interface DomainModule {

    @Binds
    fun bindScheduleInteractor(value: ScheduleInteractor.Base): ScheduleInteractor

    @Binds
    fun bindCategoriesInteractor(value: MainCategoriesInteractor.Base): MainCategoriesInteractor

    @Binds
    fun bindUndefinedTasksInteractor(value: UndefinedTasksInteractor.Base): UndefinedTasksInteractor

    @Binds
    fun bindShareTextInteractor(value: ShareTextInteractor.Base): ShareTextInteractor

    @Binds
    fun bindEitherWrapper(value: OverviewEitherWrapper.Base): OverviewEitherWrapper

    @Binds
    fun bindErrorHandler(value: OverviewErrorHandler.Base): OverviewErrorHandler
}
