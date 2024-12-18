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
package ru.aleshin.core.utils.platform.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import ru.aleshin.core.utils.platform.screenmodel.ContractProvider
import ru.aleshin.core.utils.platform.screenmodel.EmptyDeps
import ru.aleshin.core.utils.platform.screenmodel.ScreenDependencies
import ru.aleshin.core.utils.platform.screenmodel.contract.BaseEvent
import ru.aleshin.core.utils.platform.screenmodel.contract.BaseUiEffect
import ru.aleshin.core.utils.platform.screenmodel.contract.BaseViewState

/**
 * @author Stanislav Aleshin on 19.03.2023.
 */
@Composable
fun <S : BaseViewState, E : BaseEvent, F : BaseUiEffect, D : ScreenDependencies, CP : ContractProvider<S, E, F, D>> ScreenContent(
    screenModel: CP,
    initialState: S,
    dependencies: D,
    content: @Composable ScreenScope<S, E, F, D>.(state: S) -> Unit,
) {
    LaunchedEffect(key1 = Unit) { screenModel.init(dependencies) }
    val screenScope = rememberScreenScope(
        contractProvider = screenModel,
        initialState = initialState,
    )
    content.invoke(screenScope, screenScope.fetchState())
}

@Composable
fun <S : BaseViewState, E : BaseEvent, F : BaseUiEffect, CP : ContractProvider<S, E, F, EmptyDeps>> ScreenContent(
    screenModel: CP,
    initialState: S,
    content: @Composable ScreenScope<S, E, F, EmptyDeps>.(state: S) -> Unit,
) = ScreenContent(screenModel, initialState, EmptyDeps, content)