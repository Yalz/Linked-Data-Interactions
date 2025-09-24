package io.github.yalz.ldio.transformer;

import io.github.yalz.ldio.core.pipeline.component.ComponentName;
import io.github.yalz.ldio.core.pipeline.component.ComponentProperty;
import io.github.yalz.ldio.core.pipeline.component.transformer.EtlTransformer;
import io.github.yalz.ldio.core.pipeline.config.EtlComponentConfig;
import org.apache.jena.query.*;

@ComponentName(value = "Ldio:SparqlTransform", type = ComponentName.ComponentType.TRANSFORMER,
        description = "Allow an incoming model to be adjusted or transformed by using SPARQL Construct query.")
public class SparqlTransformer extends EtlTransformer {

    @ComponentProperty(key = "sparql-query", required = true)
    String queryString;

    public SparqlTransformer(String pipelineName, EtlComponentConfig config) {
        super(pipelineName, config);
    }

    @Override
    public Dataset transform(Dataset data) {
        Query query = QueryFactory.create(queryString, Syntax.syntaxARQ);

        try (QueryExecution x = QueryExecution.create(query, data)) {
            return x.execConstructDataset();
        }
    }
}
