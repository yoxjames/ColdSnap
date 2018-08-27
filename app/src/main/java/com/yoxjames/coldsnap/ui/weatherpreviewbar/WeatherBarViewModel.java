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

import com.google.auto.value.AutoValue;

/**
 * Created by yoxjames on 10/15/17.
 */

@AutoValue
public abstract class WeatherBarViewModel
{
    public static WeatherBarViewModel EMPTY = create(
        "",
        "",
        "",
        "",
        true,
        "");

    public abstract String getLocationText();
    public abstract String getHighTemperatureText();
    public abstract String getLowTemperatureText();
    public abstract String getLastUpdatedTime();
    public abstract boolean isPending();
    public abstract String errorMessage();

    public static WeatherBarViewModel create(
            String locationText,
            String highTemperatureText,
            String lowTemperatureText,
            String lastUpdatedTime,
            boolean isPending,
            String errorMessage)
    {
        return new AutoValue_WeatherBarViewModel(locationText, highTemperatureText, lowTemperatureText, lastUpdatedTime, isPending, errorMessage);
    }
}
