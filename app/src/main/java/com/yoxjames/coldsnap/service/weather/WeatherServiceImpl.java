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

import android.database.sqlite.SQLiteDatabase;

import com.yoxjames.coldsnap.db.ColdSnapDBHelper;
import com.yoxjames.coldsnap.db.WeatherDataDAO;
import com.yoxjames.coldsnap.http.HTTPWeatherService;
import com.yoxjames.coldsnap.model.WeatherData;
import com.yoxjames.coldsnap.model.WeatherLocation;
import com.yoxjames.coldsnap.service.location.WeatherLocationService;

import org.reactivestreams.Publisher;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

/**
 * Implementation of WeatherService
 */
@Singleton
public class WeatherServiceImpl implements WeatherService
{
    private final WeatherDataDAO dbService;
    private final HTTPWeatherService httpService;
    private final ColdSnapDBHelper dbHelper;
    private final WeatherLocationService weatherLocationService;
    private WeatherLocation currentLocation;
    private Single<WeatherData> cachedSingle;

    /**
     * Constructor for WeatherServiceImpl
     *
     * @param dbService              The WeatherDAO DB Service implementation.
     * @param httpService            HTTP Service Implementation for fetching WeatherData from the Internet
     * @param dbHelper               Database helper for ColdSnap
     * @param weatherLocationService Service for obtaining the currently set WeatherLocation data
     */
    @Inject
    public WeatherServiceImpl(final WeatherDataDAO dbService, final HTTPWeatherService httpService, ColdSnapDBHelper dbHelper, WeatherLocationService weatherLocationService)
    {
        this.dbService = dbService;
        this.httpService = httpService;
        this.dbHelper = dbHelper;
        this.weatherLocationService = weatherLocationService;
        recacheObservable();
    }

    @Override
    public Single<WeatherData> getCurrentForecastData()
    {
        return cachedSingle;
    }

    // TODO: Somebody tell me there's a better way....
    private void recacheObservable()
    {
        final SQLiteDatabase database = dbHelper.getWritableDatabase();

        final Single<WeatherData> http = weatherLocationService
                .readWeatherLocation()
                .flatMap(new Function<WeatherLocation, SingleSource<WeatherData>>()
                {
                    @Override
                    public SingleSource<WeatherData> apply(@NonNull WeatherLocation weatherLocation) throws Exception
                    {
                        return httpService.getWeatherData(weatherLocation);
                    }
                }).doOnSuccess(new Consumer<WeatherData>()
                {
                    @Override
                    public void accept(WeatherData weatherData) throws Exception
                    {
                        dbService
                                .deleteWeatherData(database)
                                .concatWith(dbService.saveWeatherData(database, weatherData))
                                .blockingAwait();
                    }
                });

        final Single<WeatherData> db = weatherLocationService
                .readWeatherLocation()
                .flatMap(new Function<WeatherLocation, SingleSource<WeatherData>>()
                {
                    @Override
                    public SingleSource<WeatherData> apply(@NonNull WeatherLocation weatherLocation) throws Exception
                    {
                        return dbService
                                .getWeatherData(database, weatherLocation)
                                .onErrorResumeNext(http);
                    }
                });

        cachedSingle = Single.concat(db, http)
                .filter(new Predicate<WeatherData>()
                {
                    @Override
                    public boolean test(@NonNull WeatherData weatherData) throws Exception
                    {
                        return !weatherData.isStale();
                    }
                })
                .share()
                .firstOrError()
                .observeOn(AndroidSchedulers.mainThread());
    }
}
