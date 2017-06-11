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

package com.example.yoxjames.coldsnap.ui.presenter;

import com.example.yoxjames.coldsnap.model.Plant;
import com.example.yoxjames.coldsnap.model.WeatherData;
import com.example.yoxjames.coldsnap.model.WeatherDataNotFoundException;
import com.example.yoxjames.coldsnap.service.PlantService;
import com.example.yoxjames.coldsnap.service.WeatherServiceCall;
import com.example.yoxjames.coldsnap.service.WeatherServiceCallback;
import com.example.yoxjames.coldsnap.ui.view.PlantListItemView;
import com.example.yoxjames.coldsnap.ui.view.PlantListView;

import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Inject;


public class PlantListPresenterImpl implements PlantListPresenter
{
    private final PlantListView view;
    private final PlantService plantService;
    private final WeatherServiceCall weatherServiceCall;
    private List<Plant> plantList;
    private WeatherData weatherData;

    @Inject
    public PlantListPresenterImpl(PlantListView view, PlantService plantService, WeatherServiceCall weatherServiceCall)
    {
        this.view = view;
        this.plantService = plantService;
        this.weatherServiceCall = weatherServiceCall;
    }

    @Override
    public void loadPlantView(PlantListItemView view, int position)
    {
        Plant plant = plantList.get(position);
        view.setPlantName(plant.getName());
        view.setPlantScientificName(plant.getScientificName());
        view.setUUID(plant.getUuid());
        if ((weatherData == null) || weatherData.getForecastDays().size() == 0)
            view.setStatus("...");
        else
        {
            if (plant.getMinimumTolerance().compareTo(weatherData.getTodayLow()) == 0)
                view.setStatus("\uD83D\uDE10"); // Neutral Face
            else if (plant.getMinimumTolerance().compareTo(weatherData.getTodayLow()) == -1)
                view.setStatus("\uD83D\uDE00"); // Happy Face
            else
                view.setStatus("\uD83D\uDE1E"); // Sad Face
        }
    }

    @Override
    public int getItemCount()
    {
        return plantList.size();
    }

    @Override
    public void load()
    {
        plantList = plantService.getMyPlants();
        if (plantList.size() > 0)
            weatherServiceCall.execute(new PlantListUICallback());
    }

    @Override
    public void newPlant()
    {
        Plant plant = new Plant("", "");
        plantService.cachePlant(plant);
        view.openPlant(plant.getUuid(), true);
    }

    private class PlantListUICallback implements WeatherServiceCallback
    {
        @Override
        public void callback(@Nullable WeatherData result, WeatherDataNotFoundException e)
        {
            if (e == null)
            {
                weatherData = result;
                view.notifyDataChange();
            }
        }
    }
}
