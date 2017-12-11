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

package com.yoxjames.coldsnap.db.image;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yoxjames.coldsnap.db.ColdSnapDBHelper;
import com.yoxjames.coldsnap.db.ColdsnapDbSchema;
import com.yoxjames.coldsnap.service.ActionReply;

import javax.inject.Inject;
import javax.inject.Provider;

import dagger.Lazy;
import dagger.Reusable;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by yoxjames on 10/14/17.
 */
@Reusable
public class PlantImageRowDAOSQLiteImpl implements PlantImageRowDAO
{
    private final Provider<Lazy<ContentValues>> contentValuesProvider;
    private final Provider<Lazy<PlantImageRowCursorWrapper.Factory>> plantImageCursorWrapperFactory;
    private final ColdSnapDBHelper dbHelper;

    @Inject
    public PlantImageRowDAOSQLiteImpl(Provider<Lazy<ContentValues>> contentValuesProvider, Provider<Lazy<PlantImageRowCursorWrapper.Factory>> plantImageCursorWrapperFactory, final ColdSnapDBHelper dbHelper)
    {
        this.contentValuesProvider = contentValuesProvider;
        this.plantImageCursorWrapperFactory = plantImageCursorWrapperFactory;
        this.dbHelper = dbHelper;
    }

    @Override
    public Observable<PlantImageRow> getImageForPlant(String plantUUID)
    {
        return Observable.create((ObservableOnSubscribe<PlantImageRow>) e ->
        {
            final SQLiteDatabase database = dbHelper.getReadableDatabase();
            try (PlantImageRowCursorWrapper cursor = queryPlantImageData(database, "plant_uuid = ?", new String[] { plantUUID }))
            {
                cursor.moveToFirst();
                while (!cursor.isAfterLast())
                {
                    e.onNext(cursor.getPlantImageRow());
                    cursor.moveToNext();
                }
            }
            finally
            {
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<PlantImageRow> getImage(String uuid)
    {
        return Observable.create((ObservableOnSubscribe<PlantImageRow>) e ->
        {
            final SQLiteDatabase database = dbHelper.getReadableDatabase();
            try (PlantImageRowCursorWrapper cursor = queryPlantImageData(database, "image_uuid = ?", new String[] { uuid }))
            {
                cursor.moveToFirst();
                while (!cursor.isAfterLast())
                {
                    e.onNext(cursor.getPlantImageRow());
                    cursor.moveToNext();
                }
            }
            finally
            {
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<ActionReply> replaceImage(String imageUUID, PlantImageRow newPlantImage)
    {
        return Observable.fromCallable(() ->
        {
            final SQLiteDatabase database = dbHelper.getWritableDatabase();
            final ContentValues values = contentValuesProvider.get().get();
            if (!imageUUID.equals(newPlantImage.getUuid()))
                throw new IllegalArgumentException("imageUUID must be the same UUID as newPlantImage's UUID");

            values.put(ColdsnapDbSchema.ImageTable.Cols.UUID, newPlantImage.getUuid());
            values.put(ColdsnapDbSchema.ImageTable.Cols.PLANT_UUID, newPlantImage.getPlantUUID());
            values.put(ColdsnapDbSchema.ImageTable.Cols.TITLE, newPlantImage.getTitle());
            values.put(ColdsnapDbSchema.ImageTable.Cols.IMAGE_FILENAME, newPlantImage.getImageFilename());
            values.put(ColdsnapDbSchema.ImageTable.Cols.IMAGE_DATE, newPlantImage.getImageDate());

            database.update(ColdsnapDbSchema.ImageTable.NAME, values, ColdsnapDbSchema.ImageTable.Cols.UUID + " = ?",
                    new String[]{imageUUID});
            return ActionReply.genericSuccess();

        }).subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<ActionReply> deleteImage(String plantImageUUID)
    {
        return Observable.fromCallable(() ->
                {
                    final SQLiteDatabase database = dbHelper.getWritableDatabase();
                    database.delete(ColdsnapDbSchema.ImageTable.NAME, ColdsnapDbSchema.ImageTable.Cols.UUID + " = ?",
                            new String[]{plantImageUUID});
                    return ActionReply.genericSuccess();
                })
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<ActionReply> addImage(PlantImageRow plantImageRow)
    {
        return Observable.fromCallable(() ->
        {
            final SQLiteDatabase database = dbHelper.getWritableDatabase();
            final ContentValues values = contentValuesProvider.get().get();

            values.put(ColdsnapDbSchema.ImageTable.Cols.UUID, plantImageRow.getUuid());
            values.put(ColdsnapDbSchema.ImageTable.Cols.PLANT_UUID, plantImageRow.getPlantUUID());
            values.put(ColdsnapDbSchema.ImageTable.Cols.TITLE, plantImageRow.getTitle());
            values.put(ColdsnapDbSchema.ImageTable.Cols.IMAGE_FILENAME, plantImageRow.getImageFilename());
            values.put(ColdsnapDbSchema.ImageTable.Cols.IMAGE_DATE, plantImageRow.getImageDate());

            database.insert(ColdsnapDbSchema.ImageTable.NAME, null, values);

            return ActionReply.genericSuccess();
        }).subscribeOn(Schedulers.io());
    }

    private PlantImageRowCursorWrapper queryPlantImageData(SQLiteDatabase database, String whereClause, String[] whereArgs)
    {
        final Cursor cursor = database.query(ColdsnapDbSchema.ImageTable.NAME,
                                            null,
                                            whereClause,
                                            whereArgs,
                                            null,
                                            null,
                                            null);

        return plantImageCursorWrapperFactory.get().get().create(cursor);
    }
}
