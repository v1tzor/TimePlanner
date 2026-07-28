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
package ru.aleshin.timeplanner.widgets.presentation.state

import kotlinx.serialization.json.Json

/**
 * @author Stanislav Aleshin on 28.07.2026.
 */
object WidgetStateCodec {

    val json = Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
    }

    inline fun <reified T> encode(value: T): String = json.encodeToString(value)

    inline fun <reified T> decodeOrDefault(value: String?, defaultValue: () -> T): T {
        return runCatching {
            value?.takeIf { it.isNotEmpty() }?.let { json.decodeFromString<T>(it) }
        }.getOrNull() ?: defaultValue()
    }

    inline fun <reified T> decodeCurrentOrDefault(
        value: String?,
        version: (T) -> Int,
        defaultValue: () -> T,
    ): T {
        val state = decodeOrDefault(value, defaultValue)
        return state.takeIf { version(it) == WidgetStateKeys.currentVersion } ?: defaultValue()
    }
}
