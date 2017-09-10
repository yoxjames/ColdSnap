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


import javax.annotation.concurrent.Immutable;

/**
 * Simple POJO to store a location as returned from the device's location system.
 *
 * Created by James Yox on 7/16/17.
 */
@Immutable
public class SimpleWeatherLocation
{
    private final double lat;
    private final double lon;

    /**
     * Constructor. Not much to say here this class is pretty darn simple.
     *
     * @param lat Latitude
     * @param lon Longitude
     * @throws IllegalArgumentException if lat is not between -90 and 90 or if lon is not between
     *                                  -180 and 180.
     */
    public SimpleWeatherLocation(double lat, double lon)
    {
        if (Math.abs(lat) > 90.0)
            throw new IllegalArgumentException("Invalid latitude value" + Double.toString(lat));
        if (Math.abs(lon) > 180.0)
            throw new IllegalArgumentException("Invalid longitude value" + Double.toString(lon));

        this.lat = lat;
        this.lon = lon;
    }

    public double getLat()
    {
        return lat;
    }

    public double getLon()
    {
        return lon;
    }
}
