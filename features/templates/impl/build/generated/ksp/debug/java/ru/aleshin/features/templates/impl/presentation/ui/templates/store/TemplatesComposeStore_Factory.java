package ru.aleshin.features.templates.impl.presentation.ui.templates.store;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import ru.aleshin.core.utils.architecture.store.communicators.EffectCommunicator;
import ru.aleshin.core.utils.architecture.store.communicators.StateCommunicator;
import ru.aleshin.core.utils.managers.CoroutineManager;
import ru.aleshin.features.templates.impl.presentation.ui.templates.contract.TemplatesEffect;
import ru.aleshin.features.templates.impl.presentation.ui.templates.contract.TemplatesState;

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
public final class TemplatesComposeStore_Factory implements Factory<TemplatesComposeStore> {
  private final Provider<TemplatesWorkProcessor> templatesWorkProcessorProvider;

  private final Provider<StateCommunicator<TemplatesState>> stateCommunicatorProvider;

  private final Provider<EffectCommunicator<TemplatesEffect>> effectCommunicatorProvider;

  private final Provider<CoroutineManager> coroutineManagerProvider;

  private TemplatesComposeStore_Factory(
      Provider<TemplatesWorkProcessor> templatesWorkProcessorProvider,
      Provider<StateCommunicator<TemplatesState>> stateCommunicatorProvider,
      Provider<EffectCommunicator<TemplatesEffect>> effectCommunicatorProvider,
      Provider<CoroutineManager> coroutineManagerProvider) {
    this.templatesWorkProcessorProvider = templatesWorkProcessorProvider;
    this.stateCommunicatorProvider = stateCommunicatorProvider;
    this.effectCommunicatorProvider = effectCommunicatorProvider;
    this.coroutineManagerProvider = coroutineManagerProvider;
  }

  @Override
  public TemplatesComposeStore get() {
    return newInstance(templatesWorkProcessorProvider.get(), stateCommunicatorProvider.get(), effectCommunicatorProvider.get(), coroutineManagerProvider.get());
  }

  public static TemplatesComposeStore_Factory create(
      Provider<TemplatesWorkProcessor> templatesWorkProcessorProvider,
      Provider<StateCommunicator<TemplatesState>> stateCommunicatorProvider,
      Provider<EffectCommunicator<TemplatesEffect>> effectCommunicatorProvider,
      Provider<CoroutineManager> coroutineManagerProvider) {
    return new TemplatesComposeStore_Factory(templatesWorkProcessorProvider, stateCommunicatorProvider, effectCommunicatorProvider, coroutineManagerProvider);
  }

  public static TemplatesComposeStore newInstance(TemplatesWorkProcessor templatesWorkProcessor,
      StateCommunicator<TemplatesState> stateCommunicator,
      EffectCommunicator<TemplatesEffect> effectCommunicator, CoroutineManager coroutineManager) {
    return new TemplatesComposeStore(templatesWorkProcessor, stateCommunicator, effectCommunicator, coroutineManager);
  }
}
