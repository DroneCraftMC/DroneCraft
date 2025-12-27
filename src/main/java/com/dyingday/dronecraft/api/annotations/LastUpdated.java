/* (C)2025 */
package com.dyingday.dronecraft.api.annotations;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.CONSTRUCTOR})
public @interface LastUpdated {
  /** Version the API was last updated in. */
  String value();
}
