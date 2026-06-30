/*
 * Copyright 2025 Stanislav Aleshin
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

import android.Manifest.permission.POST_NOTIFICATIONS
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.WindowManager.LayoutParams.FLAG_SECURE
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.defaultComponentContext
import com.arkivanov.decompose.extensions.compose.experimental.stack.ChildStack
import ru.aleshin.core.ui.theme.TimePlannerTheme
import ru.aleshin.core.utils.architecture.store.compose.stateAsState
import ru.aleshin.core.utils.managers.rememberDrawerManager
import ru.aleshin.core.utils.navigation.backAnimation
import ru.aleshin.timeplanner.application.fetchApp
import ru.aleshin.timeplanner.presentation.ui.main.contract.DeepLinkTarget
import ru.aleshin.timeplanner.presentation.ui.main.store.MainComponent
import ru.aleshin.timeplanner.presentation.ui.main.store.MainComponentFactory
import ru.aleshin.timeplanner.presentation.ui.splash.SplashContent
import ru.aleshin.timeplanner.presentation.ui.tabs.TabNavigationContent
import ru.aleshin.timeplanner.presentation.widgets.main.MainWidgetReceiver
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 27.02.2023.
 */
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var componentFactory: MainComponentFactory

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { _ -> }

    private lateinit var mainComponent: MainComponent

    private var notificationPermissionRequested = false

    @OptIn(ExperimentalDecomposeApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        fetchApp().appComponent.inject(this)
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
        )

        mainComponent = componentFactory.createComponent(
            componentContext = defaultComponentContext(),
            initialDeepLinkTarget = DeepLinkTarget.byIntent(intent),
        )

        setContent {
            val store = mainComponent.store
            val state by store.stateAsState()

            TimePlannerTheme(
                languageType = state.language,
                themeType = state.theme,
                colors = state.colors,
                dynamicColor = state.isEnableDynamicColors,
            ) {
                val drawerState = rememberDrawerState(DrawerValue.Closed)
                val drawerManager = rememberDrawerManager(drawerState)

                HomeNavigationDrawer(
                    drawerState = drawerState,
                    drawerManager = drawerManager,
                ) {
                    ChildStack(
                        stack = mainComponent.childStack,
                        animation = backAnimation(
                            backHandler = mainComponent.backHandler,
                            onBack = mainComponent::navigateToBack
                        )
                    ) { child ->
                        when (val instance = child.instance) {
                            is MainComponent.Child.SplashChild -> {
                                SplashContent()
                            }

                            is MainComponent.Child.TabNavigationChild -> {
                                TabNavigationContent(instance.component)
                                LaunchedEffect(Unit) {
                                    getNotificationPermission()
                                }
                            }

                            is MainComponent.Child.EditorChild -> {
                                instance.component.contentProvider.invoke(Modifier)
                            }

                            is MainComponent.Child.HomeChild -> {
                                instance.component.contentProvider.invoke(Modifier)
                            }
                        }
                    }

                    LaunchedEffect(key1 = state.secureMode) {
                        when (state.secureMode) {
                            true -> window.setFlags(FLAG_SECURE, FLAG_SECURE)
                            false -> window.clearFlags(FLAG_SECURE)
                        }
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        val target = DeepLinkTarget.byIntent(intent)
        if (target != null && ::mainComponent.isInitialized) mainComponent.onDeepLink(target)
    }

    override fun onPause() {
        super.onPause()
        sendBroadcast(MainWidgetReceiver.intent(this))
    }

    private fun getNotificationPermission() {
        try {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S_V2 && !notificationPermissionRequested) {
                notificationPermissionRequested = true
                requestPermissionLauncher.launch(arrayOf(POST_NOTIFICATIONS))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
