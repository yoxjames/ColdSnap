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

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.yoxjames.coldsnap.util.LOG;

import javax.inject.Inject;
import javax.inject.Singleton;

import static com.yoxjames.coldsnap.db.ColdsnapDbSchema.ForecastHourTable;
import static com.yoxjames.coldsnap.db.ColdsnapDbSchema.ImageTable;
import static com.yoxjames.coldsnap.db.ColdsnapDbSchema.PlantTable;

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
    private static final int VERSION = 1;

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
        db.execSQL("create table " + ForecastHourTable.NAME + "(" +
                ForecastHourTable.NAME + "_id integer primary key autoincrement, " +
                forecastHourTableCols());

        db.execSQL("create table " + PlantTable.NAME + "(" +
                PlantTable.NAME + "_id integer primary key autoincrement, " +
                plantTableCols());

        db.execSQL("create table " + ImageTable.NAME + "(" +
                ImageTable.NAME + "_id integer primary key autoincrement, " +
                imageTableCols());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        clearTables(db);

        db.execSQL("create table " + ForecastHourTable.NAME + "(" +
                ForecastHourTable.NAME + "_id integer primary key autoincrement, " +
                forecastHourTableCols());

        db.execSQL("create table " + PlantTable.NAME + "(" +
                PlantTable.NAME + "_id integer primary key autoincrement, " +
                plantTableCols());

        db.execSQL("create table " + ImageTable.NAME + "(" +
                ImageTable.NAME + "_id integer primary key autoincrement, " +
                imageTableCols());
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        clearTables(db);

        db.execSQL("create table " + ForecastHourTable.NAME + "(" +
                ForecastHourTable.NAME + "_id integer primary key autoincrement, " +
                forecastHourTableCols());

        db.execSQL("create table " + PlantTable.NAME + "(" +
                PlantTable.NAME + "_id integer primary key autoincrement, " +
                plantTableCols());

        db.execSQL("create table " + ImageTable.NAME + "(" +
                ImageTable.NAME + "_id integer primary key autoincrement, " +
                imageTableCols());
    }

    private void clearTables(SQLiteDatabase db)
    {
        // All the upgrade and downgrade stuff was starting to get on my nerves....
        try
        {
            db.execSQL("drop table " + ImageTable.NAME);
        }
        catch (SQLException e)
        {
            LOG.d("Database", "No Image Table");
        }

        try
        {
            db.execSQL("drop table " + PlantTable.NAME);
        }
        catch (SQLException e)
        {
            LOG.d("Database", "No Plant Table");
        }

        try
        {
            db.execSQL("drop table " + ForecastHourTable.NAME);
        }
        catch (SQLException e)
        {
            LOG.d("Database", "No Forecast Hour Table");
        }
    }

    private String forecastHourTableCols()
    {
        return ForecastHourTable.Cols.UUID + ", " +
                ForecastHourTable.Cols.HOUR_INSTANCE + " INTEGER" + ", " +
                ForecastHourTable.Cols.FETCH_INSTANCE + " INTEGER" + ", " +
                ForecastHourTable.Cols.TEMP_K + " REAL" + ", " +
                ForecastHourTable.Cols.FUZZ_K + " REAL" + ", " +
                ForecastHourTable.Cols.LAT + " REAL" + ", " +
                ForecastHourTable.Cols.LON + " REAL" + ")";
    }

    private String imageTableCols()
    {
        return ImageTable.Cols.UUID + ", " +
                ImageTable.Cols.PLANT_UUID + ", " +
                ImageTable.Cols.TITLE + ", " +
                ImageTable.Cols.IMAGE_FILENAME + ", " +
                ImageTable.Cols.IMAGE_DATE + ")";
    }

    private String plantTableCols()
    {
        return PlantTable.Cols.UUID + ", " +
                PlantTable.Cols.NAME + ", " +
                PlantTable.Cols.SCIENTIFIC_NAME + ", " +
                PlantTable.Cols.COLD_THRESHOLD_DEGREES + ", " +
                PlantTable.Cols.MAIN_IMAGE_UUID + ")";
    }
}
