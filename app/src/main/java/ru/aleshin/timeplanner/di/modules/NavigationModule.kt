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
package ru.aleshin.timeplanner.di.modules

import dagger.Module
import dagger.Provides
import ru.aleshin.core.utils.navigation.CommandBuffer
import ru.aleshin.core.utils.navigation.Router
import ru.aleshin.core.utils.navigation.TabRouter
import ru.aleshin.core.utils.navigation.navigator.NavigatorManager
import ru.aleshin.timeplanner.di.annotation.GlobalNavigation
import ru.aleshin.timeplanner.di.annotation.TabNavigation
import javax.inject.Singleton

/**
 * @author Stanislav Aleshin on 17.02.2023.
 */
@Module
class NavigationModule {

    // Global Navigator

    @GlobalNavigation
    @Provides
    @Singleton
    fun provideGlobalNavigatorManager(
        @GlobalNavigation commandBuffer: CommandBuffer,
    ): NavigatorManager = NavigatorManager.Base(commandBuffer)

    @GlobalNavigation
    @Provides
    @Singleton
    fun provideGlobalCommandBuffer(): CommandBuffer = CommandBuffer.Base()

    @Provides
    @Singleton
    fun provideGlobalRouter(
        @GlobalNavigation commandBuffer: CommandBuffer,
    ): Router = Router.Base(commandBuffer)

    // Tab Navigator

    @TabNavigation
    @Provides
    @Singleton
    fun provideTabNavigatorManager(
        @TabNavigation commandBuffer: CommandBuffer,
    ): NavigatorManager = NavigatorManager.Base(commandBuffer)

    @TabNavigation
    @Provides
    @Singleton
    fun provideTabCommandBuffer(): CommandBuffer = CommandBuffer.Base()

    @Provides
    @Singleton
    fun provideTabRouter(
        @TabNavigation commandBuffer: CommandBuffer,
    ): TabRouter = TabRouter.Base(Router.Base(commandBuffer))
}
