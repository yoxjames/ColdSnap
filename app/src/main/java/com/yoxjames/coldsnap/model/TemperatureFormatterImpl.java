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

import dagger.Reusable;

/**
 * Implementation of TemperatureFormatter that uses Android's SharedPreferences to determine
 * whether to show {@link Temperature} objects in either F or C.
 */
@Reusable
public class TemperatureFormatterImpl implements TemperatureFormatter
{
    /*
     * Android preferences.
     */
    private final SharedPreferences sharedPreferences;

    @Inject
    public TemperatureFormatterImpl(SharedPreferences sharedPreferences)
    {
        this.sharedPreferences = sharedPreferences;
    }

    @Override
    public String format(Temperature temperature)
    {
        if (sharedPreferences.getString(CSPreferencesFragment.TEMPERATURE_SCALE, "F").equals("F"))
            return Integer.toString(Temperature.asFahrenheitDegrees(temperature)) + "°F";
        else
            return Integer.toString(Temperature.asCelsiusDegrees(temperature)) + "°C";
    }

    @Override
    public String formatFuzz(double fuzzKelvins)
    {
        if (sharedPreferences.getString(CSPreferencesFragment.TEMPERATURE_SCALE, "F").equals("F"))
            return Integer.toString(Temperature.asFahrenheitValue(fuzzKelvins)) + "°F";
        else if (sharedPreferences.getString(CSPreferencesFragment.TEMPERATURE_SCALE, "F").equals("C"))
            return Integer.toString(Temperature.asCelsiusValue(fuzzKelvins)) + "°C";
        else
            throw new IllegalStateException("Invalid sharedPreferences");
    }
}
