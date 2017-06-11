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

package com.example.yoxjames.coldsnap.ui;

import android.content.SharedPreferences;

import com.example.yoxjames.coldsnap.model.Temperature;
import com.example.yoxjames.coldsnap.model.TemperatureFormatter;
import com.example.yoxjames.coldsnap.model.TemperatureFormatterImpl;
import com.example.yoxjames.coldsnap.ui.controls.TemperaturePickerAdapterImpl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TemperaturePickerAdapterTest
{
    private @Mock SharedPreferences sharedPreferences;
    private TemperatureFormatter temperatureFormatter;

    @Before
    public void setupTest()
    {
        temperatureFormatter = new TemperatureFormatterImpl(sharedPreferences);
    }

    @Test
    public void testMinBoundC()
    {
        when(sharedPreferences.getString(CSPreferencesFragment.TEMPERATURE_SCALE, "F")).thenReturn("C");
        TemperaturePickerAdapterImpl temperaturePickerAdapter = new TemperaturePickerAdapterImpl(sharedPreferences, temperatureFormatter);
        assertEquals("-70°C", temperaturePickerAdapter.format(temperaturePickerAdapter.getMinimumTemperatureValue()));
    }

    @Test
    public void testMaxBoundC()
    {
        when(sharedPreferences.getString(CSPreferencesFragment.TEMPERATURE_SCALE, "F")).thenReturn("C");
        TemperaturePickerAdapterImpl temperaturePickerAdapter = new TemperaturePickerAdapterImpl(sharedPreferences, temperatureFormatter);
        assertEquals("100°C", temperaturePickerAdapter.format(temperaturePickerAdapter.getMaximumTemperatureValue()));
    }

    @Test
    public void testGetValueForTemperatureC()
    {
        when(sharedPreferences.getString(CSPreferencesFragment.TEMPERATURE_SCALE, "F")).thenReturn("C");
        TemperaturePickerAdapterImpl temperaturePickerAdapter = new TemperaturePickerAdapterImpl(sharedPreferences, temperatureFormatter);
        assertEquals("0°C", temperaturePickerAdapter.format(temperaturePickerAdapter.getValueForTemperature(Temperature.newTemperatureFromC(0))));
    }

    @Test
    public void testMinBoundF()
    {
        when(sharedPreferences.getString(CSPreferencesFragment.TEMPERATURE_SCALE, "F")).thenReturn("F");
        TemperaturePickerAdapterImpl temperaturePickerAdapter = new TemperaturePickerAdapterImpl(sharedPreferences, temperatureFormatter);
        assertEquals("-94°F", temperaturePickerAdapter.format(temperaturePickerAdapter.getMinimumTemperatureValue()));
    }

    @Test
    public void testMaxBoundF()
    {
        when(sharedPreferences.getString(CSPreferencesFragment.TEMPERATURE_SCALE, "F")).thenReturn("F");
        TemperaturePickerAdapterImpl temperaturePickerAdapter = new TemperaturePickerAdapterImpl(sharedPreferences, temperatureFormatter);
        assertEquals("212°F", temperaturePickerAdapter.format(temperaturePickerAdapter.getMaximumTemperatureValue()));
    }

    @Test
    public void testGetValueForTemperatureF()
    {
        when(sharedPreferences.getString(CSPreferencesFragment.TEMPERATURE_SCALE, "F")).thenReturn("F");
        TemperaturePickerAdapterImpl temperaturePickerAdapter = new TemperaturePickerAdapterImpl(sharedPreferences, temperatureFormatter);
        assertEquals("32°F", temperaturePickerAdapter.format(temperaturePickerAdapter.getValueForTemperature(Temperature.newTemperatureFromF(32))));
    }

    @Test
    public void testGetTemperatureForValueC()
    {
        when(sharedPreferences.getString(CSPreferencesFragment.TEMPERATURE_SCALE, "F")).thenReturn("C");
        TemperaturePickerAdapterImpl temperaturePickerAdapter = new TemperaturePickerAdapterImpl(sharedPreferences, temperatureFormatter);
        assertEquals("-70°C", temperatureFormatter.format(temperaturePickerAdapter.getTemperatureForValue(0)));
    }

    @Test
    public void testGetTemperatureForValueF()
    {
        when(sharedPreferences.getString(CSPreferencesFragment.TEMPERATURE_SCALE, "F")).thenReturn("F");
        TemperaturePickerAdapterImpl temperaturePickerAdapter = new TemperaturePickerAdapterImpl(sharedPreferences, temperatureFormatter);
        assertEquals("-94°F", temperatureFormatter.format(temperaturePickerAdapter.getTemperatureForValue(0)));
    }
}
