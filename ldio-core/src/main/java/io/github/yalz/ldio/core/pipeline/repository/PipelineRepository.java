package io.github.yalz.ldio.core.pipeline.repository;

import io.github.yalz.ldio.core.pipeline.config.PipelineConfig;

import java.io.IOException;
import java.util.Map;

public interface PipelineRepository {
    boolean hasKey(String name);
    PipelineConfig findOne(String name) throws IOException;
    Map<String, PipelineConfig> findAll();
    void save(PipelineConfig pipelineConfig) throws IOException;
    void delete(String name);
}
