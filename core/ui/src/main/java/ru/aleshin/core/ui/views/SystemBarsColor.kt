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

package ru.aleshin.core.ui.views

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController

/**
 * @author Stanislav Aleshin on 06.08.2023.
 */
@Composable
fun SystemBarsColor(
    navigationBarColor: Color = MaterialTheme.colorScheme.background,
    statusBarColor: Color = MaterialTheme.colorScheme.background,
    isDarkIcons: Boolean,
) {
    val systemUiController = rememberSystemUiController()

    LaunchedEffect(key1 = navigationBarColor, key2 = statusBarColor, key3 = isDarkIcons) {
        systemUiController.setNavigationBarColor(color = navigationBarColor, darkIcons = !isDarkIcons)
        systemUiController.setStatusBarColor(color = statusBarColor, darkIcons = !isDarkIcons)
    }
}
