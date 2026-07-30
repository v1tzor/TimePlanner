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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ru.aleshin.core.presentation.models.categories.MainCategoryDetailsUi
import ru.aleshin.features.templates.impl.presentation.models.TemplatesDataUi
import ru.aleshin.features.templates.impl.presentation.theme.TemplatesThemeRes
import ru.aleshin.features.templates.impl.presentation.ui.templates.contract.TemplatesEvent
import ru.aleshin.features.templates.impl.presentation.ui.templates.contract.TemplatesState
import ru.aleshin.features.templates.impl.presentation.ui.templates.views.TemplatesEmptyState
import ru.aleshin.features.templates.impl.presentation.ui.templates.views.TemplatesFilterHeader
import ru.aleshin.features.templates.impl.presentation.ui.templates.views.TemplatesGroupHeader
import ru.aleshin.features.templates.impl.presentation.ui.templates.views.TemplatesItem
import ru.aleshin.features.templates.impl.presentation.ui.templates.views.sections.TemplatesPatternSection

/**
 * @author Stanislav Aleshin on 28.07.2026.
 */
@Composable
internal fun TemplatesGrid(
    modifier: Modifier = Modifier,
    state: TemplatesState,
    templatesData: TemplatesDataUi,
    columns: GridCells,
    contentPadding: PaddingValues,
    horizontalSpacing: Dp,
    showPattern: Boolean,
    onEvent: (TemplatesEvent) -> Unit,
) {
    LazyVerticalGrid(
        modifier = modifier,
        columns = columns,
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(horizontalSpacing),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (showPattern) {
            item(
                key = PATTERN_SECTION_KEY,
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
            key = FILTER_HEADER_KEY,
            span = { GridItemSpan(maxLineSpan) },
        ) {
            TemplatesFilterHeader(
                sortedType = state.sortedType,
                onChangeSortedType = { type ->
                    onEvent(TemplatesEvent.UpdatedSortedType(type))
                },
            )
        }
        templatesFeedItems(
            templatesData = templatesData,
            categories = state.categories,
            onEvent = onEvent,
        )
    }
}

private fun LazyGridScope.templatesFeedItems(
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
                modifier = Modifier.animateItem(),
                title = TemplatesThemeRes.strings.activeTemplatesTitle,
                count = templatesData.activeTemplatesCount,
                isActive = true,
            )
        }
        items(
            items = templatesData.activeTemplates,
            key = { template -> template.templateId },
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
                modifier = Modifier.animateItem(),
                title = TemplatesThemeRes.strings.inactiveTemplatesTitle,
                count = templatesData.inactiveTemplatesCount,
                isActive = false,
            )
        }
        items(
            items = templatesData.inactiveTemplates,
            key = { template -> template.templateId },
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
            key = EMPTY_STATE_KEY,
            span = { GridItemSpan(maxLineSpan) },
        ) {
            Box(
                modifier = Modifier
                    .animateItem()
                    .fillMaxWidth()
                    .height(180.dp),
                contentAlignment = Alignment.Center,
            ) {
                TemplatesEmptyState(
                    title = TemplatesThemeRes.strings.emptyListTitle,
                )
            }
        }
    }
}

private const val PATTERN_SECTION_KEY = "pattern_section"
private const val FILTER_HEADER_KEY = "filter_header"
private const val ACTIVE_TEMPLATES_HEADER_KEY = "active_templates_header"
private const val INACTIVE_TEMPLATES_HEADER_KEY = "inactive_templates_header"
private const val EMPTY_STATE_KEY = "empty_state"
