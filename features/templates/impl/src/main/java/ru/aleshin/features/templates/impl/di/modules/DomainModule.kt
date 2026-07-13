/*
 * Copyright 2026 Stanislav Aleshin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package ru.aleshin.features.templates.impl.di.modules

import dagger.Binds
import dagger.Module
import ru.aleshin.features.templates.impl.domain.common.HomeEitherWrapper
import ru.aleshin.features.templates.impl.domain.common.HomeErrorHandler
import ru.aleshin.features.templates.impl.domain.interactors.MainCategoriesInteractor
import ru.aleshin.features.templates.impl.domain.interactors.RepeatTaskInteractor
import ru.aleshin.features.templates.impl.domain.interactors.TemplatesInteractor

/** @author Stanislav Aleshin on 11.07.2026. */
@Module
internal interface DomainModule {

    @Binds
    fun bindTemplatesInteractor(value: TemplatesInteractor.Base): TemplatesInteractor

    @Binds
    fun bindRepeatTaskInteractor(value: RepeatTaskInteractor.Base): RepeatTaskInteractor

    @Binds
    fun bindCategoriesInteractor(value: MainCategoriesInteractor.Base): MainCategoriesInteractor

    @Binds
    fun bindEitherWrapper(value: HomeEitherWrapper.Base): HomeEitherWrapper

    @Binds
    fun bindErrorHandler(value: HomeErrorHandler.Base): HomeErrorHandler
}
