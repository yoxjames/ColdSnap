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

import android.content.SharedPreferences;

import com.example.yoxjames.coldsnap.ui.CSPreferencesFragment;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TemperatureFormatterImplTest
{
    private @Mock SharedPreferences sharedPreferences;

    @Test
    public void testFahrenheitSame()
    {
        Temperature temperature = Temperature.newTemperatureFromF(32);
        TemperatureFormatter temperatureFormatter = new TemperatureFormatterImpl(sharedPreferences);
        when(sharedPreferences.getString(CSPreferencesFragment.TEMPERATURE_SCALE,"F")).thenReturn("F");
        assertEquals(temperatureFormatter.format(temperature), "32°F");
    }

    @Test
    public void testFahrenheitTempAsC()
    {
        Temperature temperature = Temperature.newTemperatureFromF(32);
        TemperatureFormatter temperatureFormatter = new TemperatureFormatterImpl(sharedPreferences);
        when(sharedPreferences.getString(CSPreferencesFragment.TEMPERATURE_SCALE,"F")).thenReturn("C");
        assertEquals(temperatureFormatter.format(temperature), "0°C");
    }

    @Test
    public void testCelsiusSame()
    {
        Temperature temperature = Temperature.newTemperatureFromC(0);
        TemperatureFormatter temperatureFormatter = new TemperatureFormatterImpl(sharedPreferences);
        when(sharedPreferences.getString(CSPreferencesFragment.TEMPERATURE_SCALE,"F")).thenReturn("C");
        assertEquals(temperatureFormatter.format(temperature), "0°C");
    }

    @Test
    public void testCelsiusAsF()
    {
        Temperature temperature = Temperature.newTemperatureFromC(0);
        TemperatureFormatter temperatureFormatter = new TemperatureFormatterImpl(sharedPreferences);
        when(sharedPreferences.getString(CSPreferencesFragment.TEMPERATURE_SCALE,"F")).thenReturn("F");
        assertEquals(temperatureFormatter.format(temperature), "32°F");
    }
}
