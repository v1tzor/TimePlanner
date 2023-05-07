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
package ru.aleshin.core.utils.navigation.navigator

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisallowComposableCalls
import androidx.compose.runtime.remember
import cafe.adriel.voyager.navigator.Navigator
import ru.aleshin.core.utils.navigation.CommandBuffer
import ru.aleshin.core.utils.navigation.NavigationProcessor

/**
 * @author Stanislav Aleshin on 14.02.2023.
 */
interface NavigatorManager {

    fun attachNavigator(navigator: Navigator)

    fun detachNavigator()

    class Base(
        private val commandBuffer: CommandBuffer,
        private val navigationProcessor: NavigationProcessor = NavigationProcessor.Base(),
    ) : NavigatorManager {

        private var navigator: Navigator? = null

        override fun attachNavigator(navigator: Navigator) {
            this.navigator = navigator

            commandBuffer.setListener { command ->
                navigationProcessor.navigate(command, checkNotNull(this.navigator))
            }
        }

        override fun detachNavigator() {
            commandBuffer.removeListener()
            navigator = null
        }
    }
}

@Composable
fun <T : NavigatorManager> rememberNavigatorManager(
    factory: @DisallowComposableCalls () -> T,
): NavigatorManager {
    return remember { factory.invoke() }
}
