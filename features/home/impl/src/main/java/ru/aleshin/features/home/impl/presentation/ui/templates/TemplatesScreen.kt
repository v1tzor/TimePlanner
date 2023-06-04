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

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import kotlinx.coroutines.launch
import ru.aleshin.core.ui.views.Scaffold
import ru.aleshin.core.utils.managers.LocalDrawerManager
import ru.aleshin.core.utils.platform.screen.ScreenContent
import ru.aleshin.features.home.impl.presentation.mapppers.mapToMessage
import ru.aleshin.features.home.impl.presentation.theme.HomeThemeRes
import ru.aleshin.features.home.impl.presentation.ui.templates.contract.TemplatesEffect
import ru.aleshin.features.home.impl.presentation.ui.templates.contract.TemplatesEvent
import ru.aleshin.features.home.impl.presentation.ui.templates.contract.TemplatesViewState
import ru.aleshin.features.home.impl.presentation.ui.templates.screenmodel.rememberTemplatesScreenModel
import ru.aleshin.features.home.impl.presentation.ui.templates.views.TemplateEditorDialog
import ru.aleshin.features.home.impl.presentation.ui.templates.views.TemplatesTopAppBar

/**
 * @author Stanislav Aleshin on 08.05.2023.
 */
internal class TemplatesScreen : Screen {

    @Composable
    @OptIn(ExperimentalMaterial3Api::class)
    override fun Content() = ScreenContent(
        screenModel = rememberTemplatesScreenModel(),
        initialState = TemplatesViewState(),
    ) { state ->
        val scope = rememberCoroutineScope()
        val snackbarState = remember { SnackbarHostState() }
        var isShowTemplateCreator by rememberSaveable { mutableStateOf(false) }
        val drawerManager = LocalDrawerManager.current
        val strings = HomeThemeRes.strings

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            content = { paddingValues ->
                TemplatesContent(
                    state = state,
                    modifier = Modifier.padding(paddingValues),
                    onChangeSortedType = { dispatchEvent(TemplatesEvent.UpdatedSortedType(it)) },
                    onChangeToggleStatus = { dispatchEvent(TemplatesEvent.UpdatedToggleStatus(it)) },
                    onUpdateTemplate = { dispatchEvent(TemplatesEvent.UpdateTemplate(it)) },
                    onDeleteTemplate = { dispatchEvent(TemplatesEvent.DeleteTemplate(it.templateId)) },
                )
            },
            topBar = {
                TemplatesTopAppBar(onMenuIconClick = { scope.launch { drawerManager?.openDrawer() } })
            },
            snackbarHost = {
                SnackbarHost(hostState = snackbarState)
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { isShowTemplateCreator = true },
                    content = {
                        Text(
                            text = HomeThemeRes.strings.addTemplatesFabTitle,
                            style = MaterialTheme.typography.titleLarge,
                        )
                    },
                )
            },
        )

        if (isShowTemplateCreator) {
            TemplateEditorDialog(
                categories = state.categories,
                editTemplateModel = null,
                onDismiss = { isShowTemplateCreator = false },
                onConfirm = { template ->
                    dispatchEvent(TemplatesEvent.AddTemplate(template))
                    isShowTemplateCreator = false
                },
            )
        }

        handleEffect { effect ->
            when (effect) {
                is TemplatesEffect.ShowError -> snackbarState.showSnackbar(
                    message = effect.failure.mapToMessage(strings).apply { Log.d("test", "error -> ${effect.failure}") },
                )
            }
        }

        LaunchedEffect(key1 = Unit, block = { dispatchEvent(TemplatesEvent.Init) })
    }
}
