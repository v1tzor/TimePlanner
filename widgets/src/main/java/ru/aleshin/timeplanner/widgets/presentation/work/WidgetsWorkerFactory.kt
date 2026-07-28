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
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import ru.aleshin.timeplanner.widgets.domain.interactors.WidgetsInteractor
import ru.aleshin.timeplanner.widgets.presentation.mappers.WidgetsStateUiMapper
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 28.07.2026.
 */
class WidgetsWorkerFactory @Inject constructor(
    private val widgetsInteractor: WidgetsInteractor,
    private val stateMapper: WidgetsStateUiMapper,
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters,
    ): ListenableWorker? {
        return if (workerClassName == WidgetsUpdateWorker::class.java.name) {
            WidgetsUpdateWorker(appContext, workerParameters, widgetsInteractor, stateMapper)
        } else {
            null
        }
    }
}
