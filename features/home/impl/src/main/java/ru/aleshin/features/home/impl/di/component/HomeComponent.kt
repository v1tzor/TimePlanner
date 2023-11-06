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
package ru.aleshin.features.home.impl.di.component

import dagger.Component
import ru.aleshin.core.utils.di.FeatureScope
import ru.aleshin.core.utils.navigation.navigator.NavigatorManager
import ru.aleshin.features.home.api.di.HomeFeatureApi
import ru.aleshin.features.home.impl.di.HomeFeatureDependencies
import ru.aleshin.features.home.impl.di.modules.DataModule
import ru.aleshin.features.home.impl.di.modules.DomainModule
import ru.aleshin.features.home.impl.di.modules.LocalNavigationModule
import ru.aleshin.features.home.impl.di.modules.PresentationModule
import ru.aleshin.features.home.impl.presentation.ui.categories.screenmodel.CategoriesScreenModel
import ru.aleshin.features.home.impl.presentation.ui.details.screenmodel.DetailsScreenModel
import ru.aleshin.features.home.impl.presentation.ui.home.screenModel.HomeScreenModel
import ru.aleshin.features.home.impl.presentation.ui.overview.screenmodel.OverviewScreenModel
import ru.aleshin.features.home.impl.presentation.ui.templates.screenmodel.TemplatesScreenModel

/**
 * @author Stanislav Aleshin on 18.02.2023.
 */
@FeatureScope
@Component(
    modules = [DataModule::class, DomainModule::class, LocalNavigationModule::class, PresentationModule::class],
    dependencies = [HomeFeatureDependencies::class],
)
internal interface HomeComponent : HomeFeatureApi {

    fun fetchLocalNavigatorManager(): NavigatorManager
    fun fetchOverviewScreenModel(): OverviewScreenModel
    fun fetchDetailsScreenModel(): DetailsScreenModel
    fun fetchHomeScreenModel(): HomeScreenModel
    fun fetchTemplatesScreenModel(): TemplatesScreenModel
    fun fetchCategoriesScreenModel(): CategoriesScreenModel

    @Component.Builder
    interface Builder {
        fun dependencies(deps: HomeFeatureDependencies): Builder
        fun navigationModule(module: LocalNavigationModule): Builder
        fun build(): HomeComponent
    }

    companion object {
        fun create(dependencies: HomeFeatureDependencies): HomeComponent {
            return DaggerHomeComponent.builder()
                .dependencies(dependencies)
                .navigationModule(LocalNavigationModule())
                .build()
        }
    }
}
