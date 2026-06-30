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
package ru.aleshin.features.home.impl.navigation

import android.util.Log
import com.arkivanov.decompose.ComponentContext
import ru.aleshin.core.utils.architecture.component.OutputConsumer
import ru.aleshin.core.utils.inject.StartFeatureConfig
import ru.aleshin.core.utils.managers.CoroutineManager
import ru.aleshin.features.home.api.HomeFeatureComponent
import ru.aleshin.features.home.api.HomeFeatureComponent.HomeConfig
import ru.aleshin.features.home.api.HomeFeatureComponent.HomeOutput
import ru.aleshin.features.home.api.HomeFeatureComponentFactory
import ru.aleshin.features.home.impl.presentation.ui.categories.screenmodel.CategoriesComposeStore
import ru.aleshin.features.home.impl.presentation.ui.details.store.DetailsComposeStore
import ru.aleshin.features.home.impl.presentation.ui.home.store.HomeComposeStore
import ru.aleshin.features.home.impl.presentation.ui.overview.store.OverviewComposeStore
import ru.aleshin.features.home.impl.presentation.ui.root.InternalHomeFeatureComponent
import ru.aleshin.features.home.impl.presentation.ui.templates.store.TemplatesComposeStore
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 13.09.2025.
 */
internal class DefaultHomeComponentFactory @Inject constructor(
    private val homeStoreFactory: HomeComposeStore.Factory,
    private val overviewStoreFactory: OverviewComposeStore.Factory,
    private val categoriesStoreFactory: CategoriesComposeStore.Factory,
    private val templatesStoreFactory: TemplatesComposeStore.Factory,
    private val detailsStoreFactory: DetailsComposeStore.Factory,
    private val coroutineManager: CoroutineManager,
) : HomeFeatureComponentFactory {

    override fun createComponent(
        componentContext: ComponentContext,
        startConfig: StartFeatureConfig<HomeConfig>,
        outputConsumer: OutputConsumer<HomeOutput>
    ): HomeFeatureComponent {
        Log.i("test", "create new home component")
        return InternalHomeFeatureComponent.Default(
            componentContext = componentContext,
            startConfig = startConfig,
            outputConsumer = outputConsumer,
            homeStoreFactory = homeStoreFactory,
            overviewStoreFactory = overviewStoreFactory,
            categoriesStoreFactory = categoriesStoreFactory,
            templatesStoreFactory = templatesStoreFactory,
            detailsStoreFactory = detailsStoreFactory,
            coroutineManager = coroutineManager,
        )
    }
}