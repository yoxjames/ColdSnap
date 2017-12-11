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

package com.yoxjames.coldsnap.service.weather;

import com.yoxjames.coldsnap.db.WeatherDataDAO;
import com.yoxjames.coldsnap.http.HTTPForecastService;
import com.yoxjames.coldsnap.model.WeatherData;
import com.yoxjames.coldsnap.service.location.SimpleWeatherLocation;

import javax.inject.Inject;

import dagger.Reusable;
import io.reactivex.Observable;

/**
 * Implementation of WeatherService
 */
@Reusable
public class WeatherServiceImpl implements WeatherService
{
    private final WeatherDataDAO dbService;
    private final HTTPForecastService httpForecastService;

    /**
     * Constructor for WeatherServiceImpl
     * @param dbService              The WeatherDAO DB Service implementation.
     */
    @Inject
    public WeatherServiceImpl(final WeatherDataDAO dbService, HTTPForecastService httpForecastService)
    {
        this.dbService = dbService;
        this.httpForecastService = httpForecastService;
    }

    @Override
    public Observable<WeatherData> getWeatherData(SimpleWeatherLocation location)
    {
        return dbService.getWeatherData(location)
                .onErrorResumeNext(httpForecastService.getForecast(location)
                        .doOnNext(weatherData -> dbService.deleteWeatherData().concatWith(dbService.saveWeatherData(weatherData)).blockingAwait())) // When nothing is in the cache just use HTTP
                .filter(wd -> !wd.isStale())
                .concatWith(httpForecastService.getForecast(location)
                        .doOnNext(weatherData -> dbService.deleteWeatherData().concatWith(dbService.saveWeatherData(weatherData)).blockingAwait()))
                .firstOrError()
                .toObservable();
    }
}
