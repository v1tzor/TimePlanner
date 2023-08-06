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
package ru.aleshin.timeplanner.presentation.ui.splash

import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import cafe.adriel.voyager.core.screen.Screen
import ru.aleshin.core.ui.theme.material.splashGradientColors
import ru.aleshin.core.ui.views.SystemBarsColor

/**
 * @author Stanislav Aleshin on 14.02.2023.
 */
class SplashScreen : Screen {

    @Composable
    override fun Content() {
        SplashContent(
            modifier = Modifier.background(
                brush = Brush.verticalGradient(
                    colors = splashGradientColors,
                ),
            ),
        )
        SystemBarsColor(
            statusBarColor = splashGradientColors[0],
            navigationBarColor = splashGradientColors[1],
            isDarkIcons = true,
        )
    }
}
