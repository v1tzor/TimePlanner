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

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.aleshin.features.templates.impl.presentation.ui.templates.contract.TemplatesEvent
import ru.aleshin.features.templates.impl.presentation.ui.templates.contract.TemplatesState
import ru.aleshin.features.templates.impl.presentation.ui.templates.views.TemplatesGridPlaceholder
import ru.aleshin.timeplanner.core.ui.views.AdaptiveLayoutInfo
import ru.aleshin.timeplanner.core.ui.views.animations.AnimatedLoadingContent

/**
 * @author Stanislav Aleshin on 28.07.2026.
 */
@Composable
internal fun TemplatesLayout(
    modifier: Modifier = Modifier,
    state: TemplatesState,
    adaptiveLayoutInfo: AdaptiveLayoutInfo,
    onCreateTemplate: () -> Unit,
    onEvent: (TemplatesEvent) -> Unit,
) {
    val templatesData = state.templatesData

    when {
        adaptiveLayoutInfo.useTemplatesSupportingPaneLayout -> TemplatesSupportingPaneLayout(
            modifier = modifier.fillMaxSize(),
            state = state,
            adaptiveLayoutInfo = adaptiveLayoutInfo,
            onCreateTemplate = onCreateTemplate,
            onEvent = onEvent,
        )
        else -> AnimatedLoadingContent(
            modifier = modifier.fillMaxSize(),
            isLoading = templatesData == null,
            targetValue = templatesData,
            label = "TemplatesSinglePane",
        ) { data ->
            if (data != null) {
                TemplatesGrid(
                    modifier = Modifier.fillMaxSize(),
                    state = state,
                    templatesData = data,
                    columns = GridCells.Fixed(COMPACT_COLUMN_COUNT),
                    contentPadding = PaddingValues(start = 16.dp, top = 12.dp, end = 16.dp, bottom = 88.dp),
                    horizontalSpacing = 10.dp,
                    showPattern = true,
                    onEvent = onEvent,
                )
            } else {
                TemplatesGridPlaceholder(
                    modifier = Modifier.fillMaxSize(),
                    columns = GridCells.Fixed(COMPACT_COLUMN_COUNT),
                    contentPadding = PaddingValues(start = 16.dp, top = 12.dp, end = 16.dp, bottom = 88.dp),
                    horizontalSpacing = 10.dp,
                    showPattern = true,
                )
            }
        }
    }
}

internal val AdaptiveLayoutInfo.useTemplatesSupportingPaneLayout: Boolean
    get() = isBookPosture || (!isCompactWidth && !isMediumWidth && !isExpandedWidth)

private const val COMPACT_COLUMN_COUNT = 2
