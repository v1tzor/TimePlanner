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
package ru.aleshin.features.templates.impl.domain.interactors

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import ru.aleshin.core.domain.entities.categories.MainCategory
import ru.aleshin.core.domain.entities.template.RepeatTime
import ru.aleshin.core.domain.entities.template.Template
import ru.aleshin.core.domain.repository.TemplatesRepository
import ru.aleshin.core.utils.extensions.setHoursAndMinutes
import ru.aleshin.core.utils.extensions.setStartDay
import ru.aleshin.core.utils.functional.WeekDay
import ru.aleshin.core.utils.functional.firstRightOrNull
import ru.aleshin.core.utils.managers.DateManager
import ru.aleshin.features.templates.impl.domain.common.HomeEitherWrapper
import ru.aleshin.features.templates.impl.domain.common.HomeErrorHandler
import ru.aleshin.features.templates.impl.domain.entities.templates.TemplatesPatternFilter
import ru.aleshin.features.templates.impl.domain.entities.templates.TemplatesSortedType
import java.util.Calendar
import java.util.Date

/**
 * @author Stanislav Aleshin on 16.07.2026.
 */
internal class TemplatesInteractorTest {

    private lateinit var currentDate: Date
    private lateinit var interactor: TemplatesInteractor

    @Before
    fun setUp() {
        currentDate = Calendar.getInstance().apply {
            clear()
            set(2026, Calendar.JULY, 16)
            setStartDay()
        }.time
        interactor = TemplatesInteractor.Base(
            templatesRepository = FakeTemplatesRepository(
                templates = listOf(
                    template(
                        id = 1L,
                        startHour = 9,
                        repeatEnabled = true,
                        repeatTimes = listOf(RepeatTime.WeekDays(WeekDay.THURSDAY)),
                    ),
                    template(
                        id = 2L,
                        startHour = 8,
                        repeatEnabled = false,
                        repeatTimes = listOf(RepeatTime.WeekDays(WeekDay.THURSDAY)),
                    ),
                    template(
                        id = 3L,
                        startHour = 10,
                        repeatEnabled = true,
                        repeatTimes = listOf(RepeatTime.MonthDay(16)),
                    ),
                ),
            ),
            dateManager = FakeDateManager(currentDate),
            eitherWrapper = HomeEitherWrapper.Base(HomeErrorHandler.Base()),
        )
    }

    @Test
    fun fetchTemplatesData_activePatternPreparesGroupsAndCurrentPeriods() = runBlocking {
        val result = checkNotNull(
            interactor.fetchTemplatesData(
                sortedType = TemplatesSortedType.DATE,
                patternFilter = TemplatesPatternFilter.ACTIVE,
            ).firstRightOrNull(),
        )

        assertEquals(listOf(1L, 3L), result.activeTemplates.map { template -> template.templateId })
        assertEquals(listOf(2L), result.inactiveTemplates.map { template -> template.templateId })
        assertEquals(2, result.activeTemplatesCount)
        assertEquals(1, result.inactiveTemplatesCount)
        assertEquals(7, result.weekPattern.days.size)
        assertEquals(31, result.monthPattern.days.size)
        assertEquals(2, result.weekPattern.templatesCount)
        assertEquals(2, result.weekPattern.repeatsCount)
        assertEquals(2, result.monthPattern.templatesCount)
        assertEquals(6, result.monthPattern.repeatsCount)
        assertEquals(
            listOf(1L, 3L),
            result.weekPattern.days
                .first { day -> day.date == currentDate }
                .templates
                .map { template -> template.templateId },
        )
    }

    @Test
    fun fetchTemplatesData_allPatternIncludesPausedTemplatesOnlyInPattern() = runBlocking {
        val result = checkNotNull(
            interactor.fetchTemplatesData(
                sortedType = TemplatesSortedType.DATE,
                patternFilter = TemplatesPatternFilter.ALL,
            ).firstRightOrNull(),
        )

        assertEquals(listOf(1L, 3L), result.activeTemplates.map { template -> template.templateId })
        assertEquals(listOf(2L), result.inactiveTemplates.map { template -> template.templateId })
        assertEquals(3, result.weekPattern.templatesCount)
        assertEquals(3, result.weekPattern.repeatsCount)
        assertEquals(3, result.monthPattern.templatesCount)
        assertEquals(11, result.monthPattern.repeatsCount)
    }

    private fun template(
        id: Long,
        startHour: Int,
        repeatEnabled: Boolean,
        repeatTimes: List<RepeatTime>,
    ) = Template(
        templateId = id,
        startTime = currentDate.at(startHour),
        endTime = currentDate.at(startHour + 1),
        category = MainCategory(id = id),
        repeatEnabled = repeatEnabled,
        repeatTimes = repeatTimes,
    )
}

/**
 * @author Stanislav Aleshin on 16.07.2026.
 */
private class FakeTemplatesRepository(
    private val templates: List<Template>,
) : TemplatesRepository {

    override suspend fun addOrUpdateTemplate(template: Template): Long {
        return template.templateId
    }

    override suspend fun addOrUpdateTemplates(templates: List<Template>) = Unit

    override suspend fun fetchTemplatesByIdOnce(templateId: Long): Template? {
        return templates.find { template -> template.templateId == templateId }
    }

    override suspend fun fetchAllTemplates(): Flow<List<Template>> {
        return flowOf(templates)
    }

    override suspend fun deleteTemplateById(id: Long) = Unit

    override suspend fun deleteAllTemplates(): List<Template> {
        return templates
    }
}

/**
 * @author Stanislav Aleshin on 16.07.2026.
 */
private class FakeDateManager(
    private val currentDate: Date,
) : DateManager {

    override fun fetchCurrentDate(): Date {
        return currentDate
    }

    override fun fetchBeginningCurrentDay(): Date {
        return currentDate
    }

    override fun fetchEndCurrentDay(): Date {
        return Calendar.getInstance().apply {
            time = currentDate
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 59)
        }.time
    }

    override fun fetchTicker(): Flow<Date> {
        return flowOf(currentDate)
    }

    override fun calculateLeftTime(endTime: Date): Long {
        return endTime.time - currentDate.time
    }

    override fun calculateProgress(startTime: Date, endTime: Date): Float {
        return (currentDate.time - startTime.time).toFloat() / (endTime.time - startTime.time)
    }

    override fun setCurrentHMS(date: Date): Date {
        return date
    }
}

private fun Date.at(hour: Int): Date {
    return Calendar.getInstance().apply {
        time = this@at
        setHoursAndMinutes(hour, 0)
    }.time
}
