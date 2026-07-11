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
package ru.aleshin.features.settings.impl.presentation.ui.donate.contract

import kotlinx.serialization.Serializable
import ru.aleshin.core.utils.architecture.component.BaseOutput
import ru.aleshin.core.utils.architecture.store.contract.StoreAction
import ru.aleshin.core.utils.architecture.store.contract.StoreEffect
import ru.aleshin.core.utils.architecture.store.contract.StoreEvent
import ru.aleshin.core.utils.architecture.store.contract.StoreState

/**
 * @author Stanislav Aleshin on 13.10.2023
 */
@Serializable
internal sealed class DonateState : StoreState {
    object Default : DonateState()
}

internal sealed class DonateEvent : StoreEvent {
    object PressBackButton : DonateEvent()
}

internal sealed class DonateEffect : StoreEffect

internal sealed class DonateAction : StoreAction

internal sealed class DonateOutput : BaseOutput {
    data object NavigateToBack : DonateOutput()
}
