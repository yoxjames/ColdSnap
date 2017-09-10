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

package com.yoxjames.coldsnap.model;

import android.support.annotation.NonNull;

import javax.annotation.concurrent.Immutable;

import dagger.internal.Preconditions;

/**
 * POJO designed to hold location data for use in resolving Weather Data.
 *
 * Created by yoxjames on 7/8/17.
 */

@Immutable
public class WeatherLocation
{
    private final String zipCode;
    private final String placeString;
    private final double lat;
    private final double lon;

    public WeatherLocation(@NonNull String zipCode, @NonNull String placeString, double lat, double lon)
    {
        this.zipCode = Preconditions.checkNotNull(zipCode);
        this.placeString = Preconditions.checkNotNull(placeString);
        this.lat = lat;
        this.lon = lon;
    }

    public String getZipCode()
    {
        return zipCode;
    }

    public String getPlaceString()
    {
        return placeString;
    }

    public double getLat()
    {
        return lat;
    }

    public double getLon()
    {
        return lon;
    }

    @Override
    public String toString()
    {
        return "WeatherLocation{" +
                "zipCode='" + zipCode + '\'' +
                ", placeString='" + placeString + '\'' +
                ", lat=" + lat +
                ", lon=" + lon +
                '}';
    }

    public boolean equals(WeatherLocation o)
    {
        return this == o || !(o == null || getClass() != o.getClass()) && o.zipCode.equals(zipCode);
    }

    @Override
    public int hashCode()
    {
        int result;
        long temp;
        result = zipCode != null ? zipCode.hashCode() : 0;
        result = 31 * result + (placeString != null ? placeString.hashCode() : 0);
        temp = Double.doubleToLongBits(lat);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(lon);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
