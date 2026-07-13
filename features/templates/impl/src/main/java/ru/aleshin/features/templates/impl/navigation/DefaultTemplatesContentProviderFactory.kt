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
package ru.aleshin.features.templates.impl.navigation

import com.arkivanov.decompose.ComponentContext
import ru.aleshin.core.utils.architecture.component.OutputConsumer
import ru.aleshin.core.utils.inject.FeatureContentProvider
import ru.aleshin.features.templates.api.TemplatesConfig
import ru.aleshin.features.templates.api.TemplatesContentProviderFactory
import ru.aleshin.features.templates.api.TemplatesOutput
import ru.aleshin.features.templates.impl.presentation.ui.root.InternalTemplatesFeatureComponent
import ru.aleshin.features.templates.impl.presentation.ui.root.TemplatesContentProvider
import ru.aleshin.features.templates.impl.presentation.ui.templates.store.TemplatesComposeStore
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 01.07.2026.
 */
internal class DefaultTemplatesContentProviderFactory @Inject constructor(
    private val templatesStoreFactory: TemplatesComposeStore.Factory,
) : TemplatesContentProviderFactory {

    override fun createProvider(
        componentContext: ComponentContext,
        startConfig: TemplatesConfig,
        outputConsumer: OutputConsumer<TemplatesOutput>
    ): FeatureContentProvider {
        val component = InternalTemplatesFeatureComponent.Default(
            componentContext = componentContext,
            startConfig = startConfig,
            outputConsumer = outputConsumer,
            templatesStoreFactory = templatesStoreFactory,
        )

        return TemplatesContentProvider(component = component)
    }
}
