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
import ru.aleshin.features.home.api.domains.common.MainIcon

/**
 * @author Stanislav Aleshin on 24.02.2023.
 */
fun MainIcon.toIconRes(icons: TimePlannerIcons): Int = when (this) {
    MainIcon.WORK -> icons.categoryWorkIcon
    MainIcon.REST -> icons.categoryRestIcon
    MainIcon.AFFAIRS -> icons.categoryAffairsIcon
    MainIcon.TRANSPORT -> icons.categoryTransportIcon
    MainIcon.STUDY -> icons.categoryStudyIcon
    MainIcon.EAT -> icons.categoryEatIcon
    MainIcon.ENTERTAINMENTS -> icons.categoryEntertainmentsIcon
    MainIcon.SPORT -> icons.categorySportIcon
    MainIcon.SLEEP -> icons.categorySleepIcon
    MainIcon.CULTURE -> icons.categoryCultureIcon
    MainIcon.OTHER -> icons.categoryOtherIcon
    MainIcon.EMPTY -> icons.categoryEmptyIcon
    MainIcon.HYGIENE -> icons.categoryHygiene
}

fun MainIcon.toDescription(strings: TimePlannerStrings): String = when (this) {
    MainIcon.WORK -> strings.categoryWorkTitle
    MainIcon.REST -> strings.categoryRestTitle
    MainIcon.AFFAIRS -> strings.categoryAffairsTitle
    MainIcon.TRANSPORT -> strings.categoryTransportTitle
    MainIcon.STUDY -> strings.categoryStudyTitle
    MainIcon.EAT -> strings.categoryEatTitle
    MainIcon.ENTERTAINMENTS -> strings.categoryEntertainmentsTitle
    MainIcon.SPORT -> strings.categorySportTitle
    MainIcon.SLEEP -> strings.categorySleepTitle
    MainIcon.CULTURE -> strings.categoryCultureTitle
    MainIcon.OTHER -> strings.categoryOtherTitle
    MainIcon.EMPTY -> strings.categoryEmptyTitle
    MainIcon.HYGIENE -> strings.categoryHugieneTitle
}

@Composable
fun MainIcon.toDescription() = toDescription(TimePlannerRes.strings)

@Composable
fun MainIcon.toIconPainter() = painterResource(
    id = toIconRes(TimePlannerRes.icons),
)
