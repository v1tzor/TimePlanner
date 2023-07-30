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
package ru.aleshin.core.utils.managers

import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 27.02.2023.
 */
interface DrawerManager {

    val drawerValue: State<DrawerValue>

    val selectedItem: MutableStateFlow<Int>

    suspend fun openDrawer()

    suspend fun closeDrawer()

    class Base @Inject constructor(internal val drawerState: DrawerState) : DrawerManager {

        override val drawerValue = derivedStateOf { drawerState.currentValue }

        override val selectedItem = MutableStateFlow(0)

        override suspend fun openDrawer() {
            drawerState.open()
        }

        override suspend fun closeDrawer() {
            drawerState.close()
        }

        companion object {
            fun Saver(drawerState: DrawerState) = Saver<Base, Any>(
                save = { null },
                restore = { Base(drawerState) },
            )
        }
    }
}

val LocalDrawerManager = staticCompositionLocalOf<DrawerManager?> { null }

@Composable
fun rememberDrawerManager(
    drawerState: DrawerState,
): DrawerManager {
    return rememberSaveable(saver = DrawerManager.Base.Saver(drawerState)) {
        DrawerManager.Base(drawerState)
    }
}

interface DrawerItem {
    val icon: Int @Composable get
    val title: String @Composable get
}
