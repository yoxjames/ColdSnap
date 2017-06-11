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

package com.example.yoxjames.coldsnap.model;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static junit.framework.Assert.assertTrue;
import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;

public class WeatherDataTest
{
    @Test
    public void testAllAttibutesNotStale()
    {
        final String testDayString = "Apr 16 2017";
        final Temperature testTemp = Temperature.newTemperatureFromF(32);
        final Date testDate = new Date();
        final UUID testUUID = UUID.randomUUID();

        final ForecastDay testForecastDay = new ForecastDay(testDayString , testTemp, testTemp, testDate, testUUID);

        final List<ForecastDay> forecastDays = new ArrayList<>();
        forecastDays.add(testForecastDay);
        final String locationString = "Kansas City, MO";
        final String zipCode = "64105";
        final Date syncDate = new Date();

        final WeatherData data = new WeatherData(forecastDays, locationString, zipCode, syncDate);

        assertEquals(testTemp, data.getTodayLow());
        assertEquals(testTemp, data.getTodayHigh());
        assertEquals(locationString, data.getLocationString());
        assertEquals(zipCode, data.getZipCode());
        assertFalse(data.isStale()); // Ensure this is marked not stale
    }

    @Test
    public void testAllAttributesStale()
    {
        final String testDayString = "Apr 16 2017";
        final Temperature testTemp = Temperature.newTemperatureFromF(32);
        final Date testDate = new Date();
        final UUID testUUID = UUID.randomUUID();

        final ForecastDay testForecastDay = new ForecastDay(testDayString , testTemp, testTemp, testDate, testUUID);

        final List<ForecastDay> forecastDays = new ArrayList<>();
        forecastDays.add(testForecastDay);
        final String locationString = "Kansas City, MO";
        final String zipCode = "64105";
        final Date syncDate = new Date(new Date().getTime() - (2 * 60 * 1001));

        final WeatherData data = new WeatherData(forecastDays, locationString, zipCode, syncDate);
        assertEquals(testTemp, data.getTodayLow());
        assertEquals(testTemp, data.getTodayHigh());
        assertEquals(locationString, data.getLocationString());
        assertEquals(zipCode, data.getZipCode());
        assertTrue(data.isStale()); // Ensure this is marked stale
    }

    @Test(expected = IllegalStateException.class)
    public void testSizeZeroForecastDayException()
    {
        final List<ForecastDay> forecastDays = new ArrayList<>();
        final String locationString = "Kansas City, MO";
        final String zipCode = "64105";
        final Date syncDate = new Date(new Date().getTime() - (2 * 60 * 1001));

        new WeatherData(forecastDays, locationString, zipCode, syncDate);
    }
}
