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

package ru.aleshin.core.utils.architecture.store

import kotlinx.coroutines.flow.FlowCollector
import ru.aleshin.core.utils.architecture.component.BaseInput
import ru.aleshin.core.utils.architecture.store.contract.StoreEffect
import ru.aleshin.core.utils.architecture.store.contract.StoreEvent
import ru.aleshin.core.utils.architecture.store.contract.StoreState

/**
 * @author Stanislav Aleshin on 16.08.2025.
 */
interface ComposeStore<S : StoreState, E : StoreEvent, F : StoreEffect, I : BaseInput> {

    val state: S

    fun initialize(input: I, isRestore: Boolean)
    fun dispatchEvent(event: E)
    suspend fun collectState(collector: FlowCollector<S>)
    suspend fun collectEffects(collector: FlowCollector<F>)
}