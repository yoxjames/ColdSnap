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
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yoxjames.coldsnap.model.Plant;

import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import dagger.Lazy;
import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;

/**
 * Implementation for PlantDAO that uses SQLite in Android.
 */
@Singleton
public class PlantDAOSQLiteImpl implements PlantDAO
{
    private final Provider<Lazy<ContentValues>> contentValuesProvider;
    private final Provider<Lazy<PlantCursorWrapper.Factory>> plantCursorWrapperFactory;

    @Inject
    public PlantDAOSQLiteImpl(Provider<Lazy<ContentValues>> contentValuesProvider, Provider<Lazy<PlantCursorWrapper.Factory>> plantCursorWrapperFactory)
    {
        this.contentValuesProvider = contentValuesProvider;
        this.plantCursorWrapperFactory = plantCursorWrapperFactory;
    }

    @Override
    public Observable<Plant> getPlants(final SQLiteDatabase database)
    {
        return Observable.create(new ObservableOnSubscribe<Plant>()
        {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Plant> e) throws Exception
            {
                final PlantCursorWrapper cursor = queryPlantData(database, null, null);

                try
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
                    cursor.close();
                    e.onComplete();
                }
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Single<Plant> getPlant(final SQLiteDatabase database, final UUID plantUUID)
    {
        return Single.create(new SingleOnSubscribe<Plant>()
        {
            @Override
            public void subscribe(@NonNull SingleEmitter<Plant> e) throws Exception
            {
                final PlantCursorWrapper cursor = queryPlantData(database, "plant_uuid = ?", new String[] { plantUUID.toString() });
                if (cursor.moveToFirst())
                    e.onSuccess(cursor.getPlant());
                else
                    e.onError(new IllegalArgumentException("UUID " + plantUUID + " not found."));
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Completable addPlant(final SQLiteDatabase database, final Plant plant)
    {
        return Completable.create(new CompletableOnSubscribe()
        {
            @Override
            public void subscribe(@NonNull CompletableEmitter e) throws Exception
            {
                final ContentValues values = contentValuesProvider.get().get();

                values.put(ColdsnapDbSchema.PlantTable.Cols.UUID, plant.getUuid().toString());
                values.put(ColdsnapDbSchema.PlantTable.Cols.NAME, plant.getName());
                values.put(ColdsnapDbSchema.PlantTable.Cols.SCIENTIFIC_NAME, plant.getScientificName());
                values.put(ColdsnapDbSchema.PlantTable.Cols.COLD_THRESHOLD_DEGREES, plant.getMinimumTolerance().getDegreesKelvin());

                database.insert(ColdsnapDbSchema.PlantTable.NAME, null, values);

                e.onComplete();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Completable deletePlant(final SQLiteDatabase database, final UUID plantUUID)
    {
        return Completable.create(new CompletableOnSubscribe()
        {
            @Override
            public void subscribe(@NonNull CompletableEmitter e) throws Exception
            {
                final String plantUUIDString = plantUUID.toString();

                database.delete(ColdsnapDbSchema.PlantTable.NAME, ColdsnapDbSchema.PlantTable.Cols.UUID + " = ?",
                        new String[] { plantUUIDString });

                e.onComplete();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Completable updatePlant(final SQLiteDatabase database, final UUID plantUUID, final Plant newPlant)
    {
        return Completable.create(new CompletableOnSubscribe()
        {
            @Override
            public void subscribe(@NonNull CompletableEmitter e) throws Exception
            {
                final String plantUUIDstring = plantUUID.toString();
                final ContentValues values = contentValuesProvider.get().get();
                if (!plantUUID.equals(newPlant.getUuid()))
                    throw new IllegalArgumentException("plantUUID must be the same UUID as newPlant's UUID");

                values.put(ColdsnapDbSchema.PlantTable.Cols.UUID, newPlant.getUuid().toString());
                values.put(ColdsnapDbSchema.PlantTable.Cols.NAME, newPlant.getName());
                values.put(ColdsnapDbSchema.PlantTable.Cols.SCIENTIFIC_NAME, newPlant.getScientificName());
                values.put(ColdsnapDbSchema.PlantTable.Cols.COLD_THRESHOLD_DEGREES, newPlant.getMinimumTolerance().getDegreesKelvin());

                database.update(ColdsnapDbSchema.PlantTable.NAME, values, ColdsnapDbSchema.PlantTable.Cols.UUID + " = ?",
                        new String[] { plantUUIDstring });
                e.onComplete();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private PlantCursorWrapper queryPlantData(SQLiteDatabase database, String whereClause, String[] whereArgs)
    {
        final Cursor cursor = database.query(ColdsnapDbSchema.PlantTable.NAME,
                                             null,
                                             whereClause,
                                             whereArgs,
                                             null,
                                             null,
                                             null);

        return plantCursorWrapperFactory.get().get().create(cursor);
    }
}
