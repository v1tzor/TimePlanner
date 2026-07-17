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

import kotlinx.coroutines.flow.map
import ru.aleshin.core.domain.entities.template.Template
import ru.aleshin.core.domain.repository.TemplatesRepository
import ru.aleshin.core.utils.extensions.duration
import ru.aleshin.core.utils.extensions.fetchDay
import ru.aleshin.core.utils.extensions.fetchWeekDay
import ru.aleshin.core.utils.extensions.isCurrentDay
import ru.aleshin.core.utils.extensions.shiftDay
import ru.aleshin.core.utils.extensions.startThisDay
import ru.aleshin.core.utils.functional.DomainResult
import ru.aleshin.core.utils.functional.FlowDomainResult
import ru.aleshin.core.utils.managers.DateManager
import ru.aleshin.features.templates.impl.domain.common.HomeEitherWrapper
import ru.aleshin.features.templates.impl.domain.entities.TemplatesFailures
import ru.aleshin.features.templates.impl.domain.entities.templates.TemplatePatternDay
import ru.aleshin.features.templates.impl.domain.entities.templates.TemplatesData
import ru.aleshin.features.templates.impl.domain.entities.templates.TemplatesPattern
import ru.aleshin.features.templates.impl.domain.entities.templates.TemplatesPatternFilter
import ru.aleshin.features.templates.impl.domain.entities.templates.TemplatesSortedType
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 08.03.2023.
 */
internal interface TemplatesInteractor {

    suspend fun addOrUpdateTemplate(template: Template): DomainResult<TemplatesFailures, Long>
    suspend fun fetchTemplatesData(sortedType: TemplatesSortedType, patternFilter: TemplatesPatternFilter): FlowDomainResult<TemplatesFailures, TemplatesData>
    suspend fun deleteTemplate(id: Long): DomainResult<TemplatesFailures, Unit>

    class Base @Inject constructor(
        private val templatesRepository: TemplatesRepository,
        private val dateManager: DateManager,
        private val eitherWrapper: HomeEitherWrapper,
    ) : TemplatesInteractor {

        override suspend fun addOrUpdateTemplate(template: Template) = eitherWrapper.wrap {
            templatesRepository.addOrUpdateTemplate(template)
        }

        override suspend fun fetchTemplatesData(
            sortedType: TemplatesSortedType,
            patternFilter: TemplatesPatternFilter,
        ) = eitherWrapper.wrapFlow {
            templatesRepository.fetchAllTemplates().map { templates ->
                val sortedTemplates = when (sortedType) {
                    TemplatesSortedType.DATE -> templates.sortedBy { it.startTime }
                    TemplatesSortedType.CATEGORIES -> templates.sortedBy { it.category.id }
                    TemplatesSortedType.DURATION -> templates.sortedBy { duration(it.startTime, it.endTime) }
                }
                val activeTemplates = sortedTemplates.filter { template -> template.repeatEnabled }
                val inactiveTemplates = sortedTemplates.filterNot { template -> template.repeatEnabled }
                val patternTemplates = when (patternFilter) {
                    TemplatesPatternFilter.ACTIVE -> activeTemplates
                    TemplatesPatternFilter.ALL -> sortedTemplates
                }
                val currentDate = dateManager.fetchBeginningCurrentDay()

                TemplatesData(
                    activeTemplatesCount = activeTemplates.size,
                    inactiveTemplatesCount = inactiveTemplates.size,
                    activeTemplates = activeTemplates,
                    inactiveTemplates = inactiveTemplates,
                    weekPattern = fetchPattern(
                        dates = fetchCurrentWeekDates(currentDate),
                        templates = patternTemplates,
                        currentDate = currentDate,
                    ),
                    monthPattern = fetchPattern(
                        dates = fetchCurrentMonthDates(currentDate),
                        templates = patternTemplates,
                        currentDate = currentDate,
                    ),
                )
            }
        }

        override suspend fun deleteTemplate(id: Long) = eitherWrapper.wrap {
            templatesRepository.deleteTemplateById(id)
        }

        private fun fetchPattern(
            dates: List<Date>,
            templates: List<Template>,
            currentDate: Date,
        ): TemplatesPattern {
            val days = dates.map { date ->
                val repeatTemplates = templates.filter { template ->
                    template.repeatTimes.any { repeatTime -> repeatTime.checkDateIsRepeat(date) }
                }
                TemplatePatternDay(
                    date = date,
                    weekDay = date.fetchWeekDay(),
                    dayNumber = date.fetchDay(),
                    isCurrentDay = date.isCurrentDay(currentDate),
                    templatesCount = repeatTemplates.size,
                    templates = repeatTemplates,
                )
            }
            return TemplatesPattern(
                templatesCount = days
                    .flatMap { day -> day.templates }
                    .distinctBy { template -> template.templateId }
                    .size,
                repeatsCount = days.sumOf { day -> day.templatesCount },
                days = days,
            )
        }

        private fun fetchCurrentWeekDates(currentDate: Date): List<Date> {
            val calendar = Calendar.getInstance().apply { time = currentDate }
            val currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
            val firstDayOfWeek = calendar.firstDayOfWeek
            val daysFromWeekStart = Math.floorMod(currentDayOfWeek - firstDayOfWeek, WEEK_DAYS_COUNT)
            val weekStart = currentDate.shiftDay(-daysFromWeekStart)

            return List(WEEK_DAYS_COUNT) { day -> weekStart.shiftDay(day) }
        }

        private fun fetchCurrentMonthDates(currentDate: Date): List<Date> {
            val calendar = Calendar.getInstance().apply {
                time = currentDate.startThisDay()
                set(Calendar.DAY_OF_MONTH, 1)
            }
            val monthStart = calendar.time
            val monthDaysCount = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

            return List(monthDaysCount) { day -> monthStart.shiftDay(day) }
        }
    }
}

private const val WEEK_DAYS_COUNT = 7
