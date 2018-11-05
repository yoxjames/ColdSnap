package com.yoxjames.coldsnap.ui.plantlist;

import com.yoxjames.coldsnap.core.ActivityView;
import com.yoxjames.coldsnap.service.location.SimpleWeatherLocation;

import java.util.UUID;

import io.reactivex.Observable;

public interface PlantListMvpView extends ActivityView
{
    Observable<UUID> plantItemClicks();
    Observable<SimpleWeatherLocation> locationChange();
    Observable<Object> newPlantRequests();
    Observable<Object> locationClicks();

    void openAbout();
    void bindView(PlantListViewModel vm);
    void openPlant(UUID plantUUID);
    void requestLocation();
    void newPlant();
}
