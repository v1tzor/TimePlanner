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
package ru.aleshin.core.ui.views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.aleshin.core.ui.theme.TimePlannerRes
import ru.aleshin.core.ui.theme.material.surfaceThree
import ru.aleshin.core.utils.functional.TimeFormat

@Composable
fun TimeFormatSelector(
    modifier: Modifier = Modifier,
    isVisible: Boolean,
    format: TimeFormat,
    onChangeFormat: (TimeFormat) -> Unit,
) {
    if (isVisible) {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(0.dp),
        ) {
            TextButton(
                modifier = Modifier.weight(1f),
                onClick = { onChangeFormat(TimeFormat.AM) },
                shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                colors = ButtonDefaults.textButtonColors(
                    containerColor = when (format) {
                        TimeFormat.AM -> MaterialTheme.colorScheme.primaryContainer
                        TimeFormat.PM -> MaterialTheme.colorScheme.surfaceThree()
                    },
                ),
            ) {
                Text(
                    text = TimePlannerRes.strings.amFormatTitle,
                    color = when (format) {
                        TimeFormat.AM -> MaterialTheme.colorScheme.onPrimaryContainer
                        TimeFormat.PM -> MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    style = MaterialTheme.typography.labelMedium,
                )
            }
            TextButton(
                modifier = Modifier.weight(1f),
                onClick = { onChangeFormat(TimeFormat.PM) },
                shape = RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                colors = ButtonDefaults.textButtonColors(
                    containerColor = when (format) {
                        TimeFormat.PM -> MaterialTheme.colorScheme.primaryContainer
                        TimeFormat.AM -> MaterialTheme.colorScheme.surfaceThree()
                    },
                ),
            ) {
                Text(
                    text = TimePlannerRes.strings.pmFormatTitle,
                    color = when (format) {
                        TimeFormat.AM -> MaterialTheme.colorScheme.onSurfaceVariant
                        TimeFormat.PM -> MaterialTheme.colorScheme.onPrimaryContainer
                    },
                    style = MaterialTheme.typography.labelMedium,
                )
            }
        }
    }
}
