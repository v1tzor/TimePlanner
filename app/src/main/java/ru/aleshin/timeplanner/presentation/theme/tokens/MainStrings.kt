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
package ru.aleshin.timeplanner.presentation.theme.tokens

import androidx.compose.runtime.compositionLocalOf
import ru.aleshin.core.ui.theme.tokens.TimePlannerLanguage

/**
 * @author Stanislav Aleshin on 17.02.2023.
 */
data class MainStrings(
    val authorTitle: String,
    val launcherIconDesc: String,
)

val englishMainStrings = MainStrings(
    authorTitle = "Created by Aleshin Stanislav",
    launcherIconDesc = "Time Planner",
)

val russianMainStrings = MainStrings(
    authorTitle = "Created by Aleshin Stanislav",
    launcherIconDesc = "Time Planner",
)

val germanMainStrings = MainStrings(
    authorTitle = "Created by Aleshin Stanislav",
    launcherIconDesc = "Time Planner",
)

val LocalMainStrings = compositionLocalOf<MainStrings> {
    error("Splash strings is not provided")
}

fun fetchMainStrings(language: TimePlannerLanguage) = when (language) {
    TimePlannerLanguage.EN -> englishMainStrings
    TimePlannerLanguage.RU -> russianMainStrings
    TimePlannerLanguage.DE -> germanMainStrings
}
