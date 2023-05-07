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
package ru.aleshin.features.editor.impl.domain.common

import ru.aleshin.core.utils.handlers.ErrorHandler
import ru.aleshin.core.utils.managers.TimeOverlayException
import ru.aleshin.features.editor.impl.domain.entites.EditorFailures
import javax.inject.Inject

/**
 * @author Stanislav Aleshin on 08.03.2023.
 */
internal interface EditorErrorHandler : ErrorHandler<EditorFailures> {

    class Base @Inject constructor() : EditorErrorHandler {
        override fun handle(throwable: Throwable) = when (throwable) {
            is TimeOverlayException -> EditorFailures.TimeOverlayError(
                startOverlay = throwable.startOverlay,
                endOverlay = throwable.endOverlay,
            )
            else -> EditorFailures.OtherError(throwable)
        }
    }
}
