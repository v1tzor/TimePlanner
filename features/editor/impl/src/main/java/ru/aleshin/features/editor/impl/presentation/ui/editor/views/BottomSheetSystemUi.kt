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
package ru.aleshin.features.editor.impl.presentation.ui.editor.views

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import ru.aleshin.core.ui.theme.material.surfaceThree

/**
 * @author Stanislav Aleshin on 04.11.2023.
 */
@Composable
internal fun BottomSheetSystemUi(
    isShow: Boolean,
    containerColor: Color = MaterialTheme.colorScheme.surfaceThree(),
    backgroundColor: Color = MaterialTheme.colorScheme.background,
) {
    val systemUiController = rememberSystemUiController()

    DisposableEffect(key1 = isShow) {
        val color = if (isShow) containerColor else backgroundColor
        systemUiController.setNavigationBarColor(color = color)
        onDispose { systemUiController.setNavigationBarColor(color = backgroundColor) }
    }
}
