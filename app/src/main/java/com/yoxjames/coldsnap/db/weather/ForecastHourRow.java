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

package com.yoxjames.coldsnap.db.weather;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "forecast_hour")
public class ForecastHourRow
{
    @NonNull @PrimaryKey private String uuid;
    @NonNull @ColumnInfo(name = "sync_time") private long syncInstant;
    @NonNull @ColumnInfo(name = "temp_k") private double tempK;
    @NonNull @ColumnInfo(name = "forecast_time") private long hourInstant;
    @NonNull @ColumnInfo(name = "fuzz_k") private double fuzzK;
    @NonNull @ColumnInfo(name = "lat") private double lat;
    @NonNull @ColumnInfo(name = "lon") private double lon;

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
    public long getSyncInstant()
    {
        return syncInstant;
    }

    public void setSyncInstant(@NonNull long syncInstant)
    {
        this.syncInstant = syncInstant;
    }

    @NonNull
    public double getTempK()
    {
        return tempK;
    }

    public void setTempK(@NonNull double tempK)
    {
        this.tempK = tempK;
    }

    @NonNull
    public long getHourInstant()
    {
        return hourInstant;
    }

    public void setHourInstant(@NonNull long hourInstant)
    {
        this.hourInstant = hourInstant;
    }

    @NonNull
    public double getFuzzK()
    {
        return fuzzK;
    }

    public void setFuzzK(@NonNull double fuzzK)
    {
        this.fuzzK = fuzzK;
    }

    @NonNull
    public double getLat()
    {
        return lat;
    }

    public void setLat(@NonNull double lat)
    {
        this.lat = lat;
    }

    @NonNull
    public double getLon()
    {
        return lon;
    }

    public void setLon(@NonNull double lon)
    {
        this.lon = lon;
    }
}
