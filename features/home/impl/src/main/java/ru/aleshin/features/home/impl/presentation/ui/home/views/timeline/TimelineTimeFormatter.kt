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
package ru.aleshin.features.home.impl.presentation.ui.home.views.timeline

import android.text.format.DateFormat
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import java.text.DateFormat as JavaDateFormat

/**
 * @author Stanislav Aleshin on 01.08.2026.
 */
@Composable
internal fun rememberTimelineTimeFormatter(): JavaDateFormat {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val is24HourFormat = DateFormat.is24HourFormat(context)
    return remember(context, configuration, is24HourFormat) {
        DateFormat.getTimeFormat(context)
    }
}

/**
 * @author Stanislav Aleshin on 01.08.2026.
 */
@Composable
internal fun rememberTimelineUses24HourFormat(): Boolean {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val is24HourFormat = DateFormat.is24HourFormat(context)
    return remember(context, configuration, is24HourFormat) { is24HourFormat }
}
