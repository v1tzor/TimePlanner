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
package ru.aleshin.features.analytics.impl.presenatiton.ui.views

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import ru.aleshin.core.ui.views.ExpandedIcon
import ru.aleshin.core.utils.functional.TimePeriod
import ru.aleshin.features.analytics.impl.presenatiton.mappers.mapToString

@Composable
internal fun TimeSelectorSection(
    modifier: Modifier = Modifier,
    timePeriod: TimePeriod?,
    title: String,
    onTimePeriodChanged: (TimePeriod) -> Unit,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.labelLarge,
        )
        Spacer(Modifier.weight(1f))
        TimeSelector(
            timePeriod = timePeriod,
            onTimeSelected = onTimePeriodChanged,
        )
    }
}

@Composable
internal fun TimeSelector(
    modifier: Modifier = Modifier,
    timePeriod: TimePeriod?,
    onTimeSelected: (TimePeriod) -> Unit,
) {
    var isExpanded by remember { mutableStateOf(false) }
    Box {
        Surface(
            onClick = { isExpanded = true },
            modifier = modifier.height(32.dp),
            shape = MaterialTheme.shapes.small,
            color = MaterialTheme.colorScheme.background,
        ) {
            Row(
                modifier = Modifier.animateContentSize().padding(top = 6.dp, bottom = 6.dp, start = 16.dp, end = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = timePeriod?.mapToString() ?: "",
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleSmall,
                )
                ExpandedIcon(isExpanded = isExpanded, color = MaterialTheme.colorScheme.onSurface)
            }
        }
        TimePeriodMenu(
            isExpanded = isExpanded,
            enabled = timePeriod != null,
            currentPeriod = timePeriod,
            onDismiss = { isExpanded = false },
            onTimeSelected = { period ->
                onTimeSelected(period)
                isExpanded = false
            },
        )
    }
}

@Composable
internal fun TimePeriodMenu(
    modifier: Modifier = Modifier,
    isExpanded: Boolean,
    enabled: Boolean = true,
    currentPeriod: TimePeriod?,
    onDismiss: () -> Unit,
    onTimeSelected: (TimePeriod) -> Unit,
) {
    DropdownMenu(
        expanded = isExpanded,
        onDismissRequest = onDismiss,
        modifier = modifier,
        offset = DpOffset(0.dp, 6.dp),
    ) {
        val items = TimePeriod.values()
        items.forEach { period ->
            DropdownMenuItem(
                enabled = enabled,
                text = {
                    Text(
                        text = period.mapToString(),
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleMedium,
                    )
                },
                trailingIcon = {
                    if (period == currentPeriod) {
                        Icon(
                            modifier = Modifier.size(18.dp),
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                },
                onClick = { onTimeSelected(period) },
            )
        }
    }
}
