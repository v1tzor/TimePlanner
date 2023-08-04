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
package ru.aleshin.core.ui.mappers

import androidx.compose.runtime.Composable
import ru.aleshin.core.ui.theme.TimePlannerRes
import ru.aleshin.core.ui.theme.tokens.TimePlannerStrings
import ru.aleshin.core.utils.functional.Month

/**
 * @author Stanislav Aleshin on 03.08.2023.
 */
fun Month.mapToString(strings: TimePlannerStrings): String = when (this) {
    Month.JANUARY -> strings.januaryTitle
    Month.FEBRUARY -> strings.februaryTitle
    Month.MARCH -> strings.marchTitle
    Month.APRIL -> strings.aprilTitle
    Month.MAY -> strings.mayTitle
    Month.JUNE -> strings.juneTitle
    Month.JULY -> strings.julyTitle
    Month.AUGUST -> strings.augustTitle
    Month.SEPTEMBER -> strings.septemberTitle
    Month.OCTOBER -> strings.octoberTitle
    Month.NOVEMBER -> strings.novemberTitle
    Month.DECEMBER -> strings.decemberTitle
}

@Composable
fun Month.mapToString() = mapToString(TimePlannerRes.strings)
