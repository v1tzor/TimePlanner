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
package ru.aleshin.timeplanner.data

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import ru.aleshin.core.data.datasources.settings.SettingsDataBase

/**
 * @author Stanislav Aleshin on 21.07.2026.
 */
@RunWith(AndroidJUnit4::class)
class SettingsDataBaseAssetTest {

    @Test
    fun cleanInstallOpensVersionNineAsset() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        context.deleteDatabase(SettingsDataBase.NAME)

        val database = SettingsDataBase.create(context)
        try {
            val settings = database.fetchTasksSettingsDao().fetchSettingsFlow().first()

            assertEquals(9, database.openHelper.readableDatabase.version)
            assertEquals(0L, settings.id)
            assertEquals("AGENDA", settings.homeViewMode)
            assertEquals("WEEK", settings.taskAnalyticsRange)
            assertNull(settings.taskAnalyticsAnchorDate)
            assertNull(settings.customAnalyticsDateFrom)
            assertNull(settings.customAnalyticsDateTo)
        } finally {
            database.close()
            context.deleteDatabase(SettingsDataBase.NAME)
        }
        Unit
    }
}
