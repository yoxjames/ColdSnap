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

import java.util.UUID;

public class ForecastHourRow
{
    private final String forecastUUID;
    private final long hourInstant;
    private final long syncInstant;
    private final double tempK;
    private final double fuzzK;
    private final UUID uuid;
    private final double lat;
    private final double lon;

    private ForecastHourRow(String forecastUUID, long hourInstant, long syncInstant, double tempK, double fuzzK, UUID uuid, double lat, double lon)
    {
        this.forecastUUID = forecastUUID;
        this.hourInstant = hourInstant;
        this.syncInstant = syncInstant;
        this.tempK = tempK;
        this.fuzzK = fuzzK;
        this.uuid = uuid;
        this.lat = lat;
        this.lon = lon;
    }

    /**
     * Gets the forecast UUID.
     *
     * @return The Forecast UUID
     */
    public String getForecastUUID()
    {
        return forecastUUID;
    }

    public long getSyncInstant()
    {
        return syncInstant;
    }

    public double getTempK()
    {
        return tempK;
    }

    public long getHourInstant()
    {
        return hourInstant;
    }

    public double getFuzzK()
    {
        return fuzzK;
    }

    public UUID getUuid()
    {
        return uuid;
    }

    public double getLat()
    {
        return lat;
    }

    public double getLon()
    {
        return lon;
    }

    public static class Builder
    {

        private String forecastUUID;
        private long syncInstant;
        private double tempK;
        private double fuzzK;
        private UUID uuid;
        private long hourInstant;
        private double lat;
        private double lon;

        public Builder forecastUUID(String forecastUUID)
        {
            this.forecastUUID = forecastUUID;
            return this;
        }

        public Builder syncInstant(long syncInstant)
        {
            this.syncInstant = syncInstant;
            return this;
        }

        public Builder tempK(double tempK)
        {
            this.tempK = tempK;
            return this;
        }

        public Builder fuzzK(double fuzzK)
        {
            this.fuzzK = fuzzK;
            return this;
        }

        public Builder uuid(UUID uuid)
        {
            this.uuid = uuid;
            return this;
        }

        public Builder hourInstant(long hourInstant)
        {
            this.hourInstant = hourInstant;
            return this;
        }

        public Builder lon(double lon)
        {
            this.lon = lon;
            return this;
        }

        public Builder lat(double lat)
        {
            this.lat = lat;
            return this;
        }

        public ForecastHourRow build()
        {
            return new ForecastHourRow(forecastUUID, hourInstant, syncInstant, tempK, fuzzK, uuid, lat, lon);
        }
    }
}
