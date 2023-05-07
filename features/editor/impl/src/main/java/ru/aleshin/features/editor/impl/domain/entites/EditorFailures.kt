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
 * imitations under the License.
 */
package ru.aleshin.features.editor.impl.domain.entites

import kotlinx.parcelize.Parcelize
import ru.aleshin.core.utils.functional.DomainFailures
import java.util.*

/**
 * @author Stanislav Aleshin on 08.03.2023.
 */
@Parcelize
internal sealed class EditorFailures : DomainFailures {
    data class TimeOverlayError(val startOverlay: Date?, val endOverlay: Date?) : EditorFailures()
    data class OtherError(val throwable: Throwable) : EditorFailures()
}
