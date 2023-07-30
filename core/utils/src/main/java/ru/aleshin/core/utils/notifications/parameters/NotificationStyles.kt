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

import android.graphics.Bitmap
import androidx.core.app.NotificationCompat

/**
 * @author Stanislav Aleshin on 28.03.2023.
 */
sealed class NotificationStyles {

    abstract val style: NotificationCompat.Style

    data class BigTextStyle(
        val text: String,
        val bigTitle: String? = null,
        val summary: String? = null,
    ) : NotificationStyles() {
        override val style: NotificationCompat.Style
            get() {
                val style = NotificationCompat.BigTextStyle().bigText(text)
                if (bigTitle != null) style.setBigContentTitle(bigTitle)
                if (summary != null) style.setSummaryText(summary)
                return style
            }
    }

    data class BigPictureStyle(
        val bitMap: Bitmap?,
        val largeIcon: Bitmap? = null,
        val bigTitle: String? = null,
        val summary: String? = null,
    ) : NotificationStyles() {
        override val style: NotificationCompat.Style
            get() {
                val style = NotificationCompat.BigPictureStyle().bigPicture(bitMap)
                if (largeIcon != null) style.bigLargeIcon(largeIcon)
                if (bigTitle != null) style.setBigContentTitle(bigTitle)
                if (summary != null) style.setSummaryText(summary)
                return style
            }
    }

    data class InboxStyle(
        val lines: List<String>,
        val summary: String? = null,
    ) : NotificationStyles() {
        override val style: NotificationCompat.Style
            get() {
                val style = NotificationCompat.InboxStyle()
                if (summary != null) style.setSummaryText(summary)
                lines.forEach { line -> style.addLine(line) }
                return style
            }
    }

    data class Other(override val style: NotificationCompat.Style) : NotificationStyles()
}
