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

package com.yoxjames.coldsnap.model;

import org.threeten.bp.Instant;

import java.util.List;

import javax.annotation.concurrent.Immutable;

import dagger.internal.Preconditions;

/**
 * Created by James Yox on 4/8/17.
 *
 * Forecast POJO that will contain all weather data.
 */
@Immutable
public class WeatherData
{
    private final List<ForecastHour> forecastHours;
    private final WeatherLocation weatherLocation;
    private final Instant syncTime;
    private final ForecastHour lowToday;
    private final ForecastHour highToday;

    public WeatherData(List<ForecastHour> forecastHours, Instant syncTime, WeatherLocation weatherLocation, ForecastHour lowToday, ForecastHour highToday)
    {
        this.forecastHours = Preconditions.checkNotNull(forecastHours);
        this.weatherLocation = Preconditions.checkNotNull(weatherLocation);
        this.lowToday = Preconditions.checkNotNull(lowToday);
        this.highToday = Preconditions.checkNotNull(highToday);
        if (forecastHours.size() == 0)
            throw new IllegalStateException("No ForecastDay information in WeatherData");

        this.syncTime = Preconditions.checkNotNull(syncTime);
    }

    public Temperature getTodayLow()
    {
        return lowToday.getTemperature();
    }

    public Instant getTodayLowTime()
    {
        return lowToday.getHour();
    }

    public Temperature getTodayHigh()
    {
        return highToday.getTemperature();
    }

    public Instant getTodayHighTime()
    {
        return highToday.getHour();
    }

    public Instant getSyncInstant()
    {
        return syncTime;
    }

    /**
     * Checks if this weatherData is stale. Stale is defined as older than 2 minutes from the
     * current time.
     *
     * @return true if stale and false if not stale
     */
    public boolean isStale()
    {
        return this.getForecastHours().size() <= 0 || (getSyncInstant().getEpochSecond() + 60 * 2) <= Instant.now().getEpochSecond();
    }

    public List<ForecastHour> getForecastHours()
    {
        return forecastHours;
    }

    public WeatherLocation getWeatherLocation()
    {
        return weatherLocation;
    }
}
