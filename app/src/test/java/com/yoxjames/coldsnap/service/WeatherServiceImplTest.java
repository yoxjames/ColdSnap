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

package com.yoxjames.coldsnap.service;

import android.database.sqlite.SQLiteDatabase;

import com.yoxjames.coldsnap.db.ColdSnapDBHelper;
import com.yoxjames.coldsnap.db.WeatherDataDAO;
import com.yoxjames.coldsnap.http.HTTPWeatherService;
import com.yoxjames.coldsnap.mocks.WeatherDataMockFactory;
import com.yoxjames.coldsnap.mocks.WeatherLocationMockFactory;
import com.yoxjames.coldsnap.model.WeatherData;
import com.yoxjames.coldsnap.model.WeatherLocation;
import com.yoxjames.coldsnap.service.location.WeatherLocationService;
import com.yoxjames.coldsnap.service.weather.WeatherService;
import com.yoxjames.coldsnap.service.weather.WeatherServiceImpl;
import com.yoxjames.coldsnap.utils.RxColdSnap;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.observers.TestObserver;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WeatherServiceImplTest
{
    private @Mock WeatherDataDAO weatherDataDAO;
    private @Mock HTTPWeatherService httpWeatherService;
    private @Mock ColdSnapDBHelper coldSnapDBHelper;
    private @Mock WeatherLocationService weatherLocationService;
    private @Mock SQLiteDatabase database;

    @BeforeClass
    public static void setupTests()
    {
        RxColdSnap.forceSynchronous();
    }

    @Test
    public void testStaleDBReplaceWithHTTP()
    {
        final WeatherLocation kansasCity = WeatherLocationMockFactory.kansasCity();
        final WeatherData weatherDataStale = WeatherDataMockFactory.getStaleWeatherData(kansasCity);
        final WeatherData weatherDataValid = WeatherDataMockFactory.getBasicWeatherData(kansasCity);

        when(coldSnapDBHelper.getWritableDatabase()).thenReturn(database);
        when(weatherLocationService.readWeatherLocation()).thenReturn(Single.just(kansasCity));
        when(weatherDataDAO.getWeatherData(database, kansasCity)).thenReturn(Single.just(weatherDataStale));
        when(httpWeatherService.getWeatherData(kansasCity)).thenReturn(Single.just(weatherDataValid));
        when(weatherDataDAO.deleteWeatherData(database)).thenReturn(Completable.complete());
        when(weatherDataDAO.saveWeatherData(database, weatherDataValid)).thenReturn(Completable.complete());

        WeatherService weatherService = new WeatherServiceImpl(weatherDataDAO, httpWeatherService, coldSnapDBHelper, weatherLocationService);

        final TestObserver<WeatherData> weatherDataSingle = weatherService.getCurrentForecastData().test();

        weatherDataSingle.assertResult(weatherDataValid);
        verify(weatherDataDAO, times(1)).deleteWeatherData(database);
        verify(weatherDataDAO, times(1)).saveWeatherData(database, weatherDataValid);


    }

    @Test
    public void testValidDBNoHTTP()
    {
        final WeatherLocation kansasCity = WeatherLocationMockFactory.kansasCity();
        final WeatherData weatherDataValid = WeatherDataMockFactory.getBasicWeatherData(kansasCity);

        when(coldSnapDBHelper.getWritableDatabase()).thenReturn(database);
        when(weatherLocationService.readWeatherLocation()).thenReturn(Single.just(kansasCity));
        when(weatherDataDAO.getWeatherData(database, kansasCity)).thenReturn(Single.just(weatherDataValid));
        when(httpWeatherService.getWeatherData(kansasCity)).thenReturn(Single.just(weatherDataValid));
        when(weatherDataDAO.deleteWeatherData(database)).thenReturn(Completable.complete());
        when(weatherDataDAO.saveWeatherData(database, weatherDataValid)).thenReturn(Completable.complete());

        WeatherService weatherService = new WeatherServiceImpl(weatherDataDAO, httpWeatherService, coldSnapDBHelper, weatherLocationService);

        final TestObserver<WeatherData> weatherDataSingle = weatherService.getCurrentForecastData().test();

        weatherDataSingle.assertResult(weatherDataValid);

        verify(httpWeatherService, times(0)).getWeatherData(any(WeatherLocation.class));
        verify(weatherDataDAO, times(0)).deleteWeatherData(database);
        verify(weatherDataDAO, times(0)).saveWeatherData(database, weatherDataValid);
    }
}
