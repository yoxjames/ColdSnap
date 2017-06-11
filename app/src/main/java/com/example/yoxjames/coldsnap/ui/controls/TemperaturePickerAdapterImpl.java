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

package com.example.yoxjames.coldsnap.ui.controls;

import android.content.SharedPreferences;
import android.widget.NumberPicker;

import com.example.yoxjames.coldsnap.model.Temperature;
import com.example.yoxjames.coldsnap.model.TemperatureFormatter;
import com.example.yoxjames.coldsnap.ui.CSPreferencesFragment;

import javax.inject.Inject;

public class TemperaturePickerAdapterImpl implements NumberPicker.Formatter, TemperaturePickerAdapter
{
    private final SharedPreferences sharedPreferences;
    private final TemperatureFormatter temperatureFormatter;

    private static Temperature MINIMUM_TEMPERATURE = Temperature.newTemperatureFromC(-70);
    private static Temperature MAXIMUM_TEMPERATURE = Temperature.newTemperatureFromC(100);

    @Inject
    public TemperaturePickerAdapterImpl(SharedPreferences sharedPreferences, TemperatureFormatter temperatureFormatter)
    {
        this.sharedPreferences = sharedPreferences;
        this.temperatureFormatter = temperatureFormatter;
    }

    @Override
    public String format(int value)
    {
        if (sharedPreferences.getString(CSPreferencesFragment.TEMPERATURE_SCALE, "F").equals("F"))
            return temperatureFormatter.format(Temperature.newTemperatureFromF(value + offset()));
        else if (sharedPreferences.getString(CSPreferencesFragment.TEMPERATURE_SCALE, "F").equals("C"))
            return temperatureFormatter.format(Temperature.newTemperatureFromC(value + offset()));
        else
            throw new IllegalStateException("Invalid Temperature Preference: " +
                    sharedPreferences.getString(CSPreferencesFragment.TEMPERATURE_SCALE, "F"));
    }

    private int offset()
    {
        return getPreferenceValue(MINIMUM_TEMPERATURE);
    }



    @Override
    public int getMinimumTemperatureValue()
    {
        return 0;
    }

    @Override
    public int getMaximumTemperatureValue()
    {
        return getPreferenceValue(MAXIMUM_TEMPERATURE) - offset();
    }

    @Override
    public int getValueForTemperature(Temperature temperature)
    {
        return getPreferenceValue(temperature) - offset();
    }

    @Override
    public Temperature getTemperatureForValue(int value)
    {
        if (sharedPreferences.getString(CSPreferencesFragment.TEMPERATURE_SCALE, "F").equals("F"))
            return Temperature.newTemperatureFromF(value + offset());
        else if (sharedPreferences.getString(CSPreferencesFragment.TEMPERATURE_SCALE, "F").equals("C"))
            return Temperature.newTemperatureFromC(value + offset());
        else
            throw new IllegalStateException("Invalid Temperature Preference: " +
                    sharedPreferences.getString(CSPreferencesFragment.TEMPERATURE_SCALE, "F"));
    }

    private int getPreferenceValue(Temperature temperature)
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
    public NumberPicker.Formatter getFormatter()
    {
        return this;
    }
}
