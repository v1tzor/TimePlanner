package ru.aleshin.features.overview.impl.presentation.ui.details.store;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import ru.aleshin.core.utils.architecture.store.communicators.EffectCommunicator;
import ru.aleshin.core.utils.architecture.store.communicators.StateCommunicator;
import ru.aleshin.core.utils.managers.CoroutineManager;
import ru.aleshin.features.overview.impl.presentation.ui.details.contract.DetailsEffect;
import ru.aleshin.features.overview.impl.presentation.ui.details.contract.DetailsState;

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
public final class DetailsComposeStore_Factory implements Factory<DetailsComposeStore> {
  private final Provider<DetailsWorkProcessor> workProcessorProvider;

  private final Provider<StateCommunicator<DetailsState>> stateCommunicatorProvider;

  private final Provider<EffectCommunicator<DetailsEffect>> effectCommunicatorProvider;

  private final Provider<CoroutineManager> coroutineManagerProvider;

  private DetailsComposeStore_Factory(Provider<DetailsWorkProcessor> workProcessorProvider,
      Provider<StateCommunicator<DetailsState>> stateCommunicatorProvider,
      Provider<EffectCommunicator<DetailsEffect>> effectCommunicatorProvider,
      Provider<CoroutineManager> coroutineManagerProvider) {
    this.workProcessorProvider = workProcessorProvider;
    this.stateCommunicatorProvider = stateCommunicatorProvider;
    this.effectCommunicatorProvider = effectCommunicatorProvider;
    this.coroutineManagerProvider = coroutineManagerProvider;
  }

  @Override
  public DetailsComposeStore get() {
    return newInstance(workProcessorProvider.get(), stateCommunicatorProvider.get(), effectCommunicatorProvider.get(), coroutineManagerProvider.get());
  }

  public static DetailsComposeStore_Factory create(
      Provider<DetailsWorkProcessor> workProcessorProvider,
      Provider<StateCommunicator<DetailsState>> stateCommunicatorProvider,
      Provider<EffectCommunicator<DetailsEffect>> effectCommunicatorProvider,
      Provider<CoroutineManager> coroutineManagerProvider) {
    return new DetailsComposeStore_Factory(workProcessorProvider, stateCommunicatorProvider, effectCommunicatorProvider, coroutineManagerProvider);
  }

  public static DetailsComposeStore newInstance(DetailsWorkProcessor workProcessor,
      StateCommunicator<DetailsState> stateCommunicator,
      EffectCommunicator<DetailsEffect> effectCommunicator, CoroutineManager coroutineManager) {
    return new DetailsComposeStore(workProcessor, stateCommunicator, effectCommunicator, coroutineManager);
  }
}
