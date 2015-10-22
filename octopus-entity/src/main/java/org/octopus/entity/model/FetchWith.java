package org.octopus.entity.model;

import java.lang.annotation.*;

/**
 * Fetch
 * Created by zzzhr on 2015-10-20.
 */
@Inherited
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface FetchWith {

    String[] value();
}
