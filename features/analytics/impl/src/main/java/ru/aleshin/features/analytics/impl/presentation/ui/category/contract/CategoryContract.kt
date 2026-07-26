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
package ru.aleshin.features.analytics.impl.presentation.ui.category.contract

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import ru.aleshin.core.presentation.models.categories.MainCategoryUi
import ru.aleshin.core.utils.architecture.component.BaseInput
import ru.aleshin.core.utils.architecture.component.BaseOutput
import ru.aleshin.core.utils.architecture.store.contract.StoreAction
import ru.aleshin.core.utils.architecture.store.contract.StoreEffect
import ru.aleshin.core.utils.architecture.store.contract.StoreEvent
import ru.aleshin.core.utils.architecture.store.contract.StoreState
import ru.aleshin.core.utils.functional.TimePeriod
import ru.aleshin.features.analytics.impl.domain.entities.AnalyticsFailure
import ru.aleshin.features.analytics.impl.presentation.models.analytics.AnalyticsRangeUi
import ru.aleshin.features.analytics.impl.presentation.models.category.CategoryAnalyticsUi

/**
 * @author Stanislav Aleshin on 21.07.2026.
 */
@Serializable
internal data class CategoryState(
    val mainCategoryId: Long? = null,
    val isLoading: Boolean = true,
    val range: AnalyticsRangeUi? = null,
    val category: MainCategoryUi? = null,
    @Transient val analytics: CategoryAnalyticsUi? = null,
    val isUnavailable: Boolean = false,
    val isError: Boolean = false,
    val isTasksExpanded: Boolean = false,
    val selectedSubCategoryBucketKey: Long? = null,
    val selectedLoadBucketIndex: Int? = null,
) : StoreState

internal sealed class CategoryEvent : StoreEvent {
    data class Init(val input: CategoryInput) : CategoryEvent()
    data object Activate : CategoryEvent()
    data object Deactivate : CategoryEvent()
    data object NavigateBack : CategoryEvent()
    data class SelectPeriod(val period: TimePeriod) : CategoryEvent()
    data object PreviousPeriod : CategoryEvent()
    data object NextPeriod : CategoryEvent()
    data object MoveToCurrent : CategoryEvent()
    data class ConfirmCalendar(val fromPickerToken: Long, val toPickerToken: Long) : CategoryEvent()
    data object ToggleTasksExpanded : CategoryEvent()
    data class SelectSubCategoryBucket(val key: Long?) : CategoryEvent()
    data class SelectLoadBucket(val index: Int?) : CategoryEvent()
    data object Retry : CategoryEvent()
}

internal sealed class CategoryAction : StoreAction {
    data class SetupCategoryId(val mainCategoryId: Long) : CategoryAction()
    data class SetupRange(val range: AnalyticsRangeUi) : CategoryAction()
    data class UpdateAnalytics(
        val category: MainCategoryUi?,
        val analytics: CategoryAnalyticsUi,
        val isUnavailable: Boolean,
    ) : CategoryAction()
    data class UpdateLoading(
        val isLoading: Boolean,
        val isError: Boolean,
    ) : CategoryAction()
    data class UpdateTasksExpanded(val isExpanded: Boolean) : CategoryAction()
    data class UpdateChartSelection(
        val selectedSubCategoryBucketKey: Long?,
        val selectedLoadBucketIndex: Int?,
    ) : CategoryAction()
}

internal sealed class CategoryEffect : StoreEffect {
    data class ShowFailure(val failure: AnalyticsFailure) : CategoryEffect()
}

internal data class CategoryInput(val mainCategoryId: Long) : BaseInput

internal sealed interface CategoryOutput : BaseOutput {
    data object NavigateToBack : CategoryOutput
}
