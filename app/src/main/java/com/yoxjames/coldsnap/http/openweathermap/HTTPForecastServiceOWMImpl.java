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

package com.yoxjames.coldsnap.http.openweathermap;

import com.yoxjames.coldsnap.BuildConfig;
import com.yoxjames.coldsnap.http.HTTPForecastService;
import com.yoxjames.coldsnap.model.ForecastHour;
import com.yoxjames.coldsnap.model.ForecastHourUtil;
import com.yoxjames.coldsnap.model.Temperature;
import com.yoxjames.coldsnap.model.WeatherData;
import com.yoxjames.coldsnap.model.WeatherLocation;
import com.yoxjames.coldsnap.prefs.CSPreferences;
import com.yoxjames.coldsnap.service.location.SimpleWeatherLocation;
import com.yoxjames.coldsnap.util.LOG;

import org.threeten.bp.Instant;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;
import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

/**
 * Created by yoxjames on 10/7/17.
 */
public class HTTPForecastServiceOWMImpl implements HTTPForecastService
{
    private static final String OPEN_WEATHER_MAP_API_KEY = BuildConfig.OPEN_WEATHER_MAPS_API_KEY;

    private final OpenWeatherMapHTTPService owmService;
    private final CSPreferences csPreferences;

    @Inject
    public HTTPForecastServiceOWMImpl(OpenWeatherMapHTTPService owmService, CSPreferences csPreferences)
    {
        this.owmService = owmService;
        this.csPreferences = csPreferences;
    }

    @Override
    public Observable<WeatherData> getForecast(SimpleWeatherLocation location)
    {

        return owmService.getOWMForecast(location.getLat(), location.getLon(), OPEN_WEATHER_MAP_API_KEY)
                .subscribeOn(Schedulers.io())
                .doOnError(Throwable::printStackTrace)
                .filter(Response::isSuccessful)
                .map(Response::body)
                .flatMap(owmForecast ->
                {
                    WeatherData weatherData = mapToPOJO(owmForecast, location);

                    if (weatherData != null)
                        return Observable.just(weatherData);
                    else
                        return Observable.empty();
                })
                .doOnNext(ignored -> LOG.d(getClass().getName(), "API: Open Weather Map Forecast"));
    }

    @Nullable
    private WeatherData mapToPOJO(OpenWeatherMapForecast owmForecast, SimpleWeatherLocation simpleWeatherLocation)
    {
        final double fuzzK = csPreferences.getWeatherDataFuzz();

        final List<ForecastHour> forecastHourList = new ArrayList<>();

        if (owmForecast.list == null)
            return null;

        for (OpenWeatherMapForecast.ForecastPeriods period : owmForecast.list)
        {
            if (period.forecast != null && period.forecast.temp != null)
                forecastHourList.add(ForecastHour.create(
                    Instant.ofEpochSecond(period.dt),
                    new Temperature(period.forecast.temp, fuzzK),
                    UUID.randomUUID(),
                    simpleWeatherLocation.getLat(),
                    simpleWeatherLocation.getLon(),
                    Instant.now()));
        }

        ForecastHour dailyHigh = ForecastHourUtil.getDailyHigh(forecastHourList);
        ForecastHour dailyLow = ForecastHourUtil.getDailyLow(forecastHourList);

        if (dailyHigh == null || dailyLow == null)
            return null;

        String name = "";

        if (owmForecast.city != null && owmForecast.city.name != null)
            name = owmForecast.city.name;

        return WeatherData.builder()
            .forecastHours(forecastHourList)
            .syncInstant(Instant.now())
            .todayHigh(dailyHigh)
            .todayLow(dailyLow)
            .weatherLocation(WeatherLocation.builder()
                .setPlaceString(name)
                .setLat(simpleWeatherLocation.getLat())
                .setLon(simpleWeatherLocation.getLon())
                .build())
            .build();
    }
}
