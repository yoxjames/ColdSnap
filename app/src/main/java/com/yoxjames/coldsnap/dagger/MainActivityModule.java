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

package com.yoxjames.coldsnap.dagger;


import com.yoxjames.coldsnap.model.TemperatureFormatter;
import com.yoxjames.coldsnap.service.image.ImageService;
import com.yoxjames.coldsnap.service.location.WeatherLocationService;
import com.yoxjames.coldsnap.service.plant.PlantService;
import com.yoxjames.coldsnap.service.weather.WeatherService;
import com.yoxjames.coldsnap.ui.MainActivityDataProvider;
import com.yoxjames.coldsnap.ui.MainActivityDataProviderImpl;
import com.yoxjames.coldsnap.ui.plantlist.PlantListItemViewAdapter;
import com.yoxjames.coldsnap.ui.weatherpreviewbar.WeatherBarModelAdapter;
import com.yoxjames.coldsnap.ui.weatherpreviewbar.WeatherBarModelAdapterImpl;
import com.yoxjames.coldsnap.ui.weatherpreviewbar.WeatherPreviewBarView;

import dagger.Module;
import dagger.Provides;

@Module
public class MainActivityModule
{
    private final WeatherPreviewBarView weatherPreviewBarView;

    public MainActivityModule(WeatherPreviewBarView weatherPreviewBarView)
    {
        this.weatherPreviewBarView = weatherPreviewBarView;
    }

    @Provides
    static WeatherBarModelAdapter provideWeatherBarModelAdapter(MainActivityDataProvider presenter, TemperatureFormatter temperatureFormatter)
    {
        return new WeatherBarModelAdapterImpl(presenter, temperatureFormatter);
    }

    @Provides
    @ActivityScope
    static MainActivityDataProvider provideMainActivityPresenter(WeatherService weatherService, WeatherLocationService weatherLocationService, PlantService plantService)
    {
        return new MainActivityDataProviderImpl(weatherService, weatherLocationService, plantService);
    }

    @Provides
    @ActivityScope
    WeatherPreviewBarView provideWeatherPreviewBarView()
    {
        return weatherPreviewBarView;
    }

    @Provides
    @ActivityScope
    PlantListItemViewAdapter providePlantListItemViewAdapter(MainActivityDataProvider dataProvider, ImageService imageService)
    {
        return new PlantListItemViewAdapter(dataProvider, imageService);
    }
}
