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
package ru.aleshin.features.analytics.impl.presentation.ui.category.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ru.aleshin.core.domain.entities.categories.DefaultCategoryType
import ru.aleshin.core.presentation.mappers.mapToIcon
import ru.aleshin.core.utils.charts.CategoryColorsDefaults
import ru.aleshin.features.analytics.impl.presentation.theme.AnalyticsThemeRes
import ru.aleshin.timeplanner.core.ui.theme.TimePlannerRes
import ru.aleshin.timeplanner.core.ui.views.CategoryIconMonogram
import ru.aleshin.timeplanner.core.ui.views.CategoryTextMonogram

/**
 * @author Stanislav Aleshin on 23.07.2026.
 */
@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun CategoryTopAppBar(
    title: String,
    categoryId: Long?,
    defaultType: DefaultCategoryType?,
    isLoading: Boolean,
    onBack: () -> Unit,
) {
    val strings = AnalyticsThemeRes.strings
    val categoryColor = categoryId
        ?.let(CategoryColorsDefaults::fetchColor)
        ?: MaterialTheme.colorScheme.primary

    CenterAlignedTopAppBar(
        title = {
            if (isLoading && title.isEmpty()) {
                Box(
                    modifier = Modifier
                        .width(120.dp)
                        .height(20.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surfaceContainerHighest,
                            shape = RoundedCornerShape(8.dp),
                        ),
                )
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    when {
                        defaultType != null -> CategoryIconMonogram(
                            modifier = Modifier.size(32.dp),
                            icon = painterResource(
                                id = defaultType.mapToIcon(icons = TimePlannerRes.icons),
                            ),
                            iconSize = 20.dp,
                            iconDescription = null,
                            iconColor = categoryColor,
                            backgroundColor = categoryColor.copy(alpha = 0.16f),
                        )
                        title.isNotEmpty() -> CategoryTextMonogram(
                            modifier = Modifier.size(32.dp),
                            text = title.take(n = 1),
                            textColor = categoryColor,
                            backgroundColor = categoryColor.copy(alpha = 0.16f),
                        )
                    }
                    if (title.isNotEmpty()) {
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(
                        text = title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
            }
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = strings.navigateBackDesc,
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
        ),
    )
}
