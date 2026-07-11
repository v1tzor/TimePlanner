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
package ru.aleshin.features.home.api

import kotlinx.serialization.Serializable
import ru.aleshin.core.utils.architecture.component.BaseOutput
import ru.aleshin.core.utils.functional.DateSerializer
import ru.aleshin.core.utils.functional.TimeRange
import java.util.Date

/**
 * @author Stanislav Aleshin on 01.07.2026.
 */
@Serializable
public sealed class HomeConfig {

    @Serializable
    public data class Overview(
        val sharedText: String? = null,
        val sharedKey: Long = 0L,
    ) : HomeConfig()

    @Serializable
    public data class Home(
        @Serializable(DateSerializer::class) val scheduleDate: Date? = null
    ) : HomeConfig()

    @Serializable
    public data object Details : HomeConfig()

    @Serializable
    public data object Templates : HomeConfig()

    @Serializable
    public data class Categories(val mainCategoryId: Long? = null) : HomeConfig()
}

/**
 * @author Stanislav Aleshin on 01.07.2026.
 */
public sealed class HomeOutput : BaseOutput {

    public data object NavigateToBack : HomeOutput()

    public data class NavigateToEditor(
        val timeTaskId: Long? = null,
        val timeRange: TimeRange? = null,
        val date: Date? = null,
        val undefinedTaskId: Long? = null,
    ) : HomeOutput()
}
