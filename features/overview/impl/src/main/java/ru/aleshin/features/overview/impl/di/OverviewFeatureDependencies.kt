/*
 * Copyright 2026 Stanislav Aleshin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package ru.aleshin.features.overview.impl.di

import ru.aleshin.core.domain.common.ScheduleStatusChecker
import ru.aleshin.core.domain.common.TimeTaskProgressManager
import ru.aleshin.core.domain.common.TimeOverlayManager
import ru.aleshin.core.domain.common.TimeTaskStatusChecker
import ru.aleshin.core.domain.repository.MainCategoryRepository
import ru.aleshin.core.domain.repository.ScheduleRepository
import ru.aleshin.core.domain.repository.TimeTaskRepository
import ru.aleshin.core.domain.repository.TemplatesRepository
import ru.aleshin.core.domain.repository.UndefinedTaskRepository
import ru.aleshin.core.utils.inject.BaseFeatureDependencies
import ru.aleshin.core.utils.managers.CoroutineManager
import ru.aleshin.core.utils.managers.DateManager

/** @author Stanislav Aleshin on 11.07.2026. */
public interface OverviewFeatureDependencies : BaseFeatureDependencies {
    public val schedulesRepository: ScheduleRepository
    public val timeTaskRepository: TimeTaskRepository
    public val templatesRepository: TemplatesRepository
    public val undefinedTaskRepository: UndefinedTaskRepository
    public val mainCategoryRepository: MainCategoryRepository
    public val coroutineManager: CoroutineManager
    public val scheduleStatusChecker: ScheduleStatusChecker
    public val timeTaskProgressManager: TimeTaskProgressManager
    public val timeOverlayManager: TimeOverlayManager
    public val taskStatusManager: TimeTaskStatusChecker
    public val dateManger: DateManager
}
