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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import ru.aleshin.core.ui.theme.TimePlannerRes
import ru.aleshin.core.ui.theme.material.onSplashGradient
import ru.aleshin.core.utils.functional.Constants

/**
 * @author Stanislav Aleshin on 14.02.2023.
 */
@Composable
fun SplashContent(
    modifier: Modifier = Modifier,
) {
    var isVisibleLogo by remember { mutableStateOf(false) }
    var isVisibleText by remember { mutableStateOf(false) }

    Box(
        modifier = modifier.fillMaxSize(), 
        contentAlignment = Alignment.Center,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AnimatedVisibility(visible = isVisibleLogo, enter = fadeIn()) {
                Icon(
                    modifier = Modifier.size(86.dp),
                    painter = painterResource(id = TimePlannerRes.icons.splashIcon),
                    contentDescription = TimePlannerRes.strings.appName,
                    tint = onSplashGradient,
                )
            }
            AnimatedVisibility(visible = isVisibleText, enter = expandHorizontally()) {
                Text(
                    text = Constants.App.SPLASH_NAME,
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.Black,
                        lineHeight = 35.sp,
                    ),
                    color = onSplashGradient,
                )
            }
        }
    }

    LaunchedEffect(Unit) {
        delay(Constants.Delay.SPLASH_LOGO)
        isVisibleLogo = true
        delay(Constants.Delay.SPLASH_TEXT)
        isVisibleText = true
    }
}

/* ----------------------- Release Preview -----------------------
@Composable
@Preview(showBackground = true, showSystemUi = true)
fun SplashContent_Preview() {
    TimePlannerTheme {
        SplashContent(onSplashExit = {})
    }
}*/
