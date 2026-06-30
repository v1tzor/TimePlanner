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
package ru.aleshin.features.analytics.api

import com.arkivanov.decompose.ComponentContext
import kotlinx.serialization.Serializable
import ru.aleshin.core.utils.architecture.component.BaseOutput
import ru.aleshin.core.utils.architecture.component.FeatureComponent
import ru.aleshin.core.utils.architecture.component.OutputConsumer
import ru.aleshin.core.utils.inject.StartFeatureConfig
import ru.aleshin.features.analytics.api.AnalyticsFeatureComponent.AnalyticsConfig
import ru.aleshin.features.analytics.api.AnalyticsFeatureComponent.AnalyticsOutput

/**
 * @author Stanislav Aleshin on 12.09.2025.
 */
public abstract class AnalyticsFeatureComponent(
    componentContext: ComponentContext,
    startConfig: StartFeatureConfig<AnalyticsConfig>,
    outputConsumer: OutputConsumer<AnalyticsOutput>,
) : FeatureComponent<AnalyticsConfig, AnalyticsOutput>(
    componentContext = componentContext,
    startConfig = startConfig,
    outputConsumer = outputConsumer,
) {

    @Serializable
    public sealed interface AnalyticsConfig {

        @Serializable
        public data object Analytics : AnalyticsConfig
    }

    public sealed interface AnalyticsOutput : BaseOutput {
        public data object NavigateToBack : AnalyticsOutput
    }
}