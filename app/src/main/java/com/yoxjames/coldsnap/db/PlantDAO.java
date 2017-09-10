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

package com.yoxjames.coldsnap.db;

import android.database.sqlite.SQLiteDatabase;

import com.yoxjames.coldsnap.model.Plant;

import java.util.UUID;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;

/**
 * Data Access Object for Plants!
 */
public interface PlantDAO
{
    /**
     * Gets a list of all Plants in the database.
     *
     * @param database The database which we will search for Plants
     * @return A List<Plant> of plants
     */
    Observable<Plant> getPlants(SQLiteDatabase database);

    /**
     * Obtains a single plant from the database
     *
     * @param database The database which we will search for a Plant
     * @param plantUUID The UUID of the Plant we want
     * @return A Single that emits a Plant or an error.
     */
    Single<Plant> getPlant(SQLiteDatabase database, UUID plantUUID);

    /**
     * Adds a Plant to database.
     *
     * @param database The database to add the Plant to.
     * @param plant The Plant to add.
     */
    Completable addPlant(SQLiteDatabase database, Plant plant);

    /**
     * Deletes a Plant from database. If the UUID passed in is not in database this does nothing.
     *
     * @param database The database to delete the Plant from.
     * @param plantUUID The UUID of the plant to delete.
     */
    Completable deletePlant(SQLiteDatabase database, UUID plantUUID);

    /**
     * Replaces plantUUID with newPlant. plantUUID must have the same UUID as newPlant.
     *
     * @param database The database with which to replace the plant.
     * @param plantUUID The UUID of the plant we wish to replace.
     * @param newPlant Plant who's data will replace plantUUID. This must have the same UUID as plantUUID.
     * @throws IllegalArgumentException If plantUUID is not equal to newPlant's UUID.
     */
    Completable updatePlant(SQLiteDatabase database, UUID plantUUID, Plant newPlant);
}
