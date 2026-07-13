package ru.aleshin.features.templates.impl.domain.common;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
public final class HomeErrorHandler_Base_Factory implements Factory<HomeErrorHandler.Base> {
  @Override
  public HomeErrorHandler.Base get() {
    return newInstance();
  }

  public static HomeErrorHandler_Base_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static HomeErrorHandler.Base newInstance() {
    return new HomeErrorHandler.Base();
  }

  private static final class InstanceHolder {
    static final HomeErrorHandler_Base_Factory INSTANCE = new HomeErrorHandler_Base_Factory();
  }
}
