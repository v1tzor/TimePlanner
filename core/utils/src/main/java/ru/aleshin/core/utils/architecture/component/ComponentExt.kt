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

package ru.aleshin.core.utils.architecture.component

import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.arkivanov.essenty.statekeeper.ExperimentalStateKeeperApi
import kotlinx.serialization.KSerializer
import ru.aleshin.core.utils.architecture.store.BaseComposeStore
import ru.aleshin.core.utils.architecture.store.contract.StoreAction
import ru.aleshin.core.utils.architecture.store.contract.StoreEffect
import ru.aleshin.core.utils.architecture.store.contract.StoreEvent
import ru.aleshin.core.utils.architecture.store.contract.StoreState
import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadOnlyProperty

/**
 * @author Stanislav Aleshin on 20.08.2025.
 */
@OptIn(ExperimentalStateKeeperApi::class)
inline fun <reified Store : BaseComposeStore<S, E, A, F, I, O>, S : StoreState, E : StoreEvent, A : StoreAction, F : StoreEffect, I : BaseInput, O : BaseOutput> BaseComponent.saveableStore(
    storeFactory: BaseComposeStore.Factory<Store, S>,
    defaultState: S,
    stateSerializer: KSerializer<S>,
    input: I,
    outputConsumer: OutputConsumer<O>,
    storeKey: String,
): PropertyDelegateProvider<Any?, ReadOnlyProperty<Any?, Store>> {
    return PropertyDelegateProvider { _, property ->
        val store = instanceKeeper.getOrCreate<Store>(key = storeKey) {
            val savedState = stateKeeper.consume("${storeKey}State", stateSerializer)
            storeFactory.create(savedState ?: defaultState).apply {
                initialize(input, savedState != null)
            }
        }
        stateKeeper.register("${storeKey}State", stateSerializer) { store.state }
        store.setOutputConsumer(outputConsumer)
        ReadOnlyProperty { _, _ -> store }
    }
}

@OptIn(ExperimentalStateKeeperApi::class)
inline fun <reified Store : BaseComposeStore<S, E, A, F, EmptyInput, EmptyOutput>, S : StoreState, E : StoreEvent, A : StoreAction, F : StoreEffect> BaseComponent.saveableStore(
    storeFactory: BaseComposeStore.Factory<Store, S>,
    defaultState: S,
    stateSerializer: KSerializer<S>,
    storeKey: String,
): PropertyDelegateProvider<Any?, ReadOnlyProperty<Any?, Store>> {
    return PropertyDelegateProvider { _, property ->
        val store = instanceKeeper.getOrCreate<Store>(key = storeKey) {
            val savedState = stateKeeper.consume("${storeKey}State", stateSerializer)
            storeFactory.create(savedState ?: defaultState).apply {
                initialize(EmptyInput, savedState != null)
            }
        }
        stateKeeper.register("${storeKey}State", stateSerializer) { store.state }
        store.setOutputConsumer(EmptyOutputConsumer)
        ReadOnlyProperty { _, _ -> store }
    }
}

@OptIn(ExperimentalStateKeeperApi::class)
inline fun <reified Store : BaseComposeStore<S, E, A, F, I, EmptyOutput>, S : StoreState, E : StoreEvent, A : StoreAction, F : StoreEffect, I : BaseInput> BaseComponent.saveableStore(
    storeFactory: BaseComposeStore.Factory<Store, S>,
    defaultState: S,
    stateSerializer: KSerializer<S>,
    input: I,
    storeKey: String,
): PropertyDelegateProvider<Any?, ReadOnlyProperty<Any?, Store>> {
    return PropertyDelegateProvider { _, property ->
        val store = instanceKeeper.getOrCreate<Store>(key = storeKey) {
            val savedState = stateKeeper.consume("${storeKey}State", stateSerializer)
            storeFactory.create(savedState ?: defaultState).apply {
                initialize(input, savedState != null)
            }
        }
        stateKeeper.register("${storeKey}State", stateSerializer) { store.state }
        store.setOutputConsumer(EmptyOutputConsumer)
        ReadOnlyProperty { _, _ -> store }
    }
}

@OptIn(ExperimentalStateKeeperApi::class)
inline fun <reified Store : BaseComposeStore<S, E, A, F, EmptyInput, O>, S : StoreState, E : StoreEvent, A : StoreAction, F : StoreEffect, O : BaseOutput> BaseComponent.saveableStore(
    storeFactory: BaseComposeStore.Factory<Store, S>,
    defaultState: S,
    stateSerializer: KSerializer<S>,
    outputConsumer: OutputConsumer<O>,
    storeKey: String,
): PropertyDelegateProvider<Any?, ReadOnlyProperty<Any?, Store>> {
    return PropertyDelegateProvider { _, property ->
        val store = instanceKeeper.getOrCreate<Store>(key = storeKey) {
            val savedState = stateKeeper.consume("${storeKey}State", stateSerializer)
            storeFactory.create(savedState ?: defaultState).apply {
                initialize(EmptyInput, savedState != null)
            }
        }
        stateKeeper.register("${storeKey}State", stateSerializer) {
            store.state
        }
        store.setOutputConsumer(outputConsumer)
        ReadOnlyProperty { _, _ -> store }
    }
}