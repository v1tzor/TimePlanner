package ru.aleshin.features.analytics.api.di

import ru.aleshin.features.analytics.api.navigation.AnalyticsFeatureStarter
import ru.aleshin.module_injector.BaseFeatureApi

/**
 * @author Stanislav Aleshin on 30.03.2023.
 */
interface AnalyticsFeatureApi : BaseFeatureApi {
    fun fetchStarter(): AnalyticsFeatureStarter
}
