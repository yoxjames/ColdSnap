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
    public static final class ForecastHourTable
    {
        public static final String NAME = "forecast_hour";

        public static final class Cols
        {
            public static final String ID = "forecast_hour_id";
            public static final String UUID = "forecast_uuid";
            public static final String HOUR_INSTANCE = "hour_instance";
            public static final String FETCH_INSTANCE = "fetch_instance";
            public static final String TEMP_K = "temp_k";
            public static final String FUZZ_K = "fuzz_k";
            public static final String LAT = "lat";
            public static final String LON = "lon";
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
            public static final String ID = "plant_id";
            public static final String UUID = "plant_uuid";
            public static final String NAME = "name";
            public static final String SCIENTIFIC_NAME = "scientific_name";
            public static final String COLD_THRESHOLD_DEGREES = "cold_threshold_degrees";
            public static final String MAIN_IMAGE_UUID = "main_image_uuid";
        }
    }

    /**
     * Helper class for the image table
     */
    public static final class ImageTable
    {
        public static final String NAME = "image";

        public static final class Cols
        {
            public static final String ID = "image_id";
            public static final String UUID = "image_uuid";
            public static final String PLANT_UUID = "plant_uuid";
            public static final String TITLE = "image_title";
            public static final String IMAGE_FILENAME = "image_filename";
            public static final String IMAGE_DATE = "image_date";
        }
    }
}
