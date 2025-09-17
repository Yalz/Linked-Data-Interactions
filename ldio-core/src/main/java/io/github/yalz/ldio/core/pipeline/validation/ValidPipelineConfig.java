package io.github.yalz.ldio.core.pipeline.validation;

import jakarta.validation.Constraint;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PipelineConfigValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPipelineConfig {
    String message() default "Invalid pipeline configuration";
}
