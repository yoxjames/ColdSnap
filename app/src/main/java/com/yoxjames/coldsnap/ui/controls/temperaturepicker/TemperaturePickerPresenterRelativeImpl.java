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

import com.yoxjames.coldsnap.model.Temperature;
import com.yoxjames.coldsnap.model.TemperatureFormatter;
import com.yoxjames.coldsnap.model.TemperatureValueAdapter;

import javax.inject.Inject;

/**
 * Created by yoxjames on 8/12/17.
 */

public class TemperaturePickerPresenterRelativeImpl extends AbstractTemperaturePickerPresenter
        implements TemperaturePickerPresenter
{
    private static final Temperature MINIMUM_TEMPERATURE = Temperature.newTemperatureFromC(-70);
    private static final Temperature MAXIMUM_TEMPERATURE = Temperature.newTemperatureFromC(100);

    private final TemperaturePickerView temperaturePickerView;
    private final TemperatureValueAdapter temperatureValueAdapter;

    @Inject
    public TemperaturePickerPresenterRelativeImpl(TemperatureFormatter temperatureFormatter, TemperaturePickerView temperaturePickerView, TemperatureValueAdapter temperatureValueAdapter)
    {
        super(temperatureFormatter, temperaturePickerView, temperatureValueAdapter);
        this.temperaturePickerView = temperaturePickerView;
        this.temperatureValueAdapter = temperatureValueAdapter;
    }

    @Override
    int getMinimumTemperatureValue()
    {
        return temperatureValueAdapter.getValue(MINIMUM_TEMPERATURE);
    }

    @Override
    int getMaximumTemperatureValue()
    {
        return temperatureValueAdapter.getValue(MAXIMUM_TEMPERATURE);
    }

    @Override
    public int offset()
    {
        return Math.abs(temperatureValueAdapter.getValue(MINIMUM_TEMPERATURE));
    }

    @Override
    public double getKelvinValue()
    {
        return temperatureValueAdapter.getKelvinTemperature(temperaturePickerView.getTemperatureValue());
    }

    @Override
    public void setKelvinValue(double kelvinValue)
    {
        temperaturePickerView.setTemperatureValue(temperatureValueAdapter.getValue(kelvinValue));
    }
}
