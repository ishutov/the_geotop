package com.geotop.geotopproject.loader.vk.impl;

import com.geotop.geotopproject.loader.helper.CollisionResolver;
import com.geotop.geotopproject.loader.vk.VKLoader;
import com.geotop.geotopproject.model.places.APISpecificData;
import com.geotop.geotopproject.model.places.Checkin;
import com.geotop.geotopproject.model.places.Place;
import com.geotop.geotopproject.model.places.api.VkData;
import com.geotop.geotopproject.model.places.deserializer.PlaceDeserializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Type;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class VKLoaderImpl implements VKLoader {

    private static final Logger LOG = LoggerFactory.getLogger(VKLoaderImpl.class);

    private CollisionResolver collisionResolver;
    private PlaceDeserializer placeDeserializer;

    @Autowired
    public VKLoaderImpl(CollisionResolver collisionResolver, PlaceDeserializer placeDeserializerVK) {
        this.collisionResolver = collisionResolver;
        this.placeDeserializer = placeDeserializerVK;
    }

    @Override
    public List<Place> loadData() throws Exception {
        List<Place> places = loadPlaces();
        places = collisionResolver.resolvePlaceCollision(places);

        LOG.info("vk loading done");
        return places;
    }

    @Override
    public List<Place> loadSpecificUserData(String userId) throws Exception {
        List<Checkin> userCheckins = callCheckinsAPI(userId, API_USER_CHECKINS_URL);
        List<Place> userPlaces = new ArrayList<>();
        for (Checkin checkin : userCheckins) {
            String placeId = checkin.getPlace_id();
            if (!StringUtils.isEmpty(placeId)) {
                if (!placeId.equals("0")) {
                    userPlaces.add(callPlaceAPI(placeId));
                }
            }
        }
        Map<String, List<Checkin>> checkinsByPlace = userCheckins.stream().collect(Collectors.groupingBy(Checkin::getPlace_id));
        for (Map.Entry<String, List<Checkin>> entry : checkinsByPlace.entrySet()) {
            for (Place place : userPlaces) {
                VkData vkData = (VkData) place.getVkData();
                if (vkData.getId().equals(entry.getKey())) {
                    vkData.setCheckins(entry.getValue());
                }
            }
        }
        userPlaces = collisionResolver.resolvePlaceCollision(userPlaces);

        return userPlaces;
    }

    public List<Place> loadPlaces() throws Exception {
        List<Place> places = new ArrayList<>();
        places = callPlaceAPI(places, 3, 0, PLACE_COUNT, PLACE_MAX_OFFSET);

        // delete duplicates produced by merging center and corners areas
        places = collisionResolver.deleteDuplicatePlaces(places, new VkData());

        for (Place place : places) {
            APISpecificData vkData = place.getVkData();
            List<Checkin> checkins = callCheckinsAPI(vkData.getId(), API_CHECKINS_URL);
            if (!CollectionUtils.isEmpty(checkins)) {
                LOG.info("checkins = {}", checkins);
            }
            vkData.setCheckins(checkins);
            place.setVkData(vkData);
        }

        LOG.info("places loaded");
        return places;
    }

    public List<Checkin> callCheckinsAPI(String id, String apiBase) throws Exception {
        String url = String.format(apiBase, id, "100", ACCESS_TOKEN);
        String json = IOUtils.toString(new URL(url).openStream())
                .replaceAll("\\{\"response\":","")
                .replaceAll("\\{\"count\".*\"items\":","")
                .replaceAll("\\}\\}$","")
                .replaceAll("\\.000000", "");
        //LOG.info("Checkins loaded for " + placeId);
        Thread.sleep(500);
        LOG.info(json);
        Type listType = new TypeToken<ArrayList<Checkin>>(){}.getType();
        try {
            return new Gson().fromJson(json, listType);
        } catch (Exception e){
            LOG.error("place " + id + ": " + e.getMessage());
            return null;
        }
    }

    public List<Place> callPlaceAPI(List<Place> places, int radius, int offset, int count, int maxOffset) throws Exception {
        String url = String.format(API_PLACES_URL, LATTITUDE, LONGTITUDE, radius, offset, count, ACCESS_TOKEN);
        String json = IOUtils.toString(new URL(url).openStream())
                .replaceAll("\\{\"response\":","")
                .replaceAll("\\{\"count\".*\"items\":","")
                .replaceAll("\\]\\}\\}$",",")
                .replaceAll("\\.000000", "");
        Thread.sleep(500);
        if(json.endsWith(",")) {
            json = json.substring(0, json.length() - 1) + "]";
        }
        LOG.info(json);

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Place.class, placeDeserializer);
        Gson gson = gsonBuilder.create();
        Type listType = new TypeToken<ArrayList<Place>>(){}.getType();
        List<Place> placeList = gson.fromJson(json, listType);
        places.addAll(placeList);
        if (offset == maxOffset) {
            LOG.info("exit from rec");
            return places;
        }
        LOG.info("rec");
        return callPlaceAPI(places, radius, offset + count, count, maxOffset);
    }

    public Place callPlaceAPI(String placeId) throws Exception {
        String url = String.format(API_PLACE_ID_URL, placeId, ACCESS_TOKEN);
        String json = IOUtils.toString(new URL(url).openStream())
                .replaceAll("\\{\"response\":(.*)}","$1")
                .replaceAll("\\[(.*)]", "$1")
                .replaceAll("\\.000000", "");
        Thread.sleep(300);
        LOG.info(json);
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Place.class, placeDeserializer);
        Gson gson = gsonBuilder.create();

        return gson.fromJson(json, Place.class);
    }
}
