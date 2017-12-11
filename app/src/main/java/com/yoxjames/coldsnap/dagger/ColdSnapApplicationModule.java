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
import android.preference.PreferenceManager;

import com.yoxjames.coldsnap.db.ColdSnapDBHelper;
import com.yoxjames.coldsnap.db.PlantDAO;
import com.yoxjames.coldsnap.db.PlantDAOImpl;
import com.yoxjames.coldsnap.db.PlantImageDAOImpl;
import com.yoxjames.coldsnap.db.WeatherDataDAO;
import com.yoxjames.coldsnap.db.WeatherDataDAOSQLiteImpl;
import com.yoxjames.coldsnap.db.image.PlantImageRowCursorWrapper;
import com.yoxjames.coldsnap.db.image.PlantImageRowDAO;
import com.yoxjames.coldsnap.db.image.PlantImageRowDAOSQLiteImpl;
import com.yoxjames.coldsnap.db.plant.PlantRowCursorWrapper;
import com.yoxjames.coldsnap.db.plant.PlantRowDAO;
import com.yoxjames.coldsnap.db.plant.PlantRowDAOSQLiteImpl;
import com.yoxjames.coldsnap.db.weather.ForecastDayRowCursorWrapper;
import com.yoxjames.coldsnap.http.HTTPForecastService;
import com.yoxjames.coldsnap.http.openweathermap.HTTPForecastServiceOWMImpl;
import com.yoxjames.coldsnap.http.openweathermap.OpenWeatherMapHTTPService;
import com.yoxjames.coldsnap.model.TemperatureFormatter;
import com.yoxjames.coldsnap.model.TemperatureFormatterImpl;
import com.yoxjames.coldsnap.model.TemperatureValueAdapter;
import com.yoxjames.coldsnap.model.TemperatureValueAdapterImpl;
import com.yoxjames.coldsnap.service.image.ImageService;
import com.yoxjames.coldsnap.service.location.GPSLocationService;
import com.yoxjames.coldsnap.service.location.GPSLocationServiceImpl;
import com.yoxjames.coldsnap.service.location.WeatherLocationService;
import com.yoxjames.coldsnap.service.location.WeatherLocationServicePreferenceImpl;
import com.yoxjames.coldsnap.service.plant.PlantService;
import com.yoxjames.coldsnap.service.plant.PlantServiceImpl;
import com.yoxjames.coldsnap.service.weather.WeatherService;
import com.yoxjames.coldsnap.service.weather.WeatherServiceImpl;

import javax.inject.Provider;
import javax.inject.Singleton;

import dagger.Lazy;
import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
class ColdSnapApplicationModule
{
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
    static WeatherDataDAO provideWeatherDataDAO(Provider<Lazy<ContentValues>> lazyProvider, SharedPreferences sharedPreferences, Provider<Lazy<ForecastDayRowCursorWrapper.Factory>> cursorWrapperFactory, ColdSnapDBHelper coldSnapDBHelper)
    {
        return new WeatherDataDAOSQLiteImpl(lazyProvider, cursorWrapperFactory, sharedPreferences, coldSnapDBHelper);
    }

    @Provides
    static PlantRowDAO providePlantRowDAO(Provider<Lazy<ContentValues>> lazyProvider, Provider<Lazy<PlantRowCursorWrapper.Factory>> plantCursorWrapperFactory, ColdSnapDBHelper dbHelper)
    {
        return new PlantRowDAOSQLiteImpl(lazyProvider, plantCursorWrapperFactory, dbHelper);
    }

    @Provides
    static PlantDAO providePlantDAO(PlantRowDAO plantRowDAO)
    {
        return new PlantDAOImpl(plantRowDAO);
    }

    @Provides
    static PlantImageRowCursorWrapper.Factory providePlantImageRowCursorWrapperFactory()
    {
        return PlantImageRowCursorWrapper::new;
    }

    @Provides
    static PlantImageRowDAO providePlantImageRowDAO(Provider<Lazy<ContentValues>> lazyProvider, Provider<Lazy<PlantImageRowCursorWrapper.Factory>> factory, ColdSnapDBHelper dbHelper)
    {
        return new PlantImageRowDAOSQLiteImpl(lazyProvider, factory, dbHelper);
    }

    @Provides
    static PlantRowCursorWrapper.Factory provideplantCursorWrapperFactory()
    {
        return PlantRowCursorWrapper::new;
    }

    @Provides
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
    static ForecastDayRowCursorWrapper.Factory provideForecastDayCursorWrapperFactory()
    {
        return ForecastDayRowCursorWrapper::new;
    }

    @Provides
    static TemperatureFormatter provideTemperatureFormatter(SharedPreferences sharedPreferences)
    {
        return new TemperatureFormatterImpl(sharedPreferences);
    }

    @Provides
    static TemperatureValueAdapter provideTemperatureValueAdapter(SharedPreferences sharedPreferences)
    {
        return new TemperatureValueAdapterImpl(sharedPreferences);
    }

    @Provides
    static PlantService provideAsyncPlantService(PlantDAO plantDAO)
    {
        return new PlantServiceImpl(plantDAO);
    }

    @Provides
    static GPSLocationService provideGPSLocationService()
    {
        return new GPSLocationServiceImpl();
    }

    @Provides
    static WeatherService provideWeatherService(WeatherDataDAO weatherDataDAO, HTTPForecastService httpForecastService)
    {
        return new WeatherServiceImpl(weatherDataDAO, httpForecastService);
    }

    @Provides
    @Singleton
    static HTTPForecastService provideHTTPForecastService(OpenWeatherMapHTTPService owmService, SharedPreferences sharedPreferences)
    {
        return new HTTPForecastServiceOWMImpl(owmService, sharedPreferences);
    }

    @Provides
    @Singleton
    static OpenWeatherMapHTTPService provideOWMhttpService()
    {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.openweathermap.org/data/2.5/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();

        return retrofit.create(OpenWeatherMapHTTPService.class);
    }

    @Provides
    @Singleton
    static ImageService provideImageService(PlantImageRowDAO plantImageRowDAO)
    {
        return new PlantImageDAOImpl(plantImageRowDAO);
    }
}

