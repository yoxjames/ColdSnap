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

import java.util.UUID;

/**
 * Generic view representing the list of plants displayed to the user in the main "Activity."
 */
public interface PlantListView
{
    /**
     * Tells the GUI that the Plant data was changed so the list needs to be reloaded.
     */
    void notifyDataChange();

    /**
     * Opens a detail view of the plant in question.
     *
     * @param plantUUID UUID of the Plant to see the detail view of can be null if
     *                  this is a new plant.
     */
    void openPlant(UUID plantUUID);

    /**
     * Display simple device location failure message
     */
    void displayDeviceLocationFailureMessage();

    /**
     * Display location failure due to permissions
     */
    void displayLocationPermissionsError();

    /**
     * Displays an error to the user that informs them that a location device is not available
     * and potentially turned off.
     */
    void displayLocationNotAvailableError();
}
