/*
 * Copyright 2026 Stanislav Aleshin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package ru.aleshin.features.overview.impl.di.modules

import dagger.Binds
import dagger.Module
import ru.aleshin.core.utils.architecture.store.BaseComposeStore
import ru.aleshin.core.utils.architecture.store.BaseOnlyOutComposeStore
import ru.aleshin.core.utils.di.FeatureScope
import ru.aleshin.features.overview.api.OverviewContentProviderFactory
import ru.aleshin.features.overview.impl.navigation.DefaultOverviewContentProviderFactory
import ru.aleshin.features.overview.impl.presentation.ui.goal.details.contract.GoalDetailsState
import ru.aleshin.features.overview.impl.presentation.ui.goal.details.store.GoalDetailsComposeStore
import ru.aleshin.features.overview.impl.presentation.ui.goal.details.store.GoalDetailsWorkProcessor
import ru.aleshin.features.overview.impl.presentation.ui.goal.history.contract.GoalsHistoryState
import ru.aleshin.features.overview.impl.presentation.ui.goal.history.store.GoalsHistoryComposeStore
import ru.aleshin.features.overview.impl.presentation.ui.goal.history.store.GoalsHistoryWorkProcessor
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

    @Binds
    @FeatureScope
    fun bindGoalDetailsStoreFactory(
        value: GoalDetailsComposeStore.Factory,
    ): BaseComposeStore.Factory<GoalDetailsComposeStore, GoalDetailsState>

    @Binds
    @FeatureScope
    fun bindGoalDetailsProcessor(value: GoalDetailsWorkProcessor.Base): GoalDetailsWorkProcessor

    @Binds
    @FeatureScope
    fun bindGoalsHistoryStoreFactory(
        value: GoalsHistoryComposeStore.Factory,
    ): BaseOnlyOutComposeStore.Factory<GoalsHistoryComposeStore, GoalsHistoryState>

    @Binds
    @FeatureScope
    fun bindGoalsHistoryProcessor(value: GoalsHistoryWorkProcessor.Base): GoalsHistoryWorkProcessor
}
