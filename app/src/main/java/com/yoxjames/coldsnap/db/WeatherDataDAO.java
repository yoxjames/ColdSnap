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

package com.yoxjames.coldsnap.db;

import com.yoxjames.coldsnap.model.WeatherData;
import com.yoxjames.coldsnap.service.location.SimpleWeatherLocation;

import io.reactivex.Completable;
import io.reactivex.Observable;

/**
 * Data Access Object for WeatherData!
 */
public interface WeatherDataDAO
{
    Completable saveWeatherData(WeatherData weatherData);
    Observable<WeatherData> getWeatherData(SimpleWeatherLocation weatherLocation);
    Completable deleteWeatherData();
}
