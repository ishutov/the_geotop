package com.geotop.geotopproject.loader.sparql;

import com.geotop.geotopproject.model.places.Place;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.apache.jena.query.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SparqlLoader implements SparqlInterface {
    private static final Logger LOG = LoggerFactory.getLogger(SparqlLoader.class);

    public List<Place> getSuggestedPlaces(String categoryId, String lat, String lon) {
        LOG.info("SPARQL Loader: categoryId={}, latitude={}, longitude={}", categoryId, lat, lon);

        return executeQuery(QUERY_LGDO, categoryId, lat, lon);
    }

    public List<Place> getRelatedPlaces(String categoryId, String lat, String lon) {
        LOG.info("SPARQL Loader: categoryId={}, latitude={}, longitude={}", categoryId, lat, lon);

        return executeQuery(QUERY_RELATED_LGDO, categoryId, lat, lon);
    }


    private List<Place> executeQuery(String strQuery, String categoryId, String lat, String lon) {
        String result = buildQuery(categoryId, lat, lon, strQuery);
        LOG.info(result);

        Query query = QueryFactory.create(result);
        QueryExecution qExe = QueryExecutionFactory.sparqlService(ENDPOINT_URI, query);
        ResultSet results = qExe.execSelect();

        List<Place> suggestedPlaces = new ArrayList<>();
        int i = 0;
        while (results.hasNext()) {
            QuerySolution qs = results.next();
            String address = StringUtils.join(Arrays.asList(qs.getLiteral("street").getLexicalForm(), qs.getLiteral("hsn").getLexicalForm()), ", ");
            String name = qs.getLiteral("l").getLexicalForm();
            String lats = qs.getLiteral("lat").getLexicalForm();
            String longs = qs.getLiteral("lon").getLexicalForm();
            Place place = new Place("osm" + i++, name, Double.valueOf(lats), Double.valueOf(longs), Integer.valueOf(categoryId), address);
            LOG.info(place.toString());

            suggestedPlaces.add(place);
        }

        return suggestedPlaces;
    }

    private String buildQuery(String categoryId, String lat, String lon, String query) {
        String[] categories = MP_TYPE_MAPPING.get(categoryId);
        String[] sourceCategories = MP_RELATED_TYPE_MAPPING.get(categoryId);
        Map<String, String> values = new HashMap<>();
        if (query.equals(QUERY_RELATED_LGDO)) {
            values.put("scat1", sourceCategories[0]);
            values.put("scat2", sourceCategories[1]);
        }
        values.put("cat1", categories[0]);
        values.put("cat2", categories[1]);
        values.put("lon", lon);
        values.put("lat", lat);
        values.put("radius", "3");
        StrSubstitutor sub = new StrSubstitutor(values, "%(", ")");
        return sub.replace(query);
    }
}
