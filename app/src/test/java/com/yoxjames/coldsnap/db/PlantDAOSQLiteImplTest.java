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
import android.database.sqlite.SQLiteDatabase;

import com.yoxjames.coldsnap.db.plant.PlantRow;
import com.yoxjames.coldsnap.db.plant.PlantRowCursorWrapper;
import com.yoxjames.coldsnap.db.plant.PlantRowDAO;
import com.yoxjames.coldsnap.db.plant.PlantRowDAOSQLiteImpl;
import com.yoxjames.coldsnap.mocks.PlantMockFactory;
import com.yoxjames.coldsnap.utils.RxColdSnap;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.UUID;

import javax.inject.Provider;

import dagger.Lazy;
import io.reactivex.observers.TestObserver;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@RunWith(MockitoJUnitRunner.class)
public class PlantDAOSQLiteImplTest
{
    @Mock Provider<Lazy<PlantRowCursorWrapper.Factory>> plantCursorWrapperFactory;
    @Mock PlantRowCursorWrapper plantCursorWrapper;
    @Mock Provider<Lazy<ContentValues>> contentValuesProvider;
    @Mock ContentValues contentValues;
    @Mock SQLiteDatabase database;
    @Mock ColdSnapDBHelper dbHelper;

    @BeforeClass
    public static void setupTests()
    {
        RxColdSnap.forceSynchronous();
    }

    @Before
    public void setup()
    {
        when(dbHelper.getReadableDatabase()).thenReturn(database);
        when(dbHelper.getWritableDatabase()).thenReturn(database);
        when(plantCursorWrapperFactory.get()).thenReturn(() -> cursor -> plantCursorWrapper);
        when(contentValuesProvider.get()).thenReturn(() -> contentValues);

        when(plantCursorWrapper.moveToFirst()).thenReturn(true);
        when(plantCursorWrapper.moveToNext()).thenReturn(true);
    }

    @Test
    public void testGetPlants()
    {
        PlantRow plantOne = PlantMockFactory.getFreezeTolerantPlantRow();
        PlantRow plantTwo = PlantMockFactory.getFreezeTenderPlantRow();

        // Return two plants
        when(plantCursorWrapper.isAfterLast()).thenReturn(false).thenReturn(false).thenReturn(true);
        when(plantCursorWrapper.getPlant()).thenReturn(plantOne).thenReturn(plantTwo);

        PlantRowDAO plantDAO = new PlantRowDAOSQLiteImpl(contentValuesProvider, plantCursorWrapperFactory, dbHelper);

        TestObserver<PlantRow> plantList = plantDAO.getPlantRows().test();

        plantList.assertResult(plantOne, plantTwo);
    }

    @Test
    public void testGetPlant()
    {
        PlantRow plantOne = PlantMockFactory.getFreezeTolerantPlantRow();
        PlantRow plantTwo = PlantMockFactory.getFreezeTenderPlantRow();

        // Return two plants
        when(plantCursorWrapper.isAfterLast()).thenReturn(false).thenReturn(false).thenReturn(true);
        when(plantCursorWrapper.getPlant()).thenReturn(plantOne).thenReturn(plantTwo);

        PlantRowDAO plantDAO = new PlantRowDAOSQLiteImpl(contentValuesProvider, plantCursorWrapperFactory, dbHelper);
        TestObserver<PlantRow> newPlantOne = plantDAO.getPlantRow(plantOne.getUuid()).test();
        TestObserver<PlantRow> newPlantTwo = plantDAO.getPlantRow(plantTwo.getUuid()).test();

        newPlantOne.assertResult(plantOne);
        newPlantTwo.assertResult(plantTwo);
    }

    @Test
    public void testGetPlantNotExists()
    {
        PlantRow plantOne = PlantMockFactory.getFreezeTolerantPlantRow();
        PlantRow plantTwo = PlantMockFactory.getFreezeTenderPlantRow();

        // Return two plants
        when(plantCursorWrapper.isAfterLast()).thenReturn(false).thenReturn(false).thenReturn(true);
        when(plantCursorWrapper.getPlant()).thenReturn(plantOne).thenReturn(plantTwo);

        when(plantCursorWrapper.moveToFirst()).thenReturn(false); // Cursor is empty in this test!

        PlantRowDAO plantDAO = new PlantRowDAOSQLiteImpl(contentValuesProvider, plantCursorWrapperFactory, dbHelper);

        TestObserver<PlantRow> result = plantDAO.getPlantRow(UUID.randomUUID().toString()).test();
        result.assertError(IllegalArgumentException.class);
    }

    @Test
    public void testAddPlant()
    {
        PlantRow plant = PlantMockFactory.getFreezeTolerantPlantRow();
        PlantRowDAO plantDAO = new PlantRowDAOSQLiteImpl(contentValuesProvider, plantCursorWrapperFactory, dbHelper);

        TestObserver testObserver = plantDAO.addPlantRow(plant).test();
        verify(contentValues, times(1)).put(ColdsnapDbSchema.PlantTable.Cols.UUID, plant.getUuid());
        verify(contentValues, times(1)).put(ColdsnapDbSchema.PlantTable.Cols.NAME, plant.getName());
        verify(contentValues, times(1)).put(ColdsnapDbSchema.PlantTable.Cols.SCIENTIFIC_NAME, plant.getScientificName());
        verify(contentValues, times(1)).put(ColdsnapDbSchema.PlantTable.Cols.COLD_THRESHOLD_DEGREES, plant.getColdThresholdK());
        testObserver.assertComplete();
    }

    @Test
    public void testUpdatePlant()
    {
        PlantRow plant = PlantMockFactory.getFreezeTolerantPlantRow();
        PlantRowDAO plantDAO = new PlantRowDAOSQLiteImpl(contentValuesProvider, plantCursorWrapperFactory, dbHelper);

        PlantRow newPlantRow = new PlantRow.Builder()
                .name("junit")
                .scientificName("junitus")
                .mainImageUUID(UUID.randomUUID().toString())
                .uuid(plant.getUuid())
                .coldThresholdK(270.0)
                .build();

        TestObserver testObserver = plantDAO.updatePlantRow(plant.getUuid(), newPlantRow).test();
        verify(contentValues, times(1)).put(ColdsnapDbSchema.PlantTable.Cols.UUID, plant.getUuid());
        verify(contentValues, times(1)).put(ColdsnapDbSchema.PlantTable.Cols.NAME, "junit");
        verify(contentValues, times(1)).put(ColdsnapDbSchema.PlantTable.Cols.SCIENTIFIC_NAME, "junitus");
        verify(contentValues, times(1)).put(ColdsnapDbSchema.PlantTable.Cols.COLD_THRESHOLD_DEGREES, 270.0);
        testObserver.assertComplete();
    }

    @Test
    public void testDeletePlant()
    {
        PlantRow plant = PlantMockFactory.getFreezeTolerantPlantRow();
        PlantRowDAO plantDAO = new PlantRowDAOSQLiteImpl(contentValuesProvider, plantCursorWrapperFactory, dbHelper);
        TestObserver testObserver = plantDAO.deletePlantRow(plant.getUuid()).test();
        verify(database, times(1)).delete(ColdsnapDbSchema.PlantTable.NAME, ColdsnapDbSchema.PlantTable.Cols.UUID + " = ?", new String[] { plant.getUuid().toString() });
        testObserver.assertComplete();
    }
}
