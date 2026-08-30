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
package ru.aleshin.features.editor.impl.presentation.ui.task.validators

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import ru.aleshin.core.utils.extensions.setHoursAndMinutes
import ru.aleshin.core.utils.extensions.setStartDay
import ru.aleshin.core.utils.extensions.shiftDay
import ru.aleshin.core.utils.functional.TimeRange
import java.util.Calendar
import java.util.Date

/**
 * @author Stanislav Aleshin on 30.08.2026.
 */
internal class TimeRangeValidatorTest {

    private val validator = TimeRangeValidator.Base()

    private val scheduleDate = Calendar.getInstance().apply {
        set(2026, Calendar.AUGUST, 30)
        setStartDay()
    }.time

    @Test
    fun validate_acceptsNormalRange() {
        val result = validator.validate(TimeRange(scheduleDate.at(9, 0), scheduleDate.at(10, 0)))

        assertTrue(result.isValid)
    }

    @Test
    fun validate_acceptsRangeEndingAtMidnight() {
        val result = validator.validate(
            TimeRange(scheduleDate.at(23, 0), scheduleDate.shiftDay(1)),
        )

        assertTrue(result.isValid)
    }

    @Test
    fun validate_acceptsOvernightRange() {
        val result = validator.validate(
            TimeRange(scheduleDate.at(23, 0), scheduleDate.shiftDay(1).at(1, 30)),
        )

        assertTrue(result.isValid)
    }

    @Test
    fun validate_rejectsZeroDurationRange() {
        val result = validator.validate(TimeRange(scheduleDate.at(10, 0), scheduleDate.at(10, 0)))

        assertFalse(result.isValid)
    }

    @Test
    fun validate_rejectsReversedRange() {
        val result = validator.validate(TimeRange(scheduleDate.at(10, 0), scheduleDate.at(9, 0)))

        assertFalse(result.isValid)
    }

    @Test
    fun validate_rejectsRangeLongerThanDay() {
        val result = validator.validate(
            TimeRange(scheduleDate.at(9, 0), scheduleDate.shiftDay(1).at(10, 0)),
        )

        assertFalse(result.isValid)
    }

    private fun Date.at(hour: Int, minute: Int): Date {
        return Calendar.getInstance().apply {
            time = this@at
            setHoursAndMinutes(hour, minute)
        }.time
    }
}
