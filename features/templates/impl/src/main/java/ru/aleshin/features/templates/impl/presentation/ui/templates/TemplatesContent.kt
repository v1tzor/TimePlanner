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

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import ru.aleshin.core.domain.entities.template.RepeatTime
import ru.aleshin.core.presentation.models.categories.MainCategoryDetailsUi
import ru.aleshin.core.presentation.models.templates.TemplateUi
import ru.aleshin.core.utils.architecture.store.compose.handleEffects
import ru.aleshin.core.utils.architecture.store.compose.stateAsState
import ru.aleshin.features.templates.impl.domain.entities.templates.TemplatesPatternFilter
import ru.aleshin.features.templates.impl.domain.entities.templates.TemplatesSortedType
import ru.aleshin.features.templates.impl.presentation.mapppers.mapToMessage
import ru.aleshin.features.templates.impl.presentation.mapppers.mapToString
import ru.aleshin.features.templates.impl.presentation.models.TemplatesDataUi
import ru.aleshin.features.templates.impl.presentation.models.TemplatesPatternViewUi
import ru.aleshin.features.templates.impl.presentation.theme.TemplatesThemeRes
import ru.aleshin.features.templates.impl.presentation.ui.templates.contract.TemplatesEffect
import ru.aleshin.features.templates.impl.presentation.ui.templates.contract.TemplatesEvent
import ru.aleshin.features.templates.impl.presentation.ui.templates.contract.TemplatesState
import ru.aleshin.features.templates.impl.presentation.ui.templates.store.TemplatesComponent
import ru.aleshin.features.templates.impl.presentation.ui.templates.views.EmptyDateView
import ru.aleshin.features.templates.impl.presentation.ui.templates.views.TemplateEditorDialog
import ru.aleshin.features.templates.impl.presentation.ui.templates.views.TemplatesItem
import ru.aleshin.features.templates.impl.presentation.ui.templates.views.TemplatesPatternSection
import ru.aleshin.features.templates.impl.presentation.ui.templates.views.TemplatesTopAppBar
import ru.aleshin.timeplanner.core.ui.views.ErrorSnackbar
import ru.aleshin.timeplanner.core.ui.views.ExpandedIcon
import ru.aleshin.timeplanner.core.ui.views.PlaceholderBox
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
    val snackbarState = remember { SnackbarHostState() }
    var isShowTemplateCreator by rememberSaveable { mutableStateOf(false) }
    val strings = TemplatesThemeRes.strings

    Scaffold(
        modifier = modifier.fillMaxSize(),
        content = { paddingValues ->
            BaseTemplatesContent(
                state = state,
                modifier = Modifier.padding(paddingValues),
                onChangeSortedType = { store.dispatchEvent(TemplatesEvent.UpdatedSortedType(it)) },
                onChangePatternFilter = { store.dispatchEvent(TemplatesEvent.UpdatedPatternFilter(it)) },
                onChangePatternView = { store.dispatchEvent(TemplatesEvent.UpdatedPatternView(it)) },
                onDeleteTemplate = { store.dispatchEvent(TemplatesEvent.DeleteTemplate(it)) },
                onUpdateTemplate = { old, new ->
                    store.dispatchEvent(TemplatesEvent.UpdateTemplate(old, new))
                },
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
            TemplatesTopAppBar()
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
                        text = strings.addTemplatesFabTitle,
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
    onChangePatternFilter: (TemplatesPatternFilter) -> Unit,
    onChangePatternView: (TemplatesPatternViewUi) -> Unit,
    onUpdateTemplate: (TemplateUi, TemplateUi) -> Unit,
    onRestartRepeat: (TemplateUi) -> Unit,
    onStopRepeat: (TemplateUi) -> Unit,
    onAddRepeatTemplate: (RepeatTime, TemplateUi) -> Unit,
    onDeleteRepeatTemplate: (RepeatTime, TemplateUi) -> Unit,
    onDeleteTemplate: (TemplateUi) -> Unit,
) {
    val templatesData = state.templatesData

    AnimatedContent(
        modifier = modifier,
        targetState = templatesData == null,
        label = "TemplatesContent",
        transitionSpec = {
            fadeIn(animationSpec = tween(600, delayMillis = 90)).togetherWith(
                fadeOut(animationSpec = tween(300)),
            )
        },
    ) { loading ->
        if (loading || templatesData == null) {
            TemplatesGridPlaceholder()
        } else {
            TemplatesGrid(
                templatesData = templatesData,
                categories = state.categories,
                sortedType = state.sortedType,
                patternFilter = state.patternFilter,
                patternView = state.patternView,
                onChangeSortedType = onChangeSortedType,
                onChangePatternFilter = onChangePatternFilter,
                onChangePatternView = onChangePatternView,
                onUpdateTemplate = onUpdateTemplate,
                onRestartRepeat = onRestartRepeat,
                onStopRepeat = onStopRepeat,
                onAddRepeatTemplate = onAddRepeatTemplate,
                onDeleteRepeatTemplate = onDeleteRepeatTemplate,
                onDeleteTemplate = onDeleteTemplate,
            )
        }
    }
}

@Composable
private fun TemplatesGridPlaceholder(
    modifier: Modifier = Modifier,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 88.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        userScrollEnabled = false,
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth().height(40.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    PlaceholderBox(
                        modifier = Modifier.weight(1f).height(28.dp),
                        shape = MaterialTheme.shapes.small,
                    )
                    PlaceholderBox(
                        modifier = Modifier.size(width = 128.dp, height = 40.dp),
                        shape = CircleShape,
                    )
                }
                PlaceholderBox(
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    shape = MaterialTheme.shapes.large,
                )
            }
        }
        item(span = { GridItemSpan(maxLineSpan) }) {
            Row(
                modifier = Modifier.fillMaxWidth().height(40.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                PlaceholderBox(
                    modifier = Modifier.width(120.dp).height(28.dp),
                    shape = MaterialTheme.shapes.small,
                )
                Spacer(modifier = Modifier.weight(1f))
                PlaceholderBox(
                    modifier = Modifier.width(112.dp).height(32.dp),
                    shape = MaterialTheme.shapes.small,
                )
            }
        }
        item(span = { GridItemSpan(maxLineSpan) }) {
            Row(
                modifier = Modifier.fillMaxWidth().height(28.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                PlaceholderBox(
                    modifier = Modifier.size(width = 5.dp, height = 28.dp),
                    shape = CircleShape,
                )
                PlaceholderBox(
                    modifier = Modifier.weight(1f).height(24.dp),
                    shape = MaterialTheme.shapes.small,
                )
                PlaceholderBox(
                    modifier = Modifier.size(24.dp),
                    shape = CircleShape,
                )
            }
        }
        items(
            count = TEMPLATE_PLACEHOLDER_COUNT,
            key = { index -> "TemplatePlaceholder$index" },
        ) {
            PlaceholderBox(
                modifier = Modifier.fillMaxWidth().height(170.dp),
                shape = MaterialTheme.shapes.large,
            )
        }
    }
}

@Composable
private fun TemplatesGrid(
    modifier: Modifier = Modifier,
    templatesData: TemplatesDataUi,
    categories: List<MainCategoryDetailsUi>,
    sortedType: TemplatesSortedType,
    patternFilter: TemplatesPatternFilter,
    patternView: TemplatesPatternViewUi,
    onChangeSortedType: (TemplatesSortedType) -> Unit,
    onChangePatternFilter: (TemplatesPatternFilter) -> Unit,
    onChangePatternView: (TemplatesPatternViewUi) -> Unit,
    onUpdateTemplate: (TemplateUi, TemplateUi) -> Unit,
    onRestartRepeat: (TemplateUi) -> Unit,
    onStopRepeat: (TemplateUi) -> Unit,
    onAddRepeatTemplate: (RepeatTime, TemplateUi) -> Unit,
    onDeleteRepeatTemplate: (RepeatTime, TemplateUi) -> Unit,
    onDeleteTemplate: (TemplateUi) -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 88.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item(
            key = "TemplatesPattern",
            span = { GridItemSpan(maxLineSpan) },
        ) {
            TemplatesPatternSection(
                patternFilter = patternFilter,
                patternView = patternView,
                weekPattern = templatesData.weekPattern,
                monthPattern = templatesData.monthPattern,
                onChangePatternFilter = onChangePatternFilter,
                onChangePatternView = onChangePatternView,
            )
        }
        item(
            key = "TemplatesHeader",
            span = { GridItemSpan(maxLineSpan) },
        ) {
            TemplatesFiltersHeader(
                sortedType = sortedType,
                onChangeSortedType = onChangeSortedType,
            )
        }
        if (templatesData.activeTemplates.isNotEmpty()) {
            item(
                key = "ActiveTemplatesHeader",
                span = { GridItemSpan(maxLineSpan) },
            ) {
                TemplatesGroupHeader(
                    title = TemplatesThemeRes.patternStrings.activeTemplatesTitle,
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
                    onUpdate = { updated -> onUpdateTemplate(template, updated) },
                    onRestartRepeat = { onRestartRepeat(template) },
                    onStopRepeat = { onStopRepeat(template) },
                    onAddRepeat = { repeatTime -> onAddRepeatTemplate(repeatTime, template) },
                    onDeleteRepeat = { repeatTime -> onDeleteRepeatTemplate(repeatTime, template) },
                    onDeleteTemplate = { onDeleteTemplate(template) },
                )
            }
        }
        if (templatesData.inactiveTemplates.isNotEmpty()) {
            item(
                key = "InactiveTemplatesHeader",
                span = { GridItemSpan(maxLineSpan) },
            ) {
                TemplatesGroupHeader(
                    title = TemplatesThemeRes.patternStrings.inactiveTemplatesTitle,
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
                    onUpdate = { updated -> onUpdateTemplate(template, updated) },
                    onRestartRepeat = { onRestartRepeat(template) },
                    onStopRepeat = { onStopRepeat(template) },
                    onAddRepeat = { repeatTime -> onAddRepeatTemplate(repeatTime, template) },
                    onDeleteRepeat = { repeatTime -> onDeleteRepeatTemplate(repeatTime, template) },
                    onDeleteTemplate = { onDeleteTemplate(template) },
                )
            }
        }
        if (templatesData.activeTemplates.isEmpty() && templatesData.inactiveTemplates.isEmpty()) {
            item(
                key = "EmptyTemplates",
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
}

@Composable
private fun TemplatesGroupHeader(
    modifier: Modifier = Modifier,
    title: String,
    count: Int,
    isActive: Boolean,
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(top = 4.dp),
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
            content = {}
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
internal fun TemplatesFiltersHeader(
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

private const val TEMPLATE_PLACEHOLDER_COUNT = 4
