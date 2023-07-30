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

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import ru.aleshin.core.ui.theme.TimePlannerRes

/**
 * @author Stanislav Aleshin on 25.02.2023.
 */
@Composable
fun ExpandedIcon(
    modifier: Modifier = Modifier,
    isExpanded: Boolean,
    color: Color = MaterialTheme.colorScheme.onSurface,
    description: String? = null,
) {
    val icon = when (isExpanded) {
        true -> painterResource(TimePlannerRes.icons.arrowUp)
        false -> painterResource(TimePlannerRes.icons.arrowDown)
    }
    Box(modifier = modifier.animateContentSize()) {
        Icon(
            painter = icon,
            contentDescription = description,
            tint = color,
        )
    }
}
