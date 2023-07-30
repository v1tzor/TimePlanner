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

import dagger.Module
import dagger.Provides
import ru.aleshin.core.utils.di.FeatureScope
import ru.aleshin.core.utils.navigation.CommandBuffer
import ru.aleshin.core.utils.navigation.Router
import ru.aleshin.core.utils.navigation.navigator.NavigatorManager
import ru.aleshin.features.home.impl.di.annontation.LocalRouter

/**
 * @author Stanislav Aleshin on 27.02.2023.
 */
@Module
internal class LocalNavigationModule {

    @Provides
    @FeatureScope
    fun provideLocalNavigatorManager(
        commandBuffer: CommandBuffer,
    ): NavigatorManager = NavigatorManager.Base(commandBuffer)

    @Provides
    @FeatureScope
    fun provideLocalCommandBuffer(): CommandBuffer = CommandBuffer.Base()

    @Provides
    @FeatureScope
    @LocalRouter
    fun provideLocalRouter(commandBuffer: CommandBuffer): Router = Router.Base(commandBuffer)
}
