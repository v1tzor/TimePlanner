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
package ru.aleshin.timeplanner.widgets.presentation.navigation

import android.content.Context
import android.content.Intent
import android.net.Uri
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
 * @author Stanislav Aleshin on 28.07.2026.
 */
object WidgetDeepLinkFactory {

    fun createEditTaskIntent(
        context: Context,
        timeTaskId: Long,
    ): Intent = createIntent(
        context = context,
        path = EDITOR_DEEP_LINK_PATH,
        parameters = mapOf(TIME_TASK_ID_QUERY to timeTaskId),
    )

    fun createUndefinedTaskIntent(
        context: Context,
        undefinedTaskId: Long,
        date: Long,
        from: Long,
        to: Long,
    ): Intent = createIntent(
        context = context,
        path = EDITOR_DEEP_LINK_PATH,
        parameters = mapOf(
            UNDEFINED_TASK_ID_QUERY to undefinedTaskId,
            DATE_QUERY to date,
            FROM_QUERY to from,
            TO_QUERY to to,
        ),
    )

    fun createTaskCreatorIntent(
        context: Context,
        date: Long,
        from: Long,
        to: Long,
    ): Intent = createIntent(
        context = context,
        path = EDITOR_DEEP_LINK_PATH,
        parameters = mapOf(DATE_QUERY to date, FROM_QUERY to from, TO_QUERY to to),
    )

    fun createHomeIntent(
        context: Context,
        date: Long,
    ): Intent = createIntent(
        context = context,
        path = HOME_DEEP_LINK_PATH,
        parameters = mapOf(DATE_QUERY to date),
    )

    fun createOverviewIntent(context: Context): Intent {
        return createIntent(context, OVERVIEW_DEEP_LINK_PATH)
    }

    fun createAnalyticsIntent(context: Context): Intent {
        return createIntent(context, ANALYTICS_DEEP_LINK_PATH)
    }

    private fun createIntent(
        context: Context,
        path: String,
        parameters: Map<String, Long> = emptyMap(),
    ): Intent {
        val builder = Uri.Builder()
            .scheme(DEEP_LINK_SCHEME)
            .authority(DEEP_LINK_HOST)
            .path(path)
        parameters.forEach { (key, value) -> builder.appendQueryParameter(key, value.toString()) }
        return Intent(Intent.ACTION_VIEW, builder.build()).setPackage(context.packageName)
    }
}
