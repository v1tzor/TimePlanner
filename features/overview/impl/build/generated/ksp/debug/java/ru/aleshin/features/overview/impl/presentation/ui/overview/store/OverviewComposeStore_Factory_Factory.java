package ru.aleshin.features.overview.impl.presentation.ui.overview.store;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import ru.aleshin.core.utils.managers.CoroutineManager;

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
public final class OverviewComposeStore_Factory_Factory implements Factory<OverviewComposeStore.Factory> {
  private final Provider<OverviewWorkProcessor> workProcessorProvider;

  private final Provider<CoroutineManager> coroutineManagerProvider;

  private OverviewComposeStore_Factory_Factory(
      Provider<OverviewWorkProcessor> workProcessorProvider,
      Provider<CoroutineManager> coroutineManagerProvider) {
    this.workProcessorProvider = workProcessorProvider;
    this.coroutineManagerProvider = coroutineManagerProvider;
  }

  @Override
  public OverviewComposeStore.Factory get() {
    return newInstance(workProcessorProvider.get(), coroutineManagerProvider.get());
  }

  public static OverviewComposeStore_Factory_Factory create(
      Provider<OverviewWorkProcessor> workProcessorProvider,
      Provider<CoroutineManager> coroutineManagerProvider) {
    return new OverviewComposeStore_Factory_Factory(workProcessorProvider, coroutineManagerProvider);
  }

  public static OverviewComposeStore.Factory newInstance(OverviewWorkProcessor workProcessor,
      CoroutineManager coroutineManager) {
    return new OverviewComposeStore.Factory(workProcessor, coroutineManager);
  }
}
