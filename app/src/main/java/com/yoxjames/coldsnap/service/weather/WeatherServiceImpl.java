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

import com.jakewharton.rx.ReplayingShare;
import com.yoxjames.coldsnap.db.WeatherDataDAO;
import com.yoxjames.coldsnap.http.HTTPForecastService;
import com.yoxjames.coldsnap.model.WeatherData;
import com.yoxjames.coldsnap.prefs.CSPreferences;
import com.yoxjames.coldsnap.service.location.WeatherLocationService;

import javax.inject.Inject;

import io.reactivex.Observable;

/**
 * Implementation of WeatherService
 */
public class WeatherServiceImpl implements WeatherService
{
    private final WeatherDataDAO dbService;
    private final HTTPForecastService httpForecastService;
    private final WeatherLocationService weatherLocationService;
    private final Observable<WeatherData> weatherDataObservable;
    private final CSPreferences csPreferences;

    /**
     * Constructor for WeatherServiceImpl
     * @param dbService              The WeatherDAO DB Service implementation.
     * @param weatherLocationService
     * @param csPreferences
     */
    @Inject
    public WeatherServiceImpl(
        final WeatherDataDAO dbService,
        final HTTPForecastService httpForecastService,
        final WeatherLocationService weatherLocationService,
        final CSPreferences csPreferences)
    {
        this.dbService = dbService;
        this.httpForecastService = httpForecastService;
        this.weatherLocationService = weatherLocationService;
        this.csPreferences = csPreferences;
        weatherDataObservable = weatherLocationService.getWeatherLocation()
            .concatMap(location -> dbService.getWeatherData(location)
                .doOnError(Throwable::printStackTrace)
                .map(weatherData -> weatherData.withWeatherLocation(weatherData.getWeatherLocation().withLocationString(csPreferences.getLocationString())))
                .onErrorResumeNext(httpForecastService.getForecast(location).doOnNext(this::saveWeatherData)) // When nothing is in the cache just use HTTP
                .filter(wd -> !wd.isStale())
                .mergeWith(httpForecastService.getForecast(location).doOnNext(this::saveWeatherData))
                .doOnError(Throwable::printStackTrace)
                .firstOrError()
                .toObservable())
        .compose(ReplayingShare.instance());
    }

    @Override
    public Observable<WeatherData> getWeatherData()
    {
        return weatherDataObservable;
    }

    private void saveWeatherData(WeatherData weatherData)
    {
        dbService.deleteWeatherData().concatWith(dbService.saveWeatherData(weatherData)).blockingAwait();
        csPreferences.setLocationString(weatherData.getWeatherLocation().getPlaceString());
    }
}
