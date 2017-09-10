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

package com.yoxjames.coldsnap.http.google;

/**
 * POJO that contains raw data from google's reverse geolocation services
 *
 * Created by yoxjames on 7/9/17.
 */
public class GoogleLocation
{
    private final double lat;
    private final double lon;
    private final String country;
    private final String postalCode;
    private final String state;
    private final String sublocality;

    private GoogleLocation(Builder builder)
    {
        this.lat = builder.lat;
        this.lon = builder.lon;
        this.country = builder.country;
        this.postalCode = builder.postalCode;
        this.state = builder.state;
        this.sublocality = builder.sublocality;
    }

    public double getLat()
    {
        return lat;
    }

    public double getLon()
    {
        return lon;
    }

    public String getCountry()
    {
        return country;
    }

    public String getPostalCode()
    {
        return postalCode;
    }

    public String getState()
    {
        return state;
    }

    public String getSublocality()
    {
        return sublocality;
    }

    public static class Builder
    {
        private double lat;
        private double lon;
        private String country;
        private String postalCode;
        private String state;
        private String sublocality;

        public Builder lat(double lat)
        {
            this.lat = lat;
            return this;
        }

        public Builder lon(double lon)
        {
            this.lon = lon;
            return this;
        }

        public Builder country(String country)
        {
            this.country = country;
            return this;
        }

        public Builder postalCode(String postalCode)
        {
            this.postalCode = postalCode;
            return this;
        }

        public Builder state(String state)
        {
            this.state = state;
            return this;
        }

        public Builder sublocality(String sublocality)
        {
            this.sublocality = sublocality;
            return this;
        }

        public GoogleLocation build()
        {
            return new GoogleLocation(this);
        }
    }
}
