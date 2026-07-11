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
package ru.aleshin.core.utils.architecture.store.work

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.aleshin.core.utils.architecture.component.BaseInput
import ru.aleshin.core.utils.architecture.component.BaseOutput
import ru.aleshin.core.utils.architecture.store.BaseComposeStore
import ru.aleshin.core.utils.architecture.store.contract.StoreAction
import ru.aleshin.core.utils.architecture.store.contract.StoreEffect
import ru.aleshin.core.utils.architecture.store.contract.StoreEvent
import ru.aleshin.core.utils.architecture.store.contract.StoreState
import ru.aleshin.core.utils.managers.CoroutineBlock

/**
 * @author Stanislav Aleshin on 12.06.2023.
 */
interface WorkScope<S : StoreState, A : StoreAction, F : StoreEffect, O : BaseOutput> :
    WorkResultHandler<A, F, O> {

    fun launchBackgroundWork(
        key: BackgroundWorkKey,
        dispatcher: CoroutineDispatcher? = null,
        scope: CoroutineScope? = null,
        block: CoroutineBlock,
    ): Job

    fun state(): S

    suspend fun sendAction(action: A)

    fun sendEffect(effect: F)

    suspend fun consumeOutput(output: O)

    class Default<S : StoreState, E : StoreEvent, A : StoreAction, F : StoreEffect, I : BaseInput, O : BaseOutput>(
        private val store: BaseComposeStore<S, E, A, F, I, O>,
        private val coroutineScope: CoroutineScope,
    ) : WorkScope<S, A, F, O> {

        private val backgroundWorkMap = mutableMapOf<BackgroundWorkKey, Job>()

        override fun state(): S {
            return store.state
        }

        override suspend fun sendAction(action: A) {
            store.handleAction(action)
        }

        override fun sendEffect(effect: F) {
            store.postEffect(effect)
        }

        override suspend fun consumeOutput(output: O) {
            store.consumeOutput(output)
        }

        override fun launchBackgroundWork(
            key: BackgroundWorkKey,
            dispatcher: CoroutineDispatcher?,
            scope: CoroutineScope?,
            block: CoroutineBlock,
        ): Job {
            backgroundWorkMap[key].let { job ->
                if (job != null) {
                    job.cancel()
                    backgroundWorkMap.remove(key)
                }
            }
            return (scope ?: coroutineScope).launch {
                dispatcher?.let { withContext(it, block) } ?: block()
            }.apply {
                backgroundWorkMap[key] = this
                start()
            }
        }

        override suspend fun WorkResult<A, F, O>.handleWork() {
            when (this) {
                is WorkResult.Action<A> -> sendAction(action)
                is WorkResult.Effect<F> -> sendEffect(effect)
                is WorkResult.Output<O> -> consumeOutput(output)
            }
        }

        override suspend fun FlowWorkResult<A, F, O>.collectAndHandleWork() = collect { result ->
            result.handleWork()
        }
    }
}

interface BackgroundWorkKey

object MainWorkKey : BackgroundWorkKey