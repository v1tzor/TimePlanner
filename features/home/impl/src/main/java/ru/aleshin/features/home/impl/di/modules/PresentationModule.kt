/*
 * Copyright 2025 Stanislav Aleshin
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
package ru.aleshin.features.home.impl.di.modules

import dagger.Binds
import dagger.Module
import ru.aleshin.core.utils.architecture.store.BaseComposeStore
import ru.aleshin.core.utils.architecture.store.BaseOnlyOutComposeStore
import ru.aleshin.core.utils.architecture.store.BaseSimpleComposeStore
import ru.aleshin.core.utils.di.FeatureScope
import ru.aleshin.features.home.api.HomeContentProviderFactory
import ru.aleshin.features.home.impl.navigation.DefaultHomeContentProviderFactory
import ru.aleshin.features.home.impl.presentation.common.TimeTaskStatusController
import ru.aleshin.features.home.impl.presentation.mapppers.schedules.ScheduleDomainToUiMapper
import ru.aleshin.features.home.impl.presentation.mapppers.schedules.TimeTaskDomainToUiMapper
import ru.aleshin.features.home.impl.presentation.ui.categories.contract.CategoriesState
import ru.aleshin.features.home.impl.presentation.ui.categories.screenmodel.CategoriesComposeStore
import ru.aleshin.features.home.impl.presentation.ui.categories.screenmodel.CategoriesWorkProcessor
import ru.aleshin.features.home.impl.presentation.ui.details.contract.DetailsState
import ru.aleshin.features.home.impl.presentation.ui.details.store.DetailsComposeStore
import ru.aleshin.features.home.impl.presentation.ui.details.store.DetailsWorkProcessor
import ru.aleshin.features.home.impl.presentation.ui.home.contract.HomeState
import ru.aleshin.features.home.impl.presentation.ui.home.store.HomeComposeStore
import ru.aleshin.features.home.impl.presentation.ui.home.store.NavigationWorkProcessor
import ru.aleshin.features.home.impl.presentation.ui.home.store.ScheduleWorkProcessor
import ru.aleshin.features.home.impl.presentation.ui.overview.contract.OverviewState
import ru.aleshin.features.home.impl.presentation.ui.overview.store.OverviewComposeStore
import ru.aleshin.features.home.impl.presentation.ui.overview.store.OverviewWorkProcessor
import ru.aleshin.features.home.impl.presentation.ui.templates.contract.TemplatesState
import ru.aleshin.features.home.impl.presentation.ui.templates.store.TemplatesComposeStore
import ru.aleshin.features.home.impl.presentation.ui.templates.store.TemplatesWorkProcessor

/**
 * @author Stanislav Aleshin on 18.02.2023.
 */
@Module
internal interface PresentationModule {

    @Binds
    @FeatureScope
    fun bindHomeContentProviderFactory(factory: DefaultHomeContentProviderFactory): HomeContentProviderFactory

    @Binds
    @FeatureScope
    fun bindTimeTaskStatusController(controller: TimeTaskStatusController.Base): TimeTaskStatusController

    // Overview

    @Binds
    @FeatureScope
    fun bindOverviewStoreFactory(factory: OverviewComposeStore.Factory): BaseOnlyOutComposeStore.Factory<OverviewComposeStore, OverviewState>

    @Binds
    @FeatureScope
    fun bindOverviewWorkProcessor(processor: OverviewWorkProcessor.Base): OverviewWorkProcessor 
    
    // Details

    @Binds
    @FeatureScope
    fun bindDetailsStoreFactory(factory: DetailsComposeStore.Factory): BaseOnlyOutComposeStore.Factory<DetailsComposeStore, DetailsState>

    @Binds
    @FeatureScope
    fun binDetailsWorkProcessor(processor: DetailsWorkProcessor.Base): DetailsWorkProcessor

    // Home ScreenModel

    @Binds
    @FeatureScope
    fun bindHomeStoreFactory(factory: HomeComposeStore.Factory): BaseComposeStore.Factory<HomeComposeStore, HomeState>

    @Binds
    @FeatureScope
    fun bindScheduleWorkProcessor(processor: ScheduleWorkProcessor.Base): ScheduleWorkProcessor

    @Binds
    @FeatureScope
    fun bindNavigationWorkProcessor(processor: NavigationWorkProcessor.Base): NavigationWorkProcessor

    @Binds
    @FeatureScope
    fun bindTimeTaskDomainToUiMapper(mapper: TimeTaskDomainToUiMapper.Base): TimeTaskDomainToUiMapper

    @Binds
    @FeatureScope
    fun bindScheduleDomainToUiMapper(mapper: ScheduleDomainToUiMapper.Base): ScheduleDomainToUiMapper

    // Templates

    @Binds
    @FeatureScope
    fun bindTemplatesStoreFactory(factory: TemplatesComposeStore.Factory): BaseSimpleComposeStore.Factory<TemplatesComposeStore, TemplatesState>

    @Binds
    @FeatureScope
    fun bindTemplatesWorkProcessor(processor: TemplatesWorkProcessor.Base): TemplatesWorkProcessor

    // Categories

    @Binds
    @FeatureScope
    fun bindCategoriesStoreFactory(factory: CategoriesComposeStore.Factory): BaseOnlyOutComposeStore.Factory<CategoriesComposeStore, CategoriesState>

    @Binds
    @FeatureScope
    fun bindCategoriesWorkProcessor(processor: CategoriesWorkProcessor.Base): CategoriesWorkProcessor
}
