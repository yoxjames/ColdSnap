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

import android.content.Context;
import android.preference.EditTextPreference;
import android.util.AttributeSet;

import com.example.yoxjames.coldsnap.model.Temperature;
import com.example.yoxjames.coldsnap.ui.CSPreferencesFragment;

/**
 * Special EditTextPreference that can be used for Temperature amounts. Automatically converts
 * to the desired scale so that temperatures can be inputted as whatever scale the user is using and
 * is auto converted to Kelvin.
 */
public class TemperatureEditTextPreference extends EditTextPreference
{
    private static float getDefaultTempValue()
    {
        return (float) new Temperature(Temperature.WATER_FREEZING_KELVIN).getDegreesKelvin();
    }

    public TemperatureEditTextPreference(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public TemperatureEditTextPreference(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public TemperatureEditTextPreference(Context context)
    {
        super(context);
    }

    @Override
    public String getText()
    {
        return Integer.toString(convertToDisplayDegrees(getPersistedFloat(getDefaultTempValue())));
    }

    @Override
    protected String getPersistedString(String defaultReturnValue)
    {
        return String.valueOf(convertToDisplayDegrees(getPersistedFloat(getDefaultTempValue())));
    }

    @Override
    protected boolean persistString(String value)
    {
        return persistFloat((float) convertToTemperature(Integer.valueOf(value)).getDegreesKelvin());
    }

    private int convertToDisplayDegrees(float kelvinDegrees)
    {
        if (getSharedPreferences().getString(CSPreferencesFragment.TEMPERATURE_SCALE, "F").equals("F"))
            return Temperature.asFahrenheitDegrees(new Temperature(kelvinDegrees));
        else if (getSharedPreferences().getString(CSPreferencesFragment.TEMPERATURE_SCALE, "F").equals("C"))
            return Temperature.asCelsiusDegrees(new Temperature(kelvinDegrees));
        else
            throw new IllegalStateException("Invalid Preferences");
    }

    private Temperature convertToTemperature(int value)
    {
        if (getSharedPreferences().getString(CSPreferencesFragment.TEMPERATURE_SCALE, "F").equals("F"))
            return Temperature.newTemperatureFromF(value);
        else if (getSharedPreferences().getString(CSPreferencesFragment.TEMPERATURE_SCALE, "F").equals("C"))
            return Temperature.newTemperatureFromC(value);
        else
            throw new IllegalStateException("Invalid Preferences");
    }

}
