/*
 * Copyright 2026 Stanislav Aleshin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package ru.aleshin.features.templates.impl.di.modules

import dagger.Binds
import dagger.Module
import ru.aleshin.core.utils.architecture.store.BaseSimpleComposeStore
import ru.aleshin.core.utils.di.FeatureScope
import ru.aleshin.features.templates.api.TemplatesContentProviderFactory
import ru.aleshin.features.templates.impl.navigation.DefaultTemplatesContentProviderFactory
import ru.aleshin.features.templates.impl.presentation.ui.templates.contract.TemplatesState
import ru.aleshin.features.templates.impl.presentation.ui.templates.store.TemplatesComposeStore
import ru.aleshin.features.templates.impl.presentation.ui.templates.store.TemplatesWorkProcessor

/** @author Stanislav Aleshin on 11.07.2026. */
@Module
internal interface PresentationModule {

    @Binds
    @FeatureScope
    fun bindContentProviderFactory(value: DefaultTemplatesContentProviderFactory): TemplatesContentProviderFactory

    @Binds
    @FeatureScope
    fun bindStoreFactory(value: TemplatesComposeStore.Factory): BaseSimpleComposeStore.Factory<TemplatesComposeStore, TemplatesState>

    @Binds
    @FeatureScope
    fun bindProcessor(value: TemplatesWorkProcessor.Base): TemplatesWorkProcessor
}
