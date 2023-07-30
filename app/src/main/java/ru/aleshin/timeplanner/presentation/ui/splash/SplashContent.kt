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

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import ru.aleshin.core.utils.functional.Constants
import ru.aleshin.timeplanner.presentation.theme.MainThemeRes

/**
 * @author Stanislav Aleshin on 14.02.2023.
 */
@Composable
fun SplashContent(
    modifier: Modifier = Modifier,
    onSplashExit: () -> Unit,
) {
    Box(modifier = modifier.fillMaxSize()) {
        Image(
            modifier = Modifier.align(Alignment.Center).size(64.dp),
            painter = painterResource(id = MainThemeRes.icons.launcher),
            contentDescription = MainThemeRes.strings.launcherIconDesc,
        )
        Text(
            modifier = Modifier.align(Alignment.BottomCenter).padding(18.dp),
            text = MainThemeRes.strings.authorTitle,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.surfaceVariant,
        )
    }

    LaunchedEffect(key1 = Unit) {
        delay(Constants.Delay.SPLASH)
        onSplashExit.invoke()
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
