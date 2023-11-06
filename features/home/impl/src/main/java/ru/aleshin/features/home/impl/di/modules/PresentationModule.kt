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
 * limitations under the License.
 */
package ru.aleshin.features.home.impl.di.modules

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.screen.Screen
import dagger.Binds
import dagger.Module
import ru.aleshin.core.utils.di.FeatureScope
import ru.aleshin.core.utils.di.ScreenModelKey
import ru.aleshin.features.home.api.navigation.HomeFeatureStarter
import ru.aleshin.features.home.impl.navigation.HomeFeatureStarterImpl
import ru.aleshin.features.home.impl.navigation.NavigationManager
import ru.aleshin.features.home.impl.presentation.common.TimeTaskStatusController
import ru.aleshin.features.home.impl.presentation.mapppers.schedules.ScheduleDomainToUiMapper
import ru.aleshin.features.home.impl.presentation.mapppers.schedules.TimeTaskDomainToUiMapper
import ru.aleshin.features.home.impl.presentation.ui.categories.screenmodel.CategoriesEffectCommunicator
import ru.aleshin.features.home.impl.presentation.ui.categories.screenmodel.CategoriesScreenModel
import ru.aleshin.features.home.impl.presentation.ui.categories.screenmodel.CategoriesStateCommunicator
import ru.aleshin.features.home.impl.presentation.ui.categories.screenmodel.CategoriesWorkProcessor
import ru.aleshin.features.home.impl.presentation.ui.details.screenmodel.DetailsEffectCommunicator
import ru.aleshin.features.home.impl.presentation.ui.details.screenmodel.DetailsScreenModel
import ru.aleshin.features.home.impl.presentation.ui.details.screenmodel.DetailsStateCommunicator
import ru.aleshin.features.home.impl.presentation.ui.details.screenmodel.DetailsWorkProcessor
import ru.aleshin.features.home.impl.presentation.ui.home.screenModel.HomeEffectCommunicator
import ru.aleshin.features.home.impl.presentation.ui.home.screenModel.HomeScreenModel
import ru.aleshin.features.home.impl.presentation.ui.home.screenModel.HomeStateCommunicator
import ru.aleshin.features.home.impl.presentation.ui.home.screenModel.NavigationWorkProcessor
import ru.aleshin.features.home.impl.presentation.ui.home.screenModel.ScheduleWorkProcessor
import ru.aleshin.features.home.impl.presentation.ui.nav.NavScreen
import ru.aleshin.features.home.impl.presentation.ui.overview.screenmodel.OverviewEffectCommunicator
import ru.aleshin.features.home.impl.presentation.ui.overview.screenmodel.OverviewScreenModel
import ru.aleshin.features.home.impl.presentation.ui.overview.screenmodel.OverviewStateCommunicator
import ru.aleshin.features.home.impl.presentation.ui.overview.screenmodel.OverviewWorkProcessor
import ru.aleshin.features.home.impl.presentation.ui.templates.screenmodel.TemplatesEffectCommunicator
import ru.aleshin.features.home.impl.presentation.ui.templates.screenmodel.TemplatesScreenModel
import ru.aleshin.features.home.impl.presentation.ui.templates.screenmodel.TemplatesStateCommunicator
import ru.aleshin.features.home.impl.presentation.ui.templates.screenmodel.TemplatesWorkProcessor

/**
 * @author Stanislav Aleshin on 18.02.2023.
 */
@Module
internal interface PresentationModule {

    @Binds
    @FeatureScope
    fun bindHomeFeatureStarter(starter: HomeFeatureStarterImpl): HomeFeatureStarter

    @Binds
    @FeatureScope
    fun bindNavigationManager(manager: NavigationManager.Base): NavigationManager

    @Binds
    @FeatureScope
    fun bindTimeTaskStatusController(controller: TimeTaskStatusController.Base): TimeTaskStatusController

    // Nav ScreenModel

    @Binds
    @FeatureScope
    fun bindNavScreen(screen: NavScreen): Screen

    // Overview

    @Binds
    @ScreenModelKey(OverviewScreenModel::class)
    fun bindOverviewScreenModel(screenModel: OverviewScreenModel): ScreenModel

    @Binds
    fun bindOverviewStateCommunicator(communicator: OverviewStateCommunicator.Base): OverviewStateCommunicator

    @Binds
    fun bindOverviewEffectCommunicator(communicator: OverviewEffectCommunicator.Base): OverviewEffectCommunicator

    @Binds
    fun bindOverviewWorkProcessor(processor: OverviewWorkProcessor.Base): OverviewWorkProcessor 
    
    // Details

    @Binds
    @ScreenModelKey(DetailsScreenModel::class)
    fun bindDetailsScreenModel(screenModel: DetailsScreenModel): ScreenModel

    @Binds
    fun bindDetailsStateCommunicator(communicator: DetailsStateCommunicator.Base): DetailsStateCommunicator

    @Binds
    fun bindDetailsEffectCommunicator(communicator: DetailsEffectCommunicator.Base): DetailsEffectCommunicator

    @Binds
    fun binDetailsWorkProcessor(processor: DetailsWorkProcessor.Base): DetailsWorkProcessor

    // Home ScreenModel

    @Binds
    @ScreenModelKey(HomeScreenModel::class)
    fun bindHomeScreenModel(screenModel: HomeScreenModel): ScreenModel

    @Binds
    @FeatureScope
    fun bindHomeStateCommunicator(communicator: HomeStateCommunicator.Base): HomeStateCommunicator

    @Binds
    @FeatureScope
    fun bindHomeEffectCommunicator(communicator: HomeEffectCommunicator.Base): HomeEffectCommunicator

    @Binds
    fun bindScheduleWorkProcessor(processor: ScheduleWorkProcessor.Base): ScheduleWorkProcessor

    @Binds
    fun bindNavigationWorkProcessor(processor: NavigationWorkProcessor.Base): NavigationWorkProcessor

    @Binds
    fun bindTimeTaskDomainToUiMapper(mapper: TimeTaskDomainToUiMapper.Base): TimeTaskDomainToUiMapper

    @Binds
    fun bindScheduleDomainToUiMapper(mapper: ScheduleDomainToUiMapper.Base): ScheduleDomainToUiMapper

    // Templates

    @Binds
    @ScreenModelKey(TemplatesScreenModel::class)
    fun bindTemplatesScreenModel(screenModel: TemplatesScreenModel): ScreenModel

    @Binds
    @FeatureScope
    fun bindTemplatesStateCommunicator(communicator: TemplatesStateCommunicator.Base): TemplatesStateCommunicator

    @Binds
    @FeatureScope
    fun bindTemplatesEffectCommunicator(communicator: TemplatesEffectCommunicator.Base): TemplatesEffectCommunicator

    @Binds
    fun bindTemplatesWorkProcessor(processor: TemplatesWorkProcessor.Base): TemplatesWorkProcessor

    // Categories

    @Binds
    @ScreenModelKey(CategoriesScreenModel::class)
    fun bindCategoriesScreenModel(screenModel: CategoriesScreenModel): ScreenModel

    @Binds
    @FeatureScope
    fun bindCategoriesStateCommunicator(communicator: CategoriesStateCommunicator.Base): CategoriesStateCommunicator

    @Binds
    @FeatureScope
    fun bindCategoriesEffectCommunicator(communicator: CategoriesEffectCommunicator.Base): CategoriesEffectCommunicator

    @Binds
    fun bindCategoriesWorkProcessor(processor: CategoriesWorkProcessor.Base): CategoriesWorkProcessor
}
