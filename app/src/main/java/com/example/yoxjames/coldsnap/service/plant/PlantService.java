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

package com.example.yoxjames.coldsnap.service.plant;

import com.example.yoxjames.coldsnap.model.Plant;

import java.util.List;
import java.util.UUID;

/**
 * Service layer for interacting with {@link Plant} data.
 */
public interface PlantService
{
    /**
     * Obtains a list of all Plants present in the ColdSnap application.
     *
     * @return A List of {@link Plant} objects.
     */
    List<Plant> getMyPlants();

    /**
     * Adds a Plant to the system.
     *
     * @param plant The plant to add.
     */
    void addPlant(Plant plant);

    /**
     * Caches a plant. Cached plants are not guaranteed to be persistent. They are only stored for
     * short term use. Generally speaking they would not be saved to a "database."
     *
     * @param plant The plant to cache.
     */
    void cachePlant(Plant plant);

    /**
     * Gets a Plant object based on UUID. Should not be called with a UUID that is not present. This
     * will search both cached plants and saved plants.
     *
     * @param plantUUID UUID of the plant we are looking for.
     * @return The plant requested
     * @throws IllegalArgumentException if the UUID inputted does not exist as a cached or saved plant.
     */
    Plant getPlant(UUID plantUUID);

    /**
     * Replaces plantUUID with the newPlant inputted. The UUID of newPlant and plantUUID must be the same.
     *
     * @param plantUUID UUID of the Plant to update
     * @param newPlant The new Plant to replace plantUUID with.
     */
    void updatePlant(UUID plantUUID, Plant newPlant);

    /**
     * Deletes a Plant from the system. The Plant in question will be deleted from both the saved plants
     * and the cached plants.
     *
     * @param plantUUID UUID of the Plant to delete.
     */
    void deletePlant(UUID plantUUID);
}
