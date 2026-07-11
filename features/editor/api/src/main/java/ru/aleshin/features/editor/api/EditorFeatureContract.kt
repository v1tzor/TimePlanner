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
package ru.aleshin.features.editor.api

import kotlinx.serialization.Serializable
import ru.aleshin.core.utils.architecture.component.BaseOutput
import ru.aleshin.core.utils.functional.DateSerializer
import ru.aleshin.core.utils.functional.TimeRange
import java.util.Date

/**
 * @author Stanislav Aleshin on 01.07.2026.
 */
@Serializable
public sealed interface EditorConfig {

    @Serializable
    public data class Editor(
        val timeTaskId: Long? = null,
        val timeRange: TimeRange? = null,
        @Serializable(DateSerializer::class) val date: Date? = null,
        val undefinedTaskId: Long? = null,
    ) : EditorConfig
}

/**
 * @author Stanislav Aleshin on 01.07.2026.
 */
public sealed interface EditorOutput : BaseOutput {
    public data class NavigateToCategories(val categoryId: Long) : EditorOutput
    public data object NavigateToTemplates : EditorOutput
    public data object NavigateToBack : EditorOutput
}
