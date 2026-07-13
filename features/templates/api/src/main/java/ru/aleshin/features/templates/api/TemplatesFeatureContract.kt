/*
 * Copyright 2026 Stanislav Aleshin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package ru.aleshin.features.templates.api

import kotlinx.serialization.Serializable
import ru.aleshin.core.utils.architecture.component.BaseOutput

/**
 * @author Stanislav Aleshin on 11.07.2026.
 */
@Serializable
public sealed class TemplatesConfig {

    @Serializable
    public data object Templates : TemplatesConfig()
}

public sealed class TemplatesOutput : BaseOutput {
    public data object NavigateToBack : TemplatesOutput()
}