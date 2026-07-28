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
package ru.aleshin.features.templates.impl.presentation.ui.templates

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import ru.aleshin.core.presentation.models.categories.MainCategoryDetailsUi
import ru.aleshin.features.templates.impl.domain.entities.templates.TemplatesSortedType
import ru.aleshin.features.templates.impl.presentation.mapppers.mapToString
import ru.aleshin.features.templates.impl.presentation.models.TemplatesDataUi
import ru.aleshin.features.templates.impl.presentation.theme.TemplatesThemeRes
import ru.aleshin.features.templates.impl.presentation.theme.tokens.TemplatesLayoutDefaults
import ru.aleshin.features.templates.impl.presentation.ui.templates.contract.TemplatesEvent
import ru.aleshin.features.templates.impl.presentation.ui.templates.contract.TemplatesState
import ru.aleshin.features.templates.impl.presentation.ui.templates.views.EmptyDateView
import ru.aleshin.features.templates.impl.presentation.ui.templates.views.TemplatesCreateFab
import ru.aleshin.features.templates.impl.presentation.ui.templates.views.TemplatesItem
import ru.aleshin.features.templates.impl.presentation.ui.templates.views.TemplatesPatternSection
import ru.aleshin.timeplanner.core.ui.theme.tokens.AdaptiveLayoutDefaults
import ru.aleshin.timeplanner.core.ui.views.ExpandedIcon
import ru.aleshin.timeplanner.core.ui.views.PlaceholderBox

/**
 * @author Stanislav Aleshin on 28.07.2026.
 */
@Composable
internal fun TemplatesCompactGrid(
    modifier: Modifier = Modifier,
    state: TemplatesState,
    templatesData: TemplatesDataUi,
    onEvent: (TemplatesEvent) -> Unit,
) {
    TemplatesGrid(
        modifier = modifier,
        columns = GridCells.Fixed(COMPACT_COLUMN_COUNT),
        contentPadding = TemplatesLayoutDefaults.CompactContentPadding,
        horizontalSpacing = TemplatesLayoutDefaults.CompactGridHorizontalSpacing,
        state = state,
        templatesData = templatesData,
        showPattern = true,
        onEvent = onEvent,
    )
}

@Composable
internal fun TemplatesMainPane(
    modifier: Modifier = Modifier,
    state: TemplatesState,
    templatesData: TemplatesDataUi,
    onCreateTemplate: () -> Unit,
    onEvent: (TemplatesEvent) -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(
                WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom),
            ),
    ) {
        TemplatesGrid(
            modifier = Modifier.fillMaxSize(),
            columns = GridCells.Adaptive(TemplatesLayoutDefaults.TemplateCardMinWidth),
            contentPadding = TemplatesLayoutDefaults.MainPaneContentPadding,
            horizontalSpacing = TemplatesLayoutDefaults.AdaptiveGridSpacing,
            state = state,
            templatesData = templatesData,
            showPattern = false,
            onEvent = onEvent,
        )
        TemplatesCreateFab(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(AdaptiveLayoutDefaults.SpaceLarge),
            onClick = onCreateTemplate,
        )
    }
}

@Composable
internal fun TemplatesCompactGridPlaceholder(
    modifier: Modifier = Modifier,
) {
    TemplatesGridPlaceholder(
        modifier = modifier,
        columns = GridCells.Fixed(COMPACT_COLUMN_COUNT),
        contentPadding = TemplatesLayoutDefaults.CompactContentPadding,
        horizontalSpacing = TemplatesLayoutDefaults.CompactGridHorizontalSpacing,
        showPattern = true,
    )
}

@Composable
internal fun TemplatesMainPanePlaceholder(
    modifier: Modifier = Modifier,
    onCreateTemplate: () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(
                WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom),
            ),
    ) {
        TemplatesGridPlaceholder(
            modifier = Modifier.fillMaxSize(),
            columns = GridCells.Adaptive(TemplatesLayoutDefaults.TemplateCardMinWidth),
            contentPadding = TemplatesLayoutDefaults.MainPaneContentPadding,
            horizontalSpacing = TemplatesLayoutDefaults.AdaptiveGridSpacing,
            showPattern = false,
        )
        TemplatesCreateFab(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(AdaptiveLayoutDefaults.SpaceLarge),
            onClick = onCreateTemplate,
        )
    }
}

@Composable
private fun TemplatesGrid(
    modifier: Modifier = Modifier,
    columns: GridCells,
    contentPadding: PaddingValues,
    horizontalSpacing: Dp,
    state: TemplatesState,
    templatesData: TemplatesDataUi,
    showPattern: Boolean,
    onEvent: (TemplatesEvent) -> Unit,
) {
    LazyVerticalGrid(
        columns = columns,
        modifier = modifier,
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(horizontalSpacing),
        verticalArrangement = Arrangement.spacedBy(TemplatesLayoutDefaults.GridVerticalSpacing),
    ) {
        if (showPattern) {
            item(
                key = TEMPLATES_PATTERN_KEY,
                span = { GridItemSpan(maxLineSpan) },
            ) {
                TemplatesPatternSection(
                    patternFilter = state.patternFilter,
                    patternView = state.patternView,
                    weekPattern = templatesData.weekPattern,
                    monthPattern = templatesData.monthPattern,
                    onChangePatternFilter = { filter ->
                        onEvent(TemplatesEvent.UpdatedPatternFilter(filter))
                    },
                    onChangePatternView = { view ->
                        onEvent(TemplatesEvent.UpdatedPatternView(view))
                    },
                )
            }
        }
        item(
            key = TEMPLATES_HEADER_KEY,
            span = { GridItemSpan(maxLineSpan) },
        ) {
            TemplatesFiltersHeader(
                sortedType = state.sortedType,
                onChangeSortedType = { type ->
                    onEvent(TemplatesEvent.UpdatedSortedType(type))
                },
            )
        }
        TemplatesFeedItems(
            templatesData = templatesData,
            categories = state.categories,
            onEvent = onEvent,
        )
    }
}

@Composable
private fun TemplatesGridPlaceholder(
    modifier: Modifier = Modifier,
    columns: GridCells,
    contentPadding: PaddingValues,
    horizontalSpacing: Dp,
    showPattern: Boolean,
) {
    LazyVerticalGrid(
        columns = columns,
        modifier = modifier,
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(horizontalSpacing),
        verticalArrangement = Arrangement.spacedBy(TemplatesLayoutDefaults.GridVerticalSpacing),
        userScrollEnabled = false,
    ) {
        if (showPattern) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                TemplatesPatternPlaceholder()
            }
        }
        item(span = { GridItemSpan(maxLineSpan) }) {
            TemplatesFiltersPlaceholder()
        }
        item(span = { GridItemSpan(maxLineSpan) }) {
            TemplatesGroupHeaderPlaceholder()
        }
        items(
            count = TEMPLATE_PLACEHOLDER_COUNT,
            key = { index -> "TemplatePlaceholder$index" },
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
private fun TemplatesPatternPlaceholder(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(TemplatesLayoutDefaults.GridVerticalSpacing),
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
private fun TemplatesFiltersPlaceholder(
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

private fun LazyGridScope.TemplatesFeedItems(
    templatesData: TemplatesDataUi,
    categories: List<MainCategoryDetailsUi>,
    onEvent: (TemplatesEvent) -> Unit,
) {
    if (templatesData.activeTemplates.isNotEmpty()) {
        item(
            key = ACTIVE_TEMPLATES_HEADER_KEY,
            span = { GridItemSpan(maxLineSpan) },
        ) {
            TemplatesGroupHeader(
                title = TemplatesThemeRes.strings.activeTemplatesTitle,
                count = templatesData.activeTemplatesCount,
                isActive = true,
            )
        }
        items(
            items = templatesData.activeTemplates,
            key = { template -> "ActiveTemplate${template.templateId}" },
        ) { template ->
            TemplatesItem(
                modifier = Modifier.animateItem(),
                model = template,
                categories = categories,
                onUpdate = { updated ->
                    onEvent(TemplatesEvent.UpdateTemplate(template, updated))
                },
                onRestartRepeat = {
                    onEvent(TemplatesEvent.RestartTemplateRepeat(template))
                },
                onStopRepeat = {
                    onEvent(TemplatesEvent.StopTemplateRepeat(template))
                },
                onAddRepeat = { repeatTime ->
                    onEvent(TemplatesEvent.AddRepeatTemplate(repeatTime, template))
                },
                onDeleteRepeat = { repeatTime ->
                    onEvent(TemplatesEvent.DeleteRepeatTemplate(repeatTime, template))
                },
                onDeleteTemplate = {
                    onEvent(TemplatesEvent.DeleteTemplate(template))
                },
            )
        }
    }
    if (templatesData.inactiveTemplates.isNotEmpty()) {
        item(
            key = INACTIVE_TEMPLATES_HEADER_KEY,
            span = { GridItemSpan(maxLineSpan) },
        ) {
            TemplatesGroupHeader(
                title = TemplatesThemeRes.strings.inactiveTemplatesTitle,
                count = templatesData.inactiveTemplatesCount,
                isActive = false,
            )
        }
        items(
            items = templatesData.inactiveTemplates,
            key = { template -> "InactiveTemplate${template.templateId}" },
        ) { template ->
            TemplatesItem(
                modifier = Modifier.animateItem(),
                model = template,
                categories = categories,
                onUpdate = { updated ->
                    onEvent(TemplatesEvent.UpdateTemplate(template, updated))
                },
                onRestartRepeat = {
                    onEvent(TemplatesEvent.RestartTemplateRepeat(template))
                },
                onStopRepeat = {
                    onEvent(TemplatesEvent.StopTemplateRepeat(template))
                },
                onAddRepeat = { repeatTime ->
                    onEvent(TemplatesEvent.AddRepeatTemplate(repeatTime, template))
                },
                onDeleteRepeat = { repeatTime ->
                    onEvent(TemplatesEvent.DeleteRepeatTemplate(repeatTime, template))
                },
                onDeleteTemplate = {
                    onEvent(TemplatesEvent.DeleteTemplate(template))
                },
            )
        }
    }
    if (templatesData.activeTemplates.isEmpty() && templatesData.inactiveTemplates.isEmpty()) {
        item(
            key = EMPTY_TEMPLATES_KEY,
            span = { GridItemSpan(maxLineSpan) },
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentAlignment = Alignment.Center,
            ) {
                EmptyDateView(
                    emptyTitle = TemplatesThemeRes.strings.emptyListTitle,
                )
            }
        }
    }
}

@Composable
private fun TemplatesGroupHeader(
    modifier: Modifier = Modifier,
    title: String,
    count: Int,
    isActive: Boolean,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Surface(
            modifier = Modifier.size(width = 5.dp, height = 28.dp),
            shape = CircleShape,
            color = when (isActive) {
                true -> MaterialTheme.colorScheme.primary
                false -> MaterialTheme.colorScheme.outline
            },
            content = {},
        )
        Text(
            modifier = Modifier.weight(1f),
            text = title,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleMedium,
        )
        Surface(
            modifier = Modifier.size(24.dp),
            shape = CircleShape,
            color = when (isActive) {
                true -> MaterialTheme.colorScheme.primaryContainer
                false -> MaterialTheme.colorScheme.surfaceContainerHigh
            },
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = count.toString(),
                    color = when (isActive) {
                        true -> MaterialTheme.colorScheme.onPrimaryContainer
                        false -> MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    style = MaterialTheme.typography.labelMedium,
                )
            }
        }
    }
}

@Composable
private fun TemplatesFiltersHeader(
    modifier: Modifier = Modifier,
    sortedType: TemplatesSortedType,
    onChangeSortedType: (TemplatesSortedType) -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = TemplatesThemeRes.strings.topAppBarTemplatesTitle,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleLarge,
        )
        Box(contentAlignment = Alignment.CenterEnd) {
            var isExpanded by rememberSaveable { mutableStateOf(false) }
            Surface(
                onClick = { isExpanded = true },
                shape = MaterialTheme.shapes.small,
                color = Color.Transparent,
            ) {
                Row(
                    modifier = Modifier
                        .animateContentSize()
                        .padding(horizontal = 6.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = sortedType.mapToString(),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Medium,
                        ),
                    )
                    Box(
                        modifier = Modifier.size(18.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        ExpandedIcon(isExpanded = isExpanded)
                    }
                }
            }
            TemplatesSortedTypeMenu(
                modifier = Modifier.align(Alignment.TopEnd),
                isExpanded = isExpanded,
                onDismiss = { isExpanded = false },
                onSelected = { type ->
                    isExpanded = false
                    onChangeSortedType(type)
                },
            )
        }
    }
}

@Composable
private fun TemplatesSortedTypeMenu(
    modifier: Modifier = Modifier,
    isExpanded: Boolean,
    onDismiss: () -> Unit,
    onSelected: (TemplatesSortedType) -> Unit,
) {
    DropdownMenu(
        expanded = isExpanded,
        onDismissRequest = onDismiss,
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        offset = DpOffset(0.dp, 2.dp),
    ) {
        TemplatesSortedType.entries.forEach { type ->
            key(type) {
                DropdownMenuItem(
                    onClick = { onSelected(type) },
                    text = {
                        Text(
                            text = type.mapToString(),
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.titleMedium,
                        )
                    },
                )
            }
        }
    }
}

private const val COMPACT_COLUMN_COUNT = 2
private const val TEMPLATE_PLACEHOLDER_COUNT = 4
private const val TEMPLATES_PATTERN_KEY = "TemplatesPattern"
private const val TEMPLATES_HEADER_KEY = "TemplatesHeader"
private const val ACTIVE_TEMPLATES_HEADER_KEY = "ActiveTemplatesHeader"
private const val INACTIVE_TEMPLATES_HEADER_KEY = "InactiveTemplatesHeader"
private const val EMPTY_TEMPLATES_KEY = "EmptyTemplates"
