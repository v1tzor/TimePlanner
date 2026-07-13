package ru.aleshin.features.templates.impl.domain.interactors;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import ru.aleshin.core.domain.repository.TemplatesRepository;
import ru.aleshin.features.templates.impl.domain.common.HomeEitherWrapper;

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
public final class TemplatesInteractor_Base_Factory implements Factory<TemplatesInteractor.Base> {
  private final Provider<TemplatesRepository> templatesRepositoryProvider;

  private final Provider<HomeEitherWrapper> eitherWrapperProvider;

  private TemplatesInteractor_Base_Factory(
      Provider<TemplatesRepository> templatesRepositoryProvider,
      Provider<HomeEitherWrapper> eitherWrapperProvider) {
    this.templatesRepositoryProvider = templatesRepositoryProvider;
    this.eitherWrapperProvider = eitherWrapperProvider;
  }

  @Override
  public TemplatesInteractor.Base get() {
    return newInstance(templatesRepositoryProvider.get(), eitherWrapperProvider.get());
  }

  public static TemplatesInteractor_Base_Factory create(
      Provider<TemplatesRepository> templatesRepositoryProvider,
      Provider<HomeEitherWrapper> eitherWrapperProvider) {
    return new TemplatesInteractor_Base_Factory(templatesRepositoryProvider, eitherWrapperProvider);
  }

  public static TemplatesInteractor.Base newInstance(TemplatesRepository templatesRepository,
      HomeEitherWrapper eitherWrapper) {
    return new TemplatesInteractor.Base(templatesRepository, eitherWrapper);
  }
}
