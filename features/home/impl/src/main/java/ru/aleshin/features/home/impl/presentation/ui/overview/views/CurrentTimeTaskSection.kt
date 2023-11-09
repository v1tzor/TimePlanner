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
package ru.aleshin.features.home.impl.presentation.ui.overview.views

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RichTooltipBox
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberRichTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import ru.aleshin.core.ui.theme.material.surfaceTwo
import ru.aleshin.core.ui.views.CategoryIconMonogram
import ru.aleshin.core.ui.views.CategoryTextMonogram
import ru.aleshin.core.ui.views.PlaceholderBox
import ru.aleshin.features.home.api.presentation.mappers.mapToIconPainter
import ru.aleshin.features.home.impl.presentation.models.schedules.TimeTaskUi
import ru.aleshin.features.home.impl.presentation.theme.HomeThemeRes
import ru.aleshin.features.home.impl.presentation.ui.home.views.TimeTaskTitles
import kotlin.math.roundToInt

/**
 * @author Stanislav Aleshin on 02.11.2023.
 */
@Composable
internal fun CurrentTimeTaskSection(
    modifier: Modifier = Modifier,
    isLoading: Boolean,
    task: TimeTaskUi?,
    onOpenTask: () -> Unit,
) {
    Column(
        modifier = modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = HomeThemeRes.strings.currentTaskHeader,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleSmall,
        )
        AnimatedContent(
            targetState = isLoading,
            label = "Current task",
            transitionSpec = {
                fadeIn(animationSpec = tween(600, delayMillis = 90)).togetherWith(
                    fadeOut(animationSpec = tween(300)),
                )
            },
        ) { loading ->
            if (loading) {
                PlaceholderBox(
                    modifier = Modifier.fillMaxWidth().height(125.dp),
                    shape = MaterialTheme.shapes.large,
                )
            } else if (task != null) {
                CurrentTimeTaskView(
                    model = task,
                    onClick = onOpenTask,
                )
            } else {
                NoneCurrentTimeTaskView()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CurrentTimeTaskView(
    modifier: Modifier = Modifier,
    model: TimeTaskUi,
    onClick: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val tooltipState = rememberRichTooltipState(isPersistent = true)

    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.primaryContainer,
    ) {
        Column(modifier = Modifier.animateContentSize()) {
            Row(
                modifier = Modifier.padding(start = 16.dp, end = 8.dp, top = 16.dp, bottom = 4.dp).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Box(modifier = Modifier.align(Alignment.Top)) {
                    val categoryIcon = model.mainCategory.defaultType?.mapToIconPainter()
                    if (categoryIcon != null) {
                        CategoryIconMonogram(
                            icon = categoryIcon,
                            iconDescription = null,
                            iconColor = MaterialTheme.colorScheme.onPrimary,
                            badgeEnabled = model.isImportant,
                            backgroundColor = MaterialTheme.colorScheme.primary,
                        )
                    } else {
                        CategoryTextMonogram(
                            text = model.mainCategory.fetchName()?.first().toString(),
                            textColor = MaterialTheme.colorScheme.onPrimary,
                            backgroundColor = MaterialTheme.colorScheme.primary,
                            badgeEnabled = model.isImportant,
                        )
                    }
                }
                TimeTaskTitles(
                    modifier = Modifier.weight(1f),
                    title = model.mainCategory.fetchName() ?: HomeThemeRes.strings.noneTitle,
                    titleColor = MaterialTheme.colorScheme.onSurface,
                    subTitle = model.subCategory?.name,
                )
                if (model.note != null) {
                    RichTooltipBox(
                        title = { Text(text = HomeThemeRes.strings.noteTitle) },
                        text = { Text(text = model.note) },
                        tooltipState = tooltipState,
                    ) {
                        IconButton(
                            modifier = Modifier.size(32.dp),
                            onClick = {
                                scope.launch {
                                    if (!tooltipState.isVisible) tooltipState.show() else tooltipState.dismiss()
                                }
                            },
                        ) {
                            Icon(
                                modifier = Modifier.size(18.dp),
                                painter = painterResource(id = HomeThemeRes.icons.notes),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                        }
                    }
                }
            }
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        modifier = Modifier.weight(1f).height(IntrinsicSize.Min),
                        text = HomeThemeRes.strings.progressTitle,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        style = MaterialTheme.typography.titleSmall,
                    )
                    Text(
                        text = (model.progress * 100).roundToInt().toString() + "%",
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        style = MaterialTheme.typography.titleSmall,
                    )
                }
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(100.dp)),
                    progress = model.progress,
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
            }
        }
    }
}

@Composable
internal fun NoneCurrentTimeTaskView(
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surfaceTwo(),
        shape = MaterialTheme.shapes.large,
    ) {
        Text(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            text = HomeThemeRes.strings.noneTitle,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleSmall,
        )
    }
}
