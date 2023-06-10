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
package ru.aleshin.features.settings.impl.presentation.ui.contract

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import ru.aleshin.core.utils.functional.Constants
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * @author Stanislav Aleshin on 10.06.2023.
 */
internal object RestoreBackupContract : ActivityResultContract<Unit, Uri?>() {

    override fun createIntent(context: Context, input: Unit) = Intent().apply {
        action = Intent.ACTION_OPEN_DOCUMENT
        type = Constants.Backup.ZIP_FILE_TYPE
        addCategory(Intent.CATEGORY_OPENABLE)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        return if (resultCode == Activity.RESULT_OK && intent != null) intent.data else null
    }
}

internal object SaveBackupContract : ActivityResultContract<Unit, Uri?>() {

    override fun createIntent(context: Context, input: Unit) = Intent().apply {
        val dateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
        val time = dateFormat.format(Calendar.getInstance().time)
        val fileName = Constants.Backup.BACKUP_ZIP_NAME.format(time)
        action = Intent.ACTION_CREATE_DOCUMENT
        type = Constants.Backup.ZIP_FILE_TYPE
        putExtra(Intent.EXTRA_TITLE, fileName)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        return if (resultCode == Activity.RESULT_OK && intent != null) intent.data else null
    }
}

internal fun ActivityResultLauncher<Unit>.launch() = launch(Unit)
