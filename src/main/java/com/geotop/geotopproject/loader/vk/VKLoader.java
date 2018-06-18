package com.geotop.geotopproject.loader.vk;

import com.geotop.geotopproject.loader.CommonLoader;

public interface VKLoader extends CommonLoader {
    String ACCESS_TOKEN = "cc08c95d25e5eb527ebac704fa1cf4f6fecd09c5606eafb6c91e955aff473bbacff4bdcf51a93b03e3216";
    String LATTITUDE = "59.935634";
    String LONGTITUDE = "30.325935";
    int PLACE_COUNT = 1000;
    int PLACE_MAX_OFFSET = 0;


    String API_BASE = "https://api.vk.com/method";
    String API_PLACES_URL = API_BASE +
            "/places.search" +
            "?latitude=%s" +
            "&longitude=%s" +
            "&radius=%s" +
            "&offset=%d" +
            "&count=%d" +
            "&access_token=%s" +
            "&v=5.74";
    String API_PLACE_ID_URL = API_BASE +
            "/places.getById?" +
            "&places=%s" +
            "&access_token=%s" +
            "&v=5.74";

    String API_CHECKINS_URL = API_BASE +
            "/places.getCheckins?" +
            "&place=%s" +
            "&count=%s" +
            "&access_token=%s" +
            "&v=5.74";
    String API_USER_CHECKINS_URL = API_BASE +
            "/places.getCheckins?" +
            "&user_id=%s" +
            "&count=%s" +
            "&access_token=%s" +
            "&v=5.74";
}
