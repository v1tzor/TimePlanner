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
package ru.aleshin.features.analytics.impl.domain.interactors

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import ru.aleshin.core.domain.entities.settings.LanguageType
import ru.aleshin.core.domain.entities.settings.TasksSettings
import ru.aleshin.core.domain.entities.settings.ThemeSettings
import ru.aleshin.core.domain.repository.TasksSettingsRepository
import ru.aleshin.core.domain.repository.ThemeSettingsRepository
import ru.aleshin.core.utils.functional.Either
import ru.aleshin.core.utils.functional.TimePeriod
import ru.aleshin.core.utils.functional.TimeRange
import ru.aleshin.core.utils.managers.DateManager
import ru.aleshin.features.analytics.impl.domain.common.AnalyticsEitherWrapper
import ru.aleshin.features.analytics.impl.domain.common.AnalyticsErrorHandler
import ru.aleshin.features.analytics.impl.domain.common.AnalyticsRangeCalculator
import ru.aleshin.features.analytics.impl.domain.entities.AnalyticsCivilDateRange
import ru.aleshin.features.analytics.impl.domain.entities.AnalyticsRangeSelection
import java.util.Calendar
import java.util.Date
import java.util.TimeZone

/**
 * @author Stanislav Aleshin on 21.07.2026.
 */
internal class AnalyticsRangeInteractorTest {

    @Test
    fun missingAnchorIsPersistedBeforeFirstSelectionEmission() = runBlocking {
        val repository = FakeTasksSettingsRepository(TasksSettings(taskAnalyticsAnchorDate = null))
        val interactor = createInteractor(repository)

        val selection = interactor.fetchRangeSelection().firstRight()

        assertEquals(TODAY_TOKEN, repository.settings.value.taskAnalyticsAnchorDate)
        assertEquals(TODAY_TOKEN, selection.anchorDate)
        assertEquals(TimePeriod.WEEK, selection.period)
        assertEquals(1, repository.updateCount)
    }

    @Test
    fun missingAnchorUsesCustomStartOnlyForCustomPeriod() = runBlocking {
        val customRange = TimeRange(
            token(2026, Calendar.JULY, 1),
            token(2026, Calendar.JULY, 3),
        )
        val customRepository = FakeTasksSettingsRepository(
            TasksSettings(
                taskAnalyticsRange = TimePeriod.CUSTOM,
                taskAnalyticsAnchorDate = null,
                customAnalyticsDateRange = customRange,
            ),
        )
        val monthRepository = FakeTasksSettingsRepository(
            TasksSettings(
                taskAnalyticsRange = TimePeriod.MONTH,
                taskAnalyticsAnchorDate = null,
                customAnalyticsDateRange = customRange,
            ),
        )

        val customSelection = createInteractor(customRepository).fetchRangeSelection().firstRight()
        val monthSelection = createInteractor(monthRepository).fetchRangeSelection().firstRight()

        assertEquals(customRange.from, customSelection.anchorDate)
        assertEquals(TODAY_TOKEN, monthSelection.anchorDate)
        assertEquals(customRange, monthRepository.settings.value.customAnalyticsDateRange)
    }

    @Test
    fun invalidCustomStateFallsBackAtomicallyToWeek() = runBlocking {
        val repository = FakeTasksSettingsRepository(
            TasksSettings(
                taskAnalyticsRange = TimePeriod.CUSTOM,
                taskAnalyticsAnchorDate = TODAY_TOKEN,
                customAnalyticsDateRange = TimeRange(token(2026, Calendar.JULY, 22), TODAY_TOKEN),
            ),
        )
        val interactor = createInteractor(repository)

        val selection = interactor.fetchRangeSelection().firstRight()

        assertEquals(TimePeriod.WEEK, selection.period)
        assertEquals(TODAY_TOKEN, repository.settings.value.taskAnalyticsAnchorDate)
        assertNull(repository.settings.value.customAnalyticsDateRange)
    }

    @Test
    fun periodMutationAppearsOnlyAfterRepositoryEmission() = runBlocking {
        val repository = FakeTasksSettingsRepository(
            TasksSettings(taskAnalyticsRange = TimePeriod.WEEK, taskAnalyticsAnchorDate = TODAY_TOKEN),
        )
        val interactor = createInteractor(repository)
        assertEquals(TimePeriod.WEEK, interactor.fetchRangeSelection().firstRight().period)
        repository.autoEmit = false

        val result = interactor.selectPeriod(TimePeriod.MONTH)
        val unchangedSelection = interactor.fetchRangeSelection().firstRight()

        assertTrue(result is Either.Right)
        assertEquals(TimePeriod.MONTH, repository.pendingPeriod)
        assertEquals(TimePeriod.WEEK, repository.settings.value.taskAnalyticsRange)
        assertEquals(TimePeriod.WEEK, unchangedSelection.period)
    }

    @Test
    fun failedMutationKeepsPersistedRangeAndRetryEmitsSuccess() = runBlocking {
        val repository = FakeTasksSettingsRepository(
            TasksSettings(taskAnalyticsRange = TimePeriod.WEEK, taskAnalyticsAnchorDate = TODAY_TOKEN),
        )
        val interactor = createInteractor(repository)
        repository.shouldFail = true

        val failure = interactor.selectPeriod(TimePeriod.MONTH)

        assertTrue(failure is Either.Left)
        assertEquals(TimePeriod.WEEK, repository.settings.value.taskAnalyticsRange)

        repository.shouldFail = false
        val success = interactor.selectPeriod(TimePeriod.MONTH)

        assertTrue(success is Either.Right)
        assertEquals(TimePeriod.MONTH, repository.settings.value.taskAnalyticsRange)
    }

    @Test
    fun confirmCustomUsesFirstDateAsAnchorAndPickerAdapterKeepsCivilDate() = runBlocking {
        val repository = FakeTasksSettingsRepository(
            TasksSettings(taskAnalyticsRange = TimePeriod.WEEK, taskAnalyticsAnchorDate = TODAY_TOKEN),
        )
        val interactor = createInteractor(repository)
        val rangeCalculator = AnalyticsRangeCalculator.Base()
        val pickerToken = token(2026, Calendar.AUGUST, 2).time
        val civilDate = rangeCalculator.pickerTokenToCivilToken(pickerToken)

        interactor.confirmCustomRange(AnalyticsCivilDateRange(civilDate, civilDate))

        assertEquals(TimePeriod.CUSTOM, repository.settings.value.taskAnalyticsRange)
        assertEquals(civilDate, repository.settings.value.taskAnalyticsAnchorDate)
        assertEquals(TimeRange(civilDate, civilDate), repository.settings.value.customAnalyticsDateRange)
        assertEquals(pickerToken, rangeCalculator.civilTokenToPickerToken(civilDate))
        assertEquals(0, repository.fetchCount)
    }

    @Test
    fun explicitProductLanguagesUseValidLocaleTags() {
        assertEquals("vi", LanguageType.VN.fetchAnalyticsLocale().toLanguageTag())
        assertEquals("pt-BR", LanguageType.PT_BR.fetchAnalyticsLocale().toLanguageTag())
        assertEquals("fa", LanguageType.FA.fetchAnalyticsLocale().toLanguageTag())
    }

    private fun createInteractor(repository: FakeTasksSettingsRepository): AnalyticsRangeInteractor {
        return AnalyticsRangeInteractor.Base(
            tasksSettingsRepository = repository,
            themeSettingsRepository = FakeThemeSettingsRepository(),
            dateManager = FakeAnalyticsDateManager(TODAY_LOCAL),
            rangeCalculator = AnalyticsRangeCalculator.Base(),
            eitherWrapper = AnalyticsEitherWrapper.Base(AnalyticsErrorHandler.Base()),
        )
    }

    private suspend fun Flow<Either<*, AnalyticsRangeSelection>>.firstRight(): AnalyticsRangeSelection {
        return (first { it is Either.Right } as Either.Right).data
    }

    companion object {
        private val UTC = TimeZone.getTimeZone("UTC")
        private val TODAY_TOKEN = token(2026, Calendar.JULY, 21)
        private val TODAY_LOCAL = Calendar.getInstance().apply {
            clear()
            set(2026, Calendar.JULY, 21, 12, 0, 0)
        }.time

        private fun token(year: Int, month: Int, day: Int): Date {
            return Calendar.getInstance(UTC).apply {
                clear()
                set(year, month, day, 0, 0, 0)
            }.time
        }
    }
}

/**
 * @author Stanislav Aleshin on 21.07.2026.
 */
private class FakeTasksSettingsRepository(
    initialSettings: TasksSettings,
) : TasksSettingsRepository {

    val settings = MutableStateFlow(initialSettings)
    var fetchCount = 0
    var updateCount = 0
    var autoEmit = true
    var shouldFail = false
    var pendingPeriod: TimePeriod? = null

    override fun fetchSettings(): Flow<TasksSettings> {
        fetchCount++
        return settings
    }

    override suspend fun updateSettings(settings: TasksSettings) {
        if (autoEmit) this.settings.value = settings
    }

    override suspend fun updateAnalyticsRange(
        period: TimePeriod,
        anchorDate: Date?,
        customRange: TimeRange?,
    ) {
        if (shouldFail) throw IllegalStateException()
        updateCount++
        pendingPeriod = period
        if (autoEmit) {
            settings.value = settings.value.copy(
                taskAnalyticsRange = period,
                taskAnalyticsAnchorDate = anchorDate,
                customAnalyticsDateRange = customRange,
            )
        }
    }
}

/**
 * @author Stanislav Aleshin on 21.07.2026.
 */
private class FakeThemeSettingsRepository : ThemeSettingsRepository {

    private val settings = ThemeSettings(language = LanguageType.RU)

    override fun fetchSettings(): Flow<ThemeSettings> = flowOf(settings)

    override suspend fun fetchSettingsOnce(): ThemeSettings = settings

    override suspend fun updateSettings(settings: ThemeSettings) = Unit
}

/**
 * @author Stanislav Aleshin on 21.07.2026.
 */
private class FakeAnalyticsDateManager(
    private val currentDate: Date,
) : DateManager {

    override fun fetchCurrentDate(): Date = currentDate
    override fun fetchBeginningCurrentDay(): Date = currentDate
    override fun fetchEndCurrentDay(): Date = currentDate
    override fun fetchTicker(): Flow<Date> = error("Ticker must not be used")
    override fun fetchMinuteTicker(): Flow<Date> = error("Minute ticker must not be used")
    override fun calculateLeftTime(endTime: Date): Long = endTime.time - currentDate.time
    override fun calculateProgress(startTime: Date, endTime: Date): Float = 0f
    override fun setCurrentHMS(date: Date): Date = date
}
