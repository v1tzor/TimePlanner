package ru.aleshin.features.templates.impl.navigation;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import ru.aleshin.features.templates.impl.presentation.ui.templates.store.TemplatesComposeStore;

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
public final class DefaultTemplatesContentProviderFactory_Factory implements Factory<DefaultTemplatesContentProviderFactory> {
  private final Provider<TemplatesComposeStore.Factory> templatesStoreFactoryProvider;

  private DefaultTemplatesContentProviderFactory_Factory(
      Provider<TemplatesComposeStore.Factory> templatesStoreFactoryProvider) {
    this.templatesStoreFactoryProvider = templatesStoreFactoryProvider;
  }

  @Override
  public DefaultTemplatesContentProviderFactory get() {
    return newInstance(templatesStoreFactoryProvider.get());
  }

  public static DefaultTemplatesContentProviderFactory_Factory create(
      Provider<TemplatesComposeStore.Factory> templatesStoreFactoryProvider) {
    return new DefaultTemplatesContentProviderFactory_Factory(templatesStoreFactoryProvider);
  }

  public static DefaultTemplatesContentProviderFactory newInstance(
      TemplatesComposeStore.Factory templatesStoreFactory) {
    return new DefaultTemplatesContentProviderFactory(templatesStoreFactory);
  }
}
