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

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import com.yoxjames.coldsnap.http.HTTPGeolocationService;
import com.yoxjames.coldsnap.model.SimpleWeatherLocation;
import com.yoxjames.coldsnap.model.SimpleWeatherLocationNotFoundException;
import com.yoxjames.coldsnap.model.WeatherLocation;
import com.yoxjames.coldsnap.util.LOG;

import javax.inject.Inject;
import javax.inject.Singleton;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * Created by yoxjames on 8/27/17.
 */

@Singleton
public class GPSLocationServiceImpl implements GPSLocationService
{
    private final Context context;
    private final HTTPGeolocationService geolocationService;
    private final Observable<WeatherLocation> weatherLocationObservable;
    private final Subject<SimpleWeatherLocation> weatherLocationSubject;
    private final SharedPreferences sharedPreferences;
    private final WeatherLocationService weatherLocationService;

    @Inject
    public GPSLocationServiceImpl(final Context context, final HTTPGeolocationService geolocationService, final SharedPreferences sharedPreferences, final WeatherLocationService weatherLocationService)
    {
        this.context = context;
        this.geolocationService = geolocationService;
        this.sharedPreferences = sharedPreferences;
        this.weatherLocationService = weatherLocationService;
        this.weatherLocationSubject = PublishSubject.create();
        this.weatherLocationObservable = weatherLocationSubject
                .serialize()
                .observeOn(Schedulers.io())
                .flatMap(new Function<SimpleWeatherLocation, ObservableSource<WeatherLocation>>()
                {
                    @Override
                    public ObservableSource<WeatherLocation> apply(@NonNull SimpleWeatherLocation simpleWeatherLocation) throws Exception
                    {
                        return geolocationService.getCurrentWeatherLocation(simpleWeatherLocation.getLat(), simpleWeatherLocation.getLon())
                                .toObservable()
                                .doOnError(new Consumer<Throwable>()
                                {
                                    @Override
                                    public void accept(Throwable throwable) throws Exception
                                    {
                                        LOG.d(getClass().getName(), "Geolocation Service Failed, swallowing error");
                                    }
                                })
                                .onErrorResumeNext(new Function<Throwable, ObservableSource<WeatherLocation>>()
                                {
                                    @Override
                                    public ObservableSource<WeatherLocation> apply(@NonNull Throwable throwable) throws Exception
                                    {
                                        return Observable.empty();
                                    }
                                });
                    }
                })
                .doOnNext(new Consumer<WeatherLocation>()
                {
                    @Override
                    public void accept(WeatherLocation weatherLocation) throws Exception
                    {
                        weatherLocationService.saveWeatherLocation(weatherLocation).blockingAwait();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .share();
    }

    public Observable<WeatherLocation> getWeatherLocation()
    {
        return weatherLocationObservable;
    }

    @Override
    public void pushWeatherLocation()
    {
        final LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        final LocationListener listener = new LocationListener()
        {
            @Override
            public void onLocationChanged(Location location)
            {
                weatherLocationSubject.onNext(new SimpleWeatherLocation(location.getLatitude(), location.getLongitude()));
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle)
            {
            }

            @Override
            public void onProviderEnabled(String s)
            {
            }

            @Override
            public void onProviderDisabled(String s)
            {
            }
        };

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                weatherLocationSubject.onError(new SimpleWeatherLocationNotFoundException("Location permission not granted"));
            }
            else
            {
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                if (location != null)
                    weatherLocationSubject.onNext(new SimpleWeatherLocation(location.getLatitude(), location.getLongitude()));
                else
                    locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, listener, null);
            }
        }
        else
            weatherLocationSubject.onError(new SimpleWeatherLocationNotFoundException("No acceptable location providers available"));
    }
}

