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

package com.example.yoxjames.coldsnap.http.google;

import java.net.URL;

/**
 * Factory for use in dagger to create injectable URL instances.
 *
 * Created by James Yox on 7/9/17.
 */
public interface GoogleLocationURLFactory
{
    /**
     * Factory create method to return a URL based on inputted latitude and longitude.
     * @param lat Latitude
     * @param lon Longitude
     * @return A URL object that will be used to hit the Google Places API to find information
     * on the given lat and lon.
     */
    URL create(double lat, double lon);
}
