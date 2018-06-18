package com.geotop.geotopproject.controller.model;

import com.geotop.geotopproject.model.places.CategoryPopularity;
import com.geotop.geotopproject.model.places.Place;
import com.geotop.geotopproject.service.PlaceService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/rest")
public class PlaceController {

    private static Logger LOG = Logger.getLogger(PlaceController.class.getName());

    private PlaceService placeService;

    @Autowired
    public PlaceController(PlaceService placeService) {
        this.placeService = placeService;
    }

    @RequestMapping(value = "/places", method = RequestMethod.GET)
    public List<Place> getPlacesByType(@RequestParam(value = "type") String type) {
        return placeService.getPlacesByType(type);
    }

    @RequestMapping(value = "/top_places", method = RequestMethod.GET)
    public List<Place> getTopPlacesByType(@RequestParam(value = "type") String type) {
        return placeService.getTopPlacesByType(type);
    }

    @RequestMapping(value = "/place", method = RequestMethod.GET)
    public Place getPlaceById(@RequestParam(value = "id") String id) {
        return placeService.getPlaceById(id);
    }


    @RequestMapping(value = "/user", method = RequestMethod.POST)
    public void setPlacesByUser(@RequestParam(value = "id") String id) {
        placeService.loadPlacesForUser(id);
    }

    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public List<Place> getPlacesByUser(@RequestParam(value = "id") String id) {
        return placeService.getPlacesByUser(id);
    }

    @RequestMapping(value = "/user/topCategory", method = RequestMethod.GET)
    public List<CategoryPopularity> getTopCategoriesByUser(@RequestParam(value = "id") String id) {
        return placeService.getMostPopularCategoriesForUser(id);
    }

    @RequestMapping(value = "/user/suggested", method = RequestMethod.GET)
    public List<Place> findSuggestedPlacesForUser(@RequestParam(value = "cat") String category, @RequestParam(value = "lat") String lat,
                                                  @RequestParam(value = "lon") String lon) {
        return placeService.getSuggestedPlaces(category, lat, lon);
    }

    @RequestMapping(value = "/user/related", method = RequestMethod.GET)
    public List<Place> findRelatedPlacesForUser(@RequestParam(value = "cat") String category, @RequestParam(value = "lat") String lat,
                                                  @RequestParam(value = "lon") String lon) {
        return placeService.getRelatedPlaces(category, lat, lon);
    }

}
