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
package ru.aleshin.features.home.impl.di.modules

import dagger.Binds
import dagger.Module
import ru.aleshin.core.utils.architecture.store.BaseComposeStore
import ru.aleshin.core.utils.di.FeatureScope
import ru.aleshin.features.home.api.HomeContentProviderFactory
import ru.aleshin.features.home.impl.navigation.DefaultHomeContentProviderFactory
import ru.aleshin.features.home.impl.presentation.ui.home.contract.HomeState
import ru.aleshin.features.home.impl.presentation.ui.home.store.HomeComposeStore
import ru.aleshin.features.home.impl.presentation.ui.home.store.NavigationWorkProcessor
import ru.aleshin.features.home.impl.presentation.ui.home.store.ScheduleWorkProcessor

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
    fun bindHomeStoreFactory(factory: HomeComposeStore.Factory): BaseComposeStore.Factory<HomeComposeStore, HomeState>

    @Binds
    @FeatureScope
    fun bindScheduleWorkProcessor(processor: ScheduleWorkProcessor.Base): ScheduleWorkProcessor

    @Binds
    @FeatureScope
    fun bindNavigationWorkProcessor(processor: NavigationWorkProcessor.Base): NavigationWorkProcessor

}
