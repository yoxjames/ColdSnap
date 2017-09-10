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

package com.yoxjames.coldsnap.ui.controls.temperaturepicker;


import com.yoxjames.coldsnap.model.TemperatureFormatter;
import com.yoxjames.coldsnap.model.TemperatureValueAdapter;

/**
 * Created by yoxjames on 8/12/17.
 */

abstract class AbstractTemperaturePickerPresenter implements TemperaturePickerPresenter
{
    private final TemperatureFormatter temperatureFormatter;
    private final TemperaturePickerView temperaturePickerView;
    private final TemperatureValueAdapter temperatureValueAdapter;
    private TemperatureManualInputListener temperatureManualInputListener;

    AbstractTemperaturePickerPresenter(TemperatureFormatter temperatureFormatter, TemperaturePickerView temperaturePickerView, TemperatureValueAdapter temperatureValueAdapter)
    {
        this.temperatureFormatter = temperatureFormatter;
        this.temperaturePickerView = temperaturePickerView;
        this.temperatureValueAdapter = temperatureValueAdapter;
    }

    abstract int getMinimumTemperatureValue();
    abstract int getMaximumTemperatureValue();
    public abstract int offset();
    public abstract double getKelvinValue();
    public abstract void setKelvinValue(double kelvinValue);

    @Override
    public void load()
    {
        temperaturePickerView.setMaxValue(getMaximumTemperatureValue() + offset());
        temperaturePickerView.setMinValue(getMinimumTemperatureValue() + offset());
    }

    @Override
    public String format(int value)
    {
        return temperatureFormatter.format(temperatureValueAdapter.getTemperature(value - offset()));
    }

    @Override
    public int onValueChange(int oldValue, int newValue)
    {
        if (Math.abs(oldValue - newValue) == 1)
            return newValue;
        else
        {
            if (temperatureManualInputListener != null)
                temperatureManualInputListener.onTemperatureManuallyInputted();
            return newValue + offset();
        }
    }

    @Override
    public void addTemperatureManualInputListener(TemperatureManualInputListener listener)
    {
        temperatureManualInputListener = listener;
    }
}
