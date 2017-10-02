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

package com.yoxjames.coldsnap.ui.controls.preference;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.yoxjames.coldsnap.R;
import com.yoxjames.coldsnap.ui.controls.temperaturepicker.TemperaturePickerValue;

/**
 * Created by yoxjames on 8/13/17.
 */

public class TemperaturePickerValueDialogPreference extends DialogPreference
{
    private static float getDefaultTempValue()
    {
        return 0f;
    }

    private TemperaturePickerValue temperaturePickerValue;

    public TemperaturePickerValueDialogPreference(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @Override
    public View onCreateDialogView()
    {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.preference_fuzz_dialog, null);
        temperaturePickerValue = view.findViewById(R.id.temperature_value_picker);
        temperaturePickerValue.setKelvinValue(getPersistedFloat(getDefaultTempValue()));
        temperaturePickerValue.addTemperatureManualInputListener(() ->
        {
            persistFloat((float) temperaturePickerValue.getKelvinValue());
            getDialog().dismiss();
        });

        return view;
    }

    @Override
    public void onDialogClosed(boolean positiveResult)
    {
        if (positiveResult)
        {
            persistFloat((float) temperaturePickerValue.getKelvinValue());
        }
    }
}
