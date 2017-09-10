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

package com.yoxjames.coldsnap.db;

/**
 * This information is used for schema creation.
 * This is essentially a glorified struct created only for readability. Honestly this should
 * probably be changed to something not implemented in Java.
 */
public class ColdsnapDbSchema
{
    /**
     * Helper class for the forcast_day table.
     */
    public static final class ForecastDayTable
    {
        public static final String NAME = "forecast_day";

        public static final class Cols
        {
            public static final String UUID = "forecast_uuid";
            public static final String FETCH_DATE = "fetch_date";
            public static final String DATE = "date";
            public static final String HIGH_TEMP_K = "high_temp_k";
            public static final String LOW_TEMP_K = "low_temp_k";
            public static final String ZIPCODE = "zipcode";
        }
    }

    /**
     * Helper class for the plant table
     */
    public static final class PlantTable
    {
        public static final String NAME = "plant";

        public static final class Cols
        {
            public static final String UUID = "plant_uuid";
            public static final String NAME = "name";
            public static final String SCIENTIFIC_NAME = "scientific_name";
            public static final String COLD_THRESHOLD_DEGREES = "cold_threshold_degrees";
        }
    }
}
