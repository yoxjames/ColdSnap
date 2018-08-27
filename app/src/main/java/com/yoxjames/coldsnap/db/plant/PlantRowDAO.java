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

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import io.reactivex.Flowable;

/**
 * Data Access Object for Plants!
 */
@Dao
public interface PlantRowDAO
{
    @Query("SELECT * from plant ORDER BY name")
    List<PlantRow> getPlantRows();

    @Query("SELECT * from plant where uuid = :plantUUID")
    Flowable<PlantRow> getPlantRow(String plantUUID);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addPlantRow(PlantRow plant);

    @Query("DELETE from plant where uuid = :plantUUID")
    void deletePlantRow(String plantUUID);
}
