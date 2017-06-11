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

import java.util.Date;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class ForecastDayTest
{
    @Test
    public void simpleHappyPathTest()
    {
        final String testDayString = "Apr 16 2017";
        final Temperature testTemp = Temperature.newTemperatureFromF(32);
        final Date testDate = new Date();
        final UUID testUUID = UUID.randomUUID();

        final ForecastDay testForecastDay = new ForecastDay(testDayString , testTemp, testTemp, testDate, testUUID);

        assertEquals(testDayString, testForecastDay.toString());
        assertEquals(testTemp, testForecastDay.getHighTemperature());
        assertEquals(testTemp, testForecastDay.getLowTemperature());
        assertEquals(testDate, testForecastDay.getDate());
        assertEquals(testUUID, testForecastDay.getUUID());
    }
}
