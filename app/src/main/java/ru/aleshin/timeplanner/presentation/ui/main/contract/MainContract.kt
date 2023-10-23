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
package ru.aleshin.timeplanner.presentation.ui.main.contract

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.aleshin.core.ui.theme.material.ColorsUiType
import ru.aleshin.core.ui.theme.material.ThemeUiType
import ru.aleshin.core.ui.theme.tokens.LanguageUiType
import ru.aleshin.core.utils.platform.screenmodel.contract.*

/**
 * @author Stanislav Aleshin on 14.02.2023.
 */
@Parcelize
data class MainViewState(
    val language: LanguageUiType = LanguageUiType.DEFAULT,
    val theme: ThemeUiType = ThemeUiType.DEFAULT,
    val colors: ColorsUiType = ColorsUiType.PINK,
    val isEnableDynamicColors: Boolean = false,
    val secureMode: Boolean = false,
) : BaseViewState, Parcelable

sealed class MainEvent : BaseEvent {
    object Init : MainEvent()
}

sealed class MainEffect : EmptyUiEffect

sealed class MainAction : MainEffect(), BaseAction {
    object Navigate : MainAction()
    data class ChangeSettings(
        val language: LanguageUiType,
        val theme: ThemeUiType,
        val colors: ColorsUiType,
        val enableDynamicColors: Boolean,
        val secureMode: Boolean,
    ) : MainAction()
}
