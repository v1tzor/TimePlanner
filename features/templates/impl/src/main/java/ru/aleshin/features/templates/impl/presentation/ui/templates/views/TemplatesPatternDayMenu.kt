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
package ru.aleshin.features.templates.impl.presentation.ui.templates.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import ru.aleshin.core.presentation.mappers.mapToIconPainter
import ru.aleshin.core.presentation.models.templates.TemplateUi
import ru.aleshin.features.templates.impl.presentation.models.TemplatePatternDayUi
import ru.aleshin.features.templates.impl.presentation.theme.TemplatesThemeRes
import ru.aleshin.features.templates.impl.presentation.theme.tokens.fetchTemplatesCategoryColors
import ru.aleshin.timeplanner.core.ui.views.CategoryIconMonogram
import ru.aleshin.timeplanner.core.ui.views.CategoryTextMonogram
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * @author Stanislav Aleshin on 28.07.2026.
 */
@Composable
internal fun TemplatesPatternDayMenu(
    modifier: Modifier = Modifier,
    day: TemplatePatternDayUi,
    isExpanded: Boolean,
    onDismiss: () -> Unit,
) {
    val dateFormat = remember { SimpleDateFormat("EEEE, d MMMM", Locale.getDefault()) }

    DropdownMenu(
        expanded = isExpanded,
        onDismissRequest = onDismiss,
        modifier = modifier.sizeIn(minWidth = 252.dp, maxHeight = 280.dp),
        shape = MaterialTheme.shapes.large,
        offset = DpOffset(0.dp, 4.dp),
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            text = dateFormat.format(day.date),
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.labelLarge,
        )
        HorizontalDivider()
        day.templates.forEach { template ->
            DropdownMenuItem(
                onClick = onDismiss,
                text = {
                    TemplatesPatternMenuItem(template = template)
                },
            )
        }
    }
}

@Composable
private fun TemplatesPatternMenuItem(
    modifier: Modifier = Modifier,
    template: TemplateUi,
) {
    val timeFormat = remember { SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT) }
    val categoryTitle = template.category.fetchName() ?: TemplatesThemeRes.strings.subCategoryEmptyTitle
    val subCategoryTitle = template.subCategory?.name?.takeIf { title -> title.isNotBlank() }
    val title = subCategoryTitle ?: categoryTitle
    val subtitle = categoryTitle.takeIf { subCategoryTitle != null }
    val categoryIcon = template.category.defaultType?.mapToIconPainter()
    val colors = fetchTemplatesCategoryColors(template.category.id)

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (categoryIcon != null) {
            CategoryIconMonogram(
                modifier = Modifier.size(34.dp),
                icon = categoryIcon,
                iconSize = 18.dp,
                iconDescription = categoryTitle,
                iconColor = colors.accent,
                backgroundColor = colors.container,
            )
        } else {
            CategoryTextMonogram(
                modifier = Modifier.size(34.dp),
                text = title.fetchMonogram(),
                textColor = colors.accent,
                backgroundColor = colors.container,
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.labelLarge,
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.labelSmall,
                )
            }
            Text(
                text = "${timeFormat.format(template.startTime)}–${timeFormat.format(template.endTime)}",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelSmall,
            )
        }
    }
}

private fun String.fetchMonogram(): String {
    return filter { char -> char.isLetterOrDigit() }.take(2).ifEmpty { "*" }
}
