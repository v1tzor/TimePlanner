/*
 * Copyright 2026 Stanislav Aleshin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package ru.aleshin.features.overview.impl.di.modules

import dagger.Binds
import dagger.Module
import ru.aleshin.core.utils.architecture.store.BaseComposeStore
import ru.aleshin.core.utils.di.FeatureScope
import ru.aleshin.features.overview.api.OverviewContentProviderFactory
import ru.aleshin.features.overview.impl.navigation.DefaultOverviewContentProviderFactory
import ru.aleshin.features.overview.impl.presentation.ui.overview.contract.OverviewState
import ru.aleshin.features.overview.impl.presentation.ui.overview.store.OverviewComposeStore
import ru.aleshin.features.overview.impl.presentation.ui.overview.store.OverviewWorkProcessor

/** @author Stanislav Aleshin on 11.07.2026. */
@Module
internal interface PresentationModule {

    @Binds
    @FeatureScope
    fun bindContentProviderFactory(value: DefaultOverviewContentProviderFactory): OverviewContentProviderFactory

    @Binds
    @FeatureScope
    fun bindOverviewStoreFactory(value: OverviewComposeStore.Factory): BaseComposeStore.Factory<OverviewComposeStore, OverviewState>

    @Binds
    @FeatureScope
    fun bindOverviewProcessor(value: OverviewWorkProcessor.Base): OverviewWorkProcessor
}
