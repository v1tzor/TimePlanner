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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ru.aleshin.timeplanner.core.ui.views.PlaceholderBox

/**
 * @author Stanislav Aleshin on 30.07.2026.
 */
@Composable
internal fun TemplatesGridPlaceholder(
    modifier: Modifier = Modifier,
    columns: GridCells,
    contentPadding: PaddingValues,
    horizontalSpacing: Dp,
    showPattern: Boolean,
) {
    LazyVerticalGrid(
        modifier = modifier,
        columns = columns,
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(horizontalSpacing),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        userScrollEnabled = false,
    ) {
        if (showPattern) {
            item(
                key = PATTERN_SECTION_PLACEHOLDER_KEY,
                span = { GridItemSpan(maxLineSpan) },
            ) {
                TemplatesPatternSectionPlaceholder()
            }
        }
        item(
            key = FILTER_HEADER_PLACEHOLDER_KEY,
            span = { GridItemSpan(maxLineSpan) },
        ) {
            TemplatesFilterHeaderPlaceholder()
        }
        item(
            key = GROUP_HEADER_PLACEHOLDER_KEY,
            span = { GridItemSpan(maxLineSpan) },
        ) {
            TemplatesGroupHeaderPlaceholder()
        }
        items(
            count = TEMPLATE_PLACEHOLDER_COUNT,
            key = { index -> "$TEMPLATE_PLACEHOLDER_KEY_PREFIX$index" },
        ) {
            PlaceholderBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp),
                shape = MaterialTheme.shapes.large,
            )
        }
    }
}

@Composable
private fun TemplatesPatternSectionPlaceholder(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            PlaceholderBox(
                modifier = Modifier
                    .weight(1f)
                    .height(28.dp),
                shape = MaterialTheme.shapes.small,
            )
            PlaceholderBox(
                modifier = Modifier.size(width = 128.dp, height = 40.dp),
                shape = CircleShape,
            )
        }
        PlaceholderBox(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            shape = MaterialTheme.shapes.large,
        )
    }
}

@Composable
private fun TemplatesFilterHeaderPlaceholder(
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(40.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PlaceholderBox(
            modifier = Modifier
                .width(120.dp)
                .height(28.dp),
            shape = MaterialTheme.shapes.small,
        )
        Spacer(modifier = Modifier.weight(1f))
        PlaceholderBox(
            modifier = Modifier
                .width(112.dp)
                .height(32.dp),
            shape = MaterialTheme.shapes.small,
        )
    }
}

@Composable
private fun TemplatesGroupHeaderPlaceholder(
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(28.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PlaceholderBox(
            modifier = Modifier.size(width = 5.dp, height = 28.dp),
            shape = CircleShape,
        )
        PlaceholderBox(
            modifier = Modifier
                .weight(1f)
                .height(24.dp),
            shape = MaterialTheme.shapes.small,
        )
        PlaceholderBox(
            modifier = Modifier.size(24.dp),
            shape = CircleShape,
        )
    }
}

private const val TEMPLATE_PLACEHOLDER_COUNT = 4
private const val TEMPLATE_PLACEHOLDER_KEY_PREFIX = "template_placeholder_"
private const val PATTERN_SECTION_PLACEHOLDER_KEY = "pattern_section_placeholder"
private const val FILTER_HEADER_PLACEHOLDER_KEY = "filter_header_placeholder"
private const val GROUP_HEADER_PLACEHOLDER_KEY = "group_header_placeholder"
