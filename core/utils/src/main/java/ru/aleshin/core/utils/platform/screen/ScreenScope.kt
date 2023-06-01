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
package ru.aleshin.core.utils.platform.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.runtime.saveable.rememberSaveable
import kotlinx.coroutines.CoroutineScope
import ru.aleshin.core.utils.platform.screenmodel.ContractProvider
import ru.aleshin.core.utils.platform.screenmodel.contract.*

/**
 * @author Stanislav Aleshin on 18.03.2023.
 */
interface ScreenScope<S : BaseViewState, E : BaseEvent, F : BaseUiEffect> {

    fun dispatchEvent(event: E)

    @Composable
    fun fetchState(): S

    @Composable
    fun handleEffect(block: suspend CoroutineScope.(F) -> Unit)

    class Base<S : BaseViewState, E : BaseEvent, F : BaseUiEffect>(
        private val contractProvider: ContractProvider<S, E, F>,
        internal val initialState: S,
    ) : ScreenScope<S, E, F> {

        override fun dispatchEvent(event: E) {
            contractProvider.dispatchEvent(event)
        }

        @Composable
        override fun fetchState(): S {
            val state = rememberSaveable { mutableStateOf(initialState) }
            LaunchedEffect(Unit) {
                contractProvider.collectState { state.value = it }
            }

            return state.value
        }

        @Composable
        override fun handleEffect(
            block: suspend CoroutineScope.(F) -> Unit,
        ) = LaunchedEffect(Unit) {
            contractProvider.collectUiEffect { effect -> block(effect) }
        }
    }
}

@Composable
fun <S : BaseViewState, E : BaseEvent, F : BaseUiEffect> rememberScreenScope(
    contractProvider: ContractProvider<S, E, F>,
    initialState: S,
): ScreenScope<S, E, F> {
    return rememberSaveable(saver = screenScopeSaver(contractProvider)) {
        ScreenScope.Base(contractProvider, initialState)
    }
}

private fun <S : BaseViewState, E : BaseEvent, F : BaseUiEffect> screenScopeSaver(
    contractProvider: ContractProvider<S, E, F>,
): Saver<ScreenScope.Base<S, E, F>, S> = object : Saver<ScreenScope.Base<S, E, F>, S> {

    override fun SaverScope.save(value: ScreenScope.Base<S, E, F>): S {
        return value.initialState
    }

    override fun restore(value: S): ScreenScope.Base<S, E, F> {
        return ScreenScope.Base(contractProvider, value)
    }
}
