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
package ru.aleshin.core.ui.views

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.coroutines.resume

/**
 * @author Stanislav Aleshin on 27.03.2023.
 */
@Composable
fun <V : DialogVisuals, A : DialogAction> DialogHost(
    state: DialogHostState<V, A>,
    dialog: @Composable (DialogData<V, A>) -> Unit,
) {
    val dialogDate = state.currentDialogData
    if (dialogDate != null) {
        dialog(dialogDate)
    }
}

class DialogHostState<V : DialogVisuals, A : DialogAction> {

    private val mutex = Mutex()

    var currentDialogData by mutableStateOf<DialogData<V, A>?>(null)
        private set

    suspend fun showDialog(visuals: V) = mutex.withLock {
        try {
            suspendCancellableCoroutine { continuation ->
                currentDialogData = DialogData.Base(visuals, continuation)
            }
        } finally {
            currentDialogData = null
        }
    }
}

interface DialogAction

sealed class DialogResult<A : DialogAction> {
    class Dismiss<A : DialogAction> : DialogResult<A>()
    data class PerformAction<A : DialogAction>(val action: A) : DialogResult<A>()
}

suspend fun <A : DialogAction> DialogResult<A>.onPerformAction(block: suspend (A) -> Unit) {
    if (this is DialogResult.PerformAction) block.invoke(this.action)
}

interface DialogVisuals

interface DialogData<V : DialogVisuals, A : DialogAction> {

    val visuals: V

    fun dismiss()

    fun performAction(action: A)

    class Base<V : DialogVisuals, A : DialogAction> constructor(
        override val visuals: V,
        private val continuation: CancellableContinuation<DialogResult<A>>,
    ) : DialogData<V, A> {

        override fun dismiss() {
            if (continuation.isActive) continuation.resume(DialogResult.Dismiss())
        }

        override fun performAction(action: A) {
            if (continuation.isActive) continuation.resume(DialogResult.PerformAction(action))
        }
    }
}
