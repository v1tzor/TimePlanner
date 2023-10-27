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
package ru.aleshin.features.settings.impl.presentation.models

/**
 * @author Stanislav Aleshin on 14.09.2023.
 */
enum class CryptoAddress(val cryptoName: String, val address: String) {
    BTC("BITCOIN", "bc1qu0a5ujldf8rpc8yz8atlgphrj9wutgfxw82dql"),
    BNB("BNB SMART CHAIN", "0xbA08E4905B3d52f480419A8444C4de3E91BC98df"),
    ETH("ETHEREUM", "0x4cAfa6De0D1968cA8C2a7aB06CE28d0A1aD2C7b9"),
    TRX("TRON", "TKC3NsKSS9hJRvofeJKceT5wC2bqTkPRUE"),
    LTC("LITECOIN", "ltc1qj9fsz4pxrvr3eqyel4q8jnsnfpcfwdsj3mvpec"),
    XEC("ECASH(XEC)", "ecash:qqc0k95nfhkseel9p4avz5jwk6s4vum7rceegten5x"),
    AVAX("AVALANCHE", "0xbA08E4905B3d52f480419A8444C4de3E91BC98df"),
    FTM("FANTOM", "0xbA08E4905B3d52f480419A8444C4de3E91BC98df"),
}
