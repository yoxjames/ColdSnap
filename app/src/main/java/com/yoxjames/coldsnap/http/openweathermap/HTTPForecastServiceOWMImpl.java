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

import android.content.SharedPreferences;

import com.yoxjames.coldsnap.BuildConfig;
import com.yoxjames.coldsnap.http.HTTPForecastService;
import com.yoxjames.coldsnap.model.ForecastHour;
import com.yoxjames.coldsnap.model.ForecastHourUtil;
import com.yoxjames.coldsnap.model.Temperature;
import com.yoxjames.coldsnap.model.WeatherData;
import com.yoxjames.coldsnap.model.WeatherLocation;
import com.yoxjames.coldsnap.service.location.SimpleWeatherLocation;
import com.yoxjames.coldsnap.ui.CSPreferencesFragment;
import com.yoxjames.coldsnap.util.LOG;

import org.threeten.bp.Instant;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import dagger.Reusable;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

/**
 * Created by yoxjames on 10/7/17.
 */

@Reusable
public class HTTPForecastServiceOWMImpl implements HTTPForecastService
{
    private static final String OPEN_WEATHER_MAP_API_KEY = BuildConfig.OPEN_WEATHER_MAPS_API_KEY;

    private final OpenWeatherMapHTTPService owmService;
    private final SharedPreferences sharedPreferences;

    @Inject
    public HTTPForecastServiceOWMImpl(OpenWeatherMapHTTPService owmService, SharedPreferences sharedPreferences)
    {
        this.owmService = owmService;
        this.sharedPreferences = sharedPreferences;
    }

    @Override
    public Observable<WeatherData> getForecast(SimpleWeatherLocation location)
    {
        final double fuzzK = sharedPreferences.getFloat(CSPreferencesFragment.WEATHER_DATA_FUZZ, 0.0f);

        return owmService.getOWMForecast(location.getLat(), location.getLon(), OPEN_WEATHER_MAP_API_KEY)
                .subscribeOn(Schedulers.io())
                .filter(Response::isSuccessful)
                .map(Response::body)
                .map(owmForecast ->
                {
                    final List<ForecastHour> forecastHourList = new ArrayList<>();

                    for (OpenWeatherMapForecast.ForecastPeriods period : owmForecast.list)
                        forecastHourList.add(new ForecastHour(Instant.ofEpochSecond(period.dt), new Temperature(period.forecast.temp, fuzzK), UUID.randomUUID(), location.getLat(), location.getLon()));

                    ForecastHourUtil.HighLowPair highLowData = ForecastHourUtil.getDailyHighLow(forecastHourList);

                    return new WeatherData(forecastHourList,
                            Instant.now(),
                            new WeatherLocation(owmForecast.city.name, owmForecast.city.coord.lat, owmForecast.city.coord.lon),
                            highLowData.getDailyLow(),
                            highLowData.getDailyHigh());

                })
                .doOnNext(ignored -> LOG.d(getClass().getName(), "API: Open Weather Map Forecast"));
    }
}
