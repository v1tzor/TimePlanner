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
package ru.aleshin.core.utils.platform.communications.state

import ru.aleshin.core.utils.platform.communications.Communicator
import ru.aleshin.core.utils.platform.screenmodel.contract.BaseUiEffect
import ru.aleshin.core.utils.platform.screenmodel.contract.BaseViewState
import ru.aleshin.core.utils.platform.screenmodel.contract.EmptyUiEffect

/**
 * @author Stanislav Aleshin on 14.02.2023.
 */
interface StateCommunicator<S : BaseViewState> : Communicator<S> {

    abstract class Abstract<S : BaseViewState>(defaultState: S) : StateCommunicator<S>,
        Communicator.AbstractStateFlow<S>(defaultState)
}

interface EffectCommunicator<F : BaseUiEffect> : Communicator<F> {

    abstract class Abstract<F : BaseUiEffect> : EffectCommunicator<F>,
        Communicator.AbstractSharedFlow<F>(flowBufferCapacity = 1)

    class Empty<F : EmptyUiEffect> : Abstract<F>()
}
