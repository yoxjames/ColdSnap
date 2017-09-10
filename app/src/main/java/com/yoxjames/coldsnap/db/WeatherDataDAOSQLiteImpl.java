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
import com.yoxjames.coldsnap.model.ForecastDay;
import com.yoxjames.coldsnap.model.Temperature;
import com.yoxjames.coldsnap.model.WeatherData;
import com.yoxjames.coldsnap.model.WeatherDataNotFoundException;
import com.yoxjames.coldsnap.model.WeatherLocation;
import com.yoxjames.coldsnap.ui.CSPreferencesFragment;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import dagger.Lazy;
import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;

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
    public Completable saveWeatherData(final SQLiteDatabase database, final WeatherData weatherData)
    {
        return Completable.create(new CompletableOnSubscribe()
        {
            @Override
            public void subscribe(@NonNull CompletableEmitter e) throws Exception
            {
                ContentValues contentValues = contentValuesProvider.get().get();
                for (ForecastDay forecastDay : weatherData.getForecastDays())
                {
                    contentValues.put(ColdsnapDbSchema.ForecastDayTable.Cols.UUID, forecastDay.getUUID().toString());
                    contentValues.put(ColdsnapDbSchema.ForecastDayTable.Cols.DATE, forecastDay.toString());
                    contentValues.put(ColdsnapDbSchema.ForecastDayTable.Cols.FETCH_DATE, forecastDay.getDate().getTime());
                    contentValues.put(ColdsnapDbSchema.ForecastDayTable.Cols.HIGH_TEMP_K, forecastDay.getHighTemperature().getDegreesKelvin());
                    contentValues.put(ColdsnapDbSchema.ForecastDayTable.Cols.LOW_TEMP_K, forecastDay.getLowTemperature().getDegreesKelvin());
                    contentValues.put(ColdsnapDbSchema.ForecastDayTable.Cols.ZIPCODE, weatherData.getWeatherLocation().getZipCode());

                    database.insert(ColdsnapDbSchema.ForecastDayTable.NAME, null, contentValues);
                }

                e.onComplete();
            }
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public Single<WeatherData> getWeatherData(final SQLiteDatabase database, final WeatherLocation weatherLocation)
    {
        return Single.create(new SingleOnSubscribe<WeatherData>()
        {
            @Override
            public void subscribe(@NonNull SingleEmitter<WeatherData> e) throws Exception
            {
                // Read back raw data from the DB
                List<ForecastDayRow> forecastDayRows = new ArrayList<>();

                try (ForecastDayCursorWrapper cursor = queryWeatherData(database, null, null))
                {
                    cursor.moveToFirst();
                    while (!cursor.isAfterLast())
                    {
                        forecastDayRows.add(cursor.getForecastDay());

                        cursor.moveToNext();
                    }
                }

                List<ForecastDay> forecastDays = translateToForecastDay(forecastDayRows);

                if (forecastDays == null || forecastDays.size() == 0 || forecastDayRows.get(0).getZipCode() == null || forecastDayRows.get(0).getZipCode().equals(""))
                    e.onError(new WeatherDataNotFoundException("No rows returned from DB query"));
                else
                {
                    e.onSuccess(new WeatherData(forecastDays, forecastDays.get(0).getDate(), weatherLocation));
                }
            }

        }).subscribeOn(Schedulers.io());
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
    public Completable deleteWeatherData(final SQLiteDatabase database)
    {
        return Completable.create(new CompletableOnSubscribe()
        {
            @Override
            public void subscribe(@NonNull CompletableEmitter e) throws Exception
            {
                database.delete(ColdsnapDbSchema.ForecastDayTable.NAME, null, null);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io());
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
