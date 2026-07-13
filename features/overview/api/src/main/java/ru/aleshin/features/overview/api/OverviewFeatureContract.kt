/*
 * Copyright 2026 Stanislav Aleshin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package ru.aleshin.features.overview.api

import kotlinx.serialization.Serializable
import ru.aleshin.core.utils.architecture.component.BaseOutput
import ru.aleshin.core.utils.functional.TimeRange
import java.util.Date

/**
 * @author Stanislav Aleshin on 13.07.2026.
 */
@Serializable
public sealed class OverviewConfig {

    @Serializable
    public data class Overview(
        val sharedText: String? = null,
        val sharedKey: Long = 0L
    ) : OverviewConfig()

    @Serializable
    public data object Details : OverviewConfig()
}

public sealed class OverviewOutput : BaseOutput {
    public data object NavigateToBack : OverviewOutput()
    public data class NavigateToHome(val scheduleDate: Date?) : OverviewOutput()

    public data class NavigateToTaskEditor(
        val timeTaskId: Long? = null,
        val timeRange: TimeRange? = null,
        val date: Date? = null,
        val undefinedTaskId: Long? = null,
    ) : OverviewOutput()
}