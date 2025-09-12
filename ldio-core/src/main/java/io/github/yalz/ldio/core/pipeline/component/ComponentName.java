package io.github.yalz.ldio.core.pipeline.component;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ComponentName {
    String value();
    String description() default "";
    ComponentType type();

    enum ComponentType {
        INPUT, ADAPTER, TRANSFORMER, OUTPUT
    }
}


