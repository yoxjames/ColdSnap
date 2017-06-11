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

package com.example.yoxjames.coldsnap.dagger;

import com.example.yoxjames.coldsnap.service.PlantService;
import com.example.yoxjames.coldsnap.service.WeatherService;
import com.example.yoxjames.coldsnap.service.WeatherServiceAsyncProcessor;
import com.example.yoxjames.coldsnap.service.WeatherServiceAsyncProcessorImpl;
import com.example.yoxjames.coldsnap.service.WeatherServiceCall;
import com.example.yoxjames.coldsnap.service.WeatherServiceCallImpl;
import com.example.yoxjames.coldsnap.ui.presenter.PlantListPresenter;
import com.example.yoxjames.coldsnap.ui.presenter.PlantListPresenterImpl;
import com.example.yoxjames.coldsnap.ui.view.PlantListView;

import javax.inject.Provider;

import dagger.Lazy;
import dagger.Module;
import dagger.Provides;

@Module
public class PlantListFragmentModule
{
    private final PlantListView view;

    public PlantListFragmentModule(PlantListView view)
    {
        this.view = view;
    }

    @Provides
    PlantListView providePlantListView()
    {
        return view;
    }

    @Provides
    static PlantListPresenter providePlantListPresenter(PlantListView plantListView, PlantService plantService, WeatherServiceCall weatherServiceCall)
    {
        return new PlantListPresenterImpl(plantListView, plantService, weatherServiceCall);
    }

    @Provides
    static WeatherServiceCall provideWeatherServiceCall(Provider<Lazy<WeatherServiceAsyncProcessor>> weatherServiceAsyncProcessor)
    {
        return new WeatherServiceCallImpl(weatherServiceAsyncProcessor);
    }

    @Provides
    static WeatherServiceAsyncProcessor provideWeatherServiceAsyncProcessor(WeatherService weatherService)
    {
        return new WeatherServiceAsyncProcessorImpl(weatherService);
    }
}
