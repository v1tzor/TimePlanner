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
package ru.aleshin.features.editor.impl.presentation.mappers

import ru.aleshin.features.editor.impl.domain.entites.EditorFailures
import ru.aleshin.features.editor.impl.presentation.theme.tokens.EditorStrings

/**
 * @author Stanislav Aleshin on 28.03.2023.
 */
internal fun EditorFailures.mapToMessage(editorStrings: EditorStrings) = when (this) {
    is EditorFailures.TimeOverlayError -> if (this.startOverlay != null && this.endOverlay != null) {
        editorStrings.fullOverlayError
    } else if (startOverlay != null) {
        editorStrings.startOverlayError
    } else {
        editorStrings.endOverlayError
    }
    is EditorFailures.OtherError -> editorStrings.otherError
}
