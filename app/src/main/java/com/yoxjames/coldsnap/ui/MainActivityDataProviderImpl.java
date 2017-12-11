/*
 * Copyright (c) 2017 James Yox
 *
 * This file is part of ColdSnap.
 *
 * ColdSnap is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ColdSnap is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ColdSnap.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.yoxjames.coldsnap.ui;

import com.jakewharton.rx.ReplayingShare;
import com.yoxjames.coldsnap.model.Plant;
import com.yoxjames.coldsnap.model.WeatherData;
import com.yoxjames.coldsnap.service.location.SimpleWeatherLocation;
import com.yoxjames.coldsnap.service.location.WeatherLocationService;
import com.yoxjames.coldsnap.service.plant.PlantService;
import com.yoxjames.coldsnap.service.weather.WeatherService;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * Created by yoxjames on 10/24/17.
 */

public class MainActivityDataProviderImpl implements MainActivityDataProvider
{
    private final WeatherService weatherService;
    private final WeatherLocationService weatherLocationService;
    private final PlantService plantService;
    private final Subject<SimpleWeatherLocation> locationUpdates;
    private final Observable<WeatherData> weatherDataShared;
    private final Observable<List<Plant>> plantDataShared;

    @Inject
    public MainActivityDataProviderImpl(WeatherService weatherService, WeatherLocationService weatherLocationService, PlantService plantService)
    {
        this.weatherService = weatherService;
        this.weatherLocationService = weatherLocationService;
        this.plantService = plantService;
        this.locationUpdates = PublishSubject.create();

        weatherDataShared = weatherLocationService.getWeatherLocation()
                .mergeWith(locationUpdates.doOnNext(simpleWeatherLocation -> weatherLocationService.saveWeatherLocation(simpleWeatherLocation).blockingAwait()))
                .flatMap(weatherService::getWeatherData)
                .compose(ReplayingShare.instance());

        plantDataShared = plantService.getPlants()
                .toSortedList()
                .toObservable()
                .compose(ReplayingShare.instance());
    }

    @Override
    public Observable<WeatherData> getWeatherDataProvider()
    {
        return weatherDataShared;
    }

    @Override
    public Observable<List<Plant>> getPlantDataProvider()
    {
        return plantDataShared;
    }

    @Override
    public void pushLocation(SimpleWeatherLocation location)
    {
        locationUpdates.onNext(location);
    }
}
