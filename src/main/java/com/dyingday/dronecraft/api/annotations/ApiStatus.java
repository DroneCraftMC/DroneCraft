/* (C)2025 */
package com.dyingday.dronecraft.api.annotations;

import java.lang.annotation.*;

public final class ApiStatus {
  private ApiStatus() {}

  /** Stable, supported API. Safe for external mods to depend on. */
  @Documented
  @Retention(RetentionPolicy.CLASS)
  @Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.CONSTRUCTOR})
  public @interface Stable {}

  /** Experimental API. May change or be removed without notice */
  @Documented
  @Retention(RetentionPolicy.CLASS)
  @Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.CONSTRUCTOR})
  public @interface Experimental {}

  /** Internal implementation detail. Not intended for external use */
  @Documented
  @Retention(RetentionPolicy.CLASS)
  @Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.CONSTRUCTOR})
  public @interface Internal {}
}
