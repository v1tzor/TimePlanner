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
package ru.aleshin.features.settings.impl.di.modules

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.reflect.TypeToken
import dagger.Module
import dagger.Provides
import ru.aleshin.core.utils.platform.SealedClassTypeAdapter
import kotlin.jvm.internal.Reflection

/**
 * @author Stanislav Aleshin on 04.08.2023.
 */
@Module
internal class CoreModule {

    @Provides
    fun provideGson(): Gson {
        return GsonBuilder()
            .serializeNulls()
            .setPrettyPrinting()
            .registerTypeAdapterFactory(
                object : TypeAdapterFactory {
                    override fun <T : Any> create(gson: Gson, type: TypeToken<T>): TypeAdapter<T> {
                        val kclass = Reflection.getOrCreateKotlinClass(type.rawType)
                        return if (kclass.sealedSubclasses.any()) {
                            SealedClassTypeAdapter(kclass, gson)
                        } else {
                            gson.getDelegateAdapter(this, type)
                        }
                    }
                },
            ).create()
    }
}
