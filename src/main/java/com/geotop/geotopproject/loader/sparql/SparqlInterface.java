package com.geotop.geotopproject.loader.sparql;

import java.util.HashMap;

public interface SparqlInterface {

    String ENDPOINT_URI = "http://linkedgeodata.org/sparql/";

    String QUERY_LGDO =
            "Prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
            "Prefix ogc: <http://www.opengis.net/ont/geosparql#>\n" +
            "Prefix geom: <http://geovocab.org/geometry#>\n" +
            "Prefix coord: <http://www.w3.org/2003/01/geo/wgs84_pos#>\n" +
            "Prefix lgdo: <http://linkedgeodata.org/ontology/>\n" +
            "Prefix lgdm: <http://linkedgeodata.org/meta/>\n" +
            "Prefix lgda: <http://linkedgeodata.org/ontology/addr%3A>\n" +
            "Prefix bif: <bif:>\n"+
            "\n" +
            "Select ?street ?hsn ?l ?lat ?lon ?g\n" +
            "From <http://linkedgeodata.org> {\n" +
            "      {?s a lgdo:%(cat1) .}\n" +
            "      union\n" +
            "      {?s a lgdo:%(cat2) .}\n" +
            "      ?s\n" +
            "        a lgdm:Node ;\n" +
            "        lgda:street ?street ;\n" +
            "        lgda:housenumber ?hsn ;\n" +
            "        rdfs:label ?l ;\n" +
            "        coord:lat ?lat ;\n" +
            "        coord:long ?lon ;\n" +
            "        geom:geometry [\n" +
            "          ogc:asWKT ?g\n" +
            "        ] .\n" +
            "    Filter(bif:st_intersects (?g, bif:st_point (%(lon), %(lat)), %(radius))) .\n" +
            "}";

    String QUERY_RELATED_LGDO =
            "Prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
            "Prefix ogc: <http://www.opengis.net/ont/geosparql#>\n" +
            "Prefix geom: <http://geovocab.org/geometry#>\n" +
            "Prefix coord: <http://www.w3.org/2003/01/geo/wgs84_pos#>\n" +
            "Prefix lgdo: <http://linkedgeodata.org/ontology/>\n" +
            "Prefix lgdm: <http://linkedgeodata.org/meta/>\n" +
            "Prefix lgda: <http://linkedgeodata.org/ontology/addr%3A>\n" +
            "\n" +
            "\n" +
            "Select ?street2 ?hsn2 ?l2 ?lat2 ?lon2 ?geo2\n" +
            "From <http://linkedgeodata.org> {\n" +
            "    {?s a lgdo:%(scat1) .}\n" +
            "    union\n" +
            "    {?s a lgdo:%(scat2) .}\n" +
            "    ?s\n" +
            "      a lgdm:Node ;\n" +
            "      coord:lat ?lat ;\n" +
            "      coord:long ?lon ;\n" +
            "      geom:geometry [\n" +
            "        ogc:asWKT ?geo1\n" +
            "      ] .\n" +
            "\n" +
            "    {?x a lgdo:%(cat1) .}\n" +
            "    union\n" +
            "    {?x a lgdo:%(cat2) .}\n" +
            "    ?x\n" +
            "      a lgdm:Node ;\n" +
            "      lgda:street ?street2 ;\n" +
            "      lgda:housenumber ?hsn2 ;\n" +
            "      rdfs:label ?l2 ;\n" +
            "      coord:lat ?lat2 ;\n" +
            "      coord:long ?lon2 ;\n" +
            "      geom:geometry [\n" +
            "        ogc:asWKT ?geo2\n" +
            "      ] .\n" +
            "\n" +
            "  Filter (\n" +
            "    bif:st_intersects (?geo1, bif:st_point (%(lon), %(lat)), %(radius)) &&\n" +
            "    bif:st_intersects (?geo1, ?geo2, 1)\n" +
            "  ) .\n" +
            "}\n" +
            "LIMIT 25";

    HashMap<String, String[]> MP_TYPE_MAPPING = new HashMap<String, String[]>() {
        {
            put("1", new String[]{"Cafe", "Restaurant"});
            put("2", new String[]{"Bar", "Club"});
            put("4", new String[]{"Club", "Nightclub"});
            put("8", new String[]{"HistoricMuseum", "Museum"});
            put("9", new String[]{"Theatre", "Cinema"});
        }
    };

    HashMap<String, String[]> MP_RELATED_TYPE_MAPPING = new HashMap<String, String[]>() {
        {
            put("1", new String[]{"Bar", "Club"});
            put("2", new String[]{"Club", "Nightclub"});
            put("4", new String[]{"Hostel", "Hotel"});
            put("8", new String[]{"CoffeeShop", "Cafe"});
            put("9", new String[]{"Restaurant", "Cafe"});
        }
    };

}
