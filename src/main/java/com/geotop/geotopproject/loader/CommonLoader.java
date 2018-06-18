package com.geotop.geotopproject.loader;


import com.geotop.geotopproject.model.places.Place;

import java.util.List;

public interface CommonLoader {
    List<Place> loadData() throws Exception;
    List<Place> loadSpecificUserData(String userId) throws Exception ;
}
