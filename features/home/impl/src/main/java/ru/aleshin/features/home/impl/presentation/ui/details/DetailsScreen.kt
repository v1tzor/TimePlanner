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
package ru.aleshin.features.home.impl.presentation.ui.details

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import ru.aleshin.core.ui.views.ErrorSnackbar
import ru.aleshin.core.utils.platform.screen.ScreenContent
import ru.aleshin.features.home.impl.presentation.mapppers.mapToMessage
import ru.aleshin.features.home.impl.presentation.theme.HomeTheme
import ru.aleshin.features.home.impl.presentation.theme.HomeThemeRes
import ru.aleshin.features.home.impl.presentation.ui.details.contract.DetailsEffect
import ru.aleshin.features.home.impl.presentation.ui.details.contract.DetailsEvent
import ru.aleshin.features.home.impl.presentation.ui.details.contract.DetailsViewState
import ru.aleshin.features.home.impl.presentation.ui.details.screenmodel.rememberDetailsScreenModel
import ru.aleshin.features.home.impl.presentation.ui.details.views.DetailsTopAppBar
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 06.11.2023
 */
internal class DetailsScreen @Inject constructor() : Screen {

    @Composable
    override fun Content() = ScreenContent(
        screenModel = rememberDetailsScreenModel(),
        initialState = DetailsViewState(),
    ) { state ->
        HomeTheme {
            val strings = HomeThemeRes.strings
            val snackbarState = remember { SnackbarHostState() }

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                content = { paddingValues ->
                    DetailsContent(
                        state = state,
                        modifier = Modifier.padding(paddingValues),
                        onOpenSchedule = { dispatchEvent(DetailsEvent.OpenSchedule(it)) },
                    )
                },
                topBar = {
                    DetailsTopAppBar(onNavIconClick = { dispatchEvent(DetailsEvent.PressBackButton) })
                },
                snackbarHost = {
                    SnackbarHost(
                        hostState = snackbarState,
                        snackbar = { ErrorSnackbar(it) },
                    )
                },
            )

            handleEffect { effect ->
                when (effect) {
                    is DetailsEffect.ShowError -> {
                        snackbarState.showSnackbar(
                            message = effect.failures.mapToMessage(strings),
                            withDismissAction = true,
                        )
                    }
                }
            }
        }
    }
}
