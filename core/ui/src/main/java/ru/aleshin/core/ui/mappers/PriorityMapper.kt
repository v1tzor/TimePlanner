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
package ru.aleshin.core.ui.mappers

import ru.aleshin.core.domain.entities.schedules.TaskPriority
import ru.aleshin.core.ui.views.MonogramPriority

/**
 * @author Stanislav Aleshin on 16.01.2024.
 */
fun TaskPriority.mapToUi() = when (this) {
    TaskPriority.STANDARD -> MonogramPriority.STANDARD
    TaskPriority.MEDIUM -> MonogramPriority.MEDIUM
    TaskPriority.MAX -> MonogramPriority.MAX
}