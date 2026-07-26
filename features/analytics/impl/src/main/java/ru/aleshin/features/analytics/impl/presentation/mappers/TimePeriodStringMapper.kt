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
package ru.aleshin.features.analytics.impl.presentation.mappers

import ru.aleshin.core.utils.functional.TimePeriod
import ru.aleshin.features.analytics.impl.presentation.theme.tokens.AnalyticsStrings

/**
 * @author Stanislav Aleshin on 23.07.2026.
 */
internal fun TimePeriod.toRangeTitle(strings: AnalyticsStrings) = when (this) {
    TimePeriod.LAST_7_DAYS -> strings.lastSevenDays
    TimePeriod.WEEK -> strings.week
    TimePeriod.MONTH -> strings.month
    TimePeriod.HALF_YEAR -> strings.halfYear
    TimePeriod.YEAR -> strings.year
    TimePeriod.CUSTOM -> strings.selectDateRange
}

internal fun TimePeriod.toSummaryTitle(strings: AnalyticsStrings) = when (this) {
    TimePeriod.LAST_7_DAYS -> strings.summaryLastSevenDaysTitle
    TimePeriod.WEEK -> strings.summaryWeekTitle
    TimePeriod.MONTH -> strings.summaryMonthTitle
    TimePeriod.HALF_YEAR -> strings.summaryHalfYearTitle
    TimePeriod.YEAR -> strings.summaryYearTitle
    TimePeriod.CUSTOM -> strings.summaryTitle
}