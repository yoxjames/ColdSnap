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
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;

import com.yoxjames.coldsnap.http.HTTPGeolocationService;
import com.yoxjames.coldsnap.model.WeatherLocation;
import com.yoxjames.coldsnap.util.LOG;

import java.security.NoSuchProviderException;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.observables.ConnectableObservable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by yoxjames on 8/27/17.
 */

@Singleton
public class GPSLocationServiceImpl implements GPSLocationService
{
    private final Context context;
    private final HTTPGeolocationService geolocationService;
    private final WeatherLocationService weatherLocationService;
    private ConnectableObservable<Location> currentAndroidObservable;
    private Observable<WeatherLocation> currentWeatherLocationObservable;
    private Observable<Location> androidLocation;
    private Disposable connectionDisposable;

    @Inject
    public GPSLocationServiceImpl(final Context context, final HTTPGeolocationService geolocationService, final WeatherLocationService weatherLocationService)
    {
        this.context = context;
        this.geolocationService = geolocationService;
        this.weatherLocationService = weatherLocationService;
        androidLocation = getAndroidLocation();
        currentAndroidObservable = androidLocation.publish();
        currentWeatherLocationObservable = getCurrentWeatherLocationObservable().share();
    }

    @Override
    public Observable<WeatherLocation> getWeatherLocation()
    {
        return currentWeatherLocationObservable;
    }

    private Observable<WeatherLocation> getCurrentWeatherLocationObservable()
    {
        return currentAndroidObservable
                .observeOn(Schedulers.io())
                .flatMap(new Function<Location, ObservableSource<WeatherLocation>>()
                {
                    @Override
                    public ObservableSource<WeatherLocation> apply(@NonNull Location location) throws Exception
                    {
                        return geolocationService.getCurrentWeatherLocation(location.getLatitude(), location.getLongitude())
                                .toObservable()
                                .doOnError(throwable -> LOG.d(getClass().getName(), "Geolocation Service Failed, swallowing error"))
                                .onErrorResumeNext((ObservableSource<? extends WeatherLocation>) throwable -> Observable.empty());

                    }

                })
                .doOnNext(weatherLocation -> weatherLocationService.saveWeatherLocation(weatherLocation).blockingAwait())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private Observable<Location> getAndroidLocation()
    {
        return Observable.create(emitter ->
        {
            final LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

            final LocationListener listener = new LocationListener()
            {
                @Override
                public void onLocationChanged(Location location)
                {
                    emitter.onNext(location);
                    emitter.onComplete();
                }

                @Override public void onStatusChanged(String s, int i, Bundle bundle) { }
                @Override public void onProviderEnabled(String s) { }
                @Override public void onProviderDisabled(String s) { }
            };

            emitter.setCancellable(() -> locationManager.removeUpdates(listener));

            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                {
                    emitter.onError(new SecurityException("Location privs not granted"));
                    return;
                }

                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location != null)
                    emitter.onNext(location);
                locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, listener, null);
            }
            else
            {
                emitter.onError(new NoSuchProviderException("No location provider available"));
            }
        });
    }

    @Override
    public void pushWeatherLocation()
    {
        if (connectionDisposable != null)
            connectionDisposable.dispose();
        connectionDisposable = currentAndroidObservable.connect();
    }

    @Override
    public void cancelRequestLocation()
    {
        if (connectionDisposable != null)
            connectionDisposable.dispose();
    }
}

