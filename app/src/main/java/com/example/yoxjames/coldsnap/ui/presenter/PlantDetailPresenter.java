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

package com.example.yoxjames.coldsnap.ui.presenter;

import java.util.UUID;

/**
 * Presenter object for the Plant Detail Activity (when you drill into individual Plants).
 */
public interface PlantDetailPresenter
{
    /**
     * Loads and reloads the Plant information on to the view.
     *
     * @param plantUUID The UUID of the Plant we wish to load.
     */
    void load(UUID plantUUID);

    /**
     * Saves the current state (from the view) for plantUUID. This will also finish the view.
     *
     * @param plantUUID UUID of the Plant we wish to save.
     */
    void savePlantInformation(UUID plantUUID);

    /**
     * Deletes the plant represented by plantUUID.
     *
     * @param plantUUID UUID of the plant we wish to delete. This should also finish the view.
     */
    void deletePlant(UUID plantUUID);
}
