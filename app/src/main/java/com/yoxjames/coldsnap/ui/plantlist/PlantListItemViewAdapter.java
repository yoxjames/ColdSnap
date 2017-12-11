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

package com.yoxjames.coldsnap.ui.plantlist;

import com.yoxjames.coldsnap.model.Plant;
import com.yoxjames.coldsnap.model.PlantImage;
import com.yoxjames.coldsnap.model.Temperature;
import com.yoxjames.coldsnap.model.WeatherData;
import com.yoxjames.coldsnap.service.image.ImageService;
import com.yoxjames.coldsnap.ui.MainActivityDataProvider;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;

/**
 * Created by yoxjames on 12/3/17.
 */

public class PlantListItemViewAdapter
{
    private final MainActivityDataProvider dataProvider;
    private final ImageService imageService;
    private Observable<List<Plant>> cachedPlants;

    public PlantListItemViewAdapter(final MainActivityDataProvider dataProvider, final ImageService imageService)
    {
        this.dataProvider = dataProvider;
        this.imageService = imageService;
        cachedPlants = dataProvider.getPlantDataProvider();
    }

    public Single<Long> getCount()
    {
        return cachedPlants.map(list -> (long) list.size()).last(0L);
    }

    public Observable<PlantListItemViewModel> getViewModelForListItem(int position)
    {

        return cachedPlants
                .concatMap(Observable::fromIterable)
                .elementAt(position)
                .toObservable()
                .map(PlantListItemViewModel::pendingPlant)
                .concatWith(cachedPlants
                        .concatMap(Observable::fromIterable)
                        .elementAt(position)
                        .toObservable()
                        .concatMap(plant -> dataProvider.getWeatherDataProvider()
                                .map(weatherData -> PlantListItemViewModel.statusPlant(plant, getPlantStatus(plant, weatherData)))
                                .onErrorReturn(e -> PlantListItemViewModel.errorPlant(plant, e))));
    }

    public Observable<String> getImageFileName(int position)
    {
        return cachedPlants
                .concatMap(Observable::fromIterable)
                .elementAt(position)
                .toObservable()
                .filter(plant -> plant.getMainImageUUID() != null)
                .concatMap(plant -> imageService.getPlantImage(plant.getMainImageUUID()))
                .map(PlantImage::getFileName);
    }

    private PlantListItemViewModel.Status getPlantStatus(Plant plant, WeatherData weatherData)
    {
        Temperature.COMPARISON temperatureComparison = plant.getMinimumTolerance().compareSignificanceTo(weatherData.getTodayLow());

        switch (temperatureComparison)
        {
            case GREATER:
                return PlantListItemViewModel.Status.DEAD;
            case LESSER:
                return PlantListItemViewModel.Status.HAPPY;
            case MAYBE_GREATER:
                return PlantListItemViewModel.Status.SAD;
            case MAYBE_LESSER:
                return PlantListItemViewModel.Status.NEUTRAL;
            case TRUE_EQUAL:
                return PlantListItemViewModel.Status.NEUTRAL;
        }

        return PlantListItemViewModel.Status.ERROR;
    }
}
