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

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * Created by yoxjames on 10/14/17.
 */

@Entity(tableName = "plant_image")
public class PlantImageRow
{
    @NonNull @PrimaryKey private String uuid;
    @NonNull @ColumnInfo(name = "plant_uuid") private String plantUUID;
    @NonNull @ColumnInfo(name = "title") private String title;
    @NonNull @ColumnInfo(name = "image_filename") private String imageFilename;
    @NonNull @ColumnInfo(name = "image_timestamp") private long imageDate;

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
    public String getPlantUUID()
    {
        return plantUUID;
    }

    public void setPlantUUID(@NonNull String plantUUID)
    {
        this.plantUUID = plantUUID;
    }

    @NonNull
    public String getTitle()
    {
        return title;
    }

    public void setTitle(@NonNull String title)
    {
        this.title = title;
    }

    @NonNull
    public String getImageFilename()
    {
        return imageFilename;
    }

    public void setImageFilename(@NonNull String imageFilename)
    {
        this.imageFilename = imageFilename;
    }

    @NonNull
    public long getImageDate()
    {
        return imageDate;
    }

    public void setImageDate(@NonNull long imageDate)
    {
        this.imageDate = imageDate;
    }
}
