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

import javax.inject.Inject;

import static com.yoxjames.coldsnap.service.preferences.CSPreferencesService.CELSIUS;
import static com.yoxjames.coldsnap.service.preferences.CSPreferencesService.FAHRENHEIT;

/**
 * Created by yoxjames on 8/13/17.
 */

public class TemperatureValueAdapterImpl implements TemperatureValueAdapter
{
    private final CSPreferences csPreferences;

    @Inject
    public TemperatureValueAdapterImpl(CSPreferences csPreferences)
    {
        this.csPreferences = csPreferences;
    }

    @Override
    public int getValue(Temperature temperature)
    {
        if (csPreferences.getTemperatureFormat() == FAHRENHEIT)
            return Temperature.asFahrenheitDegrees(temperature);
        else if (csPreferences.getTemperatureFormat() == CELSIUS)
            return Temperature.asCelsiusDegrees(temperature);
        else
            throw new IllegalStateException("Invalid Temperature Preference");
    }

    @Override
    public int getValue(double kelvins)
    {
        return getValue(new Temperature(kelvins));
    }

    @Override
    public int getAbsoluteValue(double kelvins)
    {
        if (csPreferences.getTemperatureFormat() == FAHRENHEIT)
            return Temperature.asFahrenheitValue(kelvins);
        else if (csPreferences.getTemperatureFormat() == CELSIUS)
            return Temperature.asCelsiusValue(kelvins);
        else
            throw new IllegalStateException("Invalid Temperature Preference");
    }

    @Override
    public int getAbsoluteValue(Temperature temperature)
    {
        if (csPreferences.getTemperatureFormat() == FAHRENHEIT)
            return Temperature.asFahrenheitValue(temperature.getDegreesKelvin());
        else if (csPreferences.getTemperatureFormat() == CELSIUS)
            return Temperature.asCelsiusValue(temperature.getDegreesKelvin());
        else
            throw new IllegalStateException("Invalid Temperature Preference");
    }

    @Override
    public double getKelvinTemperature(int value)
    {
        return getTemperature(value).getDegreesKelvin();
    }

    @Override
    public double getKelvinAbsoluteTemperature(int value)
    {
        if (csPreferences.getTemperatureFormat() == FAHRENHEIT)
            return (double) value * 5.0 / 9.0;
        else if (csPreferences.getTemperatureFormat() == CELSIUS)
            return (double) value;
        else
            throw new IllegalStateException("Invalid Temperature Preference");
    }

    @Override
    public Temperature getTemperature(int value)
    {
        if (csPreferences.getTemperatureFormat() == FAHRENHEIT)
            return Temperature.newTemperatureFromF(value);
        else if (csPreferences.getTemperatureFormat() == CELSIUS)
            return Temperature.newTemperatureFromC(value);
        else
            throw new IllegalStateException("Invalid Temperature Preference");
    }
}
