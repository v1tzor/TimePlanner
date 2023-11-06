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
package ru.aleshin.core.ui.views

import androidx.compose.runtime.Composable
import ru.aleshin.core.ui.theme.TimePlannerRes
import ru.aleshin.core.utils.extensions.toDaysString
import ru.aleshin.core.utils.extensions.toMinutesAndHoursString
import ru.aleshin.core.utils.extensions.toMinutesOrHoursString

/**
 * @author Stanislav Aleshin on 08.03.2023.
 */
@Composable
fun Long.toMinutesOrHoursTitle(): String {
    val minutesSymbols = TimePlannerRes.strings.minutesSymbol
    val hoursSymbols = TimePlannerRes.strings.hoursSymbol

    return this.toMinutesOrHoursString(minutesSymbols, hoursSymbols)
}

@Composable
fun Long.toMinutesAndHoursTitle(): String {
    val minutesSymbols = TimePlannerRes.strings.minutesSymbol
    val hoursSymbols = TimePlannerRes.strings.hoursSymbol

    return this.toMinutesAndHoursString(minutesSymbols, hoursSymbols)
}

@Composable
fun Long.toDaysTitle(): String {
    val dayTitle = TimePlannerRes.strings.dayTitle

    return this.toDaysString(dayTitle)
}
