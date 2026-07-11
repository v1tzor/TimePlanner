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

package ru.aleshin.core.utils.architecture.store

import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.isActive
import kotlinx.coroutines.sync.Mutex
import ru.aleshin.core.utils.architecture.component.BaseInput
import ru.aleshin.core.utils.architecture.component.BaseOutput
import ru.aleshin.core.utils.architecture.component.EmptyInput
import ru.aleshin.core.utils.architecture.component.EmptyOutput
import ru.aleshin.core.utils.architecture.component.OutputConsumer
import ru.aleshin.core.utils.architecture.store.communicators.EffectCommunicator
import ru.aleshin.core.utils.architecture.store.communicators.StateCommunicator
import ru.aleshin.core.utils.architecture.store.contract.StoreAction
import ru.aleshin.core.utils.architecture.store.contract.StoreEffect
import ru.aleshin.core.utils.architecture.store.contract.StoreEvent
import ru.aleshin.core.utils.architecture.store.contract.StoreState
import ru.aleshin.core.utils.architecture.store.functional.Actor
import ru.aleshin.core.utils.architecture.store.functional.Reducer
import ru.aleshin.core.utils.architecture.store.work.WorkScope
import ru.aleshin.core.utils.managers.CoroutineFlow
import ru.aleshin.core.utils.managers.CoroutineManager

/**
 * @author Stanislav Aleshin on 16.08.2025.
 */
abstract class BaseComposeStore<S : StoreState, E : StoreEvent, A : StoreAction, F : StoreEffect, I : BaseInput, O : BaseOutput>(
    protected val coroutineManager: CoroutineManager,
    private val stateCommunicator: StateCommunicator<S>,
    private val effectCommunicator: EffectCommunicator<F>,
) : InstanceKeeper.Instance, ComposeStore<S, E, F, I>, Actor<S, E, A, F, O>, Reducer<S, A> {

    override val state: S get() = stateCommunicator.getValue()

    private val mutex = Mutex()

    protected val mainJob: Job = SupervisorJob()

    protected val storeScope = CoroutineScope(mainJob + coroutineManager.defaultDispatcher)

    private var innerOutputConsumer: OutputConsumer<O>? = null

    private val eventChannel = Channel<E>(Channel.UNLIMITED, BufferOverflow.SUSPEND)

    init {
        startStore()
    }

    override fun dispatchEvent(event: E) {
        eventChannel.trySend(event)
    }

    override suspend fun collectState(collector: FlowCollector<S>) {
        stateCommunicator.collect(collector)
    }

    override suspend fun collectEffects(collector: FlowCollector<F>) {
        effectCommunicator.collect(collector)
    }

    override fun initialize(input: I, isRestore: Boolean) = Unit

    override fun onDestroy() {
        super.onDestroy()
        mainJob.cancel()
    }

    fun setOutputConsumer(outputConsumer: OutputConsumer<O>) {
        innerOutputConsumer = outputConsumer
    }

    suspend fun consumeOutput(output: O) = coroutineManager.changeFlow(CoroutineFlow.UI) {
        innerOutputConsumer?.consume(output)
    }

    internal suspend fun updateState(transform: suspend (S) -> S) = mutex.withReentrantLock {
        val state = transform(stateCommunicator.getValue())
        stateCommunicator.update(state)
    }

    internal suspend fun handleAction(action: A) = updateState { currentState ->
        reduce(action, currentState)
    }

    internal fun postEffect(effect: F) {
        effectCommunicator.update(effect)
    }

    internal fun startStore() = coroutineManager.runOnIOBackground(storeScope) {
        val workScope = WorkScope.Default(store = this@BaseComposeStore, coroutineScope = this)

        while (isActive) {
            workScope.handleEvent(eventChannel.receive())
        }
    }

    interface Factory<Store, S : StoreState> {
        fun create(savedState: S): Store
    }
}

abstract class BaseSimpleComposeStore<S : StoreState, E : StoreEvent, A : StoreAction, F : StoreEffect>(
    stateCommunicator: StateCommunicator<S>,
    effectCommunicator: EffectCommunicator<F>,
    coroutineManager: CoroutineManager,
) : BaseComposeStore<S, E, A, F, EmptyInput, EmptyOutput>(
    stateCommunicator = stateCommunicator,
    effectCommunicator = effectCommunicator,
    coroutineManager = coroutineManager,
) {
    interface Factory<Store, S : StoreState> : BaseComposeStore.Factory<Store, S>
}

abstract class BaseOnlyInComposeStore<S : StoreState, E : StoreEvent, A : StoreAction, F : StoreEffect, I : BaseInput>(
    stateCommunicator: StateCommunicator<S>,
    effectCommunicator: EffectCommunicator<F>,
    coroutineManager: CoroutineManager,
) : BaseComposeStore<S, E, A, F, I, EmptyOutput>(
    stateCommunicator = stateCommunicator,
    effectCommunicator = effectCommunicator,
    coroutineManager = coroutineManager,
) {
    interface Factory<Store, S : StoreState> : BaseComposeStore.Factory<Store, S>
}

abstract class BaseOnlyOutComposeStore<S : StoreState, E : StoreEvent, A : StoreAction, F : StoreEffect, O : BaseOutput>(
    stateCommunicator: StateCommunicator<S>,
    effectCommunicator: EffectCommunicator<F>,
    coroutineManager: CoroutineManager,
) : BaseComposeStore<S, E, A, F, EmptyInput, O>(
    stateCommunicator = stateCommunicator,
    effectCommunicator = effectCommunicator,
    coroutineManager = coroutineManager,
) {
    interface Factory<Store, S : StoreState> : BaseComposeStore.Factory<Store, S>
}