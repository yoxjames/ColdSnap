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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WeatherServiceImplTest
{
    private @Mock WeatherDataDAO weatherDataDAO;
    private @Mock HTTPWeatherService httpWeatherService;
    private @Mock ColdSnapDBHelper coldSnapDBHelper;
    private @Mock SQLiteDatabase sqLiteDatabase;
    private @Mock WeatherData cachedWeatherData;
    private @Mock WeatherData httpWeatherData;
    private @Mock SharedPreferences sharedPreferences;

    private static final String zipCode = "55555";

    /**
     * Ensure that the mock cached data and mock http data are returned when the corresponding services
     * are called. This should be effective for every test.
     *
     * Also ensure that the mock SQLiteDatabase is always corectly returned.
     */
    @Before
    public void setup() throws WeatherDataNotFoundException
    {
        when(weatherDataDAO.getWeatherData(sqLiteDatabase)).thenReturn(cachedWeatherData);
        when(httpWeatherService.getWeatherData()).thenReturn(httpWeatherData);

        when(coldSnapDBHelper.getReadableDatabase()).thenReturn(sqLiteDatabase);
        when(coldSnapDBHelper.getWritableDatabase()).thenReturn(sqLiteDatabase);

        when(sharedPreferences.getString(CSPreferencesFragment.ZIPCODE, "64105")).thenReturn(zipCode);
    }

    /**
     * Ensure that when our cached data is stale that we go and get the http weather data.
     */
    @Test
    public void testGetWeatherDataCached() throws WeatherDataNotFoundException
    {
        when(cachedWeatherData.isStale()).thenReturn(false);
        when(cachedWeatherData.getZipCode()).thenReturn(zipCode);

        WeatherServiceImpl weatherService = new WeatherServiceImpl(weatherDataDAO, httpWeatherService, coldSnapDBHelper, sharedPreferences);
        final WeatherData weatherData = weatherService.getCurrentForecastData();
        assertEquals(cachedWeatherData, weatherData);
    }

    /**
     * Test that when we have stale WeatherData in the database that we get http data and delete and save the new data to the DB.
     */
    @Test
    public void testGetWeatherDataHTTP() throws WeatherDataNotFoundException
    {
        when(cachedWeatherData.isStale()).thenReturn(true);
        when(cachedWeatherData.getZipCode()).thenReturn(zipCode);

        WeatherServiceImpl weatherService = new WeatherServiceImpl(weatherDataDAO, httpWeatherService, coldSnapDBHelper, sharedPreferences);
        final WeatherData weatherData = weatherService.getCurrentForecastData();
        assertEquals(httpWeatherData, weatherData);
        verify(weatherDataDAO, times(1)).deleteWeatherData(sqLiteDatabase);
        verify(weatherDataDAO, times(1)).saveWeatherData(sqLiteDatabase, httpWeatherData);
    }

    /**
     * Test that when there is no data in the DB (like if you ran the app for the first time) that we correctly get http data
     * and then save that to the database.
     */
    @Test
    public void testNoDataInDB() throws WeatherDataNotFoundException
    {
        when(weatherDataDAO.getWeatherData(sqLiteDatabase)).thenThrow(WeatherDataNotFoundException.class);

        WeatherServiceImpl weatherService = new WeatherServiceImpl(weatherDataDAO, httpWeatherService, coldSnapDBHelper, sharedPreferences);
        final WeatherData weatherData = weatherService.getCurrentForecastData();
        assertEquals(httpWeatherData, weatherData);
        verify(weatherDataDAO, times(0)).deleteWeatherData(sqLiteDatabase);
        verify(weatherDataDAO, times(1)).saveWeatherData(sqLiteDatabase, httpWeatherData);
    }

    /**
     * Test that when our cached data is stale but we cannt get http data we throw an exception up the stack.
     */
    @Test(expected = WeatherDataNotFoundException.class)
    public void testCachedDataNoHTTP() throws WeatherDataNotFoundException
    {

        when(httpWeatherService.getWeatherData()).thenThrow(WeatherDataNotFoundException.class);
        when(cachedWeatherData.isStale()).thenReturn(true);

        WeatherServiceImpl weatherService = new WeatherServiceImpl(weatherDataDAO, httpWeatherService, coldSnapDBHelper, sharedPreferences);
        weatherService.getCurrentForecastData();
    }

    /**
     * Test that when we have no data in the db and we cannot get data via http that we throw an exception.
     */
    @Test(expected = WeatherDataNotFoundException.class)
    public void testNoDBDataNoHTTPData() throws WeatherDataNotFoundException
    {

        when(httpWeatherService.getWeatherData()).thenThrow(WeatherDataNotFoundException.class);
        when(weatherDataDAO.getWeatherData(sqLiteDatabase)).thenThrow(WeatherDataNotFoundException.class);

        WeatherServiceImpl weatherService = new WeatherServiceImpl(weatherDataDAO, httpWeatherService, coldSnapDBHelper, sharedPreferences);
        weatherService.getCurrentForecastData();
    }
}
