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

import cafe.adriel.voyager.navigator.Navigator

/**
 * @author Stanislav Aleshin on 14.02.2023.
 */
interface NavigationProcessor {

    fun navigate(command: Command, navigator: Navigator)

    class Base : NavigationProcessor {
        override fun navigate(command: Command, navigator: Navigator) {
            with(navigator) {
                when (command) {
                    is Command.Forward -> push(command.screen)
                    is Command.Replace -> replace(command.screen)
                    is Command.ReplaceAll -> replaceAll(command.screen)
                    is Command.Back -> pop()
                }
            }
        }
    }
}
