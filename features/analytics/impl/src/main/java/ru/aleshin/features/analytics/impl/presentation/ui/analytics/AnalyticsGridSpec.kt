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

/**
 * @author Stanislav Aleshin on 26.07.2026.
 */
internal object AnalyticsGridSpec {

    const val MEDIUM_COLUMN_COUNT = 8
    const val EXPANDED_COLUMN_COUNT = 12

    fun fetchColumnCount(isExpanded: Boolean): Int {
        return if (isExpanded) EXPANDED_COLUMN_COUNT else MEDIUM_COLUMN_COUNT
    }

    fun fetchSpan(
        section: AnalyticsGridSection,
        isExpanded: Boolean,
    ): Int {
        val columnCount = fetchColumnCount(isExpanded)
        if (!isExpanded) return columnCount
        return when (section) {
            AnalyticsGridSection.SUMMARY -> 5
            AnalyticsGridSection.KEY_METRICS -> 5
            AnalyticsGridSection.CATEGORIES -> columnCount
            AnalyticsGridSection.LOAD -> columnCount
            AnalyticsGridSection.CREATION -> 5
            AnalyticsGridSection.REGULARITY -> 5
            AnalyticsGridSection.HOURS -> 5
            AnalyticsGridSection.DURATION -> 7
            AnalyticsGridSection.SOURCE -> 5
        }
    }
}
