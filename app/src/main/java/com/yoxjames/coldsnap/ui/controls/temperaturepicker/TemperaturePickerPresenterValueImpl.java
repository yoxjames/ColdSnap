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

import javax.inject.Inject;

/**
 * Created by yoxjames on 8/12/17.
 */

public class TemperaturePickerPresenterValueImpl extends AbstractTemperaturePickerPresenter
        implements TemperaturePickerPresenter
{
    private final TemperatureFormatter temperatureFormatter;
    private final TemperaturePickerView temperaturePickerView;
    private final TemperatureValueAdapter temperatureValueAdapter;

    @Inject
    public TemperaturePickerPresenterValueImpl(TemperatureFormatter temperatureFormatter, TemperaturePickerView temperaturePickerView, TemperatureValueAdapter temperatureValueAdapter)
    {
        super(temperatureFormatter, temperaturePickerView, temperatureValueAdapter);
        this.temperatureFormatter = temperatureFormatter;
        this.temperaturePickerView = temperaturePickerView;
        this.temperatureValueAdapter = temperatureValueAdapter;
    }

    @Override
    int getMinimumTemperatureValue()
    {
        return 0;
    }

    @Override
    int getMaximumTemperatureValue()
    {
        return 10;
    }

    @Override
    public int offset()
    {
        return 0;
    }

    @Override
    public double getKelvinValue()
    {
        return temperatureValueAdapter.getKelvinAbsoluteTemperature(temperaturePickerView.getTemperatureValue());
    }

    @Override
    public void setKelvinValue(double kelvinValue)
    {
        temperaturePickerView.setTemperatureValue(temperatureValueAdapter.getAbsoluteValue(kelvinValue));
    }
}
