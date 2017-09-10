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

package com.yoxjames.coldsnap.service.location;


import com.yoxjames.coldsnap.model.WeatherLocation;

import io.reactivex.Completable;
import io.reactivex.Single;

/**
 * Service for reading the current WeatherLocation and updating and clearing the exsting WeatherLocation
 * data. At this time only one WeatherLocation can be used at a time.
 *
 * Created by yoxjames on 7/8/17.
 */

public interface WeatherLocationService
{
    /**
     * Obtains the WeatherLocation that is currently stored.
     *
     * @return A WeatherLocation object for the currently set location.
     */
    Single<WeatherLocation> readWeatherLocation();

    /**
     * Removes the current WeatherLocation object and replaces it with the inputted WeatherLocation.
     * This should not have to be done often as most users should really only use this once for the location
     * of the plants they are growing. At this time only one WeatherLocation at a time is supported.
     *
     * @param weatherLocation The new WeatherLocation to save.
     */
    Completable saveWeatherLocation(WeatherLocation weatherLocation);
}
