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
package ru.aleshin.features.home.impl.presentation.ui.templates

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import ru.aleshin.core.ui.views.ExpandedIcon
import ru.aleshin.features.home.api.domains.entities.template.Template
import ru.aleshin.features.home.impl.presentation.mapppers.mapToString
import ru.aleshin.features.home.impl.presentation.models.TemplatesSortedType
import ru.aleshin.features.home.impl.presentation.theme.HomeThemeRes
import ru.aleshin.features.home.impl.presentation.ui.home.views.EmptyDateView
import ru.aleshin.features.home.impl.presentation.ui.home.views.ViewToggle
import ru.aleshin.features.home.impl.presentation.ui.home.views.ViewToggleStatus
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
    onChangeToggleStatus: (ViewToggleStatus) -> Unit,
    onUpdateTemplate: (Template) -> Unit,
    onDeleteTemplate: (Template) -> Unit,
) {
    val templates = state.templates

    Column(modifier = modifier) {
        TemplatesFiltersHeader(
            sortedType = state.sortedType,
            toggleState = state.viewToggleStatus,
            onChangeSortedType = onChangeSortedType,
            onChangeToggleStatus = onChangeToggleStatus,
        )
        if (!templates.isNullOrEmpty()) {
            LazyColumn(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                items(
                    items = templates,
                    key = { it.templateId },
                ) { template ->
                    TemplatesItem(
                        model = template,
                        categories = state.categories,
                        isFullInfo = state.viewToggleStatus == ViewToggleStatus.EXPANDED,
                        onUpdateTemplate = { onUpdateTemplate(it) },
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
}

@Composable
internal fun TemplatesFiltersHeader(
    modifier: Modifier = Modifier,
    sortedType: TemplatesSortedType,
    toggleState: ViewToggleStatus,
    onChangeSortedType: (TemplatesSortedType) -> Unit,
    onChangeToggleStatus: (ViewToggleStatus) -> Unit,
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(
            text = HomeThemeRes.strings.sortedTypeTitle,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodyMedium,
        )
        SortedTypeChip(sortedType = sortedType, onChangeSortedType = onChangeSortedType)
        Spacer(modifier = Modifier.weight(1f))
        ViewToggle(status = toggleState, onStatusChange = onChangeToggleStatus, isHideTitle = true)
    }
}

@Composable
internal fun SortedTypeChip(
    modifier: Modifier = Modifier,
    sortedType: TemplatesSortedType,
    onChangeSortedType: (TemplatesSortedType) -> Unit,
) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }

    Box {
        AssistChip(
            modifier = modifier,
            onClick = { isExpanded = true },
            label = { Text(text = sortedType.mapToString()) },
            trailingIcon = { ExpandedIcon(isExpanded = isExpanded) },
        )
        SortedTypeMenu(
            isExpanded = isExpanded,
            onDismiss = { isExpanded = false },
            onSelected = { type ->
                isExpanded = false
                onChangeSortedType(type)
            },
        )
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
