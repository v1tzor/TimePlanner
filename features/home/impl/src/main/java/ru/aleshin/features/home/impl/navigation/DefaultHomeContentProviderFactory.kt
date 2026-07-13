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
package ru.aleshin.features.home.impl.navigation

import com.arkivanov.decompose.ComponentContext
import ru.aleshin.core.utils.architecture.component.OutputConsumer
import ru.aleshin.core.utils.inject.FeatureContentProvider
import ru.aleshin.features.home.api.HomeConfig
import ru.aleshin.features.home.api.HomeContentProviderFactory
import ru.aleshin.features.home.api.HomeOutput
import ru.aleshin.features.home.impl.presentation.ui.home.store.HomeComposeStore
import ru.aleshin.features.home.impl.presentation.ui.root.HomeContentProvider
import ru.aleshin.features.home.impl.presentation.ui.root.InternalHomeFeatureComponent
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 01.07.2026.
 */
internal class DefaultHomeContentProviderFactory @Inject constructor(
    private val homeStoreFactory: HomeComposeStore.Factory,
) : HomeContentProviderFactory {

    override fun createProvider(
        componentContext: ComponentContext,
        startConfig: HomeConfig,
        outputConsumer: OutputConsumer<HomeOutput>
    ): FeatureContentProvider {
        val component = InternalHomeFeatureComponent.Default(
            componentContext = componentContext,
            startConfig = startConfig,
            outputConsumer = outputConsumer,
            homeStoreFactory = homeStoreFactory,
        )

        return HomeContentProvider(component = component)
    }
}
