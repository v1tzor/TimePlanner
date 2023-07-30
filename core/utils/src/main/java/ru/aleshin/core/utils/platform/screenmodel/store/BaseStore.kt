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
package ru.aleshin.core.utils.platform.screenmodel.store

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.isActive
import kotlinx.coroutines.sync.Mutex
import ru.aleshin.core.utils.managers.CoroutineManager
import ru.aleshin.core.utils.platform.communications.state.StateCommunicator
import ru.aleshin.core.utils.platform.screenmodel.Actor
import ru.aleshin.core.utils.platform.screenmodel.Reducer
import ru.aleshin.core.utils.platform.screenmodel.StateProvider
import ru.aleshin.core.utils.platform.screenmodel.UiEffectProvider
import ru.aleshin.core.utils.platform.screenmodel.contract.*
import ru.aleshin.core.utils.platform.screenmodel.withReentrantLock
import ru.aleshin.core.utils.platform.screenmodel.work.WorkScope

/**
 * @author Stanislav Aleshin on 18.03.2023.
 */
interface BaseStore<S : BaseViewState, E : BaseEvent, A : BaseAction, F : BaseUiEffect> :
    StateProvider<S>,
    UiEffectProvider<F> {

    fun sendEvent(event: E)
    suspend fun postEffect(effect: F)
    suspend fun handleAction(action: A)
    suspend fun updateState(transform: suspend (S) -> S)
    suspend fun fetchState(): S

    abstract class Abstract<S : BaseViewState, E : BaseEvent, A : BaseAction, F : BaseUiEffect>(
        private val stateCommunicator: StateCommunicator<S>,
        private val actor: Actor<S, E, A, F>,
        private val reducer: Reducer<S, A>,
        private val coroutineManager: CoroutineManager,
    ) : BaseStore<S, E, A, F> {

        private val mutex = Mutex()

        private val eventChannel = Channel<E>(Channel.UNLIMITED, BufferOverflow.SUSPEND)

        fun start(scope: CoroutineScope) = coroutineManager.runOnBackground(scope) {
            val workScope = WorkScope.Base(this@Abstract, scope)
            while (isActive) {
                actor.apply { workScope.handleEvent(eventChannel.receive()) }
            }
        }

        override fun sendEvent(event: E) {
            eventChannel.trySend(event)
        }

        override suspend fun fetchState(): S {
            return stateCommunicator.read()
        }

        override suspend fun updateState(transform: suspend (S) -> S) = mutex.withReentrantLock {
            val state = transform(stateCommunicator.read())
            stateCommunicator.update(state)
        }

        override suspend fun handleAction(action: A) = updateState {
            reducer.reduce(action, fetchState())
        }

        override suspend fun collectState(collector: FlowCollector<S>) {
            stateCommunicator.collect(collector)
        }
    }
}
