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
import com.yoxjames.coldsnap.service.preferences.CSPreferencesService.TemperatureFormat;

import javax.inject.Inject;

import dagger.Reusable;

import static com.yoxjames.coldsnap.service.preferences.CSPreferencesService.CELSIUS;
import static com.yoxjames.coldsnap.service.preferences.CSPreferencesService.FAHRENHEIT;
import static com.yoxjames.coldsnap.service.preferences.CSPreferencesService.KELVIN;

/**
 * Implementation of TemperatureFormatter that uses Android's SharedPreferences to determine
 * whether to show {@link Temperature} objects in either F or C.
 */
@Reusable
public class TemperatureFormatterImpl implements TemperatureFormatter
{
    private final CSPreferences csPreferences;

    @Inject
    public TemperatureFormatterImpl(CSPreferences csPreferences)
    {
        this.csPreferences = csPreferences;
    }

    @Override
    public String format(Temperature temperature)
    {
        switch (csPreferences.getTemperatureFormat())
        {
            case FAHRENHEIT:
                return Integer.toString(Temperature.asFahrenheitDegrees(temperature)) + "°F";
            case CELSIUS:
                return Integer.toString(Temperature.asCelsiusDegrees(temperature)) + "°C";
            case KELVIN:
                return Double.toString(temperature.getDegreesKelvin()) + "K";
            default:
                throw new IllegalStateException("Invalikd temperature format");
        }
    }

    @Override
    public String formatFuzz(double fuzzKelvins)
    {
        switch (csPreferences.getTemperatureFormat())
        {
            case FAHRENHEIT:
                return Integer.toString(Temperature.asFahrenheitValue(fuzzKelvins)) + "°F";
            case CELSIUS:
                return Integer.toString(Temperature.asCelsiusValue(fuzzKelvins)) + "°C";
            case KELVIN:
                return Double.toString(fuzzKelvins) + "K";
            default:
                throw new IllegalStateException("Invalid temperature format");
        }
    }

    @Override
    public String formatTemperatureScale(@TemperatureFormat int temperatureScale)
    {
        switch (temperatureScale)
        {
            case KELVIN:
                return "K";
            case FAHRENHEIT:
                return "°F";
            case CELSIUS:
                return "°C";
            default:
                return "";
        }
    }
}
