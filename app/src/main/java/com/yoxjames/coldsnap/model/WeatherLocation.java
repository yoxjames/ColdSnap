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

import com.google.auto.value.AutoValue;

/**
 * POJO designed to hold location data for use in resolving Weather Data.
 *
 * Created by yoxjames on 7/8/17.
 */

@AutoValue
public abstract class WeatherLocation
{
    public abstract String getPlaceString();
    public abstract double getLat();
    public abstract double getLon();

    public abstract Builder toBuilder();

    public WeatherLocation withLocationString(String locationString)
    {
        return toBuilder().setPlaceString(locationString).build();
    }

    public static Builder builder()
    {
        return new AutoValue_WeatherLocation.Builder();
    }

    @AutoValue.Builder
    public static abstract class Builder
    {
        public abstract Builder setPlaceString(String placeString);
        public abstract Builder setLat(double lat);
        public abstract Builder setLon(double lon);

        public abstract WeatherLocation build();
    }
}
