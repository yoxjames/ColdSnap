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

import org.threeten.bp.Instant;

import java.util.UUID;

import cz.msebera.android.httpclient.annotation.Immutable;

/**
 * Created by yoxjames on 10/7/17.
 */

@Immutable
public class ForecastHour
{
    private final Instant hour;
    private final Temperature temperature;
    private final UUID uuid;
    private final double lat;
    private final double lon;

    public ForecastHour(Instant hour, Temperature temperature, UUID uuid, double lat, double lon)
    {
        this.hour = hour;
        this.temperature = temperature;
        this.uuid = uuid;
        this.lat = lat;
        this.lon = lon;
    }

    public Instant getHour()
    {
        return hour;
    }

    public Temperature getTemperature()
    {
        return temperature;
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
}
