/*
 * Copyright 2026 Stanislav Aleshin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package ru.aleshin.features.templates.impl.di.holder

import ru.aleshin.core.utils.inject.BaseFeatureController
import ru.aleshin.features.templates.api.TemplatesFeatureApi
import ru.aleshin.features.templates.impl.di.component.TemplatesComponent

/** @author Stanislav Aleshin on 11.07.2026. */
internal class TemplatesFeatureController(
    component: TemplatesComponent
) : BaseFeatureController<TemplatesFeatureApi, TemplatesComponent>(component)
