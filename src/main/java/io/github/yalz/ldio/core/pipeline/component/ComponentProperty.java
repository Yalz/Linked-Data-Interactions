package io.github.yalz.ldio.core.pipeline.component;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ComponentProperty {
    String key();
    String defaultValue();
    boolean required() default false;
    Class expectedType() default String.class;
}
