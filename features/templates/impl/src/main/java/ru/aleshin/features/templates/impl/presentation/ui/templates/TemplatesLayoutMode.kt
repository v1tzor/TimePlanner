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
package ru.aleshin.features.templates.impl.presentation.ui.templates

import ru.aleshin.timeplanner.core.ui.views.AdaptiveLayoutInfo

/**
 * @author Stanislav Aleshin on 28.07.2026.
 */
internal enum class TemplatesLayoutMode {
    COMPACT,
    SUPPORTING;

    companion object {

        fun from(adaptiveLayoutInfo: AdaptiveLayoutInfo): TemplatesLayoutMode {
            return when {
                adaptiveLayoutInfo.isBookPosture -> SUPPORTING
                adaptiveLayoutInfo.isCompactWidth || adaptiveLayoutInfo.isMediumWidth || adaptiveLayoutInfo.isExpandedWidth -> COMPACT
                else -> SUPPORTING
            }
        }
    }
}
