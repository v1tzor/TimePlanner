/*
 * Copyright 2026 Stanislav Aleshin
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
package ru.aleshin.timeplanner.widgets.presentation.utils

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp

/**
 * @author Stanislav Aleshin on 28.07.2026.
 */
data class WidgetSizeClass(
    val width: Width,
    val height: Height,
) {
    enum class Width {
        COMPACT,
        MEDIUM,
        EXPANDED,
    }

    enum class Height {
        COMPACT,
        MEDIUM,
        EXPANDED,
    }

    companion object {

        fun fetch(size: DpSize): WidgetSizeClass {
            val width = when {
                size.width < MEDIUM_WIDTH -> Width.COMPACT
                size.width < EXPANDED_WIDTH -> Width.MEDIUM
                else -> Width.EXPANDED
            }
            val height = when {
                size.height < MEDIUM_HEIGHT -> Height.COMPACT
                size.height < EXPANDED_HEIGHT -> Height.MEDIUM
                else -> Height.EXPANDED
            }
            return WidgetSizeClass(width = width, height = height)
        }
    }
}

private val MEDIUM_WIDTH = 180.dp
private val EXPANDED_WIDTH = 280.dp
private val MEDIUM_HEIGHT = 150.dp
private val EXPANDED_HEIGHT = 220.dp
