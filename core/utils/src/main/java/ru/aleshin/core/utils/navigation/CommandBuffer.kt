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
package ru.aleshin.core.utils.navigation

/**
 * @author Stanislav Aleshin on 14.02.2023.
 */
interface CommandBuffer : CommandListenerManager {

    fun sendCommand(command: Command)

    class Base : CommandBuffer {

        private val commandBuffer = mutableListOf<Command>()
        private var commandListener: CommandListener? = null

        override fun sendCommand(command: Command) {
            commandListener?.invoke(command) ?: commandBuffer.add(command)
        }

        override fun setListener(listener: CommandListener) {
            this.commandListener = listener

            commandBuffer.forEach { listener.invoke(it) }
            commandBuffer.clear()
        }

        override fun removeListener() {
            commandListener = null
        }
    }
}
