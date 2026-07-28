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

import org.junit.Assert.assertEquals
import org.junit.Test
import ru.aleshin.timeplanner.core.ui.theme.tokens.TimePlannerLanguage

/**
 * @author Stanislav Aleshin on 28.07.2026.
 */
class WidgetStringsTest {

    @Test
    fun `maps legacy Vietnamese language code to Android locale`() {
        val locale = fetchWidgetLocale(VIETNAMESE_LEGACY_CODE)

        assertEquals(VIETNAMESE_LANGUAGE_CODE, locale.language)
    }

    @Test
    fun `preserves supported language tag`() {
        val locale = fetchWidgetLocale("pt-br")

        assertEquals("pt-BR", locale.toLanguageTag())
    }

    @Test
    fun `maps Android Vietnamese code to application language`() {
        val language = fetchWidgetLanguage(VIETNAMESE_LANGUAGE_CODE)

        assertEquals(TimePlannerLanguage.VN, language)
    }
}
