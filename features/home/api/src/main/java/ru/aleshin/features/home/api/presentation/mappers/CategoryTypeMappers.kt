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
package ru.aleshin.features.home.api.presentation.mappers

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import ru.aleshin.core.ui.theme.TimePlannerRes
import ru.aleshin.core.ui.theme.tokens.TimePlannerIcons
import ru.aleshin.core.ui.theme.tokens.TimePlannerStrings
import ru.aleshin.features.home.api.domain.entities.categories.DefaultCategoryType

/**
 * @author Stanislav Aleshin on 24.02.2023.
 */
fun DefaultCategoryType.mapToIcon(icons: TimePlannerIcons): Int = when (this) {
    DefaultCategoryType.WORK -> icons.categoryWorkIcon
    DefaultCategoryType.REST -> icons.categoryRestIcon
    DefaultCategoryType.AFFAIRS -> icons.categoryAffairsIcon
    DefaultCategoryType.TRANSPORT -> icons.categoryTransportIcon
    DefaultCategoryType.STUDY -> icons.categoryStudyIcon
    DefaultCategoryType.EAT -> icons.categoryEatIcon
    DefaultCategoryType.ENTERTAINMENTS -> icons.categoryEntertainmentsIcon
    DefaultCategoryType.SPORT -> icons.categorySportIcon
    DefaultCategoryType.SLEEP -> icons.categorySleepIcon
    DefaultCategoryType.CULTURE -> icons.categoryCultureIcon
    DefaultCategoryType.OTHER -> icons.categoryOtherIcon
    DefaultCategoryType.EMPTY -> icons.categoryEmptyIcon
    DefaultCategoryType.HYGIENE -> icons.categoryHygiene
    DefaultCategoryType.HEALTH -> icons.categoryHealth
    DefaultCategoryType.SHOPPING -> icons.categoryShopping
}

fun DefaultCategoryType.mapToString(strings: TimePlannerStrings): String = when (this) {
    DefaultCategoryType.WORK -> strings.categoryWorkTitle
    DefaultCategoryType.REST -> strings.categoryRestTitle
    DefaultCategoryType.AFFAIRS -> strings.categoryChoresTitle
    DefaultCategoryType.TRANSPORT -> strings.categoryTransportTitle
    DefaultCategoryType.STUDY -> strings.categoryStudyTitle
    DefaultCategoryType.EAT -> strings.categoryEatTitle
    DefaultCategoryType.ENTERTAINMENTS -> strings.categoryEntertainmentsTitle
    DefaultCategoryType.SPORT -> strings.categorySportTitle
    DefaultCategoryType.SLEEP -> strings.categorySleepTitle
    DefaultCategoryType.CULTURE -> strings.categoryCultureTitle
    DefaultCategoryType.OTHER -> strings.categoryOtherTitle
    DefaultCategoryType.EMPTY -> strings.categoryEmptyTitle
    DefaultCategoryType.HYGIENE -> strings.categoryHugieneTitle
    DefaultCategoryType.HEALTH -> strings.categoryHealthTitle
    DefaultCategoryType.SHOPPING -> strings.categoryShoppingTitle
}

@Composable
fun DefaultCategoryType.mapToName() = mapToString(TimePlannerRes.strings)

@Composable
fun DefaultCategoryType.mapToIconPainter() = painterResource(id = mapToIcon(TimePlannerRes.icons))
