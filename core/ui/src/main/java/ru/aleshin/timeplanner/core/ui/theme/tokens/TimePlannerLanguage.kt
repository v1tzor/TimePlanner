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
package ru.aleshin.timeplanner.core.ui.theme.tokens

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.intl.Locale
import kotlinx.serialization.Serializable

/**
 * @author Stanislav Aleshin on 14.02.2023.
 */
@Serializable
@Immutable
enum class TimePlannerLanguage(val code: String) {
    EN("en"),
    RU("ru"),
    DE("de"),
    ES("es"),
    FA("fa"),
    FR("fr"),
    PT_BR("pt-br"),
    TR("tr"),
    VN("vn"),
    PL("pl"),
    IT("it"),
    ZH("zh"),
}

@Immutable
@Serializable
enum class LanguageUiType(val code: String?) {
    DEFAULT(null),
    EN("en"),
    RU("ru"),
    DE("de"),
    ES("es"),
    FA("fa"),
    FR("fr"),
    PT_BR("pt-br"),
    TR("tr"),
    VN("vn"),
    PL("pl"),
    IT("it"),
    ZH("zh"),
}

val LocalTimePlannerLanguage = staticCompositionLocalOf<TimePlannerLanguage> {
    error("Language is not provided")
}

fun fetchCoreLanguage(code: String): TimePlannerLanguage {
    return TimePlannerLanguage.entries.find { it.code == code } ?: TimePlannerLanguage.EN
}

fun fetchAppLanguage(languageType: LanguageUiType) = when (languageType) {
    LanguageUiType.DEFAULT -> fetchCoreLanguage(Locale.current.language)
    LanguageUiType.EN -> TimePlannerLanguage.EN
    LanguageUiType.RU -> TimePlannerLanguage.RU
    LanguageUiType.DE -> TimePlannerLanguage.DE
    LanguageUiType.ES -> TimePlannerLanguage.ES
    LanguageUiType.FA -> TimePlannerLanguage.FA
    LanguageUiType.FR -> TimePlannerLanguage.FR
    LanguageUiType.PT_BR -> TimePlannerLanguage.PT_BR
    LanguageUiType.TR -> TimePlannerLanguage.TR
    LanguageUiType.VN -> TimePlannerLanguage.VN
    LanguageUiType.PL -> TimePlannerLanguage.PL
    LanguageUiType.IT -> TimePlannerLanguage.IT
    LanguageUiType.ZH -> TimePlannerLanguage.ZH
}
