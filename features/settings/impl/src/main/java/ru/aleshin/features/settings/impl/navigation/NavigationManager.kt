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
package ru.aleshin.features.settings.impl.navigation

import ru.aleshin.core.utils.navigation.Router
import ru.aleshin.features.settings.impl.presentation.ui.donate.DonateScreen
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 23.02.2023.
 */
internal interface NavigationManager {

    fun navigateToDonate()
    fun navigateToBack()

    class Base @Inject constructor(private val globalRouter: Router) : NavigationManager {

        override fun navigateToDonate() {
            val screen = DonateScreen()
            globalRouter.navigateTo(screen)
        }

        override fun navigateToBack() {
            globalRouter.navigateBack()
        }
    }
}
