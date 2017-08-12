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

package com.example.yoxjames.coldsnap.model;

import java.util.Date;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import dagger.internal.Preconditions;

/**
 * Created by James Yox on 4/8/17.
 *
 * Main POJO that will contain all weather data.
 */
@Immutable
public class WeatherData
{
    /* Stores the highs and lows of upcoming weather days */
    private final List<ForecastDay> forecastDays;

    /* Location data that this Weather Data is valid for */
    private final WeatherLocation weatherLocation;

    /* Date this WeatherData was obtained */
    private final Date syncDate;

    public WeatherData(List<ForecastDay> forecastDays, Date syncDate, WeatherLocation weatherLocation)
    {
        this.forecastDays = Preconditions.checkNotNull(forecastDays);
        this.weatherLocation = weatherLocation;
        if (forecastDays.size() == 0)
            throw new IllegalStateException("No ForecastDay information in WeatherData");

        this.syncDate = Preconditions.checkNotNull(syncDate);
    }

    public Temperature getTodayLow()
    {
        return forecastDays.get(0).getLowTemperature();
    }

    public Temperature getTodayHigh()
    {
        return forecastDays.get(0).getHighTemperature();
    }

    public Date getSyncDate()
    {
        return new Date(syncDate.getTime());
    }

    /**
     * Checks if this weatherData is stale. Stale is defined as older than 2 minutes from the
     * current time.
     *
     * @return true if stale and false if not stale
     */
    public boolean isStale()
    {
        return this.getForecastDays().size() <= 0 || (getSyncDate().getTime() + (2 * 60 * 1000)) <= (new Date().getTime());
    }

    public List<ForecastDay> getForecastDays()
    {
        return forecastDays;
    }

    public WeatherLocation getWeatherLocation()
    {
        return weatherLocation;
    }
}
