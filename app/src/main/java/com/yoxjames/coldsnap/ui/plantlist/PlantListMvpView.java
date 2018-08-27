package com.yoxjames.coldsnap.ui.plantlist;

import com.yoxjames.coldsnap.service.location.SimpleWeatherLocation;
import com.yoxjames.coldsnap.ui.MvpView;
import com.yoxjames.coldsnap.ui.weatherpreviewbar.WeatherBarViewModel;

import java.util.UUID;

import io.reactivex.Observable;

public interface PlantListMvpView extends MvpView
{
    Observable<Object> retryConnection();
    Observable<UUID> plantItemClicks();
    Observable<SimpleWeatherLocation> locationChange();
    Observable<Object> newPlantRequests();
    Observable<Object> locationClicks();
    Observable<Object> settingsRequests();
    Observable<Object> aboutRequests();

    void openSettings();
    void openAbout();
    void bindView(PlantListViewModel vm);
    void bindView(WeatherBarViewModel vm);
    void openPlant(UUID plantUUID);
    void requestLocation();
    void newPlant();
}
