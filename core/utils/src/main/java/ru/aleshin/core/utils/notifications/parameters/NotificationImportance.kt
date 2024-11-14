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

import android.app.NotificationManager

/**
 * @author Stanislav Aleshin on 11.11.2024.
 */
enum class NotificationImportance(val importance: Int) {
    UNSPECIFIED(NotificationManager.IMPORTANCE_UNSPECIFIED),
    NONE(NotificationManager.IMPORTANCE_NONE),
    MIN(NotificationManager.IMPORTANCE_MIN),
    LOW(NotificationManager.IMPORTANCE_LOW),
    DEFAULT(NotificationManager.IMPORTANCE_DEFAULT),
    HIGH(NotificationManager.IMPORTANCE_HIGH),
    MAX(NotificationManager.IMPORTANCE_MAX)
}