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

import android.content.SharedPreferences;

import com.yoxjames.coldsnap.ui.CSPreferencesFragment;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by yoxjames on 8/13/17.
 */

@Singleton
public class TemperatureValueAdapterImpl implements TemperatureValueAdapter
{
    private final SharedPreferences sharedPreferences;

    @Inject
    public TemperatureValueAdapterImpl(SharedPreferences sharedPreferences)
    {
        this.sharedPreferences = sharedPreferences;
    }

    @Override
    public int getValue(Temperature temperature)
    {
        if (sharedPreferences.getString(CSPreferencesFragment.TEMPERATURE_SCALE, "F").equals("F"))
            return Temperature.asFahrenheitDegrees(temperature);
        else if (sharedPreferences.getString(CSPreferencesFragment.TEMPERATURE_SCALE, "F").equals("C"))
            return Temperature.asCelsiusDegrees(temperature);
        else
            throw new IllegalStateException("Invalid Temperature Preference: " +
                    sharedPreferences.getString(CSPreferencesFragment.TEMPERATURE_SCALE, "F"));
    }

    @Override
    public int getValue(double kelvins)
    {
        return getValue(new Temperature(kelvins));
    }

    @Override
    public int getAbsoluteValue(double kelvins)
    {
        if (sharedPreferences.getString(CSPreferencesFragment.TEMPERATURE_SCALE, "F").equals("F"))
            return Temperature.asFahrenheitValue(kelvins);
        else if (sharedPreferences.getString(CSPreferencesFragment.TEMPERATURE_SCALE, "F").equals("C"))
            return Temperature.asCelsiusValue(kelvins);
        else
            throw new IllegalStateException("Invalid Temperature Preference: " +
                    sharedPreferences.getString(CSPreferencesFragment.TEMPERATURE_SCALE, "F"));
    }

    @Override
    public int getAbsoluteValue(Temperature temperature)
    {
        if (sharedPreferences.getString(CSPreferencesFragment.TEMPERATURE_SCALE, "F").equals("F"))
            return Temperature.asFahrenheitValue(temperature.getDegreesKelvin());
        else if (sharedPreferences.getString(CSPreferencesFragment.TEMPERATURE_SCALE, "F").equals("C"))
            return Temperature.asCelsiusValue(temperature.getDegreesKelvin());
        else
            throw new IllegalStateException("Invalid Temperature Preference: " +
                    sharedPreferences.getString(CSPreferencesFragment.TEMPERATURE_SCALE, "F"));
    }

    @Override
    public double getKelvinTemperature(int value)
    {
        return getTemperature(value).getDegreesKelvin();
    }

    @Override
    public double getKelvinAbsoluteTemperature(int value)
    {
        if (sharedPreferences.getString(CSPreferencesFragment.TEMPERATURE_SCALE, "F").equals("F"))
            return (double) value * 5.0 / 9.0;
        else if (sharedPreferences.getString(CSPreferencesFragment.TEMPERATURE_SCALE, "F").equals("C"))
            return (double) value;
        else
            throw new IllegalStateException("Invalid Temperature Preference: " +
                    sharedPreferences.getString(CSPreferencesFragment.TEMPERATURE_SCALE, "F"));
    }

    @Override
    public Temperature getTemperature(int value)
    {
        if (sharedPreferences.getString(CSPreferencesFragment.TEMPERATURE_SCALE, "F").equals("F"))
            return Temperature.newTemperatureFromF(value);
        else if (sharedPreferences.getString(CSPreferencesFragment.TEMPERATURE_SCALE, "F").equals("C"))
            return Temperature.newTemperatureFromC(value);
        else
            throw new IllegalStateException("Invalid Temperature Preference: " +
                    sharedPreferences.getString(CSPreferencesFragment.TEMPERATURE_SCALE, "F"));
    }
}
