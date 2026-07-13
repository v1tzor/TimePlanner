/*
 * Copyright 2026 Stanislav Aleshin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package ru.aleshin.features.templates.impl.di

import ru.aleshin.core.domain.common.TimeOverlayManager
import ru.aleshin.core.domain.repository.MainCategoryRepository
import ru.aleshin.core.domain.repository.ScheduleRepository
import ru.aleshin.core.domain.repository.TemplatesRepository
import ru.aleshin.core.domain.repository.TimeTaskRepository
import ru.aleshin.core.presentation.notifications.TemplatesAlarmManager
import ru.aleshin.core.presentation.notifications.TimeTaskAlarmManager
import ru.aleshin.core.utils.inject.BaseFeatureDependencies
import ru.aleshin.core.utils.managers.CoroutineManager
import ru.aleshin.core.utils.managers.DateManager

/** @author Stanislav Aleshin on 11.07.2026. */
public interface TemplatesFeatureDependencies : BaseFeatureDependencies {
    public val templatesRepository: TemplatesRepository
    public val mainCategoryRepository: MainCategoryRepository
    public val schedulesRepository: ScheduleRepository
    public val timeTaskRepository: TimeTaskRepository
    public val coroutineManager: CoroutineManager
    public val timeOverlayManager: TimeOverlayManager
    public val timeTaskAlarmManager: TimeTaskAlarmManager
    public val templatesAlarmManager: TemplatesAlarmManager
    public val dateManger: DateManager
}
