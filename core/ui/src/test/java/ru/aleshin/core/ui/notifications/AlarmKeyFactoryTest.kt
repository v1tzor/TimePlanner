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
package ru.aleshin.core.ui.notifications

import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Test
import ru.aleshin.core.domain.entities.schedules.TaskNotificationType
import ru.aleshin.core.domain.entities.template.RepeatTime
import ru.aleshin.core.ui.models.NotificationTimeType
import ru.aleshin.core.utils.functional.WeekDay

/**
 * @author Stanislav Aleshin on 01.07.2026.
 */
class AlarmKeyFactoryTest {

    private lateinit var alarmKeyFactory: AlarmKeyFactory

    @Before
    fun setUp() {
        alarmKeyFactory = AlarmKeyFactory.Base()
    }

    @Test
    fun test_time_task_alarm_ids_are_different_for_notification_types() {
        val startId = alarmKeyFactory.fetchTimeTaskAlarmId(100L, TaskNotificationType.START)
        val beforeId = alarmKeyFactory.fetchTimeTaskAlarmId(100L, TaskNotificationType.ONE_HOUR_BEFORE)

        assertTrue(startId != beforeId)
    }

    @Test
    fun test_template_alarm_ids_are_different_for_repeat_times() {
        val mondayId = alarmKeyFactory.fetchTemplateAlarmId(
            templateId = 1,
            repeatTime = RepeatTime.WeekDays(WeekDay.MONDAY),
            timeType = NotificationTimeType.START_TASK,
        )
        val tuesdayId = alarmKeyFactory.fetchTemplateAlarmId(
            templateId = 1,
            repeatTime = RepeatTime.WeekDays(WeekDay.TUESDAY),
            timeType = NotificationTimeType.START_TASK,
        )

        assertTrue(mondayId != tuesdayId)
    }
}
