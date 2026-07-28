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
package ru.aleshin.timeplanner.presentation.widgets.main

import org.junit.Assert.assertEquals
import org.junit.Test
import ru.aleshin.core.domain.entities.tasks.TimeTaskStatus
import ru.aleshin.core.utils.functional.TimeRange
import ru.aleshin.timeplanner.domain.entities.TimeTasks
import java.util.Date

/**
 * @author Stanislav Aleshin on 28.07.2026.
 */
class MainWidgetTest {

    @Test
    fun `decode time tasks returns empty value for corrupted state`() {
        val actual = decodeTimeTasks("invalid")

        assertEquals(TimeTasks(), actual)
    }

    @Test
    fun `time task is running at start boundary`() {
        val status = fetchWidgetTimeTaskStatus(
            currentTime = Date(1_000L),
            timeRange = TimeRange(Date(1_000L), Date(2_000L)),
        )

        assertEquals(TimeTaskStatus.RUNNING, status)
    }

    @Test
    fun `time task is completed at end boundary`() {
        val status = fetchWidgetTimeTaskStatus(
            currentTime = Date(2_000L),
            timeRange = TimeRange(Date(1_000L), Date(2_000L)),
        )

        assertEquals(TimeTaskStatus.COMPLETED, status)
    }
}
