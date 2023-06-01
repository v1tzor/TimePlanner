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
package ru.aleshin.core.utils.platform.screenmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.FlowCollector
import ru.aleshin.core.utils.managers.CoroutineManager
import ru.aleshin.core.utils.platform.communications.state.EffectCommunicator
import ru.aleshin.core.utils.platform.communications.state.StateCommunicator
import ru.aleshin.core.utils.platform.screenmodel.contract.*
import ru.aleshin.core.utils.platform.screenmodel.store.launchedStore
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Provider

/**
 * @author Stanislav Aleshin on 21.03.2023.
 */
abstract class BaseViewModel<S : BaseViewState, E : BaseEvent, A : BaseAction, F : BaseUiEffect>(
    protected val stateCommunicator: StateCommunicator<S>,
    protected val effectCommunicator: EffectCommunicator<F>,
    coroutineManager: CoroutineManager,
) : ViewModel(), Reducer<S, A>, Actor<S, E, A, F>, ContractProvider<S, E, F> {

    private val scope get() = viewModelScope

    protected val isInitialize = AtomicBoolean(false)

    private val store = launchedStore(
        scope = scope,
        effectCommunicator = effectCommunicator,
        stateCommunicator = stateCommunicator,
        actor = this,
        reducer = this,
        coroutineManager = coroutineManager,
    )

    override fun init() {
        isInitialize.set(true)
    }

    override fun dispatchEvent(event: E) = store.sendEvent(event)

    override suspend fun collectState(collector: FlowCollector<S>) {
        store.collectState(collector)
    }

    override suspend fun collectUiEffect(collector: FlowCollector<F>) {
        store.collectUiEffect(collector)
    }

    abstract class Factory(
        private val viewModel: Provider<out ViewModel>,
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return viewModel.get() as T
        }
    }
}
