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
package ru.aleshin.features.home.impl.presentation.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ru.aleshin.core.ui.theme.TimePlannerRes
import ru.aleshin.core.ui.theme.material.surfaceThree
import ru.aleshin.core.ui.theme.material.surfaceTwo
import ru.aleshin.core.utils.extensions.isCurrentDay
import ru.aleshin.features.home.api.domain.entities.schedules.DailyScheduleStatus
import ru.aleshin.features.home.impl.presentation.models.schedules.ScheduleUi
import ru.aleshin.features.home.impl.presentation.theme.HomeThemeRes
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * @author Stanislav Aleshin on 06.11.2023.
 */
@Composable
internal fun OverviewScheduleItem(
    modifier: Modifier = Modifier,
    model: ScheduleUi,
    onClick: () -> Unit,
) {
    val dateFormat = SimpleDateFormat("EEE, MMM d", Locale.getDefault())
    Surface(
        onClick = onClick,
        modifier = modifier.height(125.dp),
        shape = MaterialTheme.shapes.large,
        color = when (model.date.isCurrentDay()) {
            true -> MaterialTheme.colorScheme.surfaceThree()
            false -> MaterialTheme.colorScheme.surfaceTwo()
        },
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                CircularProgressIndicator(
                    progress = when (model.dateStatus) {
                        DailyScheduleStatus.REALIZED -> model.progress
                        DailyScheduleStatus.ACCOMPLISHMENT -> model.progress
                        DailyScheduleStatus.PLANNED -> 0f
                    },
                    color = when (model.dateStatus) {
                        DailyScheduleStatus.REALIZED -> MaterialTheme.colorScheme.secondary
                        else -> MaterialTheme.colorScheme.primary
                    },
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
                Spacer(modifier = Modifier.weight(1f))
                Column {
                    Text(
                        text = when (model.dateStatus) {
                            DailyScheduleStatus.REALIZED -> HomeThemeRes.strings.realizedScheduleTitle
                            DailyScheduleStatus.ACCOMPLISHMENT -> HomeThemeRes.strings.accomplishmentScheduleTitle
                            DailyScheduleStatus.PLANNED -> HomeThemeRes.strings.plannedScheduleTitle
                        },
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.labelMedium,
                    )
                    Text(
                        text = dateFormat.format(model.date),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                ShortInfoView(
                    modifier = Modifier.weight(1f),
                    enabled = model.dateStatus != DailyScheduleStatus.PLANNED,
                    text = model.timeTasks.count { !it.isCompleted }.toString(),
                    icon = painterResource(id = HomeThemeRes.icons.unexecutedTask),
                )
                ShortInfoView(
                    modifier = Modifier.weight(1f),
                    enabled = model.dateStatus != DailyScheduleStatus.PLANNED,
                    text = model.timeTasks.count { it.progress == 1f && it.isCompleted }.toString(),
                    icon = painterResource(id = HomeThemeRes.icons.completedTask),
                )
                val plannedTimeTasks = model.timeTasks.count { it.progress < 1f }
                ShortInfoView(
                    modifier = Modifier.weight(1f),
                    enabled = model.dateStatus != DailyScheduleStatus.REALIZED,
                    text = plannedTimeTasks.toString(),
                    icon = painterResource(id = TimePlannerRes.icons.plannedTask),
                    color = if (plannedTimeTasks == 0) {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    } else {
                        MaterialTheme.colorScheme.primary
                    },
                )
            }
        }
    }
}

@Composable
internal fun ShortInfoView(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: String,
    icon: Painter,
    iconDescriptor: String? = null,
    color: Color = MaterialTheme.colorScheme.onSurfaceVariant,
) {
    Row(
        modifier = modifier.alpha(if (enabled) 1f else 0.6f),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            modifier = Modifier.size(18.dp),
            painter = icon,
            contentDescription = iconDescriptor,
            tint = color,
        )
        Text(
            text = text,
            color = color,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.labelMedium,
        )
    }
}
