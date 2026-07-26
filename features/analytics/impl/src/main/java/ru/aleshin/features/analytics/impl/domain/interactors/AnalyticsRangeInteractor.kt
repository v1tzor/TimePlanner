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

import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ru.aleshin.core.domain.entities.settings.LanguageType
import ru.aleshin.core.domain.entities.settings.TasksSettings
import ru.aleshin.core.domain.repository.TasksSettingsRepository
import ru.aleshin.core.domain.repository.ThemeSettingsRepository
import ru.aleshin.core.utils.functional.FlowDomainResult
import ru.aleshin.core.utils.functional.TimePeriod
import ru.aleshin.core.utils.functional.TimeRange
import ru.aleshin.core.utils.functional.UnitDomainResult
import ru.aleshin.core.utils.managers.DateManager
import ru.aleshin.features.analytics.impl.domain.common.AnalyticsEitherWrapper
import ru.aleshin.features.analytics.impl.domain.common.AnalyticsRangeCalculator
import ru.aleshin.features.analytics.impl.domain.entities.AnalyticsCivilDateRange
import ru.aleshin.features.analytics.impl.domain.entities.AnalyticsFailure
import ru.aleshin.features.analytics.impl.domain.entities.AnalyticsRangeSelection
import java.util.Date
import java.util.Locale
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 21.07.2026.
 */
internal interface AnalyticsRangeInteractor {

    suspend fun fetchRangeSelection(): FlowDomainResult<AnalyticsFailure, AnalyticsRangeSelection>
    suspend fun selectPeriod(period: TimePeriod): UnitDomainResult<AnalyticsFailure>
    suspend fun shiftRange(direction: Int): UnitDomainResult<AnalyticsFailure>
    suspend fun moveToCurrent(): UnitDomainResult<AnalyticsFailure>
    suspend fun confirmCustomRange(range: AnalyticsCivilDateRange): UnitDomainResult<AnalyticsFailure>

    class Base @Inject constructor(
        private val tasksSettingsRepository: TasksSettingsRepository,
        private val themeSettingsRepository: ThemeSettingsRepository,
        private val dateManager: DateManager,
        private val rangeCalculator: AnalyticsRangeCalculator,
        private val eitherWrapper: AnalyticsEitherWrapper,
    ) : AnalyticsRangeInteractor {

        private val mutationMutex = Mutex()

        override suspend fun fetchRangeSelection() = eitherWrapper.wrapFlow {
            combine(
                tasksSettingsRepository.fetchSettings(),
                themeSettingsRepository.fetchSettings(),
            ) { tasksSettings, themeSettings ->
                tasksSettings to themeSettings.language.fetchAnalyticsLocale()
            }.transform { (settings, locale) ->
                fetchValidSelection(settings, locale)?.let { emit(it) }
            }
        }

        override suspend fun selectPeriod(period: TimePeriod) = eitherWrapper.wrap {
            require(period != TimePeriod.CUSTOM)
            mutationMutex.withLock {
                val settings = tasksSettingsRepository.fetchSettings().first()
                val anchorDate = settings.taskAnalyticsAnchorDate?.let(rangeCalculator::normalizeCivilToken)
                    ?: fetchTodayToken()
                tasksSettingsRepository.updateAnalyticsRange(
                    period = period,
                    anchorDate = anchorDate,
                    customRange = settings.customAnalyticsDateRange.fetchValidCustomRange(),
                )
            }
        }

        override suspend fun shiftRange(direction: Int) = eitherWrapper.wrap {
            mutationMutex.withLock {
                val settings = tasksSettingsRepository.fetchSettings().first()
                val anchorDate = settings.taskAnalyticsAnchorDate?.let(rangeCalculator::normalizeCivilToken)
                    ?: fetchTodayToken()
                val customRange = settings.customAnalyticsDateRange.fetchValidCustomRange()
                val (shiftedAnchor, shiftedCustom) = rangeCalculator.shift(
                    period = settings.taskAnalyticsRange,
                    anchorDate = anchorDate,
                    customRange = customRange?.toCivilRange(),
                    direction = direction,
                )
                tasksSettingsRepository.updateAnalyticsRange(
                    period = settings.taskAnalyticsRange,
                    anchorDate = shiftedAnchor,
                    customRange = shiftedCustom?.toTimeRange() ?: customRange,
                )
            }
        }

        override suspend fun moveToCurrent() = eitherWrapper.wrap {
            mutationMutex.withLock {
                val settings = tasksSettingsRepository.fetchSettings().first()
                val customRange = settings.customAnalyticsDateRange.fetchValidCustomRange()
                val (anchorDate, movedCustom) = rangeCalculator.moveToCurrent(
                    period = settings.taskAnalyticsRange,
                    currentDate = dateManager.fetchCurrentDate(),
                    customRange = customRange?.toCivilRange(),
                )
                tasksSettingsRepository.updateAnalyticsRange(
                    period = settings.taskAnalyticsRange,
                    anchorDate = anchorDate,
                    customRange = movedCustom?.toTimeRange() ?: customRange,
                )
            }
        }

        override suspend fun confirmCustomRange(
            range: AnalyticsCivilDateRange,
        ) = eitherWrapper.wrap {
            val normalizedRange = AnalyticsCivilDateRange(
                from = rangeCalculator.normalizeCivilToken(range.from),
                to = rangeCalculator.normalizeCivilToken(range.to),
            )
            require(!normalizedRange.from.after(normalizedRange.to))
            mutationMutex.withLock {
                tasksSettingsRepository.updateAnalyticsRange(
                    period = TimePeriod.CUSTOM,
                    anchorDate = normalizedRange.from,
                    customRange = normalizedRange.toTimeRange(),
                )
            }
        }

        private suspend fun fetchValidSelection(
            settings: TasksSettings,
            locale: Locale,
        ): AnalyticsRangeSelection? = mutationMutex.withLock {
            val today = fetchTodayToken()
            val customRange = settings.customAnalyticsDateRange
            val validCustomRange = customRange.fetchValidCustomRange()
            val normalizedAnchor = settings.taskAnalyticsAnchorDate?.let(rangeCalculator::normalizeCivilToken)
            val normalizedCustom = validCustomRange?.let {
                TimeRange(
                    from = rangeCalculator.normalizeCivilToken(it.from),
                    to = rangeCalculator.normalizeCivilToken(it.to),
                )
            }

            if (settings.taskAnalyticsRange == TimePeriod.CUSTOM && normalizedCustom == null) {
                tasksSettingsRepository.updateAnalyticsRange(TimePeriod.WEEK, today, null)
                return@withLock null
            }
            if (normalizedAnchor == null) {
                val initializedAnchor = if (settings.taskAnalyticsRange == TimePeriod.CUSTOM) {
                    checkNotNull(normalizedCustom).from
                } else {
                    today
                }
                tasksSettingsRepository.updateAnalyticsRange(
                    settings.taskAnalyticsRange,
                    initializedAnchor,
                    normalizedCustom,
                )
                return@withLock null
            }
            if (customRange != null && normalizedCustom == null) {
                tasksSettingsRepository.updateAnalyticsRange(
                    settings.taskAnalyticsRange,
                    normalizedAnchor,
                    null,
                )
                return@withLock null
            }
            if (settings.taskAnalyticsAnchorDate != normalizedAnchor || validCustomRange != normalizedCustom) {
                tasksSettingsRepository.updateAnalyticsRange(
                    settings.taskAnalyticsRange,
                    normalizedAnchor,
                    normalizedCustom,
                )
                return@withLock null
            }
            if (settings.taskAnalyticsRange == TimePeriod.CUSTOM && normalizedAnchor != normalizedCustom?.from) {
                tasksSettingsRepository.updateAnalyticsRange(
                    TimePeriod.CUSTOM,
                    checkNotNull(normalizedCustom).from,
                    normalizedCustom,
                )
                return@withLock null
            }

            return@withLock rangeCalculator.calculate(
                period = settings.taskAnalyticsRange,
                anchorDate = normalizedAnchor,
                customRange = normalizedCustom?.toCivilRange(),
                locale = locale,
            )
        }

        private fun fetchTodayToken(): Date {
            return rangeCalculator.localDateToCivilToken(dateManager.fetchCurrentDate())
        }

        private fun TimeRange?.fetchValidCustomRange(): TimeRange? {
            return this?.takeUnless { it.from.after(it.to) }
        }

        private fun TimeRange.toCivilRange(): AnalyticsCivilDateRange {
            return AnalyticsCivilDateRange(from, to)
        }

        private fun AnalyticsCivilDateRange.toTimeRange(): TimeRange {
            return TimeRange(from, to)
        }
    }
}

internal fun LanguageType.fetchAnalyticsLocale(): Locale = when (this) {
    LanguageType.DEFAULT -> Locale.getDefault()
    LanguageType.PT_BR -> Locale.forLanguageTag("pt-BR")
    LanguageType.VN -> Locale.forLanguageTag("vi")
    else -> Locale.forLanguageTag(checkNotNull(code))
}
