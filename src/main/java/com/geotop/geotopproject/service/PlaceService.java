package com.geotop.geotopproject.service;

import com.geotop.geotopproject.dao.PlaceCollectionRepository;
import com.geotop.geotopproject.dao.PlaceRepository;
import com.geotop.geotopproject.exception.ExceptionMessageConstants;
import com.geotop.geotopproject.exception.LoaderException;
import com.geotop.geotopproject.exception.NotFoundException;
import com.geotop.geotopproject.loader.sparql.SparqlLoader;
import com.geotop.geotopproject.loader.vk.VKLoader;
import com.geotop.geotopproject.model.places.CategoryPopularity;
import com.geotop.geotopproject.model.places.Place;
import com.geotop.geotopproject.model.places.PlaceCollection;
import com.geotop.geotopproject.utils.Utils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PlaceService {
    private static Logger LOG = Logger.getLogger(PlaceService.class.getName());

    private PlaceRepository placeRepository;
    private PlaceCollectionRepository placeCollectionRepository;
    private LoaderService loaderService;
    private VKLoader vkLoader;
    private SparqlLoader sparqlLoader;

    @Autowired
    public PlaceService(PlaceRepository placeRepository, PlaceCollectionRepository placeCollectionRepository,
                        LoaderService loaderService, VKLoader vkLoader, SparqlLoader sparqlLoader) {
        this.placeRepository = placeRepository;
        this.placeCollectionRepository = placeCollectionRepository;
        this.loaderService = loaderService;
        this.vkLoader = vkLoader;
        this.sparqlLoader = sparqlLoader;
    }

    public List<Place> getTopPlacesByType(String type) throws NotFoundException {
        List<Place> places = getPlacesByType(type);

        return places.stream()
                .sorted((p1, p2) -> Double.compare((p2.getOverallPopularity() + p2.getOverallRating()),
                        (p1.getOverallPopularity() + p1.getOverallRating())))
                .collect(Collectors.toList());
    }

    public List<Place> getPlacesByType(String type) throws NotFoundException {
        List<PlaceCollection> placeCollections = placeCollectionRepository.findByTitle(type);
        if (placeCollections == null || placeCollections.isEmpty()) {
            throw new NotFoundException(String.format(ExceptionMessageConstants.NF_TYPE_NOT_FOUND, type));
        }
        List<Place> places;
        try {
            places = Utils.getSingle(placeCollections).getPlaces();
            if (places == null || places.isEmpty()) {
                throw new NotFoundException(String.format(ExceptionMessageConstants.NF_PLACES_NOT_FOUND, type));
            }
        } catch (Exception e) {
            throw new NotFoundException(e.getMessage(), e.getCause());
        }

        return places;
    }

    public Place getPlaceById(String id) throws NotFoundException {
        Place place;
        try {
            place = placeRepository.findById(id);
        } catch (NotFoundException e) {
            LOG.error(e.getMessage());
            throw new NotFoundException(String.format(ExceptionMessageConstants.NF_PLACE_NOT_FOUND, id), e.getCause());
        }

        return place;
    }

    public List<Place> getPlacesByUser(String userId) throws NotFoundException {
        List<Place> places = placeRepository.getPlacesForSpecificUser(userId);
        if (CollectionUtils.isEmpty(places)) {
            throw new NotFoundException(String.format(ExceptionMessageConstants.NF_PLACES_NOT_FOUND_USER, userId));
        }
        return places;
    }

    public List<CategoryPopularity> getMostPopularCategoriesForUser(String userId) throws NotFoundException {
        List<Place> places = getPlacesByUser(userId);
        Map<Integer, Long> map = getPlaceCategoryWithPopularity(places);

        return map.entrySet()
                .stream()
                .sorted(Map.Entry.<Integer, Long>comparingByValue().reversed())
                .filter(e -> !e.getKey().equals(11)) // type Other is not needed
                .map(e -> new CategoryPopularity(e.getKey(), e.getValue().intValue()))
                .limit(3)
                .collect(Collectors.toList());
    }

    public void loadPlacesForUser(String userId) throws LoaderException {
        try {
            //loaderService.initPlacesTypes();
            List<Place> places = loaderService.load(vkLoader, userId);
            List<PlaceCollection> collection = loaderService.categorizeAndIndexPlaces(places);

            loaderService.save(collection);
        } catch (Exception e) {
            throw new LoaderException(e.getMessage(), e);
        }
        LOG.info("places loaded for user=" + userId);
    }

    public List<Place> getSuggestedPlaces(String categoryId, String lat, String lon) throws NotFoundException {
        return sparqlLoader.getSuggestedPlaces(categoryId, lat, lon);
    }

    public List<Place> getRelatedPlaces(String categoryId, String lat, String lon) throws NotFoundException {
        return sparqlLoader.getRelatedPlaces(categoryId, lat, lon);
    }

    private Map<Integer, Long> getPlaceCategoryWithPopularity(List<Place> places) {
        return places.stream()
                .sorted(Comparator.comparing(Place::getType).reversed())
                .collect(Collectors.groupingBy(Place::getType, Collectors.counting()));
    }
}
