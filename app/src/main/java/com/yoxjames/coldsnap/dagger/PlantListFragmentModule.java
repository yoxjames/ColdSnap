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


import com.yoxjames.coldsnap.service.location.GPSLocationService;
import com.yoxjames.coldsnap.service.plant.PlantService;
import com.yoxjames.coldsnap.service.weather.WeatherService;
import com.yoxjames.coldsnap.ui.presenter.PlantListPresenter;
import com.yoxjames.coldsnap.ui.presenter.PlantListPresenterImpl;
import com.yoxjames.coldsnap.ui.view.PlantListView;

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
    static PlantListPresenter providePlantListPresenter(PlantListView plantListView,
                                                        PlantService plantService,
                                                        GPSLocationService gpsLocationService,
                                                        WeatherService weatherService)
    {
        return new PlantListPresenterImpl(plantListView, plantService, weatherService, gpsLocationService);
    }
}
