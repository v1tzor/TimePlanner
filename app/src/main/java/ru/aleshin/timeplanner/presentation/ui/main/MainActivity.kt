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
package ru.aleshin.timeplanner.presentation.ui.main

/**
 * @author Stanislav Aleshin on 27.02.2023.
 */
import android.Manifest.permission.POST_NOTIFICATIONS
import android.Manifest.permission.SCHEDULE_EXACT_ALARM
import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.os.Build
import android.view.WindowManager.LayoutParams.FLAG_SECURE
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import cafe.adriel.voyager.navigator.CurrentScreen
import ru.aleshin.core.ui.theme.TimePlannerTheme
import ru.aleshin.core.utils.functional.Constants.App.EDITOR_DEEP_LINK
import ru.aleshin.core.utils.navigation.navigator.AppNavigator
import ru.aleshin.core.utils.navigation.navigator.NavigatorManager
import ru.aleshin.core.utils.platform.activity.BaseActivity
import ru.aleshin.core.utils.platform.screen.ScreenContent
import ru.aleshin.timeplanner.application.fetchApp
import ru.aleshin.timeplanner.di.annotation.GlobalNavigation
import ru.aleshin.timeplanner.presentation.ui.main.contract.DeepLinkTarget
import ru.aleshin.timeplanner.presentation.ui.main.contract.MainAction
import ru.aleshin.timeplanner.presentation.ui.main.contract.MainDeps
import ru.aleshin.timeplanner.presentation.ui.main.contract.MainEffect
import ru.aleshin.timeplanner.presentation.ui.main.contract.MainEvent
import ru.aleshin.timeplanner.presentation.ui.main.contract.MainViewState
import ru.aleshin.timeplanner.presentation.ui.main.viewmodel.MainViewModel
import ru.aleshin.timeplanner.presentation.ui.splash.SplashScreen
import ru.aleshin.timeplanner.presentation.ui.tabs.TabsScreen
import ru.aleshin.timeplanner.presentation.widgets.main.MainWidgetReceiver
import javax.inject.Inject

class MainActivity : BaseActivity<MainViewState, MainEvent, MainAction, MainEffect, MainDeps>() {

    @Inject
    @GlobalNavigation
    lateinit var navigatorManager: NavigatorManager

    @Inject
    lateinit var viewModelFactory: MainViewModel.Factory

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { _ -> }

    override fun initDI() = fetchApp().appComponent.inject(this)

    @Composable
    override fun Content() = ScreenContent(
        screenModel = viewModel,
        initialState = MainViewState(),
        dependencies = MainDeps(screenTarget = DeepLinkTarget.byIntent(intent))
    ) { state ->
        TimePlannerTheme(
            languageType = state.language,
            themeType = state.theme,
            colors = state.colors,
            dynamicColor = state.isEnableDynamicColors,
        ) {
            AppNavigator(
                initialScreen = SplashScreen(),
                navigatorManager = navigatorManager,
                content = { navigator ->
                    CurrentScreen()
                    if (navigator.lastItemOrNull is TabsScreen) getNotificationPermission()
                },
            )
            throw NullPointerException("Test")
            LaunchedEffect(key1 = state.secureMode) {
                when (state.secureMode) {
                    true -> window.setFlags(FLAG_SECURE, FLAG_SECURE)
                    false -> window.clearFlags(FLAG_SECURE)
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent.action == ACTION_VIEW && intent.dataString == EDITOR_DEEP_LINK) {
            viewModel.dispatchEvent(MainEvent.NavigateToEditor)
        }
    }

    override fun onPause() {
        super.onPause()
        sendBroadcast(MainWidgetReceiver.intent(this))
    }

    override fun fetchViewModelFactory() = viewModelFactory

    override fun fetchViewModelClass() = MainViewModel::class.java

    private fun getNotificationPermission() {
        try {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S_V2) {
                requestPermissionLauncher.launch(arrayOf(POST_NOTIFICATIONS, SCHEDULE_EXACT_ALARM))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
