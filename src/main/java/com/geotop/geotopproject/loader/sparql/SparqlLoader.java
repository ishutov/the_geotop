package com.geotop.geotopproject.loader.sparql;

import org.apache.jena.query.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SparqlLoader {
    private static final Logger LOG = LoggerFactory.getLogger(SparqlLoader.class);

    public static void getResult(String categoryId, String lat, String lon) {
        LOG.info("SPARQL Loader: categoryId={}, latitude={}, longitude={}", categoryId, lat, lon);

        String s2 = "Prefix lgdo:<http://linkedgeodata.org/ontology/>\n" +
                "Prefix geom:<http://geovocab.org/geometry#>\n" +
                "Prefix ogc: <http://www.opengis.net/ont/geosparql#>\n" +
                "Prefix coord: <http://www.w3.org/2003/01/geo/wgs84_pos#>\n" +
                "SELECT * WHERE {\n" +
                "    ?node a lgdo:CoffeeShop ; coord:lat ?lat; coord:long ?longs\n" +
                "\n" +
                "}\n" +
                "LIMIT 10";

        Query query = QueryFactory.create(s2);
        QueryExecution qExe = QueryExecutionFactory.sparqlService("http://linkedgeodata.org/sparql/", query);
        ResultSet results = qExe.execSelect();
        ResultSetFormatter.out(System.out, results);
        // Pretty output
        //ResultSetFormatter.out(System.out, results, query);

        // Suitable for implementation logic
        while (results.hasNext()) {
            QuerySolution qs = results.next();
            String lats = qs.getLiteral("lat").getLexicalForm();
            String longs = qs.getLiteral("longs").getLexicalForm();

            LOG.info("latitude: " + lats + " longitude: " + longs);
        }
    }

}
