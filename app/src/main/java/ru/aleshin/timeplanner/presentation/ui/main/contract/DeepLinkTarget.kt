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
import android.net.Uri
import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable
import ru.aleshin.core.utils.functional.Constants.App.ANALYTICS_DEEP_LINK_PATH
import ru.aleshin.core.utils.functional.Constants.App.DATE_QUERY
import ru.aleshin.core.utils.functional.Constants.App.DEEP_LINK_HOST
import ru.aleshin.core.utils.functional.Constants.App.DEEP_LINK_SCHEME
import ru.aleshin.core.utils.functional.Constants.App.EDITOR_DEEP_LINK_PATH
import ru.aleshin.core.utils.functional.Constants.App.FROM_QUERY
import ru.aleshin.core.utils.functional.Constants.App.HOME_DEEP_LINK_PATH
import ru.aleshin.core.utils.functional.Constants.App.OVERVIEW_DEEP_LINK_PATH
import ru.aleshin.core.utils.functional.Constants.App.TIME_TASK_ID_QUERY
import ru.aleshin.core.utils.functional.Constants.App.TO_QUERY
import ru.aleshin.core.utils.functional.Constants.App.UNDEFINED_TASK_ID_QUERY

/**
 * @author Stanislav Aleshin on 16.11.2024.
 */
@Immutable
@Serializable
sealed interface DeepLinkTarget {

    @Serializable
    data class Editor(
        val timeTaskId: Long? = null,
        val undefinedTaskId: Long? = null,
        val date: Long? = null,
        val from: Long? = null,
        val to: Long? = null,
    ) : DeepLinkTarget

    @Serializable
    data class Home(val date: Long? = null) : DeepLinkTarget

    @Serializable
    data object Overview : DeepLinkTarget

    @Serializable
    data object Analytics : DeepLinkTarget

    companion object {

        fun byIntent(intent: Intent): DeepLinkTarget? {
            return runCatching { parseIntent(intent) }.getOrNull()
        }

        private fun parseIntent(intent: Intent): DeepLinkTarget? {
            val uri = intent.data ?: return null
            if (intent.action != Intent.ACTION_VIEW) return null
            if (uri.scheme != DEEP_LINK_SCHEME || uri.host != DEEP_LINK_HOST) return null
            if (uri.hasInvalidLongParameters()) return null

            return when (uri.path) {
                EDITOR_DEEP_LINK_PATH -> Editor(
                    timeTaskId = uri.fetchLong(TIME_TASK_ID_QUERY),
                    undefinedTaskId = uri.fetchLong(UNDEFINED_TASK_ID_QUERY),
                    date = uri.fetchLong(DATE_QUERY),
                    from = uri.fetchLong(FROM_QUERY),
                    to = uri.fetchLong(TO_QUERY),
                ).takeIf { target ->
                    (target.from == null) == (target.to == null) &&
                        !(target.timeTaskId != null && target.undefinedTaskId != null)
                }
                HOME_DEEP_LINK_PATH -> Home(date = uri.fetchLong(DATE_QUERY))
                OVERVIEW_DEEP_LINK_PATH -> Overview
                ANALYTICS_DEEP_LINK_PATH -> Analytics
                else -> null
            }
        }

        private fun Uri.hasInvalidLongParameters(): Boolean {
            return LONG_PARAMETERS.any { parameter ->
                getQueryParameter(parameter)?.toLongOrNull() == null &&
                    getQueryParameter(parameter) != null
            }
        }

        private fun Uri.fetchLong(parameter: String): Long? {
            return getQueryParameter(parameter)?.toLongOrNull()
        }

        private val LONG_PARAMETERS = listOf(
            TIME_TASK_ID_QUERY,
            UNDEFINED_TASK_ID_QUERY,
            DATE_QUERY,
            FROM_QUERY,
            TO_QUERY,
        )
    }
}
