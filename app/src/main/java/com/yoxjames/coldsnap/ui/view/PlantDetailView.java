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

package com.yoxjames.coldsnap.ui.view;

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
     * Called to terminate the view.
     */
    void finishView();

    /**
     * Called to display a loading error message
     */
    void showLoadError();

    /**
     * Called to display unable to save plant error message.
     */
    void showUnableToSaveError();

    /**
     * Called to display unable to add plant error message.
     */
    void showUnableToAddError();

    /**
     * Called to display unable to delete plant error message.
     */
    void showUnableToDeleteError();
}
