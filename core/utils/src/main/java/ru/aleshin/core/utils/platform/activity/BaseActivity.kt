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
package ru.aleshin.core.utils.platform.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModelProvider
import ru.aleshin.core.utils.platform.screenmodel.BaseViewModel
import ru.aleshin.core.utils.platform.screenmodel.contract.*

/**
 * @author Stanislav Aleshin on 19.02.2023.
 */
abstract class BaseActivity<S : BaseViewState, E : BaseEvent, A : BaseAction, F : BaseUiEffect> : ComponentActivity() {

    protected val viewModel by lazy {
        ViewModelProvider(this, fetchViewModelFactory())[fetchViewModelClass()]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        initDI()
        setTheme()
        super.onCreate(savedInstanceState)
        setContent { Content() }
    }

    open fun setTheme() {}

    open fun initDI() {}

    @Composable
    abstract fun Content()

    abstract fun fetchViewModelFactory(): ViewModelProvider.Factory

    abstract fun fetchViewModelClass(): Class<out BaseViewModel<S, E, A, F>>
}
