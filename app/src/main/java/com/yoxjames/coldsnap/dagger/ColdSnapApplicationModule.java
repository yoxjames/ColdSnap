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

import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.yoxjames.coldsnap.db.ColdSnapDatabase;
import com.yoxjames.coldsnap.db.PlantDAO;
import com.yoxjames.coldsnap.db.PlantDAOImpl;
import com.yoxjames.coldsnap.db.PlantImageDAO;
import com.yoxjames.coldsnap.db.PlantImageDAOImpl;
import com.yoxjames.coldsnap.db.WeatherDataDAO;
import com.yoxjames.coldsnap.db.WeatherDataDAOImpl;
import com.yoxjames.coldsnap.db.image.PlantImageRowDAO;
import com.yoxjames.coldsnap.db.plant.PlantRowDAO;
import com.yoxjames.coldsnap.db.weather.ForecastHourRowDAO;
import com.yoxjames.coldsnap.http.HTTPForecastService;
import com.yoxjames.coldsnap.http.openweathermap.HTTPForecastServiceOWMImpl;
import com.yoxjames.coldsnap.http.openweathermap.OpenWeatherMapHTTPService;
import com.yoxjames.coldsnap.model.TemperatureFormatter;
import com.yoxjames.coldsnap.model.TemperatureFormatterImpl;
import com.yoxjames.coldsnap.model.TemperatureValueAdapter;
import com.yoxjames.coldsnap.model.TemperatureValueAdapterImpl;
import com.yoxjames.coldsnap.prefs.CSPreferences;
import com.yoxjames.coldsnap.prefs.CSSharedPreferencesImpl;
import com.yoxjames.coldsnap.service.image.ImageService;
import com.yoxjames.coldsnap.service.image.ImageServiceImpl;
import com.yoxjames.coldsnap.service.image.file.ImageFileService;
import com.yoxjames.coldsnap.service.image.file.ImageFileServiceImpl;
import com.yoxjames.coldsnap.service.location.WeatherLocationService;
import com.yoxjames.coldsnap.service.location.WeatherLocationServicePreferenceImpl;
import com.yoxjames.coldsnap.service.plant.PlantService;
import com.yoxjames.coldsnap.service.plant.PlantServiceImpl;
import com.yoxjames.coldsnap.service.weather.WeatherService;
import com.yoxjames.coldsnap.service.weather.WeatherServiceImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.Reusable;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
class ColdSnapApplicationModule
{
    @Provides
    @Singleton
    static ColdSnapDatabase provideColdSnapDatabase(Context context)
    {
        return Room.databaseBuilder(context, ColdSnapDatabase.class, "coldsnap-db")
                .fallbackToDestructiveMigration()
                .build();
    }

    @Provides
    static PlantRowDAO providePlantRowDAO(ColdSnapDatabase database)
    {
        return database.plantRowDAO();
    }

    @Provides
    static PlantImageRowDAO providePlantImageRowDAO(ColdSnapDatabase database)
    {
        return database.plantImageRowDAO();
    }

    @Provides
    static ForecastHourRowDAO provideForecastHourRowDAO(ColdSnapDatabase database)
    {
        return database.forecastHourRowDAO();
    }

    @Provides
    static WeatherDataDAO provideWeatherDataDAO(ForecastHourRowDAO forecastHourRowDAO)
    {
        return new WeatherDataDAOImpl(forecastHourRowDAO);
    }

    @Provides
    static PlantImageDAO providePlantImageDAO(PlantImageRowDAO plantImageRowDAO)
    {
        return new PlantImageDAOImpl(plantImageRowDAO);
    }

    @Provides
    static WeatherLocationService provideWeatherLocationService(CSPreferences csPreferences)
    {
        return new WeatherLocationServicePreferenceImpl(csPreferences);
    }

    @Provides
    static SharedPreferences provideSharedPreferences(Context context)
    {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Provides
    static PlantDAO providePlantDAO(PlantRowDAO plantRowDAO)
    {
        return new PlantDAOImpl(plantRowDAO);
    }

    @Provides
    static TemperatureFormatter provideTemperatureFormatter(CSPreferences csPreferences)
    {
        return new TemperatureFormatterImpl(csPreferences);
    }

    @Provides
    @Reusable
    static TemperatureValueAdapter provideTemperatureValueAdapter(CSPreferences csPreferences)
    {
        return new TemperatureValueAdapterImpl(csPreferences);
    }

    @Provides
    static PlantService provideAsyncPlantService(PlantDAO plantDAO)
    {
        return new PlantServiceImpl(plantDAO);
    }

    @Provides
    static WeatherService provideWeatherService(
        WeatherDataDAO weatherDataDAO,
        HTTPForecastService httpForecastService,
        WeatherLocationService weatherLocationService,
        CSPreferences csPreferences)
    {
        return new WeatherServiceImpl(weatherDataDAO, httpForecastService, weatherLocationService, csPreferences);
    }

    @Provides
    @Singleton
    static HTTPForecastService provideHTTPForecastService(OpenWeatherMapHTTPService owmService, CSPreferences csPreferences)
    {
        return new HTTPForecastServiceOWMImpl(owmService, csPreferences);
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
    static ImageService provideImageService(PlantImageDAO plantImageDAO, ImageFileService imageFileService, Context context)
    {
        return new ImageServiceImpl(plantImageDAO, imageFileService, context);
    }

    @Provides
    @Singleton
    static CSPreferences provideCSPreferences(SharedPreferences sharedPreferences)
    {
        return new CSSharedPreferencesImpl(sharedPreferences);
    }

    @Provides
    static ImageFileService provideImageFileService(Context context)
    {
        return new ImageFileServiceImpl(context);
    }
}

