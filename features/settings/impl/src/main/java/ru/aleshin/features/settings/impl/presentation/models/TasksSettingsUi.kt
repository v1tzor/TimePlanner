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
package ru.aleshin.features.settings.impl.presentation.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.aleshin.core.ui.views.CalendarButtonBehavior
import ru.aleshin.core.ui.views.ViewToggleStatus

/**
 * @author Stanislav Aleshin on 15.09.2023.
 */
@Parcelize
data class TasksSettingsUi(
    val taskViewStatus: ViewToggleStatus = ViewToggleStatus.COMPACT,
    val calendarButtonBehavior: CalendarButtonBehavior = CalendarButtonBehavior.SET_CURRENT_DATE,
    val secureMode: Boolean = false,
) : Parcelable
