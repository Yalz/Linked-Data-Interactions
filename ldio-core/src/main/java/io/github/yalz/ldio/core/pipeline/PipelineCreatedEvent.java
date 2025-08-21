package io.github.yalz.ldio.core.pipeline;

import io.github.yalz.ldio.core.pipeline.component.input.EtlInput;

public record PipelineCreatedEvent (String pipelineId, EtlInput input) {
}
