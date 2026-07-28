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
package ru.aleshin.timeplanner.presentation.ui.main

import android.Manifest.permission.POST_NOTIFICATIONS
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.KeyboardShortcutGroup
import android.view.KeyboardShortcutInfo
import android.view.Menu
import android.view.WindowManager.LayoutParams.FLAG_SECURE
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.defaultComponentContext
import com.arkivanov.decompose.extensions.compose.experimental.stack.ChildStack
import ru.aleshin.core.utils.architecture.store.compose.stateAsState
import ru.aleshin.core.utils.navigation.backAnimation
import ru.aleshin.timeplanner.R
import ru.aleshin.timeplanner.application.fetchApp
import ru.aleshin.timeplanner.core.ui.theme.TimePlannerTheme
import ru.aleshin.timeplanner.presentation.ui.main.contract.DeepLinkTarget
import ru.aleshin.timeplanner.presentation.ui.main.contract.ShareTarget
import ru.aleshin.timeplanner.presentation.ui.main.store.MainComponent
import ru.aleshin.timeplanner.presentation.ui.main.store.MainComponentFactory
import ru.aleshin.timeplanner.presentation.ui.splash.SplashContent
import ru.aleshin.timeplanner.presentation.ui.tabs.TabNavigationContent
import ru.aleshin.timeplanner.presentation.widgets.main.MainWidgetUpdateWorker
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
            initialShareTarget = ShareTarget.byIntent(intent),
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
                            TabNavigationContent(component = instance.component)
                        }
                        is MainComponent.Child.EditorChild -> {
                            instance.contentProvider.invoke(Modifier)
                        }
                        is MainComponent.Child.SettingsChild -> {
                            instance.contentProvider.invoke(Modifier)
                        }
                        is MainComponent.Child.TemplatesChild -> {
                            instance.contentProvider.invoke(Modifier)
                        }
                    }
                }

                LaunchedEffect(Unit) {
                    getNotificationPermission()
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

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)

        val target = DeepLinkTarget.byIntent(intent)
        if (target != null && ::mainComponent.isInitialized) mainComponent.onDeepLink(target)

        val shareTarget = ShareTarget.byIntent(intent)
        if (shareTarget != null && ::mainComponent.isInitialized) mainComponent.onShare(shareTarget)
    }

    override fun onPause() {
        super.onPause()
        MainWidgetUpdateWorker.enqueue(this)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_ESCAPE) {
            onBackPressedDispatcher.onBackPressed()
            return true
        }
        return super.onKeyUp(keyCode, event)
    }

    override fun onProvideKeyboardShortcuts(
        data: MutableList<KeyboardShortcutGroup>?,
        menu: Menu?,
        deviceId: Int,
    ) {
        data?.add(
            KeyboardShortcutGroup(
                getString(R.string.shortcut_navigation),
                listOf(
                    KeyboardShortcutInfo(
                        getString(R.string.shortcut_home),
                        KeyEvent.KEYCODE_1,
                        KeyEvent.META_ALT_ON,
                    ),
                    KeyboardShortcutInfo(
                        getString(R.string.shortcut_overview),
                        KeyEvent.KEYCODE_2,
                        KeyEvent.META_ALT_ON,
                    ),
                    KeyboardShortcutInfo(
                        getString(R.string.shortcut_templates),
                        KeyEvent.KEYCODE_3,
                        KeyEvent.META_ALT_ON,
                    ),
                    KeyboardShortcutInfo(
                        getString(R.string.shortcut_analytics),
                        KeyEvent.KEYCODE_4,
                        KeyEvent.META_ALT_ON,
                    ),
                ),
            )
        )
        data?.add(
            KeyboardShortcutGroup(
                getString(R.string.shortcut_editor),
                listOf(
                    KeyboardShortcutInfo(
                        getString(R.string.shortcut_save),
                        KeyEvent.KEYCODE_S,
                        KeyEvent.META_CTRL_ON,
                    ),
                ),
            )
        )
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
