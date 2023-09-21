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
package ru.aleshin.features.home.impl.presentation.ui.templates

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import ru.aleshin.core.ui.views.ExpandedIcon
import ru.aleshin.features.home.api.domain.entities.template.RepeatTime
import ru.aleshin.features.home.impl.presentation.mapppers.templates.mapToString
import ru.aleshin.features.home.impl.presentation.models.categories.CategoriesUi
import ru.aleshin.features.home.impl.presentation.models.templates.TemplateUi
import ru.aleshin.features.home.impl.presentation.models.templates.TemplatesSortedType
import ru.aleshin.features.home.impl.presentation.theme.HomeThemeRes
import ru.aleshin.features.home.impl.presentation.ui.home.views.EmptyDateView
import ru.aleshin.features.home.impl.presentation.ui.templates.contract.TemplatesViewState
import ru.aleshin.features.home.impl.presentation.ui.templates.views.TemplatesItem

/**
 * @author Stanislav Aleshin on 08.05.2023.
 */
@Composable
internal fun TemplatesContent(
    state: TemplatesViewState,
    modifier: Modifier = Modifier,
    onChangeSortedType: (TemplatesSortedType) -> Unit,
    onUpdateTemplate: (TemplateUi) -> Unit,
    onRestartRepeat: (TemplateUi) -> Unit,
    onStopRepeat: (TemplateUi) -> Unit,
    onAddRepeatTemplate: (RepeatTime, TemplateUi) -> Unit,
    onDeleteRepeatTemplate: (RepeatTime, TemplateUi) -> Unit,
    onDeleteTemplate: (TemplateUi) -> Unit,
) {
    Column(modifier = modifier) {
        TemplatesFiltersHeader(
            sortedType = state.sortedType,
            onChangeSortedType = onChangeSortedType,
        )
        Divider()
        TemplatesLazyColumn(
            templates = state.templates,
            categories = state.categories,
            onUpdateTemplate = onUpdateTemplate,
            onRestartRepeat = onRestartRepeat,
            onStopRepeat = onStopRepeat,
            onAddRepeatTemplate = onAddRepeatTemplate,
            onDeleteRepeatTemplate = onDeleteRepeatTemplate,
            onDeleteTemplate = onDeleteTemplate,
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun TemplatesLazyColumn(
    modifier: Modifier = Modifier,
    templates: List<TemplateUi>?,
    categories: List<CategoriesUi>,
    onUpdateTemplate: (TemplateUi) -> Unit,
    onRestartRepeat: (TemplateUi) -> Unit,
    onStopRepeat: (TemplateUi) -> Unit,
    onAddRepeatTemplate: (RepeatTime, TemplateUi) -> Unit,
    onDeleteRepeatTemplate: (RepeatTime, TemplateUi) -> Unit,
    onDeleteTemplate: (TemplateUi) -> Unit,
) {
    if (!templates.isNullOrEmpty()) {
        LazyColumn(
            modifier = modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(
                items = templates,
                key = { it.templateId },
            ) { template ->
                TemplatesItem(
                    modifier = Modifier.animateItemPlacement(),
                    model = template,
                    categories = categories,
                    onUpdate = { onUpdateTemplate(it) },
                    onRestartRepeat = { onRestartRepeat(template) },
                    onStopRepeat = { onStopRepeat(template) },
                    onAddRepeat = { onAddRepeatTemplate(it, template) },
                    onDeleteRepeat = { onDeleteRepeatTemplate(it, template) },
                    onDeleteTemplate = { onDeleteTemplate(template) },
                )
            }
            item { Spacer(modifier = Modifier.height(40.dp)) }
        }
    } else if (templates != null && templates.isEmpty()) {
        Box(Modifier.fillMaxSize()) {
            EmptyDateView(
                modifier = Modifier.align(Alignment.Center),
                emptyTitle = HomeThemeRes.strings.emptyListTitle,
            )
        }
    } else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}

@Composable
internal fun TemplatesFiltersHeader(
    modifier: Modifier = Modifier,
    sortedType: TemplatesSortedType,
    onChangeSortedType: (TemplatesSortedType) -> Unit,
) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }
    Row(
        modifier = modifier.fillMaxWidth().padding(start = 16.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = HomeThemeRes.strings.sortedTypeTitle,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.labelLarge,
        )
        Spacer(modifier = Modifier.weight(1f))
        Box {
            Surface(
                onClick = { isExpanded = true },
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.background,
            ) {
                Row(
                    modifier = Modifier.animateContentSize().padding(6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = sortedType.mapToString(),
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Medium,
                        ),
                    )
                    Box(modifier = Modifier.size(18.dp), contentAlignment = Alignment.Center) {
                        ExpandedIcon(isExpanded = isExpanded)
                    }
                }
            }
            SortedTypeMenu(
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
internal fun SortedTypeMenu(
    modifier: Modifier = Modifier,
    isExpanded: Boolean,
    onDismiss: () -> Unit,
    onSelected: (TemplatesSortedType) -> Unit,
) {
    DropdownMenu(
        expanded = isExpanded,
        onDismissRequest = onDismiss,
        modifier = modifier,
        offset = DpOffset(0.dp, 2.dp),
    ) {
        TemplatesSortedType.values().forEach { type ->
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
