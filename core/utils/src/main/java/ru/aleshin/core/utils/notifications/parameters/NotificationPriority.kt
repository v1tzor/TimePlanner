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
package ru.aleshin.core.utils.notifications.parameters

import androidx.core.app.NotificationCompat

/**
 * @author Stanislav Aleshin on 28.03.2023.
 */
enum class NotificationPriority(val importance: Int) {
    DEFAULT(NotificationCompat.PRIORITY_DEFAULT),
    MIN(NotificationCompat.PRIORITY_MIN),
    LOW(NotificationCompat.PRIORITY_LOW),
    HIGH(NotificationCompat.PRIORITY_HIGH),
    MAX(NotificationCompat.PRIORITY_MAX),
}
