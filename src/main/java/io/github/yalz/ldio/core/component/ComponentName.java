package io.github.yalz.ldio.core.component;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ComponentName {
    String value();
    ComponentType type();

    enum ComponentType {
        INPUT, ADAPTER, TRANSFORMER, OUTPUT
    }
}


