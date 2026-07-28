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
package ru.aleshin.timeplanner.widgets.presentation.work

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.Date
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 28.07.2026.
 */
interface WidgetsUpdateScheduler {

    fun enqueueImmediate()

    class Base @Inject constructor(
        private val context: Context
    ) : WidgetsUpdateScheduler {
        override fun enqueueImmediate() = enqueueImmediate(context)
    }

    companion object {

        fun enqueueImmediate(context: Context) {
            val workManager = WorkManager.getInstance(context.applicationContext)

            workManager.enqueueUniqueWork(
                uniqueWorkName = IMMEDIATE_WORK_NAME,
                existingWorkPolicy = ExistingWorkPolicy.REPLACE,
                request = OneTimeWorkRequestBuilder<WidgetsUpdateWorker>().build()
            )

            workManager.enqueueUniquePeriodicWork(
                uniqueWorkName = PERIODIC_WORK_NAME,
                existingPeriodicWorkPolicy = ExistingPeriodicWorkPolicy.KEEP,
                request = PeriodicWorkRequestBuilder<WidgetsUpdateWorker>(
                    repeatInterval = 1,
                    repeatIntervalTimeUnit = TimeUnit.HOURS
                ).build()
            )
        }

        fun scheduleBoundary(context: Context, boundary: Date) {
            val delay = (boundary.time - System.currentTimeMillis()).coerceAtLeast(MINIMUM_DELAY)

            WorkManager.getInstance(context.applicationContext).enqueueUniqueWork(
                uniqueWorkName = BOUNDARY_WORK_NAME,
                existingWorkPolicy = ExistingWorkPolicy.REPLACE,
                request = OneTimeWorkRequestBuilder<WidgetsUpdateWorker>()
                    .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                    .build()
            )
        }

        fun cancelBoundary(context: Context) {
            WorkManager.getInstance(context.applicationContext).cancelUniqueWork(BOUNDARY_WORK_NAME)
        }

        private const val IMMEDIATE_WORK_NAME = "WIDGETS_IMMEDIATE_UPDATE"
        private const val PERIODIC_WORK_NAME = "WIDGETS_PERIODIC_UPDATE"
        private const val BOUNDARY_WORK_NAME = "WIDGETS_BOUNDARY_UPDATE"
        private const val MINIMUM_DELAY = 1_000L
    }
}
