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

package com.yoxjames.coldsnap.db.image;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by yoxjames on 10/14/17.
 */

@Dao
public interface PlantImageRowDAO
{
    @Query("SELECT * FROM plant_image WHERE plant_uuid = :plantUUID")
    PlantImageRow getImageForPlant(String plantUUID);

    @Query("SELECT * FROM plant_image WHERE uuid = :uuid")
    PlantImageRow getImage(String uuid);

    @Query("SELECT * FROM plant_image")
    List<PlantImageRow> getPlantImages();

    @Query("DELETE FROM plant_image where uuid = :plantImageUUID")
    void deleteImage(String plantImageUUID);

    @Query("DELETE FROM plant_image where plant_uuid = :plantUUID")
    void deleteImagesForPlant(String plantUUID);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addImage(PlantImageRow plantImageRow);
}
