/*
 * Copyright 2026 Stanislav Aleshin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package ru.aleshin.features.overview.impl.di.holder

import ru.aleshin.core.utils.inject.BaseFeatureController
import ru.aleshin.features.overview.api.OverviewFeatureApi
import ru.aleshin.features.overview.impl.di.component.OverviewComponent

/** @author Stanislav Aleshin on 11.07.2026. */
internal class OverviewFeatureController(
    component: OverviewComponent
) : BaseFeatureController<OverviewFeatureApi, OverviewComponent>(component)
