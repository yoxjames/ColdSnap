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

package com.yoxjames.coldsnap.ui.weatherpreviewbar;

import javax.annotation.Nullable;

import dagger.internal.Preconditions;

/**
 * Created by yoxjames on 10/15/17.
 */

public class WeatherBarViewModel
{
    private final String locationText;
    private final String highTemperatureText;
    private final String lowTemperatureText;
    private final String lastUpdatedTime;
    private final boolean isPending;
    private final Throwable error;

    private WeatherBarViewModel(String locationText, String highTemperatureText, String lowTemperatureText, String lastUpdatedTime, boolean isPending, @Nullable Throwable error)
    {
        this.locationText = Preconditions.checkNotNull(locationText);
        this.highTemperatureText = Preconditions.checkNotNull(highTemperatureText);
        this.lowTemperatureText = Preconditions.checkNotNull(lowTemperatureText);
        this.lastUpdatedTime = Preconditions.checkNotNull(lastUpdatedTime);
        this.isPending = Preconditions.checkNotNull(isPending);
        this.error = error;
    }

    public WeatherBarViewModel(String locationText, String highTemperatureText, String lowTemperatureText, String lastUpdatedTime, Throwable error)
    {
        this(locationText, highTemperatureText, lowTemperatureText, lastUpdatedTime, false, error);
    }

    public WeatherBarViewModel(String locationText, String highTemperatureText, String lowTemperatureText, String lastUpdatedTime)
    {
        this(locationText, highTemperatureText, lowTemperatureText, lastUpdatedTime, false, null);
    }

    public String getLocationText()
    {
        return locationText;
    }

    public String getHighTemperatureText()
    {
        return highTemperatureText;
    }

    public String getLowTemperatureText()
    {
        return lowTemperatureText;
    }

    public String getLastUpdatedTime()
    {
        return lastUpdatedTime;
    }

    public boolean isPending()
    {
        return isPending;
    }

    public Throwable getError()
    {
        return error;
    }

    static WeatherBarViewModel nullInstance()
    {
        return new WeatherBarViewModel("", "", "", "", true, null);
    }

    static WeatherBarViewModel errorInstance(Throwable error)
    {
        return new WeatherBarViewModel("", "", "", "", false, error);
    }
}
