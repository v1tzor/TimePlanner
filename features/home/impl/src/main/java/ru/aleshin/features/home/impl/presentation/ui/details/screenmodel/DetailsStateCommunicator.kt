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
package ru.aleshin.features.home.impl.presentation.ui.details.screenmodel

import ru.aleshin.core.utils.platform.communications.state.StateCommunicator
import ru.aleshin.features.home.impl.presentation.ui.details.contract.DetailsViewState
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 06.11.2023.
 */
internal interface DetailsStateCommunicator : StateCommunicator<DetailsViewState> {

    class Base @Inject constructor() : DetailsStateCommunicator,
        StateCommunicator.Abstract<DetailsViewState>(defaultState = DetailsViewState())
}
