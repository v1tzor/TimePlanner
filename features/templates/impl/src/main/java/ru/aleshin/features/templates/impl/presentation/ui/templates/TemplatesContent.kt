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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import ru.aleshin.core.domain.entities.template.RepeatTime
import ru.aleshin.core.presentation.models.categories.MainCategoryDetailsUi
import ru.aleshin.core.presentation.models.templates.TemplateUi
import ru.aleshin.core.utils.architecture.store.compose.handleEffects
import ru.aleshin.core.utils.architecture.store.compose.stateAsState
import ru.aleshin.core.utils.managers.LocalDrawerManager
import ru.aleshin.features.templates.impl.domain.entities.TemplatesSortedType
import ru.aleshin.features.templates.impl.presentation.mapppers.mapToMessage
import ru.aleshin.features.templates.impl.presentation.mapppers.mapToString
import ru.aleshin.features.templates.impl.presentation.theme.TemplatesThemeRes
import ru.aleshin.features.templates.impl.presentation.ui.templates.contract.TemplatesEffect
import ru.aleshin.features.templates.impl.presentation.ui.templates.contract.TemplatesEvent
import ru.aleshin.features.templates.impl.presentation.ui.templates.contract.TemplatesState
import ru.aleshin.features.templates.impl.presentation.ui.templates.store.TemplatesComponent
import ru.aleshin.features.templates.impl.presentation.ui.templates.views.EmptyDateView
import ru.aleshin.features.templates.impl.presentation.ui.templates.views.TemplateEditorDialog
import ru.aleshin.features.templates.impl.presentation.ui.templates.views.TemplatesItem
import ru.aleshin.features.templates.impl.presentation.ui.templates.views.TemplatesTopAppBar
import ru.aleshin.timeplanner.core.ui.views.ErrorSnackbar
import ru.aleshin.timeplanner.core.ui.views.ExpandedIcon
import ru.aleshin.timeplanner.core.ui.views.Scaffold

/**
 * @author Stanislav Aleshin on 08.05.2023.
 */
@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun TemplatesContent(
    templatesComponent: TemplatesComponent,
    modifier: Modifier = Modifier,
) {
    val store = templatesComponent.store
    val state by store.stateAsState()
    val scope = rememberCoroutineScope()
    val snackbarState = remember { SnackbarHostState() }
    var isShowTemplateCreator by rememberSaveable { mutableStateOf(false) }
    val drawerManager = LocalDrawerManager.current
    val strings = TemplatesThemeRes.strings

    Scaffold(
        modifier = modifier.fillMaxSize(),
        content = { paddingValues ->
            BaseTemplatesContent(
                state = state,
                modifier = Modifier.padding(paddingValues),
                onChangeSortedType = { store.dispatchEvent(TemplatesEvent.UpdatedSortedType(it)) },
                onDeleteTemplate = { store.dispatchEvent(TemplatesEvent.DeleteTemplate(it)) },
                onUpdateTemplate = { store.dispatchEvent(TemplatesEvent.UpdateTemplate(it)) },
                onRestartRepeat = { store.dispatchEvent(TemplatesEvent.RestartTemplateRepeat(it)) },
                onStopRepeat = { store.dispatchEvent(TemplatesEvent.StopTemplateRepeat(it)) },
                onAddRepeatTemplate = { time, template ->
                    store.dispatchEvent(TemplatesEvent.AddRepeatTemplate(time, template))
                },
                onDeleteRepeatTemplate = { time, template ->
                    store.dispatchEvent(TemplatesEvent.DeleteRepeatTemplate(time, template))
                },
            )
        },
        topBar = {
            TemplatesTopAppBar(
                onMenuIconClick = { scope.launch { drawerManager?.openDrawer() } },
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarState) {
                ErrorSnackbar(snackbarData = it)
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { isShowTemplateCreator = true },
                content = {
                    Text(
                        text = TemplatesThemeRes.strings.addTemplatesFabTitle,
                        style = MaterialTheme.typography.titleLarge,
                    )
                },
            )
        },
    )

    if (isShowTemplateCreator) {
        TemplateEditorDialog(
            categories = state.categories,
            model = null,
            onDismiss = { isShowTemplateCreator = false },
            onConfirm = { template ->
                store.dispatchEvent(TemplatesEvent.AddTemplate(template))
                isShowTemplateCreator = false
            },
        )
    }

    store.handleEffects { effect ->
        when (effect) {
            is TemplatesEffect.ShowError -> snackbarState.showSnackbar(
                message = effect.failures.mapToMessage(strings),
            )
        }
    }
}

@Composable
private fun BaseTemplatesContent(
    state: TemplatesState,
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
        HorizontalDivider()
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
    categories: List<MainCategoryDetailsUi>,
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
            items(items = templates, key = { it.templateId }) { template ->
                TemplatesItem(
                    modifier = Modifier.animateItem(),
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
                emptyTitle = TemplatesThemeRes.strings.emptyListTitle,
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
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = TemplatesThemeRes.strings.sortedTypeTitle,
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
