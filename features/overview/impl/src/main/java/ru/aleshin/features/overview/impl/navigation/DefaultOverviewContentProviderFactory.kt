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
package ru.aleshin.features.overview.impl.navigation

import com.arkivanov.decompose.ComponentContext
import ru.aleshin.core.utils.architecture.component.OutputConsumer
import ru.aleshin.core.utils.inject.FeatureContentProvider
import ru.aleshin.features.overview.api.OverviewConfig
import ru.aleshin.features.overview.api.OverviewContentProviderFactory
import ru.aleshin.features.overview.api.OverviewOutput
import ru.aleshin.features.overview.impl.presentation.ui.overview.store.OverviewComposeStore
import ru.aleshin.features.overview.impl.presentation.ui.root.InternalOverviewFeatureComponent
import ru.aleshin.features.overview.impl.presentation.ui.root.OverviewContentProvider
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 01.07.2026.
 */
internal class DefaultOverviewContentProviderFactory @Inject constructor(
    private val overviewStoreFactory: OverviewComposeStore.Factory
) : OverviewContentProviderFactory {

    override fun createProvider(
        componentContext: ComponentContext,
        startConfig: OverviewConfig,
        outputConsumer: OutputConsumer<OverviewOutput>
    ): FeatureContentProvider {
        val component = InternalOverviewFeatureComponent.Default(
            componentContext = componentContext,
            startConfig = startConfig,
            outputConsumer = outputConsumer,
            overviewStoreFactory = overviewStoreFactory
        )

        return OverviewContentProvider(component = component)
    }
}
