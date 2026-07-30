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
package ru.aleshin.features.overview.impl.presentation.ui.overview.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ru.aleshin.core.presentation.mappers.mapToIconPainter
import ru.aleshin.core.presentation.models.tasks.TimeTaskUi
import ru.aleshin.features.overview.impl.presentation.theme.OverviewThemeRes
import ru.aleshin.features.overview.impl.presentation.theme.tokens.fetchOverviewCategoryColors
import ru.aleshin.timeplanner.core.ui.views.CategoryIconMonogram
import ru.aleshin.timeplanner.core.ui.views.CategoryTextMonogram
import java.text.SimpleDateFormat

/**
 * @author Stanislav Aleshin on 30.07.2026.
 */
@Composable
internal fun SelectedDayTaskItem(
    modifier: Modifier = Modifier,
    task: TimeTaskUi,
    onClick: () -> Unit,
) {
    val timeFormat = remember {
        SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT)
    }
    val categoryTitle = task.category.fetchName() ?: OverviewThemeRes.strings.noneTitle
    val subCategoryTitle = task.subCategory?.name?.takeIf { name -> name.isNotBlank() }
    val noteTitle = task.note?.takeIf { note -> note.isNotBlank() }
    val taskTitle = subCategoryTitle ?: categoryTitle
    val taskSubtitle = noteTitle ?: categoryTitle.takeIf { subCategoryTitle != null }
    val categoryColors = fetchOverviewCategoryColors(task.category.id)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .clickable(onClick = onClick)
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.width(48.dp),
            horizontalAlignment = Alignment.End,
        ) {
            Text(
                text = timeFormat.format(task.timeRanges.from),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall,
            )
            Text(
                text = timeFormat.format(task.timeRanges.to),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall,
            )
        }
        Box(
            modifier = Modifier
                .width(18.dp)
                .fillMaxHeight(),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.outlineVariant),
            )
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(categoryColors.accent),
            )
        }
        val categoryIcon = task.category.defaultType?.mapToIconPainter()
        if (categoryIcon != null) {
            CategoryIconMonogram(
                modifier = Modifier.size(36.dp),
                icon = categoryIcon,
                iconSize = 18.dp,
                iconDescription = categoryTitle,
                iconColor = categoryColors.accent,
                backgroundColor = categoryColors.container,
            )
        } else {
            CategoryTextMonogram(
                modifier = Modifier.size(36.dp),
                text = remember(categoryTitle) { categoryTitle.fetchMonogram() },
                textColor = categoryColors.accent,
                backgroundColor = categoryColors.container,
            )
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = taskTitle,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleSmall,
            )
            if (taskSubtitle != null) {
                Text(
                    text = taskSubtitle,
                    color = if (noteTitle != null) {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    } else {
                        categoryColors.accent
                    },
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}

private fun String.fetchMonogram(): String {
    return filter { char -> char.isLetterOrDigit() }.take(2).ifEmpty { "*" }
}
