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
package ru.aleshin.timeplanner.presentation.ui.main.contract

import android.content.Intent
import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

/**
 * @author Stanislav Aleshin on 01.07.2026.
 */
@Immutable
@Serializable
data class ShareTarget(
    val text: String,
) {

    companion object {

        fun byIntent(intent: Intent): ShareTarget? {
            val text = intent.getCharSequenceExtra(Intent.EXTRA_TEXT)?.toString()?.trim()
            val isTextShare = intent.action == Intent.ACTION_SEND && intent.type?.startsWith("text/") == true
            return when {
                isTextShare && !text.isNullOrBlank() -> ShareTarget(text)
                else -> null
            }
        }
    }
}
