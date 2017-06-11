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

package com.example.yoxjames.coldsnap.model;

import java.util.Date;
import java.util.UUID;

import javax.annotation.concurrent.Immutable;

import dagger.internal.Preconditions;

/**
 * Business object POJO representing the forecast for a given day. This stores what the high and low
 * temperatures for that day are. This class is immutable.
 */
@Immutable
public class ForecastDay
{
    private final String dayString;
    private final Temperature highTemperature;
    private final Temperature lowTemperature;
    private final Date syncDate;
    private final UUID uuid;

    /**
     * Generic constructor for ForecastDay. Nulls are not allowed for any parameters.
     *
     * @param dayString A string representing the Day (ex "April 1st, 2017")
     * @param highTemperature Temperature representing the high temperature for this day.
     * @param lowTemperature Temperature representing the low temperature for this day.
     * @param syncDate The date that this information was obtained. Not the date that the temperatures
     *                 are actually valid for.
     * @param uuid UUID representing this object.
     */
    public ForecastDay(String dayString, Temperature highTemperature, Temperature lowTemperature, Date syncDate, UUID uuid)
    {
        this.dayString = Preconditions.checkNotNull(dayString);
        this.highTemperature = Preconditions.checkNotNull(highTemperature);
        this.lowTemperature = Preconditions.checkNotNull(lowTemperature);
        this.syncDate = Preconditions.checkNotNull(syncDate);
        this.uuid = Preconditions.checkNotNull(uuid);
    }

    /**
     * Returns a new Date object that represents the synchronization date of this class.
     *
     * @return A new Date object who's time is the same as the this class's syncDate.
     */
    public Date getDate()
    {
        return new Date(syncDate.getTime());
    }

    /**
     * Gets the high temperature for this ForecastDay.
     *
     * @return A Temperature representing the high Temperature
     */
    public Temperature getHighTemperature()
    {
        return highTemperature;
    }

    /**
     * Gets the low temperature for this ForecastDay
     *
     * @return A Temperature representing the low Temperature.
     */
    public Temperature getLowTemperature()
    {
        return lowTemperature;
    }

    /**
     * Gets the UUID of this ForecastDay.
     *
     * @return The UUID for this ForecastDay.
     */
    public UUID getUUID()
    {
        return uuid;
    }

    /**
     * Gets the string that represents the day of this ForecastDay.
     *
     * @return A string representing this ForecastDay.
     */
    @Override
    public String toString()
    {
        return dayString;
    }
}
