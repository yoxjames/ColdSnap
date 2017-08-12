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

package com.example.yoxjames.coldsnap.dagger;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;

import com.example.yoxjames.coldsnap.db.ColdSnapDBHelper;
import com.example.yoxjames.coldsnap.db.ForecastDayCursorWrapper;
import com.example.yoxjames.coldsnap.db.PlantCursorWrapper;
import com.example.yoxjames.coldsnap.db.PlantDAO;
import com.example.yoxjames.coldsnap.db.PlantDAOSQLiteImpl;
import com.example.yoxjames.coldsnap.db.WeatherDataDAO;
import com.example.yoxjames.coldsnap.db.WeatherDataDAOSQLiteImpl;
import com.example.yoxjames.coldsnap.http.HTTPGeolocationService;
import com.example.yoxjames.coldsnap.http.HTTPWeatherService;
import com.example.yoxjames.coldsnap.http.google.GoogleLocationURLFactory;
import com.example.yoxjames.coldsnap.http.google.HTTPGeolocationServiceGoogleImpl;
import com.example.yoxjames.coldsnap.http.wu.HTTPWeatherServiceWUImpl;
import com.example.yoxjames.coldsnap.http.wu.WundergroundURLFactory;
import com.example.yoxjames.coldsnap.model.SimpleWeatherLocation;
import com.example.yoxjames.coldsnap.model.WeatherData;
import com.example.yoxjames.coldsnap.model.WeatherLocation;
import com.example.yoxjames.coldsnap.service.location.WeatherLocationService;
import com.example.yoxjames.coldsnap.service.location.WeatherLocationServicePreferenceImpl;
import com.example.yoxjames.coldsnap.service.plant.PlantService;
import com.example.yoxjames.coldsnap.service.plant.PlantServiceImpl;
import com.example.yoxjames.coldsnap.service.weather.WeatherService;
import com.example.yoxjames.coldsnap.service.weather.WeatherServiceImpl;

import java.net.URL;

import javax.inject.Provider;
import javax.inject.Singleton;

import dagger.Lazy;
import dagger.Module;
import dagger.Provides;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

@Module
class ColdSnapApplicationModule
{
    @Provides
    @Singleton
    static WeatherService provideWeatherService(WeatherDataDAO dbService, HTTPWeatherService httpWeatherService, ColdSnapDBHelper dbHelper, WeatherLocationService weatherLocationService)
    {
        return new WeatherServiceImpl(dbService, httpWeatherService, dbHelper, weatherLocationService);
    }

    @Provides
    static HTTPWeatherService provideHTTPForecastService(WundergroundURLFactory urlFactory, SharedPreferences sharedPreferences)
    {
        return new HTTPWeatherServiceWUImpl(urlFactory, sharedPreferences);
    }

    @Provides
    static WundergroundURLFactory provideWundergroundURLFactory()
    {
        return new WundergroundURLFactory()
        {
            @Override
            public URL create(WeatherLocation weatherLocation)
            {
                return HTTPWeatherServiceWUImpl.getAbsoluteUrl(weatherLocation.getZipCode());
            }
        };
    }

    @Provides
    static WeatherLocationService provideWeatherLocationService(SharedPreferences sharedPreferences)
    {
        return new WeatherLocationServicePreferenceImpl(sharedPreferences);
    }

    @Provides
    static SharedPreferences provideSharedPreferences(Context context)
    {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Provides
    @Singleton
    static PlantService providePlantService(PlantDAO dbService, ColdSnapDBHelper dbHelper)
    {
        return new PlantServiceImpl(dbService, dbHelper);
    }
    @Provides
    @Singleton
    static WeatherDataDAO provideWeatherDataDAO(Provider<Lazy<ContentValues>> lazyProvider, SharedPreferences sharedPreferences, Provider<Lazy<ForecastDayCursorWrapper.Factory>> cursorWrapperFactory)
    {
        return new WeatherDataDAOSQLiteImpl(lazyProvider, cursorWrapperFactory, sharedPreferences);
    }

    @Provides
    @Singleton
    static PlantDAO providePlantDAO(Provider<Lazy<ContentValues>> lazyProvider, Provider<Lazy<PlantCursorWrapper.Factory>> plantCursorWrapperFactory)
    {
        return new PlantDAOSQLiteImpl(lazyProvider, plantCursorWrapperFactory);
    }

    @Provides
    static PlantCursorWrapper.Factory provideplantCursorWrapperFactory()
    {
        return new PlantCursorWrapper.Factory()
        {
            @Override
            public PlantCursorWrapper create(Cursor cursor)
            {
                return new PlantCursorWrapper(cursor);
            }
        };
    }

    @Provides
    @Singleton
    static ColdSnapDBHelper provideConditionsDBHelper(Context context)
    {
        return new ColdSnapDBHelper(context);
    }

    @Provides
    static ContentValues provideContentValues()
    {
        return new ContentValues();
    }

    @Provides
    static ForecastDayCursorWrapper.Factory provideForecastDayCursorWrapperFactory()
    {
        return new ForecastDayCursorWrapper.Factory()
        {
            @Override
            public ForecastDayCursorWrapper create(Cursor cursor)
            {
                return new ForecastDayCursorWrapper(cursor);
            }
        };
    }

    @Provides
    static HTTPGeolocationService provideHTTPGeolocationService(GoogleLocationURLFactory factory)
    {
        return new HTTPGeolocationServiceGoogleImpl(factory);
    }

    @Provides
    static GoogleLocationURLFactory provideGoogleLocationURLFactory()
    {
        return new GoogleLocationURLFactory()
        {
            @Override
            public URL create(final double lat, final double lon)
            {
                return HTTPGeolocationServiceGoogleImpl.getAbsoluteUrl(lat, lon);
            }
        };
    }

    @Provides
    @Singleton
    static Single<WeatherData> provideWeatherDataObservable(final WeatherService weatherService)
    {
        return Single.create(new SingleOnSubscribe<WeatherData>()
        {
            @Override
            public void subscribe(@NonNull SingleEmitter<WeatherData> e) throws Exception
            {
                e.onSuccess(weatherService.getCurrentForecastData());
            }
        })
                .subscribeOn(Schedulers.io())
                .cache()
                .observeOn(AndroidSchedulers.mainThread());
    }


    @Provides
    @Singleton
    static Subject<SimpleWeatherLocation> provideWeatherLocationSubject()
    {
        return PublishSubject.create();
    }

    @Provides
    @Singleton
    static Observable<WeatherLocation> provideWeatherLocationObservable(Subject<SimpleWeatherLocation> simpleWeatherLocationObservable, final HTTPGeolocationService httpGeolocationService)
    {
        return simpleWeatherLocationObservable
                .serialize()
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<SimpleWeatherLocation, Observable<WeatherLocation>>()
                {
                    @Override
                    public Observable<WeatherLocation> apply(@NonNull SimpleWeatherLocation simpleWeatherLocation) throws Exception
                    {

                        return Observable.just(simpleWeatherLocation).observeOn(Schedulers.io()).flatMap(new Function<SimpleWeatherLocation, ObservableSource<WeatherLocation>>()
                        {
                            @Override
                            public ObservableSource<WeatherLocation> apply(@NonNull SimpleWeatherLocation simpleWeatherLocation) throws Exception
                            {
                                try
                                {
                                    return Observable.just(httpGeolocationService.getCurrentWeatherLocation(simpleWeatherLocation.getLat(), simpleWeatherLocation.getLon()));
                                }
                                catch (Exception e)
                                {
                                    return Observable.never();
                                }
                            }
                        });
                    }
                }).cache();
    }
}
