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
package ru.aleshin.features.analytics.impl.presenatiton.models.timetask

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.aleshin.core.utils.functional.TimeRange
import ru.aleshin.features.analytics.impl.presenatiton.models.categories.MainCategoryUi
import ru.aleshin.features.analytics.impl.presenatiton.models.categories.SubCategoryUi
import java.util.Date

/**
 * @author Stanislav Aleshin on 30.07.2023.
 */
@Parcelize
internal data class TimeTaskUi(
    val key: Long = 0L,
    val date: Date,
    val timeRanges: TimeRange,
    val category: MainCategoryUi,
    val subCategory: SubCategoryUi? = null,
    val isCompleted: Boolean = true,
    val isImportant: Boolean = false,
    val isEnableNotification: Boolean = true,
    val taskNotifications: TaskNotificationsUi = TaskNotificationsUi(),
    val isConsiderInStatistics: Boolean = true,
    val note: String? = null,
) : Parcelable
