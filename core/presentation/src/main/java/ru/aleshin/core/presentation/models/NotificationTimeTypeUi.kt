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
package ru.aleshin.core.presentation.models

import ru.aleshin.core.domain.entities.tasks.TaskNotificationType

/**
 * @author Stanislav Aleshin on 10.11.2023.
 */
enum class NotificationTimeTypeUi {
    BEFORE_TASK, START_TASK, AFTER_TASK
}

fun TaskNotificationType.toTimeType() = when (this) {
    TaskNotificationType.START -> NotificationTimeTypeUi.START_TASK
    TaskNotificationType.FIFTEEN_MINUTES_BEFORE -> NotificationTimeTypeUi.BEFORE_TASK
    TaskNotificationType.ONE_HOUR_BEFORE -> NotificationTimeTypeUi.BEFORE_TASK
    TaskNotificationType.THREE_HOUR_BEFORE -> NotificationTimeTypeUi.BEFORE_TASK
    TaskNotificationType.ONE_DAY_BEFORE -> NotificationTimeTypeUi.BEFORE_TASK
    TaskNotificationType.ONE_WEEK_BEFORE -> NotificationTimeTypeUi.BEFORE_TASK
    TaskNotificationType.AFTER_START_BEFORE_END -> NotificationTimeTypeUi.AFTER_TASK
    TaskNotificationType.END_ONGOING -> NotificationTimeTypeUi.AFTER_TASK
}
