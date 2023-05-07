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

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import hu.ma.charts.legend.data.LegendEntry
import ru.aleshin.core.ui.theme.TimePlannerRes
import kotlin.math.roundToInt

@Composable
internal fun AnalyticsTimeLegend(
    modifier: Modifier = Modifier,
    legendEntries: List<LegendEntry>,
    selectedItem: Int,
    onSelectedItem: (Int) -> Unit,
) {
    val listState = rememberLazyListState()
    LazyColumn(
        modifier = modifier,
        state = listState,
    ) {
        items(6) { index ->
            val entry = legendEntries[index]
            AnalyticsTimeLegendItem(
                isSelected = selectedItem == index,
                categoryName = entry.text.text,
                percent = entry.percent,
                color = entry.shape.color,
                onSelectedItem = { onSelectedItem(index) },
            )
        }
    }
}

@Composable
internal fun AnalyticsTimeLegendItem(
    modifier: Modifier = Modifier,
    isSelected: Boolean,
    categoryName: String,
    percent: Float,
    color: Color,
    onSelectedItem: () -> Unit,
) {
    Surface(
        modifier = modifier.fillMaxWidth().clip(MaterialTheme.shapes.small).clickable() {
            onSelectedItem()
        },
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = when (isSelected) {
            true -> TimePlannerRes.elevations.levelOne
            false -> TimePlannerRes.elevations.levelZero
        },
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier.size(10.dp).clip(RoundedCornerShape(100.dp)).background(color),
            )
            Text(
                modifier = Modifier.weight(1f),
                text = categoryName,
                color = MaterialTheme.colorScheme.onBackground,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                style = MaterialTheme.typography.labelLarge,
            )
            Text(
                text = "${percent.roundToInt()}%",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelMedium,
            )
        }
    }
}
