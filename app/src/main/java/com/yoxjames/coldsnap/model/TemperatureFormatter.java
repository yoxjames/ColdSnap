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

package com.yoxjames.coldsnap.model;

/**
 * Implementing classes will have the ability to format {@link Temperature} objects into human
 * readable Strings that are suitable to be shown in the GUI.
 */
public interface TemperatureFormatter
{
    /**
     * Returns a human readable string representing the inputted Temperature. This string
     * is intended to be used directly in the GUI.
     *
     * @param temperature The Temperature to format
     * @return A String representing the formatted temperature
     */
    String format(Temperature temperature);

    /**
     * Takes a double value in K and returns a string representation (probably not in K) of the
     * temperature amount that represents.
     *
     * @param fuzzKelvins double value in Kelvins that we are to format
     * @return A String representing that amount formatted for the user to view
     */
    String formatFuzz(double fuzzKelvins);
}
