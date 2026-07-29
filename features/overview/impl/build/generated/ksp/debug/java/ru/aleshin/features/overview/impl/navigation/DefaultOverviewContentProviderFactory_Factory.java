package ru.aleshin.features.overview.impl.navigation;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import ru.aleshin.features.overview.impl.presentation.ui.goal.details.store.GoalDetailsComposeStore;
import ru.aleshin.features.overview.impl.presentation.ui.goal.history.store.GoalsHistoryComposeStore;
import ru.aleshin.features.overview.impl.presentation.ui.overview.store.OverviewComposeStore;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class DefaultOverviewContentProviderFactory_Factory implements Factory<DefaultOverviewContentProviderFactory> {
  private final Provider<OverviewComposeStore.Factory> overviewStoreFactoryProvider;

  private final Provider<GoalDetailsComposeStore.Factory> goalDetailsStoreFactoryProvider;

  private final Provider<GoalsHistoryComposeStore.Factory> goalsHistoryStoreFactoryProvider;

  private DefaultOverviewContentProviderFactory_Factory(
      Provider<OverviewComposeStore.Factory> overviewStoreFactoryProvider,
      Provider<GoalDetailsComposeStore.Factory> goalDetailsStoreFactoryProvider,
      Provider<GoalsHistoryComposeStore.Factory> goalsHistoryStoreFactoryProvider) {
    this.overviewStoreFactoryProvider = overviewStoreFactoryProvider;
    this.goalDetailsStoreFactoryProvider = goalDetailsStoreFactoryProvider;
    this.goalsHistoryStoreFactoryProvider = goalsHistoryStoreFactoryProvider;
  }

  @Override
  public DefaultOverviewContentProviderFactory get() {
    return newInstance(overviewStoreFactoryProvider.get(), goalDetailsStoreFactoryProvider.get(), goalsHistoryStoreFactoryProvider.get());
  }

  public static DefaultOverviewContentProviderFactory_Factory create(
      Provider<OverviewComposeStore.Factory> overviewStoreFactoryProvider,
      Provider<GoalDetailsComposeStore.Factory> goalDetailsStoreFactoryProvider,
      Provider<GoalsHistoryComposeStore.Factory> goalsHistoryStoreFactoryProvider) {
    return new DefaultOverviewContentProviderFactory_Factory(overviewStoreFactoryProvider, goalDetailsStoreFactoryProvider, goalsHistoryStoreFactoryProvider);
  }

  public static DefaultOverviewContentProviderFactory newInstance(
      OverviewComposeStore.Factory overviewStoreFactory,
      GoalDetailsComposeStore.Factory goalDetailsStoreFactory,
      GoalsHistoryComposeStore.Factory goalsHistoryStoreFactory) {
    return new DefaultOverviewContentProviderFactory(overviewStoreFactory, goalDetailsStoreFactory, goalsHistoryStoreFactory);
  }
}
