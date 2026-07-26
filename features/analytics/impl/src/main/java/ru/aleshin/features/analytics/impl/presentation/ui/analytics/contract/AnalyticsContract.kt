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
package ru.aleshin.features.analytics.impl.presentation.ui.analytics.contract

import kotlinx.serialization.Serializable
import ru.aleshin.core.utils.architecture.component.BaseOutput
import ru.aleshin.core.utils.architecture.store.contract.StoreAction
import ru.aleshin.core.utils.architecture.store.contract.StoreEffect
import ru.aleshin.core.utils.architecture.store.contract.StoreEvent
import ru.aleshin.core.utils.architecture.store.contract.StoreState
import ru.aleshin.core.utils.functional.TimePeriod
import ru.aleshin.features.analytics.impl.domain.entities.AnalyticsCategorySort
import ru.aleshin.features.analytics.impl.domain.entities.AnalyticsFailure
import ru.aleshin.features.analytics.impl.presentation.models.analytics.AnalyticsOverviewUi
import ru.aleshin.features.analytics.impl.presentation.models.analytics.AnalyticsRangeUi

/**
 * @author Stanislav Aleshin on 21.07.2026.
 */
@Serializable
internal data class AnalyticsState(
    val isLoading: Boolean = true,
    val isError: Boolean = false,
    val range: AnalyticsRangeUi? = null,
    val overview: AnalyticsOverviewUi? = null,
    val categorySort: AnalyticsCategorySort = AnalyticsCategorySort.BY_TIME,
    val isCategoriesExpanded: Boolean = false,
    val selectedChartKey: Long? = null,
    val selectedCreationBucketKey: Long? = null,
) : StoreState

internal sealed class AnalyticsEvent : StoreEvent {
    data class Init(val isRestore: Boolean) : AnalyticsEvent()
    data object Retry : AnalyticsEvent()
    data class SelectPeriod(val period: TimePeriod) : AnalyticsEvent()
    data object PreviousPeriod : AnalyticsEvent()
    data object NextPeriod : AnalyticsEvent()
    data object MoveToCurrent : AnalyticsEvent()
    data class ConfirmCalendar(val fromPickerToken: Long, val toPickerToken: Long) : AnalyticsEvent()
    data class ChangeCategorySort(val sort: AnalyticsCategorySort) : AnalyticsEvent()
    data object ToggleCategories : AnalyticsEvent()
    data class SelectChartItem(val key: Long?) : AnalyticsEvent()
    data class SelectCreationBucket(val key: Long?) : AnalyticsEvent()
    data class ClickCategoryItem(val mainCategoryId: Long) : AnalyticsEvent()
}

internal sealed class AnalyticsEffect : StoreEffect {
    data class ShowFailure(val failure: AnalyticsFailure) : AnalyticsEffect()
}

internal sealed class AnalyticsAction : StoreAction {
    data class UpdateLoading(val isLoading: Boolean, val isError: Boolean) : AnalyticsAction()
    data class SetupRange(val range: AnalyticsRangeUi) : AnalyticsAction()
    data class UpdateAnalytics(val categorySort: AnalyticsCategorySort, val overview: AnalyticsOverviewUi) : AnalyticsAction()
    data class UpdateCategoriesExpanded(val isExpanded: Boolean) : AnalyticsAction()
    data class UpdateChartItem(val key: Long?) : AnalyticsAction()
    data class UpdateCreationBucket(val key: Long?) : AnalyticsAction()
}

internal sealed interface AnalyticsOutput : BaseOutput {
    data class NavigateToCategory(val mainCategoryId: Long) : AnalyticsOutput
}
