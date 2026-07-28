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

package ru.aleshin.features.analytics.impl.presentation.ui.analytics

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * @author Stanislav Aleshin on 26.07.2026.
 */
class AnalyticsGridSpecTest {

    @Test
    fun `uses planned medium spans`() {
        assertEquals(8, AnalyticsGridSpec.fetchColumnCount(isExpanded = false))
        assertEquals(8, fetchMediumSpan(AnalyticsGridSection.SUMMARY))
        assertEquals(8, fetchMediumSpan(AnalyticsGridSection.KEY_METRICS))
        assertEquals(8, fetchMediumSpan(AnalyticsGridSection.CATEGORIES))
        assertEquals(8, fetchMediumSpan(AnalyticsGridSection.LOAD))
        assertEquals(8, fetchMediumSpan(AnalyticsGridSection.CREATION))
        assertEquals(8, fetchMediumSpan(AnalyticsGridSection.REGULARITY))
        assertEquals(8, fetchMediumSpan(AnalyticsGridSection.HOURS))
        assertEquals(8, fetchMediumSpan(AnalyticsGridSection.DURATION))
        assertEquals(8, fetchMediumSpan(AnalyticsGridSection.SOURCE))
    }

    @Test
    fun `uses planned expanded spans`() {
        assertEquals(12, AnalyticsGridSpec.fetchColumnCount(isExpanded = true))
        assertEquals(5, fetchExpandedSpan(AnalyticsGridSection.SUMMARY))
        assertEquals(5, fetchExpandedSpan(AnalyticsGridSection.KEY_METRICS))
        assertEquals(12, fetchExpandedSpan(AnalyticsGridSection.CATEGORIES))
        assertEquals(12, fetchExpandedSpan(AnalyticsGridSection.LOAD))
        assertEquals(5, fetchExpandedSpan(AnalyticsGridSection.CREATION))
        assertEquals(5, fetchExpandedSpan(AnalyticsGridSection.REGULARITY))
        assertEquals(5, fetchExpandedSpan(AnalyticsGridSection.HOURS))
        assertEquals(7, fetchExpandedSpan(AnalyticsGridSection.DURATION))
        assertEquals(5, fetchExpandedSpan(AnalyticsGridSection.SOURCE))
    }

    private fun fetchMediumSpan(section: AnalyticsGridSection): Int {
        return AnalyticsGridSpec.fetchSpan(
            section = section,
            isExpanded = false,
        )
    }

    private fun fetchExpandedSpan(section: AnalyticsGridSection): Int {
        return AnalyticsGridSpec.fetchSpan(
            section = section,
            isExpanded = true,
        )
    }
}
