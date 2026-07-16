package ru.aleshin.features.overview.impl.navigation;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
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

  private DefaultOverviewContentProviderFactory_Factory(
      Provider<OverviewComposeStore.Factory> overviewStoreFactoryProvider) {
    this.overviewStoreFactoryProvider = overviewStoreFactoryProvider;
  }

  @Override
  public DefaultOverviewContentProviderFactory get() {
    return newInstance(overviewStoreFactoryProvider.get());
  }

  public static DefaultOverviewContentProviderFactory_Factory create(
      Provider<OverviewComposeStore.Factory> overviewStoreFactoryProvider) {
    return new DefaultOverviewContentProviderFactory_Factory(overviewStoreFactoryProvider);
  }

  public static DefaultOverviewContentProviderFactory newInstance(
      OverviewComposeStore.Factory overviewStoreFactory) {
    return new DefaultOverviewContentProviderFactory(overviewStoreFactory);
  }
}
