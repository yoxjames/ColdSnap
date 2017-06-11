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

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.yoxjames.coldsnap.model.ForecastDay;
import com.example.yoxjames.coldsnap.model.Temperature;
import com.example.yoxjames.coldsnap.model.WeatherData;
import com.example.yoxjames.coldsnap.model.WeatherDataNotFoundException;
import com.example.yoxjames.coldsnap.ui.CSPreferencesFragment;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import dagger.Lazy;

/**
 * Implementation for WeatherDataDAO. This is implemented using SQLite for Android.
 */
@Singleton
public class WeatherDataDAOSQLiteImpl implements WeatherDataDAO
{
    private final Provider<Lazy<ContentValues>> contentValuesProvider;
    private final Provider<Lazy<ForecastDayCursorWrapper.Factory>> cursorWrapperFactoryProvider;
    private final SharedPreferences sharedPreferences;

    @Inject
    public WeatherDataDAOSQLiteImpl(Provider<Lazy<ContentValues>> contentValuesProvider, Provider<Lazy<ForecastDayCursorWrapper.Factory>> cursorWrapperFactoryProvider, SharedPreferences sharedPreferences)
    {
        this.contentValuesProvider = contentValuesProvider;
        this.cursorWrapperFactoryProvider = cursorWrapperFactoryProvider;
        this.sharedPreferences = sharedPreferences;
    }

    @Override
    public void saveWeatherData(SQLiteDatabase database, WeatherData weatherData)
    {
        ContentValues contentValues = contentValuesProvider.get().get();
        for (ForecastDay forecastDay : weatherData.getForecastDays())
        {
            contentValues.put(ColdsnapDbSchema.ForecastDayTable.Cols.UUID, forecastDay.getUUID().toString());
            contentValues.put(ColdsnapDbSchema.ForecastDayTable.Cols.DATE, forecastDay.toString());
            contentValues.put(ColdsnapDbSchema.ForecastDayTable.Cols.FETCH_DATE, forecastDay.getDate().getTime());
            contentValues.put(ColdsnapDbSchema.ForecastDayTable.Cols.HIGH_TEMP_K, forecastDay.getHighTemperature().getDegreesKelvin());
            contentValues.put(ColdsnapDbSchema.ForecastDayTable.Cols.LOW_TEMP_K, forecastDay.getLowTemperature().getDegreesKelvin());
            contentValues.put(ColdsnapDbSchema.ForecastDayTable.Cols.ZIPCODE, weatherData.getZipCode());

            database.insert(ColdsnapDbSchema.ForecastDayTable.NAME, null, contentValues);
        }
    }

    @Override
    public WeatherData getWeatherData(SQLiteDatabase database) throws WeatherDataNotFoundException
    {

        // Read back raw data from the DB
        ForecastDayCursorWrapper cursor = queryWeatherData(database,null,null);
        List<ForecastDayRow> forecastDayRows = new ArrayList<>();

        try
        {
            cursor.moveToFirst();
            while (!cursor.isAfterLast())
            {
                forecastDayRows.add(cursor.getForecastDay());

                cursor.moveToNext();
            }
        }
        finally
        {
            cursor.close();
        }

        List<ForecastDay> forecastDays = translateToForecastDay(forecastDayRows);

        if (forecastDays == null || forecastDays.size() == 0 || forecastDayRows.get(0).getZipCode() == null || forecastDayRows.get(0).getZipCode().equals(""))
            throw new WeatherDataNotFoundException("No rows returned from DB query");

        return new WeatherData(forecastDays, sharedPreferences.getString(CSPreferencesFragment.LOCATION_STRING, "Location"), forecastDayRows.get(0).getZipCode(), forecastDays.get(0).getDate());
    }

    private List<ForecastDay> translateToForecastDay(List<ForecastDayRow> rows)
    {
        final double fuzz = sharedPreferences.getFloat(CSPreferencesFragment.WEATHER_DATA_FUZZ,0f);
        List<ForecastDay> forecastDays = new ArrayList<>();
        for (ForecastDayRow row : rows)
        {
            Temperature highTemp = new Temperature(row.getHighTempK(), fuzz);
            Temperature lowTemp = new Temperature(row.getLowTempK(), fuzz);

            forecastDays.add(new ForecastDay(row.getDate(), highTemp, lowTemp, new Date(row.getSyncDateTime()), UUID.fromString(row.getForecastUUID())));
        }
        return forecastDays;
    }

    @Override
    public void deleteWeatherData(SQLiteDatabase database)
    {
        database.delete(ColdsnapDbSchema.ForecastDayTable.NAME, null, null);
    }

    private ForecastDayCursorWrapper queryWeatherData(SQLiteDatabase database, String whereClause, String[] whereArgs)
    {

        Cursor cursor = database.query(
                ColdsnapDbSchema.ForecastDayTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );

        return cursorWrapperFactoryProvider.get().get().create(cursor);
    }
}
