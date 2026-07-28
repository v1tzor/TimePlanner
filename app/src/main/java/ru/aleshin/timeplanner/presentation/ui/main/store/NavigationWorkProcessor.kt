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
package ru.aleshin.timeplanner.presentation.ui.main.store

import kotlinx.coroutines.delay
import ru.aleshin.core.utils.architecture.store.work.OutputResult
import ru.aleshin.core.utils.architecture.store.work.WorkCommand
import ru.aleshin.core.utils.architecture.store.work.WorkProcessor
import ru.aleshin.core.utils.architecture.store.work.WorkResult
import ru.aleshin.core.utils.extensions.generateUniqueKey
import ru.aleshin.core.utils.functional.Constants
import ru.aleshin.core.utils.functional.TimeRange
import ru.aleshin.core.utils.managers.DateManager
import ru.aleshin.features.editor.api.EditorConfig
import ru.aleshin.timeplanner.presentation.ui.main.contract.DeepLinkTarget
import ru.aleshin.timeplanner.presentation.ui.main.contract.MainAction
import ru.aleshin.timeplanner.presentation.ui.main.contract.MainEffect
import ru.aleshin.timeplanner.presentation.ui.main.contract.MainOutput
import ru.aleshin.timeplanner.presentation.ui.main.contract.MainTabTarget
import ru.aleshin.timeplanner.presentation.ui.main.contract.ShareTarget
import java.util.Date
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 04.08.2023.
 */
interface NavigationWorkProcessor : WorkProcessor<NavWorkCommand, MainAction, MainEffect, MainOutput> {

    class Base @Inject constructor(
        private val dateManager: DateManager,
    ) : NavigationWorkProcessor {

        override suspend fun work(command: NavWorkCommand) = when (command) {
            is NavWorkCommand.InitialNavigation -> initialNavigationWork()
            is NavWorkCommand.ProcessDeepLink -> processDeepLink(command.deepLinkTarget)
            is NavWorkCommand.ProcessShare -> processShare(command.shareTarget)
        }

        private suspend fun initialNavigationWork(): NavWorkResult {
            delay(Constants.Delay.SPLASH_NAV)
            return OutputResult(MainOutput.NavigateToTabNavigation(MainTabTarget.Home()))
        }

        private fun processDeepLink(deepLinkTarget: DeepLinkTarget): NavWorkResult {
            return when (deepLinkTarget) {
                is DeepLinkTarget.Editor -> {
                    val isEmptyTarget = deepLinkTarget.timeTaskId == null &&
                        deepLinkTarget.undefinedTaskId == null &&
                        deepLinkTarget.date == null &&
                        deepLinkTarget.from == null &&
                        deepLinkTarget.to == null
                    val currentTime = dateManager.fetchCurrentDate()
                    val date = deepLinkTarget.date?.let(::Date) ?: if (isEmptyTarget) {
                        dateManager.fetchBeginningCurrentDay()
                    } else {
                        null
                    }
                    val from = deepLinkTarget.from
                    val to = deepLinkTarget.to
                    val timeRange = if (from != null && to != null) {
                        TimeRange(Date(from), Date(to))
                    } else if (isEmptyTarget) {
                        TimeRange(currentTime, currentTime)
                    } else {
                        null
                    }
                    OutputResult(
                        MainOutput.NavigateToEditor(
                            EditorConfig.Task(
                                timeTaskId = deepLinkTarget.timeTaskId,
                                undefinedTaskId = deepLinkTarget.undefinedTaskId,
                                date = date,
                                timeRange = timeRange,
                            ),
                        ),
                    )
                }
                is DeepLinkTarget.Home -> {
                    OutputResult(
                        MainOutput.NavigateToTabNavigation(
                            MainTabTarget.Home(deepLinkTarget.date),
                        ),
                    )
                }
                is DeepLinkTarget.Overview -> {
                    OutputResult(MainOutput.NavigateToTabNavigation(MainTabTarget.Overview))
                }
                is DeepLinkTarget.Analytics -> {
                    OutputResult(MainOutput.NavigateToTabNavigation(MainTabTarget.Analytics))
                }
            }
        }

        private fun processShare(shareTarget: ShareTarget): NavWorkResult {
            return OutputResult(MainOutput.NavigateToOverview(shareTarget.text, generateUniqueKey()))
        }
    }
}

sealed class NavWorkCommand : WorkCommand {
    data object InitialNavigation : NavWorkCommand()
    data class ProcessDeepLink(val deepLinkTarget: DeepLinkTarget) : NavWorkCommand()
    data class ProcessShare(val shareTarget: ShareTarget) : NavWorkCommand()
}

typealias NavWorkResult = WorkResult<MainAction, MainEffect, MainOutput>
