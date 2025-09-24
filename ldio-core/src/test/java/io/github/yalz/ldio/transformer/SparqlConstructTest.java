package io.github.yalz.ldio.transformer;

import io.github.yalz.ldio.core.pipeline.config.EtlComponentConfig;
import org.apache.jena.query.Dataset;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class SparqlConstructTest {
    SparqlConstruct sparqlConstruct;

    @BeforeEach
    void setUp() throws Exception {

    }

    @Test
    void validate_supportGraph() {
        EtlComponentConfig config = new EtlComponentConfig("Ldio:SparqlConstruct", Map.of(
                "sparql-query", """
                        PREFIX schema: <http://schema.org/>
                        PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
                        
                        CONSTRUCT {
                          GRAPH ?g {
                            ?s schema:fullJobTitle ?fullJobTitle .
                          }
                        }
                        WHERE {
                          GRAPH ?g {
                            ?s schema:jobTitle ?jobTitle .
                            ?s schema:name ?name .
                            BIND(CONCAT(?jobTitle, " ", ?name) AS ?fullJobTitle)
                          }
                        }
                        """
        ));

        sparqlConstruct = new SparqlConstruct("testPipeline", config);

        Dataset dataset = RDFParser.fromString("""
                _:b0 <http://schema.org/jobTitle> "Professor" <http://example.org/graph/janedoe> .
                _:b0 <http://schema.org/name> "Jane Doe" <http://example.org/graph/janedoe> .
                """, Lang.NQ).toDataset();

        var result = sparqlConstruct.transform(dataset);

        var expectedModel = RDFParser.fromString("""
                _:b0 <http://schema.org/fullJobTitle> "Professor Jane Doe" .
                """, Lang.NQ).toModel();

        assertTrue(result.containsNamedModel("http://example.org/graph/janedoe"));
        var janeDoeModel = result.getNamedModel("http://example.org/graph/janedoe");

        assertTrue(janeDoeModel.isIsomorphicWith(expectedModel));
    }

    @Test
    void validate_geosparql_functionalities() {
        EtlComponentConfig config = new EtlComponentConfig("Ldio:SparqlConstruct", Map.of(
                "sparql-query", """
                        PREFIX geo:  <http://www.opengis.net/ont/geosparql#>
                        PREFIX geof: <http://www.opengis.net/def/function/geosparql/>
                        PREFIX ex:   <http://example.org/>
                        
                        CONSTRUCT {
                          ?feature geo:hasGeometry ?geom .
                        }
                        WHERE {
                          ?feature geo:hasGeometry ?geom .
                          ?geom geo:asWKT ?wkt .
                          ex:AreaGeom geo:asWKT ?areaWKT .
                          FILTER(geof:sfWithin(?wkt, ?areaWKT))
                        }
                        
                        """
        ));

        sparqlConstruct = new SparqlConstruct("testPipeline", config);

        Dataset dataset = RDFParser.fromString("""
                 @prefix geo: <http://www.opengis.net/ont/geosparql#> .
                 @prefix ex: <http://example.org/> .
                
                 ex:PointA a geo:Feature ;
                     geo:hasGeometry ex:GeomA .
                
                 ex:GeomA a geo:Geometry ;
                     geo:asWKT "POINT(4.4025 51.2194)"^^geo:wktLiteral .
                
                 ex:Area a geo:Feature ;
                     geo:hasGeometry ex:AreaGeom .
                
                 ex:AreaGeom a geo:Geometry ;
                     geo:asWKT "POLYGON((4.40 51.22, 4.40 51.21, 4.42 51.21, 4.42 51.22, 4.40 51.22))"^^geo:wktLiteral .
                
                """, Lang.TURTLE).toDataset();

        var result = sparqlConstruct.transform(dataset);

        var expected = RDFParser.fromString("""
                @prefix geo: <http://www.opengis.net/ont/geosparql#> .
                @prefix ex: <http://example.org/> .
                
                ex:PointA geo:hasGeometry ex:GeomA .
                ex:Area  geo:hasGeometry  ex:AreaGeom .
                """, Lang.TURTLE).toModel();

        assertTrue(result.getDefaultModel().isIsomorphicWith(expected));
    }
}
