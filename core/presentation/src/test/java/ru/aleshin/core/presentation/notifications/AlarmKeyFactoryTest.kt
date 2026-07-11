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
package ru.aleshin.core.presentation.notifications

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import ru.aleshin.core.domain.entities.tasks.TaskNotificationType

/**
 * @author Stanislav Aleshin on 09.07.2026.
 */
class AlarmKeyFactoryTest {

    private val factory = AlarmKeyFactory.Base()

    @Test
    fun test_time_task_alarm_tag_is_stable_and_contains_notification_type() {
        val startTag = factory.fetchTimeTaskAlarmTag(42L, TaskNotificationType.START)
        val beforeEndTag = factory.fetchTimeTaskAlarmTag(42L, TaskNotificationType.AFTER_START_BEFORE_END)

        assertEquals(startTag, factory.fetchTimeTaskAlarmTag(42L, TaskNotificationType.START))
        assertNotEquals(startTag, beforeEndTag)
    }

    @Test
    fun test_alarm_tags_are_unique_between_time_task_and_template() {
        val timeTaskTag = factory.fetchTimeTaskAlarmTag(42L, TaskNotificationType.START)
        val templateTag = factory.fetchTemplateAlarmTag(templateId = 42L)

        assertNotEquals(timeTaskTag, templateTag)
    }

    @Test
    fun test_template_alarm_tag_is_stable_for_template() {
        val firstTag = factory.fetchTemplateAlarmTag(templateId = 42L)
        val secondTag = factory.fetchTemplateAlarmTag(templateId = 42L)

        assertEquals(firstTag, secondTag)
    }
}
