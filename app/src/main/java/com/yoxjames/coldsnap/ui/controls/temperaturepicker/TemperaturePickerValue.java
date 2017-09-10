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

import android.content.Context;
import android.util.AttributeSet;
import android.widget.NumberPicker;

import com.yoxjames.coldsnap.ColdSnapApplication;
import com.yoxjames.coldsnap.dagger.TemperaturePickerModule;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by yoxjames on 8/13/17.
 */

public class TemperaturePickerValue extends NumberPicker
        implements NumberPicker.Formatter, TemperaturePickerView, NumberPicker.OnValueChangeListener
{

    @Inject @Named("value") TemperaturePickerPresenter presenter;

    public TemperaturePickerValue(Context context)
    {
        super(context);
        load();
    }

    public TemperaturePickerValue(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        load();
    }

    private void load()
    {
        ((ColdSnapApplication) getContext().getApplicationContext())
                .getInjector()
                .temperaturePickerSubcomponent(new TemperaturePickerModule(this))
                .inject(this);


        this.setFormatter(this);
        this.setWrapSelectorWheel(false);
        this.setOnValueChangedListener(this);
        this.reflectionFormatHack();

        presenter.load();
    }

    @Override
    public String format(int value)
    {
        return presenter.format(value);
    }

    @Override
    public int getTemperatureValue()
    {
        return super.getValue();
    }

    @Override
    public void setTemperatureValue(int temperatureValue)
    {
        super.setValue(temperatureValue);
    }

    @Override
    public double getKelvinValue()
    {
        return presenter.getKelvinValue();
    }

    @Override
    public void setKelvinValue(double kelvinValue)
    {
        presenter.setKelvinValue(kelvinValue);
    }

    @Override
    public void addTemperatureManualInputListener(TemperatureManualInputListener listener)
    {
        presenter.addTemperatureManualInputListener(listener);
    }

    private void reflectionFormatHack()
    {
        // Disgusting hack to get around an android bug: https://issuetracker.google.com/issues/36952035
        try
        {
            Method changeValueByOne = getClass().getSuperclass().getDeclaredMethod("changeValueByOne", boolean.class);
            changeValueByOne.setAccessible(true);
            changeValueByOne.invoke(this, true);
        }
        catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e)
        {
            // I dont really care if this doesn't work. It just means the NumberPicker will format poorly
        }
    }

    @Override
    public void onValueChange(NumberPicker numberPicker, int oldValue, int newValue)
    {
        if (Math.abs(oldValue - newValue) != 1)
            reflectionFormatHack();
        presenter.onValueChange(oldValue, newValue);
    }
}
