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
package ru.aleshin.timeplanner.core.ui.theme.material

import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.ZeroCornerSize
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * @author Stanislav Aleshin on 14.02.2023.
 */
val baseShapes = Shapes()

val Shapes.full: RoundedCornerShape
    get() = RoundedCornerShape(100.dp)

val CornerBasedShape.topSide
    get() = RoundedCornerShape(
        topStart = topStart,
        bottomStart = ZeroCornerSize,
        topEnd = topEnd,
        bottomEnd = ZeroCornerSize
    )

val CornerBasedShape.bottomSide
    get() = RoundedCornerShape(
        topStart = ZeroCornerSize,
        bottomStart = bottomStart,
        topEnd = ZeroCornerSize,
        bottomEnd = bottomEnd
    )

val CornerBasedShape.startSide
    get() = RoundedCornerShape(
        topStart = topStart,
        bottomStart = bottomStart,
        topEnd = ZeroCornerSize,
        bottomEnd = ZeroCornerSize
    )

val CornerBasedShape.endSide
    get() = RoundedCornerShape(
        topStart = ZeroCornerSize,
        bottomStart = ZeroCornerSize,
        topEnd = topEnd,
        bottomEnd = bottomEnd
    )