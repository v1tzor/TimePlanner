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

package ru.aleshin.core.utils.architecture.store.communicators

import ru.aleshin.core.utils.architecture.communications.Communicator
import ru.aleshin.core.utils.architecture.store.contract.StoreState

/**
 * @author Stanislav Aleshin on 12.06.2023.
 */
abstract class StateCommunicator<S : StoreState>(defaultState: S) : Communicator.AbstractStateFlow<S>(
    defaultValue = defaultState
) {
    class Default<S : StoreState>(defaultState: S) : StateCommunicator<S>(defaultState)
}