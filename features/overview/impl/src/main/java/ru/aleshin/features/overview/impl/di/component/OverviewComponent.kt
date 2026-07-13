/*
 * Copyright 2026 Stanislav Aleshin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package ru.aleshin.features.overview.impl.di.component

import dagger.Component
import ru.aleshin.core.utils.di.FeatureScope
import ru.aleshin.features.overview.api.OverviewFeatureApi
import ru.aleshin.features.overview.impl.di.OverviewFeatureDependencies
import ru.aleshin.features.overview.impl.di.modules.DomainModule
import ru.aleshin.features.overview.impl.di.modules.PresentationModule

/** @author Stanislav Aleshin on 11.07.2026. */
@FeatureScope
@Component(
    modules = [DomainModule::class, PresentationModule::class],
    dependencies = [OverviewFeatureDependencies::class]
)
internal interface OverviewComponent : OverviewFeatureApi {

    @Component.Builder
    interface Builder {
        fun dependencies(deps: OverviewFeatureDependencies): Builder;
        fun build(): OverviewComponent
    }

    companion object {
        fun create(deps: OverviewFeatureDependencies): OverviewComponent {
            return DaggerOverviewComponent.builder()
                .dependencies(deps)
                .build()
        }
    }
}
