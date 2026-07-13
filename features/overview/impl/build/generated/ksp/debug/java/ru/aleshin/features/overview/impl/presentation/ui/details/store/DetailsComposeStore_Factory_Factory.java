package ru.aleshin.features.overview.impl.presentation.ui.details.store;

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
public final class DetailsComposeStore_Factory_Factory implements Factory<DetailsComposeStore.Factory> {
  private final Provider<DetailsWorkProcessor> workProcessorProvider;

  private final Provider<CoroutineManager> coroutineManagerProvider;

  private DetailsComposeStore_Factory_Factory(Provider<DetailsWorkProcessor> workProcessorProvider,
      Provider<CoroutineManager> coroutineManagerProvider) {
    this.workProcessorProvider = workProcessorProvider;
    this.coroutineManagerProvider = coroutineManagerProvider;
  }

  @Override
  public DetailsComposeStore.Factory get() {
    return newInstance(workProcessorProvider.get(), coroutineManagerProvider.get());
  }

  public static DetailsComposeStore_Factory_Factory create(
      Provider<DetailsWorkProcessor> workProcessorProvider,
      Provider<CoroutineManager> coroutineManagerProvider) {
    return new DetailsComposeStore_Factory_Factory(workProcessorProvider, coroutineManagerProvider);
  }

  public static DetailsComposeStore.Factory newInstance(DetailsWorkProcessor workProcessor,
      CoroutineManager coroutineManager) {
    return new DetailsComposeStore.Factory(workProcessor, coroutineManager);
  }
}
