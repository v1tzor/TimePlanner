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
package ru.aleshin.features.settings.impl.presentation.mappers

import ru.aleshin.features.settings.impl.domain.common.SettingsFailures
import ru.aleshin.features.settings.impl.presentation.theme.tokens.SettingsStrings

/**
 * @author Stanislav Aleshin on 10.06.2023.
 */
internal fun SettingsFailures.mapToMessage(string: SettingsStrings) = when (this) {
    is SettingsFailures.BackupError -> string.errorBackupMessage
    is SettingsFailures.OtherError -> string.otherError
}
