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

package com.yoxjames.coldsnap.db.plant;

import com.yoxjames.coldsnap.service.ActionReply;

import io.reactivex.Observable;

/**
 * Data Access Object for Plants!
 */
public interface PlantRowDAO
{
    /**
     * Gets a list of all Plants in the database.
     *
     * @return A ForecastPeriods<Plant> of plants
     */
    Observable<PlantRow> getPlantRows();

    /**
     * Obtains a single plant from the database
     *
     * @param plantUUID The UUID of the Plant we want
     * @return A Single that emits a Plant or an error.
     */
    Observable<PlantRow> getPlantRow(String plantUUID);

    /**
     * Adds a Plant to database.
     *
     * @param plant The Plant to add.
     */
    Observable<ActionReply> addPlantRow(PlantRow plant);

    /**
     * Deletes a Plant from database. If the UUID passed in is not in database this does nothing.
     *
     * @param plantUUID The UUID of the plant to delete.
     */
    Observable<ActionReply> deletePlantRow(String plantUUID);

    /**
     * Replaces plantUUID with newPlant. plantUUID must have the same UUID as newPlant.
     *
     * @param plantUUID The UUID of the plant we wish to replace.
     * @param newPlant Plant who's data will replace plantUUID. This must have the same UUID as plantUUID.
     * @throws IllegalArgumentException If plantUUID is not equal to newPlant's UUID.
     */
    Observable<ActionReply> updatePlantRow(String plantUUID, PlantRow newPlant);
}
