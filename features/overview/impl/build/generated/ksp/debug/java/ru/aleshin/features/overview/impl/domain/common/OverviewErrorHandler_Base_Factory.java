package ru.aleshin.features.overview.impl.domain.common;

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
public final class OverviewErrorHandler_Base_Factory implements Factory<OverviewErrorHandler.Base> {
  @Override
  public OverviewErrorHandler.Base get() {
    return newInstance();
  }

  public static OverviewErrorHandler_Base_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static OverviewErrorHandler.Base newInstance() {
    return new OverviewErrorHandler.Base();
  }

  private static final class InstanceHolder {
    static final OverviewErrorHandler_Base_Factory INSTANCE = new OverviewErrorHandler_Base_Factory();
  }
}
