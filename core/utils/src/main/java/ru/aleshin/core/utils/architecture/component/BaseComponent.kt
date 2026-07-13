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

package ru.aleshin.core.utils.architecture.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.arkivanov.essenty.instancekeeper.getOrCreate

/**
 * @author Stanislav Aleshin on 20.08.2025.
 */
abstract class BaseComponent(
    componentContext: ComponentContext
) : ComponentContext by componentContext {

    init {
        instanceKeeper.getOrCreate(key = this.toString()) {
            object : InstanceKeeper.Instance {
                override fun onDestroy() {
                    super.onDestroy()
                    onDestroyInstance()
                }
            }
        }
    }

    open fun onDestroyInstance() {}
}

abstract class ChildComponent(
    componentContext: ComponentContext
) : BaseComponent(componentContext)

abstract class FeatureComponent<C, O : BaseOutput>(
    componentContext: ComponentContext
) : BaseComponent(componentContext) {
    abstract fun navigateToBack()
}