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

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import javax.annotation.Nullable;

/**
 * Created by yoxjames on 10/14/17.
 */
@Entity(tableName = "plant")
public class PlantRow
{
    @NonNull @PrimaryKey private String uuid;
    @NonNull @ColumnInfo(name = "name") private String name;
    @NonNull @ColumnInfo(name = "scientific_name") private String scientificName;
    @NonNull @ColumnInfo(name = "cold_threshold_k") private double coldThresholdK;
    @Nullable @ColumnInfo(name = "main_image_uuid") private String mainImageUUID;

    @NonNull
    public String getUuid()
    {
        return uuid;
    }

    public void setUuid(@NonNull String uuid)
    {
        this.uuid = uuid;
    }

    @NonNull
    public String getName()
    {
        return name;
    }

    public void setName(@NonNull String name)
    {
        this.name = name;
    }

    @NonNull
    public String getScientificName()
    {
        return scientificName;
    }

    public void setScientificName(@NonNull String scientificName)
    {
        this.scientificName = scientificName;
    }

    @NonNull
    public double getColdThresholdK()
    {
        return coldThresholdK;
    }

    public void setColdThresholdK(@NonNull double coldThresholdK)
    {
        this.coldThresholdK = coldThresholdK;
    }

    @Nullable
    public String getMainImageUUID()
    {
        return mainImageUUID;
    }

    public void setMainImageUUID(@Nullable String mainImageUUID)
    {
        this.mainImageUUID = mainImageUUID;
    }
}
