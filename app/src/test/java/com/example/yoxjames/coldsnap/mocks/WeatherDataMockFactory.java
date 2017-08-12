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

package com.example.yoxjames.coldsnap.mocks;

import com.example.yoxjames.coldsnap.model.ForecastDay;
import com.example.yoxjames.coldsnap.model.Temperature;
import com.example.yoxjames.coldsnap.model.WeatherData;
import com.example.yoxjames.coldsnap.model.WeatherLocation;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class WeatherDataMockFactory
{
    public static WeatherData getBasicWeatherData()
    {
        List<ForecastDay> forecastDayList = new ArrayList<>();

        forecastDayList.add(new ForecastDay(
                "Jun 1 2017",
                Temperature.newTemperatureFromF(72),
                Temperature.newTemperatureFromF(31),
                new Date(),
                UUID.randomUUID()));

        forecastDayList.add(new ForecastDay(
                "Jun 2 2017",
                Temperature.newTemperatureFromF(80),
                Temperature.newTemperatureFromF(40),
                new Date(),
                UUID.randomUUID()));

        forecastDayList.add(new ForecastDay(
                "Jun 3 2017",
                Temperature.newTemperatureFromF(60),
                Temperature.newTemperatureFromF(30),
                new Date(),
                UUID.randomUUID()));

        forecastDayList.add(new ForecastDay(
                "Jun 4 2017",
                Temperature.newTemperatureFromF(70),
                Temperature.newTemperatureFromF(35),
                new Date(),
                UUID.randomUUID()));

        final WeatherLocation weatherLocation = new WeatherLocation("64105", "Kansas City, MO", 0f, 0f);

        return new WeatherData(forecastDayList, new Date(), weatherLocation);
    }

    public static List<ForecastDay> getForecastDays()
    {
        List<ForecastDay> forecastDayList = new ArrayList<>();

        forecastDayList.add(new ForecastDay(
                "Jun 1 2017",
                Temperature.newTemperatureFromF(72),
                Temperature.newTemperatureFromF(32),
                new Date(),
                UUID.randomUUID()));

        forecastDayList.add(new ForecastDay(
                "Jun 2 2017",
                Temperature.newTemperatureFromF(80),
                Temperature.newTemperatureFromF(40),
                new Date(),
                UUID.randomUUID()));

        forecastDayList.add(new ForecastDay(
                "Jun 3 2017",
                Temperature.newTemperatureFromF(60),
                Temperature.newTemperatureFromF(30),
                new Date(),
                UUID.randomUUID()));

        forecastDayList.add(new ForecastDay(
                "Jun 4 2017",
                Temperature.newTemperatureFromF(70),
                Temperature.newTemperatureFromF(35),
                new Date(),
                UUID.randomUUID()));
        return forecastDayList;
    }
}
