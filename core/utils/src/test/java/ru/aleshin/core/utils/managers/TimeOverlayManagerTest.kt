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
package ru.aleshin.core.utils.managers

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Test
import ru.aleshin.core.utils.extensions.setStartDay
import ru.aleshin.core.utils.extensions.shiftHours
import ru.aleshin.core.utils.functional.TimeRange
import java.util.Calendar

/**
 * @author Stanislav Aleshin on 01.07.2026.
 */
class TimeOverlayManagerTest {

    private lateinit var overlayManager: TimeOverlayManager

    @Before
    fun setUp() {
        overlayManager = TimeOverlayManager.Base()
    }

    @Test
    fun test_same_end_nested_task_is_overlay() {
        val date = Calendar.getInstance().apply { setStartDay() }.time
        val existedRange = TimeRange(from = date.shiftHours(9), to = date.shiftHours(17))
        val currentRange = TimeRange(from = date.shiftHours(15), to = date.shiftHours(17))

        val actual = overlayManager.isOverlay(currentRange, listOf(existedRange))

        assertTrue(actual.isOverlay)
        assertEquals(date.shiftHours(17), actual.leftTimeBorder)
    }

    @Test
    fun test_same_border_tasks_are_not_overlay() {
        val date = Calendar.getInstance().apply { setStartDay() }.time
        val existedRange = TimeRange(from = date.shiftHours(9), to = date.shiftHours(17))
        val currentRange = TimeRange(from = date.shiftHours(17), to = date.shiftHours(18))

        val actual = overlayManager.isOverlay(currentRange, listOf(existedRange))

        assertFalse(actual.isOverlay)
    }
}
