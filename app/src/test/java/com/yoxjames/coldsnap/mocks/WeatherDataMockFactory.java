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

package com.yoxjames.coldsnap.mocks;

import com.yoxjames.coldsnap.model.ForecastHour;
import com.yoxjames.coldsnap.model.Temperature;
import com.yoxjames.coldsnap.model.WeatherData;
import com.yoxjames.coldsnap.model.WeatherLocation;

import org.threeten.bp.Instant;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by yoxjames on 12/10/17.
 */

public class WeatherDataMockFactory
{
    public static WeatherData getStaleWeatherData(WeatherLocation location)
    {
        final List<ForecastHour> forecastHourList = ForecastHourFactory.getStandardTestForecast(location);
        return new WeatherData(forecastHourList,
                Instant.now().minusSeconds(60 * 3), // Three minutes ago
                location,
                forecastHourList.get(0),
                forecastHourList.get(10));

    }

    public static WeatherData getValidWeatherData(WeatherLocation location)
    {

        final List<ForecastHour> forecastHourList = ForecastHourFactory.getStandardTestForecast(location);
        return new WeatherData(forecastHourList,
                Instant.now(),
                location,
                forecastHourList.get(0),
                forecastHourList.get(10));
    }

    public static class ForecastHourFactory
    {
        public static List<ForecastHour> getStandardTestForecast(WeatherLocation location)
        {
            final List<ForecastHour> forecastHourList = new ArrayList<>();

            for (int i = 0; i < 20; i++)
            {
                final ForecastHour forecastHour =
                        new ForecastHour(Instant.now().plusSeconds(i * 60 * 60 * 3),
                                Temperature.newTemperatureFromC(0),
                                UUID.randomUUID(),
                                location.getLat(),
                                location.getLon());
                forecastHourList.add(forecastHour);
            }

            return forecastHourList;
        }
    }
}
