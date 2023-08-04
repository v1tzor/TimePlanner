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
package ru.aleshin.features.home.api.presentation.mappers

import androidx.compose.runtime.Composable
import ru.aleshin.core.ui.theme.TimePlannerRes
import ru.aleshin.core.ui.theme.tokens.TimePlannerStrings
import ru.aleshin.features.home.api.domain.entities.template.RepeatTimeType

/**
 * @author Stanislav Aleshin on 03.08.2023.
 */
fun RepeatTimeType.mapToString(strings: TimePlannerStrings): String = when (this) {
    RepeatTimeType.WEEK_DAY -> strings.repeatTimeDayInWeekTitle
    RepeatTimeType.WEEK_DAY_IN_MONTH -> strings.repeatTimeWeekDayInMonthTitle
    RepeatTimeType.MONTH_DAY -> strings.repeatTimeDayInMonthTitle
    RepeatTimeType.YEAR_DAY -> strings.repeatTimeDayInYearTitle
}

@Composable
fun RepeatTimeType.mapToString() = mapToString(TimePlannerRes.strings)
