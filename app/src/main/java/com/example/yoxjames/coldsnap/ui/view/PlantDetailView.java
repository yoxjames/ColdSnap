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

package com.example.yoxjames.coldsnap.ui.view;

import android.widget.NumberPicker;

/**
 * View representing the details of a Plant. All of these methods should cause actual changes in
 * the GUI.
 */
public interface PlantDetailView
{
    /**
     * Sets the plant name.
     *
     * @param name Name to set the plant to.
     */
    void setPlantName(String name);

    /**
     * Gets the plant name from the GUI implementation.
     *
     * @return The name of the Plant as defined in the GUI.
     */
    String getPlantName();

    /**
     * Sets the plant scientific name
     *
     * @param scientificName The scientific name.
     */
    void setPlantScientificName(String scientificName);

    /**
     * Gets the plant scientific name from the GUI implementation.
     *
     * @return The plant scientific name.
     */
    String getPlantScientificName();

    /**
     * Sets the cold tolerance temperature of the current plant. This just takes an integer so scale
     * needs to be taken into account.
     *
     * @param minTemperature Integer representing the cold tolerance of the plant in question.
     */
    void setMinTemperature(int minTemperature);

    /**
     * Gets the minimum cold tolerance temperature from the GUI implementation.
     *
     * @return An integer representing the cold tolerance temperature. Scale needs to be determined
     * after calling this.
     */
    int getMinTemperature();

    /**
     * Sets the maximum temperature bound allowed. Current scale needs to be taken into account.
     *
     * @param maxTemperature Int representing the max temperature value allowed.
     */
    void setMaxBound(int maxTemperature);

    /**
     * Sets the minimum temperature bound allowed. Current scale needs to be taken into account.
     *
     * @param minTemperature Int representing the min temperature value allowed.
     */
    void setMinBound(int minTemperature);

    /**
     * Called when the responsible view is being used to add a completely new plant vs updating an
     * existing plant.
     */
    void setAddMode();

    /**
     * Broadcasts a message to the GUI for when either a plant is saved or updated.
     *
     * @param isNewPlant TRUE if a new plant is being added
     *                   FALSE if an existing plant is being updated
     */
    void displaySaveMessage(boolean isNewPlant);

    /**
     * Displays message when deleting the plant alerting the user the plant was successfully deleted.
     */
    void displayDeleteMessage();

    /**
     * Sets the formatter for the Number Picker for the cold tolerance temperature.
     *
     * @param formatter Formatter object to use.
     */
    void setMinimumTemperatureFormatter(NumberPicker.Formatter formatter);

    /**
     * Determines whether this is a new plant from the GUI implementation. The GUI is responsible for
     * persisting certain data so state will be saved there.
     *
     * @return true if this is a new Plant (cached)
     *         false if this is an existing Plant (saved)
     */
    boolean isNewPlantInd();
}
