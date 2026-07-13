package ru.aleshin.features.templates.impl.presentation.ui.templates.store;

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
public final class TemplatesComposeStore_Factory_Factory implements Factory<TemplatesComposeStore.Factory> {
  private final Provider<TemplatesWorkProcessor> templatesWorkProcessorProvider;

  private final Provider<CoroutineManager> coroutineManagerProvider;

  private TemplatesComposeStore_Factory_Factory(
      Provider<TemplatesWorkProcessor> templatesWorkProcessorProvider,
      Provider<CoroutineManager> coroutineManagerProvider) {
    this.templatesWorkProcessorProvider = templatesWorkProcessorProvider;
    this.coroutineManagerProvider = coroutineManagerProvider;
  }

  @Override
  public TemplatesComposeStore.Factory get() {
    return newInstance(templatesWorkProcessorProvider.get(), coroutineManagerProvider.get());
  }

  public static TemplatesComposeStore_Factory_Factory create(
      Provider<TemplatesWorkProcessor> templatesWorkProcessorProvider,
      Provider<CoroutineManager> coroutineManagerProvider) {
    return new TemplatesComposeStore_Factory_Factory(templatesWorkProcessorProvider, coroutineManagerProvider);
  }

  public static TemplatesComposeStore.Factory newInstance(
      TemplatesWorkProcessor templatesWorkProcessor, CoroutineManager coroutineManager) {
    return new TemplatesComposeStore.Factory(templatesWorkProcessor, coroutineManager);
  }
}
