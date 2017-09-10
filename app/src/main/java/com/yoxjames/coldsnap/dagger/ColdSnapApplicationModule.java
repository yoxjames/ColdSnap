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

package com.yoxjames.coldsnap.dagger;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;


import com.yoxjames.coldsnap.db.ColdSnapDBHelper;
import com.yoxjames.coldsnap.db.ForecastDayCursorWrapper;
import com.yoxjames.coldsnap.db.PlantCursorWrapper;
import com.yoxjames.coldsnap.db.PlantDAO;
import com.yoxjames.coldsnap.db.PlantDAOSQLiteImpl;
import com.yoxjames.coldsnap.db.WeatherDataDAO;
import com.yoxjames.coldsnap.db.WeatherDataDAOSQLiteImpl;
import com.yoxjames.coldsnap.http.HTTPGeolocationService;
import com.yoxjames.coldsnap.http.HTTPWeatherService;
import com.yoxjames.coldsnap.http.google.GoogleLocationURLFactory;
import com.yoxjames.coldsnap.http.google.HTTPGeolocationServiceGoogleImpl;
import com.yoxjames.coldsnap.http.wu.HTTPWeatherServiceWUImpl;
import com.yoxjames.coldsnap.http.wu.WundergroundURLFactory;
import com.yoxjames.coldsnap.model.TemperatureFormatter;
import com.yoxjames.coldsnap.model.TemperatureFormatterImpl;
import com.yoxjames.coldsnap.model.TemperatureValueAdapter;
import com.yoxjames.coldsnap.model.TemperatureValueAdapterImpl;
import com.yoxjames.coldsnap.model.WeatherLocation;
import com.yoxjames.coldsnap.service.location.GPSLocationService;
import com.yoxjames.coldsnap.service.location.GPSLocationServiceImpl;
import com.yoxjames.coldsnap.service.location.WeatherLocationService;
import com.yoxjames.coldsnap.service.location.WeatherLocationServicePreferenceImpl;
import com.yoxjames.coldsnap.service.plant.PlantService;
import com.yoxjames.coldsnap.service.plant.PlantServiceImpl;
import com.yoxjames.coldsnap.service.weather.WeatherService;
import com.yoxjames.coldsnap.service.weather.WeatherServiceImpl;

import java.net.URL;

import javax.inject.Provider;
import javax.inject.Singleton;

import dagger.Lazy;
import dagger.Module;
import dagger.Provides;

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
    static TemperatureFormatter provideTemperatureFormatter(SharedPreferences sharedPreferences)
    {
        return new TemperatureFormatterImpl(sharedPreferences);
    }

    @Provides
    @Singleton
    static TemperatureValueAdapter provideTemperatureValueAdapter(SharedPreferences sharedPreferences)
    {
        return new TemperatureValueAdapterImpl(sharedPreferences);
    }

    @Provides
    @Singleton
    static PlantService provideAsyncPlantService(ColdSnapDBHelper coldSnapDBHelper, PlantDAO plantDAO)
    {
        return new PlantServiceImpl(coldSnapDBHelper, plantDAO);
    }

    @Provides
    @Singleton
    static GPSLocationService provideGPSLocationService(Context context, HTTPGeolocationService geolocationService, SharedPreferences sharedPreferences, WeatherLocationService weatherLocationService)
    {
        return new GPSLocationServiceImpl(context, geolocationService, sharedPreferences, weatherLocationService);
    }
}
