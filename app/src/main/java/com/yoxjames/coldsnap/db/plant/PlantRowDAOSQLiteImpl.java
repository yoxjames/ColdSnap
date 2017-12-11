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

package com.yoxjames.coldsnap.db.plant;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yoxjames.coldsnap.db.ColdSnapDBHelper;
import com.yoxjames.coldsnap.db.ColdsnapDbSchema;
import com.yoxjames.coldsnap.service.ActionReply;
import com.yoxjames.coldsnap.util.LOG;

import javax.inject.Inject;
import javax.inject.Provider;

import dagger.Lazy;
import dagger.Reusable;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.schedulers.Schedulers;

/**
 * Implementation for PlantRowDAO that uses SQLite in Android.
 */
@Reusable
public class PlantRowDAOSQLiteImpl implements PlantRowDAO
{
    private final Provider<Lazy<ContentValues>> contentValuesProvider;
    private final Provider<Lazy<PlantRowCursorWrapper.Factory>> plantCursorWrapperFactory;
    private final ColdSnapDBHelper dbHelper;

    @Inject
    public PlantRowDAOSQLiteImpl(Provider<Lazy<ContentValues>> contentValuesProvider, Provider<Lazy<PlantRowCursorWrapper.Factory>> plantCursorWrapperFactory, final ColdSnapDBHelper dbHelper)
    {
        this.contentValuesProvider = contentValuesProvider;
        this.plantCursorWrapperFactory = plantCursorWrapperFactory;
        this.dbHelper = dbHelper;
    }

    @Override
    public Observable<PlantRow> getPlantRows()
    {
        return Observable.create((ObservableOnSubscribe<PlantRow>) e ->
        {
            final SQLiteDatabase database = dbHelper.getReadableDatabase();

            try (PlantRowCursorWrapper cursor = queryPlantData(database, null, null))
            {
                cursor.moveToFirst();
                while (!cursor.isAfterLast())
                {
                    e.onNext(cursor.getPlant());
                    cursor.moveToNext();
                }
            }
            finally
            {
                e.onComplete();
            }
        })
                .doOnComplete(() -> LOG.d(getClass().getName(), "getPlantRows DB access"))
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<PlantRow> getPlantRow(final String plantUUID)
    {
        return Observable.fromCallable(() ->
        {
            final SQLiteDatabase database = dbHelper.getReadableDatabase();
            try (PlantRowCursorWrapper cursor = queryPlantData(database, "plant_uuid = ?", new String[] { plantUUID }))
            {
                if (cursor.moveToFirst())
                    return cursor.getPlant();
                else
                    throw new IllegalArgumentException("UUID " + plantUUID + " not found.");
            }
        })
                .doOnNext(plantRow -> LOG.d(getClass().getName(), "getPlantRow DB access UUID = " + plantRow.getUuid()))
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<ActionReply> addPlantRow(final PlantRow plant)
    {
        return Observable.fromCallable(() ->
        {
            final SQLiteDatabase database = dbHelper.getWritableDatabase();
            final ContentValues values = contentValuesProvider.get().get();

            values.put(ColdsnapDbSchema.PlantTable.Cols.UUID, plant.getUuid());
            values.put(ColdsnapDbSchema.PlantTable.Cols.NAME, plant.getName());
            values.put(ColdsnapDbSchema.PlantTable.Cols.SCIENTIFIC_NAME, plant.getScientificName());
            values.put(ColdsnapDbSchema.PlantTable.Cols.COLD_THRESHOLD_DEGREES, plant.getColdThresholdK());
            values.put(ColdsnapDbSchema.PlantTable.Cols.MAIN_IMAGE_UUID, plant.getMainImageUUID());
            database.insert(ColdsnapDbSchema.PlantTable.NAME, null, values);

            return ActionReply.genericSuccess();
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<ActionReply> deletePlantRow(final String plantUUID)
    {
        return Observable.fromCallable(() ->
                {
                    final SQLiteDatabase database = dbHelper.getWritableDatabase();

                    database.delete(ColdsnapDbSchema.PlantTable.NAME,
                            ColdsnapDbSchema.PlantTable.Cols.UUID + " = ?",
                            new String[]{plantUUID});
                    return ActionReply.genericSuccess();
                })
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<ActionReply> updatePlantRow(final String plantUUID, final PlantRow newPlant)
    {
        return Observable.fromCallable(() ->
        {
            final SQLiteDatabase database = dbHelper.getWritableDatabase();
            final ContentValues values = contentValuesProvider.get().get();
            if (!plantUUID.equals(newPlant.getUuid()))
                throw new IllegalArgumentException("plantUUID must be the same UUID as newPlant's UUID");

            values.put(ColdsnapDbSchema.PlantTable.Cols.UUID, newPlant.getUuid());
            values.put(ColdsnapDbSchema.PlantTable.Cols.NAME, newPlant.getName());
            values.put(ColdsnapDbSchema.PlantTable.Cols.SCIENTIFIC_NAME, newPlant.getScientificName());
            values.put(ColdsnapDbSchema.PlantTable.Cols.COLD_THRESHOLD_DEGREES, newPlant.getColdThresholdK());
            values.put(ColdsnapDbSchema.PlantTable.Cols.MAIN_IMAGE_UUID, newPlant.getMainImageUUID());

            database.update(ColdsnapDbSchema.PlantTable.NAME, values, ColdsnapDbSchema.PlantTable.Cols.UUID + " = ?",
                    new String[]{plantUUID});

            return ActionReply.genericSuccess();

        }).subscribeOn(Schedulers.io());
    }

    private PlantRowCursorWrapper queryPlantData(SQLiteDatabase database, String whereClause, String[] whereArgs)
    {
        final Cursor cursor = database.query(ColdsnapDbSchema.PlantTable.NAME,
                                             null,
                                             whereClause,
                                             whereArgs,
                                             null,
                                             null,
                                             ColdsnapDbSchema.PlantTable.Cols.NAME);

        return plantCursorWrapperFactory.get().get().create(cursor);
    }
}
