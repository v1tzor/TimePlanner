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
 * imitations under the License.
 */
package ru.aleshin.core.ui.theme.tokens

import androidx.compose.runtime.staticCompositionLocalOf

/**
 * @author Stanislav Aleshin on 14.02.2023.
 */
enum class TimePlannerLanguage(val code: String) {
    EN("en"), RU("ru"), DE("de"), ES("es")
}

enum class LanguageUiType {
    DEFAULT, EN, RU, DE, ES
}

val LocalTimePlannerLanguage = staticCompositionLocalOf<TimePlannerLanguage> {
    error("Language is not provided")
}

fun fetchAppLanguage(language: String) = when (language) {
    "ru" -> TimePlannerLanguage.RU
    "en" -> TimePlannerLanguage.EN
    "de" -> TimePlannerLanguage.DE
    "es" -> TimePlannerLanguage.ES
    else -> TimePlannerLanguage.EN
}
