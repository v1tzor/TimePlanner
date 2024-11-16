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
package ru.aleshin.timeplanner.presentation.ui.main.contract

import android.content.Intent
import android.content.Intent.ACTION_VIEW
import ru.aleshin.core.utils.functional.Constants.App.EDITOR_DEEP_LINK

/**
 * @author Stanislav Aleshin on 16.11.2024.
 */
enum class DeepLinkTarget {
    MAIN, EDITOR;

    companion object {
        fun byIntent(intent: Intent) = when {
            intent.action == ACTION_VIEW && intent.dataString == EDITOR_DEEP_LINK -> EDITOR
            else -> MAIN
        }
    }
}