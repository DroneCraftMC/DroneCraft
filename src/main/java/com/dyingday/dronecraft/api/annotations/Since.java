/* (C)2025 */
package com.dyingday.dronecraft.api.annotations;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.CONSTRUCTOR})
public @interface Since {
  /** Version the API was introduced in. */
  String value();
}
