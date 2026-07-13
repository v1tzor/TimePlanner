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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ru.aleshin.timeplanner.core.ui.theme.TimePlannerRes
import ru.aleshin.timeplanner.core.ui.views.DialogButtons
import ru.aleshin.core.presentation.models.categories.MainCategoryDetailsUi
import ru.aleshin.core.presentation.models.categories.MainCategoryUi
import ru.aleshin.core.presentation.models.categories.SubCategoryUi
import ru.aleshin.core.presentation.models.tasks.UndefinedTaskUi
import ru.aleshin.features.overview.impl.presentation.theme.OverviewThemeRes
import ru.aleshin.features.overview.impl.presentation.ui.common.CompactCategoryChooser
import ru.aleshin.features.overview.impl.presentation.ui.common.CompactSubCategoryChooser
import java.util.Date

/**
 * @author Stanislav Aleshin on 01.07.2026.
 */
@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun UndefinedTasksBatchEditorDialog(
    modifier: Modifier = Modifier,
    tasks: List<UndefinedTaskUi>,
    categories: List<MainCategoryDetailsUi>,
    onDismiss: () -> Unit,
    onConfirm: (List<UndefinedTaskUi>) -> Unit,
) {
    var editTasks by remember(tasks) { mutableStateOf(tasks) }
    val isEnabled = editTasks.isNotEmpty() && editTasks.all { it.mainCategory.id != 0L }

    BasicAlertDialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = modifier.widthIn(max = 360.dp).fillMaxWidth().wrapContentHeight(),
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.surfaceContainer,
        ) {
            Column {
                UndefinedTasksBatchEditorHeader(tasksCount = editTasks.size)
                HorizontalDivider()
                LazyColumn(
                    modifier = Modifier.heightIn(max = 380.dp),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    items(editTasks, key = { it.id }) { task ->
                        UndefinedTasksBatchEditorItem(
                            modifier = Modifier.fillMaxWidth(),
                            task = task,
                            categories = categories,
                            onCategoryChange = { category ->
                                editTasks = editTasks.changeTask(task) {
                                    copy(
                                        mainCategory = category,
                                        subCategory = subCategory?.takeIf { it.mainCategoryId == category.id },
                                    )
                                }
                            },
                            onSubCategoryChange = { subCategory ->
                                editTasks = editTasks.changeTask(task) { copy(subCategory = subCategory) }
                            },
                            onDeadlineChange = { deadline ->
                                editTasks = editTasks.changeTask(task) { copy(deadline = deadline) }
                            },
                        )
                    }
                }
                DialogButtons(
                    enabledConfirm = isEnabled,
                    confirmTitle = TimePlannerRes.strings.okConfirmTitle,
                    onConfirmClick = { if (isEnabled) onConfirm(editTasks) },
                    onCancelClick = onDismiss,
                )
            }
        }
    }
}

@Composable
internal fun UndefinedTasksBatchEditorHeader(
    modifier: Modifier = Modifier,
    tasksCount: Int,
) {
    Column(
        modifier = modifier.padding(top = 24.dp, bottom = 12.dp, start = 24.dp, end = 24.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = OverviewThemeRes.strings.sharedTasksDialogTitle,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.headlineSmall,
        )
        Text(
            text = OverviewThemeRes.strings.sharedTasksDialogDesc,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium,
        )
        Text(
            text = OverviewThemeRes.strings.sharedTasksCountFormat.format(tasksCount),
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.labelLarge,
        )
    }
}

@Composable
internal fun UndefinedTasksBatchEditorItem(
    modifier: Modifier = Modifier,
    task: UndefinedTaskUi,
    categories: List<MainCategoryDetailsUi>,
    onCategoryChange: (MainCategoryUi) -> Unit,
    onSubCategoryChange: (SubCategoryUi?) -> Unit,
    onDeadlineChange: (Date?) -> Unit,
) {
    val subCategories = remember(categories, task.mainCategory) {
        categories.find { it.mainCategory == task.mainCategory }?.subCategories ?: emptyList()
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(
            text = task.note.orEmpty(),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyLarge,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
        )
        CompactCategoryChooser(
            allCategories = categories,
            selectedCategory = task.mainCategory,
            onCategoryChange = onCategoryChange,
        )
        if (subCategories.isNotEmpty()) {
            CompactSubCategoryChooser(
                allCategories = categories,
                selectedMainCategory = task.mainCategory,
                selectedSubCategory = task.subCategory,
                onSubCategoryChange = onSubCategoryChange,
            )
        }
        DeadlineChooser(
            deadline = task.deadline,
            onChooseDeadline = onDeadlineChange,
        )
    }
}

private fun List<UndefinedTaskUi>.changeTask(
    task: UndefinedTaskUi,
    transform: UndefinedTaskUi.() -> UndefinedTaskUi,
): List<UndefinedTaskUi> {
    return map { if (it.id == task.id) it.transform() else it }
}

