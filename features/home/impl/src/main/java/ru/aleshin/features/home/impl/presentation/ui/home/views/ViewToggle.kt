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
 * imitations under the License.
 */
package ru.aleshin.features.home.impl.presentation.ui.home.views

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import ru.aleshin.core.ui.theme.TimePlannerRes

/**
 * @author Stanislav Aleshin on 08.04.2023.
 */
@Composable
internal fun ViewToggle(
    modifier: Modifier = Modifier,
    isHideTitle: Boolean = false,
    status: ViewToggleStatus,
    onStatusChange: (ViewToggleStatus) -> Unit,
) {
    val title = when (status) {
        ViewToggleStatus.EXPANDED -> TimePlannerRes.strings.expandedViewToggleTitle
        ViewToggleStatus.COMPACT -> TimePlannerRes.strings.compactViewToggleTitle
    }
    val icon = when (status) {
        ViewToggleStatus.EXPANDED -> TimePlannerRes.icons.expandedViewIcon
        ViewToggleStatus.COMPACT -> TimePlannerRes.icons.compactViewIcon
    }
    TextButton(
        onClick = { onStatusChange(status) },
        modifier = modifier.height(40.dp),
    ) {
        BoxWithConstraints {
            if (maxWidth >= 129.dp && !isHideTitle) {
                Row {
                    Text(
                        text = title,
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.labelMedium,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        painter = painterResource(id = icon),
                        contentDescription = title,
                        tint = MaterialTheme.colorScheme.onBackground,
                    )
                }
            } else {
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(id = icon),
                    contentDescription = title,
                    tint = MaterialTheme.colorScheme.onBackground,
                )
            }
        }
    }
}

internal enum class ViewToggleStatus {
    EXPANDED, COMPACT
}

/* ----------------------- Release Preview -----------------------
@Composable
@Preview(showBackground = true)
private fun ViewToggle_Preview() {
    TimePlannerTheme(themeColorsType = ThemeColorsUiType.LIGHT) {
        Row(Modifier.width(160.dp)) {
            ViewToggle(
                modifier = Modifier,
                status = ViewToggleStatus.EXPANDED,
                onStatusChange = {},
            )
        }
    }
}
*/
