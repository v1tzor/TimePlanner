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

import ru.aleshin.core.domain.entities.tasks.TaskNotificationType
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 01.07.2026.
 */
interface AlarmKeyFactory {

    fun fetchTimeTaskAlarmTag(timeTaskKey: Long, notificationType: TaskNotificationType): String
    fun fetchTimeTaskAlarmId(timeTaskKey: Long, notificationType: TaskNotificationType): Int
    fun fetchTemplateAlarmTag(templateId: Long): String
    fun fetchTemplateAlarmId(templateId: Long): Int

    class Base @Inject constructor() : AlarmKeyFactory {

        override fun fetchTimeTaskAlarmTag(timeTaskKey: Long, notificationType: TaskNotificationType): String {
            return "$TIME_TASK_PREFIX:$timeTaskKey:${notificationType.name}"
        }

        override fun fetchTimeTaskAlarmId(timeTaskKey: Long, notificationType: TaskNotificationType): Int {
            return calculateHash(TIME_TASK_PREFIX, timeTaskKey, notificationType.name)
        }

        override fun fetchTemplateAlarmTag(templateId: Long): String {
            return "$TEMPLATE_PREFIX:$templateId"
        }

        override fun fetchTemplateAlarmId(templateId: Long): Int {
            return calculateHash(TEMPLATE_PREFIX, templateId)
        }

        private fun calculateHash(vararg values: Any): Int {
            return values.fold(INITIAL_HASH) { hash, value -> HASH_MULTIPLIER * hash + value.hashCode() }
        }

        private companion object {
            const val TIME_TASK_PREFIX = "time_task"
            const val TEMPLATE_PREFIX = "template"
            const val INITIAL_HASH = 17
            const val HASH_MULTIPLIER = 31
        }
    }
}
