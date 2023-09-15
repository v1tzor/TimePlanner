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
package ru.aleshin.features.settings.impl.presentation.theme.tokens

import androidx.compose.runtime.staticCompositionLocalOf
import ru.aleshin.features.settings.impl.R

/**
 * @author Stanislav Aleshin on 23.02.2023.
 */
internal data class SettingsIcons(
    val back: Int,
    val default: Int,
    val git: Int,
)

internal val baseSettingIcons = SettingsIcons(
    back = R.drawable.ic_back,
    default = R.drawable.ic_restart,
    git = R.drawable.ic_github,
)

internal val LocalSettingsIcons = staticCompositionLocalOf<SettingsIcons> {
    error("Settings Icons is not provided")
}

internal fun fetchSettingsIcons() = baseSettingIcons
