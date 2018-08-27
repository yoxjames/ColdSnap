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


package com.yoxjames.coldsnap.http.openweathermap;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import javax.annotation.Nullable;

/**
 * Created by yoxjames on 10/7/17.
 */

class OpenWeatherMapForecast
{
    @SerializedName("list") @Expose @Nullable List<ForecastPeriods> list;
    @SerializedName("city") @Expose @Nullable City city;

    class City
    {
        @SerializedName("id") @Expose @Nullable Integer id;
        @SerializedName("name") @Expose @Nullable String name;
        @SerializedName("coord") @Expose @Nullable Location coord;
        @SerializedName("country") @Expose @Nullable String country;

        class Location
        {
            @SerializedName("lat") @Expose @Nullable Double lat;
            @SerializedName("lon") @Expose @Nullable Double lon;
        }
    }

    class ForecastPeriods
    {
        @SerializedName("dt") @Expose long dt;
        @SerializedName("main") @Expose @Nullable Forecast forecast;

        class Forecast
        {
            @SerializedName("temp") @Expose @Nullable Double temp;
            @SerializedName("temp_min") @Expose @Nullable Double tempMin;
            @SerializedName("temp_max") @Expose @Nullable Double tempMax;
            @SerializedName("pressure") @Expose @Nullable Double pressure;
            @SerializedName("humidity") @Expose @Nullable Integer humidity;
        }
    }
}

