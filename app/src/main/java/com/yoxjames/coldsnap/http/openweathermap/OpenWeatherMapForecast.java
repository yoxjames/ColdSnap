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

/**
 * Created by yoxjames on 10/7/17.
 */

class OpenWeatherMapForecast
{
    @SerializedName("list") @Expose List<ForecastPeriods> list;
    @SerializedName("city") @Expose City city;

    class City
    {
        @SerializedName("id") @Expose Integer id;
        @SerializedName("name") @Expose String name;
        @SerializedName("coord") @Expose Location coord;
        @SerializedName("country") @Expose String country;

        class Location
        {
            @SerializedName("lat") @Expose Double lat;
            @SerializedName("lon") @Expose Double lon;
        }
    }

    class ForecastPeriods
    {
        @SerializedName("dt") @Expose long dt;
        @SerializedName("main") @Expose Forecast forecast;

        class Forecast
        {
            @SerializedName("temp") @Expose Double temp;
            @SerializedName("temp_min") @Expose Double tempMin;
            @SerializedName("temp_max") @Expose Double tempMax;
            @SerializedName("pressure") @Expose Double pressure;
            @SerializedName("humidity") @Expose Integer humidity;
        }
    }
}

