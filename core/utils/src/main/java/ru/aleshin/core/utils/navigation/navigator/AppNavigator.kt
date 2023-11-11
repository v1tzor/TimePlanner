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
import androidx.compose.runtime.DisposableEffect
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.NavigatorContent
import cafe.adriel.voyager.navigator.NavigatorDisposeBehavior
import cafe.adriel.voyager.navigator.OnBackPressed

/**
 * @author Stanislav Aleshin on 19.02.2023.
 */
@Composable
fun AppNavigator(
    initialScreen: Screen = EmptyScreen,
    navigatorManager: NavigatorManager,
    onBackPressed: OnBackPressed = { true },
    content: NavigatorContent = { CurrentScreen() },
) {
    Navigator(
        screen = initialScreen,
        onBackPressed = onBackPressed,
        disposeBehavior = NavigatorDisposeBehavior(disposeNestedNavigators = false),
    ) { navigator ->
        DisposableEffect(Unit) {
            navigatorManager.attachNavigator(navigator)
            onDispose { navigatorManager.detachNavigator() }
        }
        content.invoke(navigator)
    }
}
