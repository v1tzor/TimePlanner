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

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import ru.aleshin.core.utils.architecture.store.compose.handleEffects
import ru.aleshin.core.utils.architecture.store.compose.stateAsState
import ru.aleshin.features.templates.impl.presentation.mapppers.mapToMessage
import ru.aleshin.features.templates.impl.presentation.theme.TemplatesThemeRes
import ru.aleshin.features.templates.impl.presentation.ui.templates.contract.TemplatesEffect
import ru.aleshin.features.templates.impl.presentation.ui.templates.contract.TemplatesEvent
import ru.aleshin.features.templates.impl.presentation.ui.templates.store.TemplatesComponent
import ru.aleshin.features.templates.impl.presentation.ui.templates.views.TemplateEditorDialog
import ru.aleshin.features.templates.impl.presentation.ui.templates.views.TemplatesCreateFab
import ru.aleshin.features.templates.impl.presentation.ui.templates.views.TemplatesTopAppBar
import ru.aleshin.timeplanner.core.ui.views.AdaptiveLayoutInfo
import ru.aleshin.timeplanner.core.ui.views.ErrorSnackbar
import ru.aleshin.timeplanner.core.ui.views.Scaffold
import ru.aleshin.timeplanner.core.ui.views.rememberAdaptiveLayoutInfo

/**
 * @author Stanislav Aleshin on 08.05.2023.
 */
@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun TemplatesContent(
    modifier: Modifier = Modifier,
    templatesComponent: TemplatesComponent,
    adaptiveLayoutInfo: AdaptiveLayoutInfo = rememberAdaptiveLayoutInfo(),
) {
    val store = templatesComponent.store
    val state by store.stateAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var isTemplateCreatorOpen by rememberSaveable { mutableStateOf(false) }
    val layoutMode = TemplatesLayoutMode.from(adaptiveLayoutInfo)
    val strings = TemplatesThemeRes.strings

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TemplatesTopAppBar(
                isCompact = adaptiveLayoutInfo.isCompactWidth,
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { snackbarData ->
                ErrorSnackbar(snackbarData = snackbarData)
            }
        },
        floatingActionButton = {
            if (layoutMode == TemplatesLayoutMode.COMPACT) {
                TemplatesCreateFab(
                    onClick = { isTemplateCreatorOpen = true },
                )
            }
        },
    ) { contentPadding ->
        TemplatesLayout(
            modifier = when (layoutMode) {
                TemplatesLayoutMode.COMPACT -> Modifier.padding(contentPadding)
                TemplatesLayoutMode.SUPPORTING -> Modifier.padding(
                    top = contentPadding.calculateTopPadding(),
                )
            },
            state = state,
            adaptiveLayoutInfo = adaptiveLayoutInfo,
            layoutMode = layoutMode,
            onCreateTemplate = { isTemplateCreatorOpen = true },
            onEvent = store::dispatchEvent,
        )
    }

    if (isTemplateCreatorOpen) {
        TemplateEditorDialog(
            categories = state.categories,
            model = null,
            onDismiss = { isTemplateCreatorOpen = false },
            onConfirm = { template ->
                store.dispatchEvent(TemplatesEvent.AddTemplate(template))
                isTemplateCreatorOpen = false
            },
        )
    }

    store.handleEffects { effect ->
        when (effect) {
            is TemplatesEffect.ShowError -> snackbarHostState.showSnackbar(
                message = effect.failures.mapToMessage(strings),
            )
        }
    }
}
