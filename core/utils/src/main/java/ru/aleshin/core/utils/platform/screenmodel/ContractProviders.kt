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
package ru.aleshin.core.utils.platform.screenmodel

import kotlinx.coroutines.flow.FlowCollector
import ru.aleshin.core.utils.platform.screenmodel.contract.BaseEvent
import ru.aleshin.core.utils.platform.screenmodel.contract.BaseUiEffect
import ru.aleshin.core.utils.platform.screenmodel.contract.BaseViewState

/**
 * @author Stanislav Aleshin on 18.03.2023.
 */
interface StateProvider<S : BaseViewState> {
    suspend fun collectState(collector: FlowCollector<S>)
}

interface UiEffectProvider<F : BaseUiEffect> {
    suspend fun collectUiEffect(collector: FlowCollector<F>)
}

interface EventReceiver<E : BaseEvent> {
    fun dispatchEvent(event: E)
}

interface ContractProvider<S : BaseViewState, E : BaseEvent, F : BaseUiEffect> :
    StateProvider<S>,
    EventReceiver<E>,
    UiEffectProvider<F>,
    Init
