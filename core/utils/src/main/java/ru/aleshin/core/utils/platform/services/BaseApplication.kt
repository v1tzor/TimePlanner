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
package ru.aleshin.core.utils.platform.services

import android.app.Application

/**
 * @author Stanislav Aleshin on 13.04.2025.
 */
abstract class BaseApplication : Application() {

    abstract var appService: AppService

    abstract var analyticsService: AnalyticsService

    abstract var crashlyticsService: CrashlyticsService

    abstract fun initDI()

    abstract fun initPlatformService()

    abstract fun initSettings()

    override fun onCreate() {
        super.onCreate()
        initDI()
        initPlatformService()
        initSettings()
    }
}