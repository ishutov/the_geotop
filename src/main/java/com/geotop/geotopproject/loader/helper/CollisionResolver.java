package com.geotop.geotopproject.loader.helper;

import com.geotop.geotopproject.model.places.APISpecificData;
import com.geotop.geotopproject.model.places.Place;
import com.geotop.geotopproject.model.places.api.VkData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import static java.lang.Math.*;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;

@Component
public class CollisionResolver {

    private static final Logger LOG = LoggerFactory.getLogger(CollisionResolver.class);

    /**
     * Default percentage to ensure that names of the places are the same.
     */
    private static final double DEFAULT_NAME = 0.5;

    /**
     * Default distance to ensure that places are the same.
     */
    private static final double DEFAULT_DISTANCE = 0.1;

    /**
     * Default count of neighbours in the same coordinates
     */
    private static final int NEIGHBOURS_COUNT = 3;

    /**
     * Coefficient for coordinates transformation.
     */
    private static final double MAGIC_CONST = 1.852;

    /**
     * CityDictionary contains a set of words need to be ignored during title comparison
     */
    private CityDictionary cityDictionary;
    private PlaceMerger placeMerger;

    @Autowired
    public CollisionResolver(CityDictionary cityDictionary, PlaceMerger placeMerger) {
        this.cityDictionary = cityDictionary;
        this.placeMerger = placeMerger;
    }

    public List<Place> resolvePlaceCollision(List<Place> places) {
        return collapsePlaces(places);
    }

    public List<Place> deleteDuplicatePlaces(List<Place> places, APISpecificData data) throws Exception {
        if (places == null) {
            return null;
        }
        if (data instanceof VkData) {
            return places.stream()
                    .collect(collectingAndThen(toCollection(() -> new TreeSet<>(Comparator.comparing(f -> f.getVkData().getId()))),
                            ArrayList::new));
        }
        return places;
    }

    private List<Place> collapsePlaces(List<Place> places) {
        ArrayList<Integer> group = new ArrayList<>(places.size());
        int last = 1;

        for (int i = 0; i < places.size(); i++) {
            boolean collapsed = false;
            Place place = places.get(i);
            String newTitle = cityDictionary.removeCityWords(place.getTitle());
            for (int j = 0; j < i; j++) {
                Place mPlace = places.get(j);
                String mNewTitle = cityDictionary.removeCityWords(mPlace.getTitle());

                float knear = getKNear(place, mPlace);
                boolean near = isNear(knear, DEFAULT_DISTANCE);

                double ksim = getKSimilarity(newTitle, mNewTitle);
                boolean same = isSame(ksim, DEFAULT_NAME);

                if (near && same) {
                    group.add(group.get(j));
                    collapsed = true;
                    break;
                }
            }
            if (!collapsed) {
                group.add(last++);
            }
        }

        return getCollapsedPlaces(places, group, last);
    }

    private List<Place> getCollapsedPlaces(List<Place> places, List<Integer> groups, int lastIdx) {
        List<Place> collapsedPlaces = new ArrayList<>();

        for (int i = 1; i < lastIdx; i++) {
            boolean first = true;
            Place place = new Place();

            for (int j = 0; j < groups.size(); j++) {
                Place mPlace = places.get(j);
                if (groups.get(j) == i) {
                    if (!first) {
                        // merge places
                        place = placeMerger.mergePlaces(place, mPlace);

                        LOG.info(String.format("Place %s collapsed with %s", mPlace.getTitle(), place.getTitle()));
                    } else {
                        place = mPlace;
                        first = false;
                    }
                }
            }

            collapsedPlaces.add(place);
        }

        return collapsedPlaces;
    }

    private float getKNear(Place mark1, Place mark2) {
        double lat1 = (PI / 180) * mark1.getLatitude();
        double lat2 = (PI / 180) * mark2.getLatitude();
        double lon1 = (PI / 180) * mark1.getLongitude();
        double lon2 = (PI / 180) * mark2.getLongitude();

        double tmp = acos(sin(lat1) * sin(lat2) + cos(lat1) * cos(lat2) * cos(lon1 - lon2));

        return (float) (tmp * (181 / PI) * 60 * MAGIC_CONST);
    }

    private boolean isNear(double knear, double pctg) {
        return knear <= pctg;
    }

    private boolean isSame(double ksim, double pctg) {
        return ksim > pctg;
    }

    private double getKSimilarity(String place1, String place2) {
       return compareTitles(place1, place2);
    }

    private double compareTitles(String title1, String title2) {
        StringPairComparator comparator = new StringPairComparator();
        TransliterationHelper transliterationHelper = new TransliterationHelper();
        title1 = transliterationHelper.cyr2lat(title1);
        title2 = transliterationHelper.cyr2lat(title2);

        if (StringUtils.isEmpty(title1.replaceAll("\\s+", "")) || StringUtils.isEmpty(title2.replaceAll("\\s+", ""))) {
            return 0;
        }
        return comparator.compareStrings(title1, title2);
    }

}
