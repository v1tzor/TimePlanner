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

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import ru.aleshin.core.utils.extensions.hoursToMillis
import ru.aleshin.core.utils.extensions.minutesToMillis
import java.util.Date

/**
 * @author Stanislav Aleshin on 30.08.2026.
 */
class DateManagerTest {

    private val dateManager = DateManager.Base(TestWorkDispatchersProvider())

    @Test
    fun calculateProgress_returnsFullForZeroDurationInPast() {
        val moment = Date(System.currentTimeMillis() - 1.hoursToMillis())

        val progress = dateManager.calculateProgress(moment, moment)

        assertFalse(progress.isNaN())
        assertEquals(1f, progress, 0f)
    }

    @Test
    fun calculateProgress_returnsEmptyForZeroDurationInFuture() {
        val moment = Date(System.currentTimeMillis() + 1.hoursToMillis())

        val progress = dateManager.calculateProgress(moment, moment)

        assertFalse(progress.isNaN())
        assertEquals(0f, progress, 0f)
    }

    @Test
    fun calculateProgress_returnsFullForZeroDurationAtCurrentMoment() {
        val moment = Date(System.currentTimeMillis())

        val progress = dateManager.calculateProgress(moment, moment)

        assertFalse(progress.isNaN())
        assertEquals(1f, progress, 0f)
    }

    @Test
    fun calculateProgress_returnsEmptyForNotStartedTask() {
        val currentTime = System.currentTimeMillis()
        val startTime = Date(currentTime + 1.hoursToMillis())
        val endTime = Date(currentTime + 2.hoursToMillis())

        val progress = dateManager.calculateProgress(startTime, endTime)

        assertFalse(progress.isNaN())
        assertEquals(0f, progress, 0f)
    }

    @Test
    fun calculateProgress_returnsFullForPassedTask() {
        val currentTime = System.currentTimeMillis()
        val startTime = Date(currentTime - 2.hoursToMillis())
        val endTime = Date(currentTime - 1.hoursToMillis())

        val progress = dateManager.calculateProgress(startTime, endTime)

        assertFalse(progress.isNaN())
        assertEquals(1f, progress, 0f)
    }

    @Test
    fun calculateProgress_returnsHalfForRunningTask() {
        val currentTime = System.currentTimeMillis()
        val startTime = Date(currentTime - 30.minutesToMillis())
        val endTime = Date(currentTime + 30.minutesToMillis())

        val progress = dateManager.calculateProgress(startTime, endTime)

        assertFalse(progress.isNaN())
        assertEquals(0.5f, progress, 0.05f)
    }

    @Test
    fun calculateProgress_returnsValueInBoundsForReversedRange() {
        val currentTime = System.currentTimeMillis()
        val startTime = Date(currentTime + 1.hoursToMillis())
        val endTime = Date(currentTime - 1.hoursToMillis())

        val progress = dateManager.calculateProgress(startTime, endTime)

        assertFalse(progress.isNaN())
        assertTrue(progress in 0f..1f)
    }

    @Test
    fun calculateProgress_returnsValueInBoundsForShortTask() {
        val currentTime = System.currentTimeMillis()
        val startTime = Date(currentTime - 1.minutesToMillis())
        val endTime = Date(currentTime - 1.minutesToMillis() + 1)

        val progress = dateManager.calculateProgress(startTime, endTime)

        assertFalse(progress.isNaN())
        assertTrue(progress in 0f..1f)
    }

    private class TestWorkDispatchersProvider : WorkDispatchersProvider {

        override val ioDispatcher: CoroutineDispatcher = Dispatchers.Unconfined
        override val defaultDispatcher: CoroutineDispatcher = Dispatchers.Unconfined
        override val uiDispatcher: CoroutineDispatcher = Dispatchers.Unconfined
    }
}
