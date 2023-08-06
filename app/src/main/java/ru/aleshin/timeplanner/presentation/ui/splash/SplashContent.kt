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
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import ru.aleshin.core.ui.theme.TimePlannerRes
import ru.aleshin.core.ui.theme.material.onSplashGradient

/**
 * @author Stanislav Aleshin on 14.02.2023.
 */
@Composable
fun SplashContent(modifier: Modifier = Modifier) {
    var isVisible by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        AnimatedVisibility(visible = isVisible, enter = fadeIn()) {
            Icon(
                modifier = Modifier.size(86.dp),
                painter = painterResource(id = TimePlannerRes.icons.logo),
                contentDescription = TimePlannerRes.strings.appName,
                tint = onSplashGradient,
            )
        }
        Text(
            modifier = Modifier.align(Alignment.BottomCenter).padding(18.dp),
            text = TimePlannerRes.strings.authorTitle,
            style = MaterialTheme.typography.labelSmall,
            color = onSplashGradient,
        )
    }

    SideEffect {
        isVisible = true
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
