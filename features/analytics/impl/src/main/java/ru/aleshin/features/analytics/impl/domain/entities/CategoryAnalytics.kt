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
package ru.aleshin.features.analytics.impl.domain.entities

import ru.aleshin.core.domain.entities.categories.MainCategory

/**
 * @author Stanislav Aleshin on 21.07.2026.
 */
internal data class CategoryAnalytics(
    val category: MainCategory?,
    val summary: CategorySummary?,
    val keyMetrics: CategoryKeyMetrics?,
    val subCategories: SubCategoryDistribution?,
    val load: CategoryLoadDistribution?,
    val dayParts: List<CategoryDayPartCell>,
    val taskRows: List<CategoryTaskRow>,
    val observation: CategoryObservation?,
    val dayPartSummaries: List<CategoryDayPartSummary> = emptyList(),
)
