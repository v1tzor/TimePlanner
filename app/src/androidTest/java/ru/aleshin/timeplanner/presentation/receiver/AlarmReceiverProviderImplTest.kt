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
package ru.aleshin.timeplanner.presentation.receiver

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import ru.aleshin.core.domain.entities.tasks.TaskNotificationType
import ru.aleshin.core.domain.entities.template.RepeatTime
import ru.aleshin.core.presentation.models.NotificationTimeTypeUi
import ru.aleshin.core.presentation.models.toTimeType
import ru.aleshin.core.presentation.notifications.AlarmKeyFactory
import ru.aleshin.core.utils.functional.Constants
import ru.aleshin.core.utils.functional.WeekDay
import java.util.Date

/**
 * @author Stanislav Aleshin on 09.07.2026.
 */
@RunWith(AndroidJUnit4::class)
class AlarmReceiverProviderImplTest {

    private val provider = AlarmReceiverProviderImpl(
        context = InstrumentationRegistry.getInstrumentation().targetContext,
        alarmKeyFactory = AlarmKeyFactory.Base(),
    )

    @Test
    fun test_time_task_alarm_intents_have_different_pending_intent_identity() {
        val startIntent = provideTimeTaskIntent(TaskNotificationType.START)
        val beforeEndIntent = provideTimeTaskIntent(TaskNotificationType.AFTER_START_BEFORE_END)

        assertNotEquals(startIntent.data, beforeEndIntent.data)
        assertFalse(startIntent.filterEquals(beforeEndIntent))
    }

    @Test
    fun test_template_alarm_intents_have_same_pending_intent_identity() {
        val tuesdayIntent = provideTemplateIntent(
            repeatTime = RepeatTime.WeekDays(WeekDay.TUESDAY),
            triggerTime = Date(1L),
        )
        val wednesdayIntent = provideTemplateIntent(
            repeatTime = RepeatTime.WeekDays(WeekDay.WEDNESDAY),
            triggerTime = Date(2L),
        )

        assertEquals(tuesdayIntent.data, wednesdayIntent.data)
        assertTrue(tuesdayIntent.filterEquals(wednesdayIntent))
        assertNotEquals(
            tuesdayIntent.getLongExtra(Constants.Alarm.TEMPLATE_NOTIFICATION_TRIGGER_TIME, 0L),
            wednesdayIntent.getLongExtra(Constants.Alarm.TEMPLATE_NOTIFICATION_TRIGGER_TIME, 0L),
        )
    }

    private fun provideTimeTaskIntent(notificationType: TaskNotificationType) = provider.provideReceiverIntent(
        category = "Category",
        subCategory = "Subcategory",
        icon = null,
        appIcon = 1,
        notificationId = 1,
        timeTaskId = 42L,
        taskNotificationType = notificationType,
        timeType = notificationType.toTimeType(),
    )

    private fun provideTemplateIntent(repeatTime: RepeatTime, triggerTime: Date) = provider.provideReceiverIntent(
        category = "Category",
        subCategory = "Subcategory",
        icon = null,
        appIcon = 1,
        notificationId = 1,
        templateId = 42L,
        repeatTime = repeatTime,
        templateNotificationTriggerTime = triggerTime,
        timeType = NotificationTimeTypeUi.START_TASK,
    )
}
