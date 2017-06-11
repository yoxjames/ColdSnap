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

import android.widget.NumberPicker;

import com.example.yoxjames.coldsnap.model.Temperature;

/**
 * Adapter to be used for Temperature Pickers. This is sort of closely coupled to NumberPickers
 * in Android.
 */
public interface TemperaturePickerAdapter
{
    /**
     * Gets the maximum temperature value in whatever scale is currently being used.
     *
     * @return An integer representing the maximum supported temperature in the current scale.
     */
    int getMinimumTemperatureValue();

    /**
     * Gets the minimum temperature value in whatever scale is currently being used.
     *
     * @return An integer representing the minimum supported temperature in the current scale.
     */
    int getMaximumTemperatureValue();

    /**
     * Gets an integer value that represents an inputted temperature. This will be suitable for display
     * as the int returned should be in the desired scale.
     *
     * This is used to display an existing Temperature on the GUI NumberPicker.
     *
     * @param temperature Temperature to represent as an int.
     * @return An integer representing Temperature in the desired scale.
     */
    int getValueForTemperature(Temperature temperature);

    /**
     * Gets a temperature object based on the inputted value.
     *
     * This is used to create a Temperature object based on what the user selected in the GUI NumberPicker.
     *
     * @param value Integer represnting the temperature we want. Implementation should establish how we know what
     *              scale it is.
     * @return A Temperature object based on the inputted value.
     */
    Temperature getTemperatureForValue(int value);

    /**
     * Gets a NumberPicker.Formatter for use in the GUI. This should be based on the above methods.
     *
     * @return A NumberPicker.Formatter object for use in Android NumberPickers.
     */
    NumberPicker.Formatter getFormatter();
}
