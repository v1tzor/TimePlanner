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

package ru.aleshin.core.utils.architecture.store.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import kotlinx.coroutines.CoroutineScope
import ru.aleshin.core.utils.architecture.component.BaseInput
import ru.aleshin.core.utils.architecture.store.ComposeStore
import ru.aleshin.core.utils.architecture.store.contract.StoreEffect
import ru.aleshin.core.utils.architecture.store.contract.StoreEvent
import ru.aleshin.core.utils.architecture.store.contract.StoreState

/**
 * @author Stanislav Aleshin on 20.08.2025.
 */
@Composable
inline fun <S : StoreState, E : StoreEvent, F : StoreEffect, I : BaseInput> ComposeStore<S, E, F, I>.handleEffects(
    crossinline block: suspend CoroutineScope.(F) -> Unit,
) {
    LaunchedEffect(Unit) {
        collectEffects { effect -> block(effect) }
    }
}

@Composable
fun <S : StoreState, E : StoreEvent, F : StoreEffect, I : BaseInput> ComposeStore<S, E, F, I>.stateAsState(): State<S> {
    return produceState(initialValue = state) {
        collectState {
            if (value != it) {
                value = it
            }
        }
    }
}