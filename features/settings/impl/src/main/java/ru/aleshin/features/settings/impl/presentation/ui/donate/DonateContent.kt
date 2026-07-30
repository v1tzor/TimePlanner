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
package ru.aleshin.features.settings.impl.presentation.ui.donate

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import ru.aleshin.core.utils.architecture.store.compose.stateAsState
import ru.aleshin.features.settings.impl.presentation.ui.donate.contract.DonateEvent
import ru.aleshin.features.settings.impl.presentation.ui.donate.store.DonateComponent
import ru.aleshin.features.settings.impl.presentation.ui.donate.views.DonateTopAppBar
import ru.aleshin.timeplanner.core.ui.views.AdaptiveLayoutInfo
import ru.aleshin.timeplanner.core.ui.views.rememberAdaptiveLayoutInfo

/**
 * @author Stanislav Aleshin on 13.10.2023.
 */
@Composable
internal fun DonateContent(
    modifier: Modifier = Modifier,
    donateComponent: DonateComponent,
    adaptiveLayoutInfo: AdaptiveLayoutInfo = rememberAdaptiveLayoutInfo(),
) {
    val store = donateComponent.store
    val state by store.stateAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            DonateTopAppBar(
                isCompact = adaptiveLayoutInfo.isCompactWidth,
                onNavButtonClick = { store.dispatchEvent(DonateEvent.PressBackButton) },
            )
        },
        contentWindowInsets = WindowInsets()
    ) { paddingValues ->
        DonateLayout(
            state = state,
            modifier = Modifier.padding(paddingValues),
            adaptiveLayoutInfo = adaptiveLayoutInfo,
        )
    }
}
