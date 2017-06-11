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

package com.example.yoxjames.coldsnap.service;

import com.example.yoxjames.coldsnap.model.WeatherData;
import com.example.yoxjames.coldsnap.model.WeatherDataNotFoundException;

/**
 * High level service that obtains {@link WeatherData}.
 */
public interface WeatherService
{
    /**
     * Gets the current forecast data and returns that information as a {@link WeatherData}
     *
     * @return WeatherData representing the current forecast.
     * @throws WeatherDataNotFoundException If No valid and not Stale WeatherData could be found.
     */
    WeatherData getCurrentForecastData() throws WeatherDataNotFoundException;
}
