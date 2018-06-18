package com.geotop.geotopproject.loader;

import com.geotop.geotopproject.loader.vk.VKLoader;
import com.geotop.geotopproject.model.places.Place;
import com.geotop.geotopproject.model.places.PlaceCollection;
import com.geotop.geotopproject.service.LoaderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LoaderJob {

    private static final Logger LOG = LoggerFactory.getLogger(LoaderJob.class);

    private LoaderService loaderService;
    private VKLoader vkLoader;

    @Autowired
    public LoaderJob(LoaderService loaderService, VKLoader vkLoader) {
        this.loaderService = loaderService;
        this.vkLoader = vkLoader;
    }

    // will be replaced with cron
    @Scheduled(fixedRate = 500000000)
    public void updateDatabase() {
        try {
            initPlacesTypes(loaderService);
            //loadAndSaveData(loaderService);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private void loadAndSaveData(LoaderService loaderService) throws Exception{
        List<PlaceCollection> placeCollectionList;
        List<Place> vkPlaces = loaderService.load(vkLoader, "35037762");

        placeCollectionList = loaderService.categorizeAndIndexPlaces(vkPlaces);

        //save to db
        loaderService.save(placeCollectionList);
        LOG.info("data was saved to db");
    }

    private void initPlacesTypes(LoaderService loaderService) {
        loaderService.initPlacesTypes();
    }

}
