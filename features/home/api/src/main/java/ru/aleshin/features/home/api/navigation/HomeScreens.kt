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
package ru.aleshin.features.home.api.navigation

import java.util.Date

/**
 * @author Stanislav Aleshin on 29.07.2023.
 */
sealed class HomeScreens {
    data object Overview : HomeScreens()
    data class Home(val scheduleDate: Date? = null) : HomeScreens()
    data object Details : HomeScreens()
    data object Templates : HomeScreens()
    data class Categories(val mainCategoryId: Int? = null) : HomeScreens()
}
