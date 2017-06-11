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

package com.example.yoxjames.coldsnap.service;

import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;

import com.example.yoxjames.coldsnap.db.ColdSnapDBHelper;
import com.example.yoxjames.coldsnap.db.WeatherDataDAO;
import com.example.yoxjames.coldsnap.http.HTTPWeatherService;
import com.example.yoxjames.coldsnap.model.WeatherData;
import com.example.yoxjames.coldsnap.model.WeatherDataNotFoundException;
import com.example.yoxjames.coldsnap.ui.CSPreferencesFragment;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Implementation of WeatherService
 */
@Singleton
public class WeatherServiceImpl implements WeatherService
{
    private final WeatherDataDAO dbService;
    private final HTTPWeatherService httpService;
    private final ColdSnapDBHelper dbHelper;
    private final SharedPreferences sharedPreferences;

    /**
     * Constructor for WeatherServiceImpl
     *
     * @param dbService The WeatherDAO DB Service implementation.
     * @param httpService HTTP Service Implementation for fetching WeatherData from the Internet
     * @param dbHelper Database helper for ColdSnap
     * @param sharedPreferences SharedPreference service, used to obtain zipcode.
     */
    @Inject
    public WeatherServiceImpl(WeatherDataDAO dbService, HTTPWeatherService httpService, ColdSnapDBHelper dbHelper, SharedPreferences sharedPreferences)
    {
        this.dbService = dbService;
        this.httpService = httpService;
        this.dbHelper = dbHelper;
        this.sharedPreferences = sharedPreferences;
    }

    @Override
    public synchronized WeatherData getCurrentForecastData() throws WeatherDataNotFoundException
    {
        final WeatherData cachedWeatherData;
        String zipCode = sharedPreferences.getString(CSPreferencesFragment.ZIPCODE, "64105");

        try
        {
            cachedWeatherData = dbService.getWeatherData(dbHelper.getReadableDatabase());
            if (cachedWeatherData.isStale() || !cachedWeatherData.getZipCode().equals(zipCode))
            {
                final SQLiteDatabase database = dbHelper.getWritableDatabase();
                // Clear out any weather data in the DB as it is stale
                dbService.deleteWeatherData(database);

                // Fetch forecast data via HTTP
                final WeatherData currentWeatherData = httpService.getWeatherData();
                dbService.saveWeatherData(database, currentWeatherData);

                return currentWeatherData;
            }
            return cachedWeatherData;
        }
        catch (WeatherDataNotFoundException e)
        {
            final WeatherData currentWeatherData = httpService.getWeatherData();
            final SQLiteDatabase database = dbHelper.getWritableDatabase();
            dbService.saveWeatherData(database, currentWeatherData);

            return currentWeatherData;
        }
    }
}
