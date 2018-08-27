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

package com.yoxjames.coldsnap.ui.prefs;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.yoxjames.coldsnap.R;
import com.yoxjames.coldsnap.ui.BaseColdsnapView;
import com.yoxjames.coldsnap.ui.controls.temperaturepicker.TemperaturePickerView;
import com.yoxjames.coldsnap.ui.controls.temperaturepicker.TemperaturePickerViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;

/**
 * Created by yoxjames on 8/13/17.
 */

public class TemperaturePickerDialog extends DialogPreference implements BaseColdsnapView<TemperaturePickerViewModel>
{
    @BindView(R.id.temperature_value_picker) TemperaturePickerView tpvPicker;

    public TemperaturePickerDialog(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public TemperaturePickerDialog(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    public TemperaturePickerDialog(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public TemperaturePickerDialog(Context context)
    {
        super(context);
    }

    @Override
    public View onCreateDialogView()
    {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_fuzz, null);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void bindView(TemperaturePickerViewModel temperaturePickerViewModel)
    {
        tpvPicker.bindView(temperaturePickerViewModel);
    }

    public Observable<Integer> valueChanged()
    {
        return tpvPicker.valueChanged();
    }
}
