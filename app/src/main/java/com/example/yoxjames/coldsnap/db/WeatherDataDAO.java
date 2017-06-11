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

package com.example.yoxjames.coldsnap.db;

import android.database.sqlite.SQLiteDatabase;

import com.example.yoxjames.coldsnap.model.WeatherData;
import com.example.yoxjames.coldsnap.model.WeatherDataNotFoundException;

/**
 * Data Access Object for WeatherData!
 */
public interface WeatherDataDAO
{

    /**
     * Saves weatherData to database.
     *
     * @param database The database to save WeatherData to
     * @param weatherData The WeatherData to save.
     */
    void saveWeatherData(SQLiteDatabase database, WeatherData weatherData);

    /**
     * Obtains weatherData from the database. At this time, only one instance of WeatherData
     * is saved on the database at a time
     *
     * @param database The database to search for WeatherData
     * @return WeatherData from database
     */
    WeatherData getWeatherData(SQLiteDatabase database) throws WeatherDataNotFoundException;


    /**
     * Deletes all WeatherData from database
     *
     * @param database The databse to delete WeatherData from
     */
    void deleteWeatherData(SQLiteDatabase database);
}
