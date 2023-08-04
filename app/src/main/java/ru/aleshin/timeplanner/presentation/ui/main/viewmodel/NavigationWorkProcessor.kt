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
package ru.aleshin.timeplanner.presentation.ui.main.viewmodel

import kotlinx.coroutines.delay
import ru.aleshin.core.utils.functional.Constants
import ru.aleshin.core.utils.platform.screenmodel.work.ActionResult
import ru.aleshin.core.utils.platform.screenmodel.work.WorkCommand
import ru.aleshin.core.utils.platform.screenmodel.work.WorkProcessor
import ru.aleshin.core.utils.platform.screenmodel.work.WorkResult
import ru.aleshin.timeplanner.navigation.GlobalNavigationManager
import ru.aleshin.timeplanner.presentation.ui.main.contract.MainAction
import ru.aleshin.timeplanner.presentation.ui.main.contract.MainEffect
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 04.08.2023.
 */
interface NavigationWorkProcessor : WorkProcessor<NavWorkCommand, MainAction, MainEffect> {

    class Base @Inject constructor(
        private val globalNavManager: GlobalNavigationManager,
    ) : NavigationWorkProcessor {

        override suspend fun work(command: NavWorkCommand) = when (command) {
            is NavWorkCommand.NavigateToTab -> navigateToTabWork(command.initDelay)
        }

        private suspend fun navigateToTabWork(initDelay: Boolean): WorkResult<MainAction, MainEffect> {
            if (initDelay) delay(Constants.Delay.SPLASH)
            return globalNavManager.showTabScreen().let {
                ActionResult(MainAction.Navigate)
            }
        }
    }
}

sealed class NavWorkCommand : WorkCommand {
    data class NavigateToTab(val initDelay: Boolean = true) : NavWorkCommand()
}
