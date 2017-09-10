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
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.widget.NumberPicker;

import com.yoxjames.coldsnap.ColdSnapApplication;
import com.yoxjames.coldsnap.dagger.TemperaturePickerModule;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Extension of the NumberPicker UI control to be used for picking temperatures.
 */
public class TemperaturePickerRelative extends NumberPicker
        implements NumberPicker.Formatter, TemperaturePickerView, NumberPicker.OnValueChangeListener
{
    @Inject SharedPreferences sharedPreferences;
    @Inject @Named("relative")TemperaturePickerPresenter presenter;

    public TemperaturePickerRelative(Context context)
    {
        super(context);
        load();
    }

    public TemperaturePickerRelative(Context context, AttributeSet attributeSet)
    {

        super(context, attributeSet);
        ((ColdSnapApplication) getContext().getApplicationContext())
                .getInjector()
                .temperaturePickerSubcomponent(new TemperaturePickerModule(this))
                .inject(this);

        load();
    }

    private void load()
    {
        ((ColdSnapApplication) getContext().getApplicationContext())
                .getInjector()
                .temperaturePickerSubcomponent(new TemperaturePickerModule(this))
                .inject(this);

        this.setFormatter(this);
        this.setOnValueChangedListener(this);
        this.reflectionFormatHack();

        presenter.load();
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
    public String format(int value)
    {
        return presenter.format(value);
    }

    @Override
    public int getTemperatureValue()
    {
        return super.getValue() - presenter.offset();
    }

    @Override
    public void setTemperatureValue(int temperatureValue)
    {
        super.setValue(temperatureValue + presenter.offset());
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

    @Override
    public void onValueChange(NumberPicker numberPicker, int oldValue, int newValue)
    {
        if (Math.abs(oldValue - newValue) != 1)
        {
            numberPicker.setValue(presenter.onValueChange(oldValue, newValue) - 1);
            reflectionFormatHack();
        }
        else
        {
            numberPicker.setValue(presenter.onValueChange(oldValue, newValue));
        }
    }
}
