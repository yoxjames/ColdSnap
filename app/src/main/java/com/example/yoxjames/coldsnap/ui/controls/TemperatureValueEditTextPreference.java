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
 * Implementation of EditTextPreference for temperature values. These are not absolute temperatures
 * but rather the absolute value of a Temperature. This type of input would be used for something like
 * "How many degrees should we heat something." vs "How hot is something." This is currently used for
 * fuzz values.
 */
public class TemperatureValueEditTextPreference extends EditTextPreference
{
    private static float getDefaultTempValue()
    {
        return 0f;
    }

    public TemperatureValueEditTextPreference(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    public TemperatureValueEditTextPreference(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public TemperatureValueEditTextPreference(Context context)
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
        return persistFloat((float) convertToFuzzValue(Integer.valueOf(value)));
    }

    private int convertToDisplayDegrees(float kelvinDegrees)
    {
        if (getSharedPreferences().getString(CSPreferencesFragment.TEMPERATURE_SCALE, "F").equals("F"))
            return Temperature.asFahrenheitValue(kelvinDegrees);
        else if (getSharedPreferences().getString(CSPreferencesFragment.TEMPERATURE_SCALE, "F").equals("C"))
            return Temperature.asCelsiusValue(kelvinDegrees);
        else
            throw new IllegalStateException("Invalid Preferences");
    }

    private double convertToFuzzValue(int displayDegrees)
    {
        if (getSharedPreferences().getString(CSPreferencesFragment.TEMPERATURE_SCALE, "F").equals("F"))
            return Temperature.asKelvinValueF(displayDegrees);
        else if (getSharedPreferences().getString(CSPreferencesFragment.TEMPERATURE_SCALE, "F").equals("C"))
            return Temperature.asKelvinValueC(displayDegrees);
        else
            throw new IllegalStateException("Invalid Preferences");
    }

}
