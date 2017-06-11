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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.yoxjames.coldsnap.db.ColdsnapDbSchema.ForecastDayTable;
import com.example.yoxjames.coldsnap.db.ColdsnapDbSchema.PlantTable;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Helper class designed to create and upgrade the database for android. This is sort of the main
 * "configuration" class for the database. The schema and starter data is all populated here.
 */
@Singleton
public class ColdSnapDBHelper extends SQLiteOpenHelper
{
    /**
     * Database version. For migration purposes.
     */
    private static final int VERSION = 2;

    /**
     * Name of the database where all plant and weather data will be stored.
     */
    private static final String DATABASE_NAME = "Coldsnap.db";

    @Inject
    public ColdSnapDBHelper(Context context)
    {
        super(context, DATABASE_NAME, null, VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("create table " + ForecastDayTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                ForecastDayTable.Cols.UUID + ", " +
                ForecastDayTable.Cols.FETCH_DATE + ", " +
                ForecastDayTable.Cols.DATE + ", " +
                ForecastDayTable.Cols.HIGH_TEMP_K + ", " +
                ForecastDayTable.Cols.LOW_TEMP_K + ", " +
                ForecastDayTable.Cols.ZIPCODE + ")"
        );

        db.execSQL("create table " + PlantTable.NAME + "(" +
                "_id integer primary key autoincrement, " +
                PlantTable.Cols.UUID + ", " +
                PlantTable.Cols.NAME + ", " +
                PlantTable.Cols.SCIENTIFIC_NAME + ", " +
                PlantTable.Cols.COLD_THRESHOLD_DEGREES + ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("drop table " + ForecastDayTable.NAME);
        db.execSQL("create table " + ForecastDayTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                ForecastDayTable.Cols.UUID + ", " +
                ForecastDayTable.Cols.FETCH_DATE + ", " +
                ForecastDayTable.Cols.DATE + ", " +
                ForecastDayTable.Cols.HIGH_TEMP_K + ", " +
                ForecastDayTable.Cols.LOW_TEMP_K + ", " +
                ForecastDayTable.Cols.ZIPCODE + ")"
        );

        db.execSQL("drop table " + PlantTable.NAME);
        db.execSQL("create table " + PlantTable.NAME + "(" +
                "_id integer primary key autoincrement, " +
                PlantTable.Cols.UUID + ", " +
                PlantTable.Cols.NAME + ", " +
                PlantTable.Cols.SCIENTIFIC_NAME + ", " +
                PlantTable.Cols.COLD_THRESHOLD_DEGREES + ")"
        );
    }
}
