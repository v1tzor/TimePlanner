package ru.aleshin.features.analytics.api.navigation

import cafe.adriel.voyager.core.screen.Screen

/**
 * @author Stanislav Aleshin on 30.03.2023.
 */
interface AnalyticsFeatureStarter {
    fun provideMainScreen(): Screen
}
