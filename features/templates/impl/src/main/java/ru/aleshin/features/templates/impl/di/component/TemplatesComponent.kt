/*
 * Copyright 2026 Stanislav Aleshin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package ru.aleshin.features.templates.impl.di.component

import dagger.Component
import ru.aleshin.core.utils.di.FeatureScope
import ru.aleshin.features.templates.api.TemplatesFeatureApi
import ru.aleshin.features.templates.impl.di.TemplatesFeatureDependencies
import ru.aleshin.features.templates.impl.di.modules.DomainModule
import ru.aleshin.features.templates.impl.di.modules.PresentationModule

/** @author Stanislav Aleshin on 11.07.2026. */
@FeatureScope
@Component(
    modules = [DomainModule::class, PresentationModule::class],
    dependencies = [TemplatesFeatureDependencies::class]
)
internal interface TemplatesComponent : TemplatesFeatureApi {

    @Component.Builder
    interface Builder {
        fun dependencies(value: TemplatesFeatureDependencies): Builder;
        fun build(): TemplatesComponent
    }

    companion object {
        fun create(value: TemplatesFeatureDependencies): TemplatesComponent =
            DaggerTemplatesComponent.builder().dependencies(value).build()
    }
}
