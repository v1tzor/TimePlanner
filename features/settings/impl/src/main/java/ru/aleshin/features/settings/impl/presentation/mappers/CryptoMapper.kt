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
package ru.aleshin.features.settings.impl.presentation.mappers

import androidx.compose.runtime.Composable
import ru.aleshin.features.settings.impl.presentation.models.CryptoAddress
import ru.aleshin.features.settings.impl.presentation.models.CryptoAddress.*
import ru.aleshin.features.settings.impl.presentation.theme.SettingsThemeRes

/**
 * @author Stanislav Aleshin on 13.10.2023.
 */
@Composable
internal fun CryptoAddress.mapToIcon() = when (this) {
    BTC -> SettingsThemeRes.icons.cryptoBitcoin
    BNB -> SettingsThemeRes.icons.cryptoBNB
    ETH -> SettingsThemeRes.icons.cryptoEthereum
    TRX -> SettingsThemeRes.icons.cryptoTron
    LTC -> SettingsThemeRes.icons.cryptoLitecoin
    XEC -> SettingsThemeRes.icons.cryptoECash
    AVAX -> SettingsThemeRes.icons.cryptoAvax
    FTM -> SettingsThemeRes.icons.cryptoFtm
}
