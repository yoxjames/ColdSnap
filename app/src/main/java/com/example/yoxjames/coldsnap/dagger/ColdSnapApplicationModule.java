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
import android.support.annotation.Nullable;

import com.example.yoxjames.coldsnap.db.ColdSnapDBHelper;
import com.example.yoxjames.coldsnap.db.ForecastDayCursorWrapper;
import com.example.yoxjames.coldsnap.db.PlantCursorWrapper;
import com.example.yoxjames.coldsnap.db.PlantDAO;
import com.example.yoxjames.coldsnap.db.PlantDAOSQLiteImpl;
import com.example.yoxjames.coldsnap.db.WeatherDataDAO;
import com.example.yoxjames.coldsnap.db.WeatherDataDAOSQLiteImpl;
import com.example.yoxjames.coldsnap.http.HTTPWeatherService;
import com.example.yoxjames.coldsnap.http.wu.HTTPWeatherServiceWUImpl;
import com.example.yoxjames.coldsnap.service.PlantService;
import com.example.yoxjames.coldsnap.service.PlantServiceImpl;
import com.example.yoxjames.coldsnap.service.WeatherService;
import com.example.yoxjames.coldsnap.service.WeatherServiceImpl;
import com.example.yoxjames.coldsnap.ui.CSPreferencesFragment;

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
    static WeatherService provideWeatherService(WeatherDataDAO dbService, HTTPWeatherService httpWeatherService, ColdSnapDBHelper dbHelper, SharedPreferences sharedPreferences)
    {
        return new WeatherServiceImpl(dbService, httpWeatherService, dbHelper, sharedPreferences);
    }

    @Provides
    static HTTPWeatherService provideHTTPForecastService(@Nullable Provider<Lazy<URL>> url, SharedPreferences sharedPreferences)
    {
        return new HTTPWeatherServiceWUImpl(url, sharedPreferences);
    }

    @Provides
    @Nullable
    static URL provideURL(SharedPreferences sharedPreferences)
    {

        return HTTPWeatherServiceWUImpl.getAbsoluteUrl(sharedPreferences.getString(CSPreferencesFragment.ZIPCODE, "64105"));
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
}
