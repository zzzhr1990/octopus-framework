package org.octopus.entity.model;

import java.lang.annotation.*;

/**
 * From Fangzhuyuan's framework
 * Created by zzzhr on 2015-10-22.
 */
@Inherited
@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Condition {

    String property() default "";

    QuerySymbol symbol() default QuerySymbol.EQ;
}