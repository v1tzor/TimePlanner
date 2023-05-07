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
package ru.aleshin.core.utils.navigation

import cafe.adriel.voyager.core.screen.Screen

/**
 * @author Stanislav Aleshin on 14.02.2023.
 */
interface Router {

    fun navigateTo(screen: Screen)

    fun replaceTo(screen: Screen, isAll: Boolean = false)

    fun navigateBack()

    abstract class Abstract constructor(private val commandBuffer: CommandBuffer) : Router {

        override fun navigateTo(screen: Screen) {
            commandBuffer.sendCommand(Command.Forward(screen))
        }

        override fun replaceTo(screen: Screen, isAll: Boolean) {
            val command = if (isAll) Command.ReplaceAll(screen) else Command.Replace(screen)
            commandBuffer.sendCommand(command)
        }

        override fun navigateBack() {
            commandBuffer.sendCommand(Command.Back)
        }
    }

    class Base constructor(commandBuffer: CommandBuffer) : Router, Abstract(commandBuffer)
}

interface TabRouter {

    fun showTab(screen: Screen)

    class Base constructor(private val router: Router) : TabRouter {
        override fun showTab(screen: Screen) {
            router.replaceTo(screen = screen, isAll = true)
        }
    }
}
