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

package com.yoxjames.coldsnap.job;

import android.content.SharedPreferences;

import com.yoxjames.coldsnap.model.TemperatureFormatter;
import com.yoxjames.coldsnap.service.plant.PlantService;
import com.yoxjames.coldsnap.service.weather.WeatherService;

import javax.inject.Inject;

/**
 * Created by yoxjames on 9/3/17.
 */

public class ColdAlarmPresenter
{
    private final ColdAlarmView view;
    private final WeatherService weatherService;
    private final PlantService plantService;
    private final SharedPreferences sharedPreferences;
    private final TemperatureFormatter temperatureFormatter;

    @Inject
    public ColdAlarmPresenter(ColdAlarmView view, WeatherService weatherService, PlantService plantService, SharedPreferences sharedPreferences, TemperatureFormatter temperatureFormatter)
    {
        this.view = view;
        this.weatherService = weatherService;
        this.plantService = plantService;
        this.sharedPreferences = sharedPreferences;
        this.temperatureFormatter = temperatureFormatter;
    }

    public void load()
    {
        /*
        final Temperature coldThresholdPref = new Temperature(sharedPreferences.getFloat(CSPreferencesFragment.THRESHOLD, 273f));

        weatherService.getWeatherData()
                .subscribe(weatherData ->
        {
            if (weatherData.getTodayLow().compareTo(coldThresholdPref) == -1)
                view.displayThresholdMessage(temperatureFormatter.format(weatherData.getTodayLow()), temperatureFormatter.format(coldThresholdPref));

            plantService.getPlants().filter(plant -> (plant.getMinimumTolerance().compareTo(weatherData.getTodayLow()) > 0))
                    .subscribe(plant -> view.pushPlantWarning(plant.getName()),
                            Throwable::printStackTrace,
                            view::onCompletePlantWarnings);
        },

        throwable ->
        {
            // TODO: This probably needs to try again at a later time or something
            LOG.e(getClass().getName(), "Get Current Forecast Observer error");
            throwable.printStackTrace();
        });
        */
    }
}
