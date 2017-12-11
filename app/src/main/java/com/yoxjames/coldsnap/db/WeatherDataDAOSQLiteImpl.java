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

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yoxjames.coldsnap.db.weather.ForecastDayRowCursorWrapper;
import com.yoxjames.coldsnap.db.weather.ForecastHourRow;
import com.yoxjames.coldsnap.model.ForecastHour;
import com.yoxjames.coldsnap.model.ForecastHourUtil;
import com.yoxjames.coldsnap.model.Temperature;
import com.yoxjames.coldsnap.model.WeatherData;
import com.yoxjames.coldsnap.model.WeatherDataNotFoundException;
import com.yoxjames.coldsnap.model.WeatherLocation;
import com.yoxjames.coldsnap.service.location.SimpleWeatherLocation;
import com.yoxjames.coldsnap.ui.CSPreferencesFragment;

import org.threeten.bp.Instant;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Provider;

import dagger.Lazy;
import dagger.Reusable;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

/**
 * Implementation for WeatherDataDAO. This is implemented using SQLite for Android.
 */
@Reusable
public class WeatherDataDAOSQLiteImpl implements WeatherDataDAO
{
    private final Provider<Lazy<ContentValues>> contentValuesProvider;
    private final Provider<Lazy<ForecastDayRowCursorWrapper.Factory>> cursorWrapperFactoryProvider;
    private final SharedPreferences sharedPreferences;
    private final ColdSnapDBHelper coldSnapDBHelper;

    @Inject
    public WeatherDataDAOSQLiteImpl(Provider<Lazy<ContentValues>> contentValuesProvider, Provider<Lazy<ForecastDayRowCursorWrapper.Factory>> cursorWrapperFactoryProvider, SharedPreferences sharedPreferences, ColdSnapDBHelper coldSnapDBHelper)
    {
        this.contentValuesProvider = contentValuesProvider;
        this.cursorWrapperFactoryProvider = cursorWrapperFactoryProvider;
        this.sharedPreferences = sharedPreferences;
        this.coldSnapDBHelper = coldSnapDBHelper;
    }

    @Override
    public Completable saveWeatherData(final WeatherData weatherData)
    {
        return Completable.fromRunnable(() ->
        {
            final SQLiteDatabase database = coldSnapDBHelper.getWritableDatabase();
            ContentValues contentValues = contentValuesProvider.get().get();
            for (ForecastHour forecastHour : weatherData.getForecastHours())
            {
                contentValues.put(ColdsnapDbSchema.ForecastHourTable.Cols.UUID, forecastHour.getUuid().toString());
                contentValues.put(ColdsnapDbSchema.ForecastHourTable.Cols.FETCH_INSTANCE, weatherData.getSyncInstant().getEpochSecond());
                contentValues.put(ColdsnapDbSchema.ForecastHourTable.Cols.HOUR_INSTANCE, forecastHour.getHour().getEpochSecond());
                contentValues.put(ColdsnapDbSchema.ForecastHourTable.Cols.TEMP_K, forecastHour.getTemperature().getDegreesKelvin());
                contentValues.put(ColdsnapDbSchema.ForecastHourTable.Cols.FUZZ_K, forecastHour.getTemperature().getFuzz());
                contentValues.put(ColdsnapDbSchema.ForecastHourTable.Cols.LAT, forecastHour.getLat());
                contentValues.put(ColdsnapDbSchema.ForecastHourTable.Cols.LON, forecastHour.getLon());

                database.insert(ColdsnapDbSchema.ForecastHourTable.NAME, null, contentValues);
            }

            final SharedPreferences.Editor preferenceEditor = sharedPreferences.edit();

            preferenceEditor.putString(CSPreferencesFragment.LOCATION_STRING, weatherData.getWeatherLocation().getPlaceString());
            if (!preferenceEditor.commit())
                throw new IllegalStateException("Saving preferences failed");

        }).subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<WeatherData> getWeatherData(final SimpleWeatherLocation weatherLocation)
    {

        return Observable.fromCallable(() ->
        {
            final SQLiteDatabase database = coldSnapDBHelper.getReadableDatabase();
            // Read back raw data from the DB
            List<ForecastHourRow> forecastDayRows = new ArrayList<>();

            try (ForecastDayRowCursorWrapper cursor = queryWeatherData(database,
                    "lat = ? AND lon = ?",
                    new String[] { String.valueOf(weatherLocation.getLat()), String.valueOf(weatherLocation.getLon()) }))
            {
                cursor.moveToFirst();
                while (!cursor.isAfterLast())
                {
                    forecastDayRows.add(cursor.getForecastDay());
                    cursor.moveToNext();
                }
            }

            final String locationString = sharedPreferences.getString(CSPreferencesFragment.LOCATION_STRING, "");

            List<ForecastHour> forecastHours = translateToForecastHour(forecastDayRows);

            if (forecastHours == null || forecastHours.size() == 0)
                throw new WeatherDataNotFoundException("No rows returned from DB query");
            else
            {
                ForecastHourUtil.HighLowPair highLowData = ForecastHourUtil.getDailyHighLow(forecastHours);

                return new WeatherData(forecastHours,
                        Instant.ofEpochSecond(forecastDayRows.get(0).getSyncInstant()),
                        new WeatherLocation(locationString, forecastDayRows.get(0).getLat(), forecastDayRows.get(0).getLon()),
                        highLowData.getDailyLow(),
                        highLowData.getDailyHigh());
            }
        }).subscribeOn(Schedulers.io());
    }

    private List<ForecastHour> translateToForecastHour(List<ForecastHourRow> rows)
    {
        List<ForecastHour> forecastHours = new ArrayList<>();

        final double fuzzK = sharedPreferences.getFloat(CSPreferencesFragment.WEATHER_DATA_FUZZ, 0.0f);

        for (ForecastHourRow row : rows)
            forecastHours.add(new ForecastHour(Instant.ofEpochSecond(row.getHourInstant()),
                    new Temperature(row.getTempK(), fuzzK),
                    UUID.fromString(row.getForecastUUID()),
                    row.getLat(),
                    row.getLon()));

        return forecastHours;
    }

    @Override
    public Completable deleteWeatherData()
    {
        return Completable.fromRunnable(() ->
                {
                    final SQLiteDatabase database = coldSnapDBHelper.getWritableDatabase();
                    database.delete(ColdsnapDbSchema.ForecastHourTable.NAME, null, null);
                })
                .subscribeOn(Schedulers.io());
    }

    private ForecastDayRowCursorWrapper queryWeatherData(SQLiteDatabase database, String whereClause, String[] whereArgs)
    {
        Cursor cursor = database.query(
                ColdsnapDbSchema.ForecastHourTable.NAME,
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
