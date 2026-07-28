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
package ru.aleshin.timeplanner.widgets.presentation.theme

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.glance.LocalContext
import ru.aleshin.timeplanner.core.ui.theme.TimePlannerRes
import ru.aleshin.timeplanner.core.ui.theme.tokens.TimePlannerLanguage
import ru.aleshin.timeplanner.core.ui.theme.tokens.fetchCoreLanguage
import java.util.Locale

/**
 * @author Stanislav Aleshin on 28.07.2026.
 */
fun Context.fetchWidgetString(
    language: String,
    @StringRes resource: Int,
    vararg arguments: Any,
): String {
    return localizedContext(language).getString(resource, *arguments)
}

fun Context.fetchWidgetQuantityString(
    language: String,
    @PluralsRes resource: Int,
    quantity: Int,
    vararg arguments: Any,
): String {
    return localizedContext(language).resources.getQuantityString(resource, quantity, *arguments)
}

fun fetchWidgetLocale(language: String): Locale {
    val languageTag = if (language == VIETNAMESE_LEGACY_CODE) VIETNAMESE_LANGUAGE_CODE else language

    return Locale.forLanguageTag(languageTag)
}

fun fetchWidgetLanguage(language: String): TimePlannerLanguage {
    val languageCode = if (language == VIETNAMESE_LANGUAGE_CODE) VIETNAMESE_LEGACY_CODE else language

    return fetchCoreLanguage(languageCode)
}

@Composable
fun widgetString(
    @StringRes resource: Int,
    vararg arguments: Any,
): String {
    return LocalContext.current.fetchWidgetString(TimePlannerRes.language.code, resource, *arguments)
}

@Composable
fun widgetQuantityString(
    @PluralsRes resource: Int,
    quantity: Int,
    vararg arguments: Any,
): String {
    return LocalContext.current.fetchWidgetQuantityString(TimePlannerRes.language.code, resource, quantity, *arguments)
}

@SuppressLint("AppBundleLocaleChanges")
private fun Context.localizedContext(language: String): Context {
    val configuration = Configuration(resources.configuration)
    configuration.setLocale(fetchWidgetLocale(language))

    return createConfigurationContext(configuration)
}

internal const val VIETNAMESE_LANGUAGE_CODE = "vi"
internal const val VIETNAMESE_LEGACY_CODE = "vn"
