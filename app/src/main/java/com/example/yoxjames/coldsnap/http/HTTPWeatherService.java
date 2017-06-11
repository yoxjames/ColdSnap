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

package com.example.yoxjames.coldsnap.http;

import com.example.yoxjames.coldsnap.model.WeatherData;
import com.example.yoxjames.coldsnap.model.WeatherDataNotFoundException;

/**
 * Service designed to obtain WeatherData via HTTP. This could be implemented with many different
 * Weather Data providers and may require more than one call. This is just a single point by which you are
 * guaranteed a full WeatherData business object.
 */
public interface HTTPWeatherService
{
    /**
     * Gets the WeatherData via HTTP
     *
     * @return WeatherData from HTTP
     * @throws WeatherDataNotFoundException If for some reason this fails (no internet connectivity, API key out of hits, etc)
     */
    WeatherData getWeatherData() throws WeatherDataNotFoundException;
}
