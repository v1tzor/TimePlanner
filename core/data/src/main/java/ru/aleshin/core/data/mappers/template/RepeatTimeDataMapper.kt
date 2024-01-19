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
package ru.aleshin.core.data.mappers.template

import ru.aleshin.core.data.models.template.RepeatTimeEntity
import ru.aleshin.core.domain.entities.template.RepeatTime
import ru.aleshin.core.domain.entities.template.RepeatTimeType

/**
 * @author Stanislav Aleshin on 03.08.2023.
 */
fun RepeatTime.mapToData(templateId: Int) = when (this) {
    is RepeatTime.MonthDay -> mapToData(templateId)
    is RepeatTime.WeekDays -> mapToData(templateId)
    is RepeatTime.WeekDayInMonth -> mapToData(templateId)
    is RepeatTime.YearDay -> mapToData(templateId)
}

fun RepeatTimeEntity.mapToDomain() = when (type) {
    RepeatTimeType.WEEK_DAY -> mapToWeekDayRepeatTime()
    RepeatTimeType.WEEK_DAY_IN_MONTH -> mapToWeekDayInMonthRepeatTime()
    RepeatTimeType.MONTH_DAY -> mapToMonthRepeatTime()
    RepeatTimeType.YEAR_DAY -> mapToYearRepeatTime()
}

fun RepeatTimeEntity.mapToWeekDayRepeatTime() = RepeatTime.WeekDays(
    day = checkNotNull(day),
)

fun RepeatTimeEntity.mapToWeekDayInMonthRepeatTime() = RepeatTime.WeekDayInMonth(
    day = checkNotNull(day),
    weekNumber = checkNotNull(weekNumber),
)

fun RepeatTimeEntity.mapToMonthRepeatTime() = RepeatTime.MonthDay(
    dayNumber = checkNotNull(dayNumber),
)

fun RepeatTimeEntity.mapToYearRepeatTime() = RepeatTime.YearDay(
    month = checkNotNull(month),
    dayNumber = checkNotNull(dayNumber),
)

fun RepeatTime.WeekDays.mapToData(templateId: Int) = RepeatTimeEntity(
    templateId = templateId,
    type = RepeatTimeType.WEEK_DAY,
    day = day,
)

fun RepeatTime.WeekDayInMonth.mapToData(templateId: Int) = RepeatTimeEntity(
    templateId = templateId,
    type = RepeatTimeType.WEEK_DAY_IN_MONTH,
    day = day,
    weekNumber = weekNumber,
)

fun RepeatTime.MonthDay.mapToData(templateId: Int) = RepeatTimeEntity(
    templateId = templateId,
    type = RepeatTimeType.MONTH_DAY,
    dayNumber = dayNumber,
)

fun RepeatTime.YearDay.mapToData(templateId: Int) = RepeatTimeEntity(
    templateId = templateId,
    type = RepeatTimeType.YEAR_DAY,
    month = month,
    dayNumber = dayNumber,
)
