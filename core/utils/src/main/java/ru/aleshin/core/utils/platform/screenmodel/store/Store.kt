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
import ru.aleshin.core.utils.managers.CoroutineManager
import ru.aleshin.core.utils.platform.communications.state.EffectCommunicator
import ru.aleshin.core.utils.platform.communications.state.StateCommunicator
import ru.aleshin.core.utils.platform.screenmodel.Actor
import ru.aleshin.core.utils.platform.screenmodel.Reducer
import ru.aleshin.core.utils.platform.screenmodel.contract.*

/**
 * @author Stanislav Aleshin on 19.03.2023.
 */
fun <S : BaseViewState, E : BaseEvent, A : BaseAction, F : BaseUiEffect> MVIStore(
    effectCommunicator: EffectCommunicator<F>,
    stateCommunicator: StateCommunicator<S>,
    actor: Actor<S, E, A, F>,
    reducer: Reducer<S, A>,
    coroutineManager: CoroutineManager,
) = SharedStore(effectCommunicator, stateCommunicator, actor, reducer, coroutineManager)

fun <S : BaseViewState, E : BaseEvent, A : BaseAction, F : BaseUiEffect> launchedStore(
    scope: CoroutineScope,
    effectCommunicator: EffectCommunicator<F>,
    stateCommunicator: StateCommunicator<S>,
    actor: Actor<S, E, A, F>,
    reducer: Reducer<S, A>,
    coroutineManager: CoroutineManager,
) = MVIStore(effectCommunicator, stateCommunicator, actor, reducer, coroutineManager).apply {
    start(scope)
}
