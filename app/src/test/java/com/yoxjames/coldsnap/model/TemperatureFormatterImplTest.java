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

package com.yoxjames.coldsnap.model;

import com.yoxjames.coldsnap.prefs.CSPreferences;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static com.yoxjames.coldsnap.service.preferences.CSPreferencesService.CELSIUS;
import static com.yoxjames.coldsnap.service.preferences.CSPreferencesService.FAHRENHEIT;
import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TemperatureFormatterImplTest
{
    private @Mock CSPreferences csPreferences;

    @Test
    public void testFahrenheitSame()
    {
        Temperature temperature = Temperature.fromFahrenheit(32);
        TemperatureFormatter temperatureFormatter = new TemperatureFormatterImpl(csPreferences);
        when(csPreferences.getTemperatureFormat()).thenReturn(FAHRENHEIT);
        assertEquals(temperatureFormatter.format(temperature), "32°F");
    }

    @Test
    public void testFahrenheitTempAsC()
    {
        Temperature temperature = Temperature.fromFahrenheit(32);
        TemperatureFormatter temperatureFormatter = new TemperatureFormatterImpl(csPreferences);
        when(csPreferences.getTemperatureFormat()).thenReturn(CELSIUS);
        assertEquals(temperatureFormatter.format(temperature), "0°C");
    }

    @Test
    public void testCelsiusSame()
    {
        Temperature temperature = Temperature.fromCelsius(0);
        TemperatureFormatter temperatureFormatter = new TemperatureFormatterImpl(csPreferences);
        when(csPreferences.getTemperatureFormat()).thenReturn(CELSIUS);
        assertEquals(temperatureFormatter.format(temperature), "0°C");
    }

    @Test
    public void testCelsiusAsF()
    {
        Temperature temperature = Temperature.fromCelsius(0);
        TemperatureFormatter temperatureFormatter = new TemperatureFormatterImpl(csPreferences);
        when(csPreferences.getTemperatureFormat()).thenReturn(FAHRENHEIT);
        assertEquals(temperatureFormatter.format(temperature), "32°F");
    }
}
