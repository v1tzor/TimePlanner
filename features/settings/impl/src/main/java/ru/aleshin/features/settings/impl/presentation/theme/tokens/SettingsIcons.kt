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
    val git: Int,
    val cryptoBitcoin: Int,
    val cryptoBNB: Int,
    val cryptoEthereum: Int,
    val cryptoTron: Int,
    val cryptoLitecoin: Int,
    val cryptoECash: Int,
    val copy: Int,
    val crypto: Int,
    val cryptoAvax: Int,
    val cryptoFtm: Int,
)

internal val baseSettingIcons = SettingsIcons(
    back = R.drawable.ic_back,
    git = R.drawable.ic_github,
    cryptoBitcoin = R.drawable.ic_bitcoin,
    cryptoBNB = R.drawable.ic_bnb,
    cryptoEthereum = R.drawable.ic_ethereum,
    cryptoTron = R.drawable.ic_tron,
    cryptoLitecoin = R.drawable.ic_litecoin,
    cryptoECash = R.drawable.ic_ecash_xec,
    cryptoAvax = R.drawable.ic_avax,
    cryptoFtm = R.drawable.ic_fantom,
    copy = R.drawable.ic_content_copy,
    crypto = R.drawable.ic_crypto,
)

internal val LocalSettingsIcons = staticCompositionLocalOf<SettingsIcons> {
    error("Settings Icons is not provided")
}

internal fun fetchSettingsIcons() = baseSettingIcons
