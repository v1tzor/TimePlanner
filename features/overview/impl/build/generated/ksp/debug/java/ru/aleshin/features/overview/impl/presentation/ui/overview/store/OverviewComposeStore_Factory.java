package ru.aleshin.features.overview.impl.presentation.ui.overview.store;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import ru.aleshin.core.utils.architecture.store.communicators.EffectCommunicator;
import ru.aleshin.core.utils.architecture.store.communicators.StateCommunicator;
import ru.aleshin.core.utils.managers.CoroutineManager;
import ru.aleshin.features.overview.impl.presentation.ui.overview.contract.OverviewEffect;
import ru.aleshin.features.overview.impl.presentation.ui.overview.contract.OverviewState;

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
public final class OverviewComposeStore_Factory implements Factory<OverviewComposeStore> {
  private final Provider<OverviewWorkProcessor> workProcessorProvider;

  private final Provider<StateCommunicator<OverviewState>> stateCommunicatorProvider;

  private final Provider<EffectCommunicator<OverviewEffect>> effectCommunicatorProvider;

  private final Provider<CoroutineManager> coroutineManagerProvider;

  private OverviewComposeStore_Factory(Provider<OverviewWorkProcessor> workProcessorProvider,
      Provider<StateCommunicator<OverviewState>> stateCommunicatorProvider,
      Provider<EffectCommunicator<OverviewEffect>> effectCommunicatorProvider,
      Provider<CoroutineManager> coroutineManagerProvider) {
    this.workProcessorProvider = workProcessorProvider;
    this.stateCommunicatorProvider = stateCommunicatorProvider;
    this.effectCommunicatorProvider = effectCommunicatorProvider;
    this.coroutineManagerProvider = coroutineManagerProvider;
  }

  @Override
  public OverviewComposeStore get() {
    return newInstance(workProcessorProvider.get(), stateCommunicatorProvider.get(), effectCommunicatorProvider.get(), coroutineManagerProvider.get());
  }

  public static OverviewComposeStore_Factory create(
      Provider<OverviewWorkProcessor> workProcessorProvider,
      Provider<StateCommunicator<OverviewState>> stateCommunicatorProvider,
      Provider<EffectCommunicator<OverviewEffect>> effectCommunicatorProvider,
      Provider<CoroutineManager> coroutineManagerProvider) {
    return new OverviewComposeStore_Factory(workProcessorProvider, stateCommunicatorProvider, effectCommunicatorProvider, coroutineManagerProvider);
  }

  public static OverviewComposeStore newInstance(OverviewWorkProcessor workProcessor,
      StateCommunicator<OverviewState> stateCommunicator,
      EffectCommunicator<OverviewEffect> effectCommunicator, CoroutineManager coroutineManager) {
    return new OverviewComposeStore(workProcessor, stateCommunicator, effectCommunicator, coroutineManager);
  }
}
