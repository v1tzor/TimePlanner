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
package ru.aleshin.features.analytics.impl.navigation

import com.arkivanov.decompose.ComponentContext
import ru.aleshin.core.utils.architecture.component.OutputConsumer
import ru.aleshin.core.utils.inject.FeatureContentProvider
import ru.aleshin.features.analytics.api.AnalyticsConfig
import ru.aleshin.features.analytics.api.AnalyticsContentProviderFactory
import ru.aleshin.features.analytics.api.AnalyticsOutput
import ru.aleshin.features.analytics.impl.presentation.ui.analytics.store.AnalyticsComposeStore
import ru.aleshin.features.analytics.impl.presentation.ui.root.AnalyticsContentProvider
import ru.aleshin.features.analytics.impl.presentation.ui.root.InternalAnalyticsFeatureComponent
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 01.07.2026.
 */
internal class DefaultAnalyticsContentProviderFactory @Inject constructor(
    private val analyticsStoreFactory: AnalyticsComposeStore.Factory,
) : AnalyticsContentProviderFactory {

    override fun createProvider(
        componentContext: ComponentContext,
        startConfig: AnalyticsConfig,
        outputConsumer: OutputConsumer<AnalyticsOutput>
    ): FeatureContentProvider {
        val component = InternalAnalyticsFeatureComponent.Default(
            componentContext = componentContext,
            startConfig = startConfig,
            outputConsumer = outputConsumer,
            analyticsStoreFactory = analyticsStoreFactory,
        )

        return AnalyticsContentProvider(component = component)
    }
}
