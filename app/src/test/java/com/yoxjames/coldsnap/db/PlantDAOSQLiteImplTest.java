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

import com.yoxjames.coldsnap.mocks.PlantMockFactory;
import com.yoxjames.coldsnap.model.Plant;
import com.yoxjames.coldsnap.model.Temperature;
import com.yoxjames.coldsnap.utils.RxColdSnap;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;
import java.util.UUID;

import javax.inject.Provider;

import dagger.Lazy;
import io.reactivex.observers.TestObserver;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@RunWith(MockitoJUnitRunner.class)
public class PlantDAOSQLiteImplTest
{

    @Mock Provider<Lazy<PlantCursorWrapper.Factory>> plantCursorWrapperFactory;
    @Mock PlantCursorWrapper plantCursorWrapper;
    @Mock Provider<Lazy<ContentValues>> contentValuesProvider;
    @Mock ContentValues contentValues;
    @Mock SQLiteDatabase database;

    @BeforeClass
    public static void setupTests()
    {
        RxColdSnap.forceSynchronous();
    }

    @Before
    public void setup()
    {
        when(plantCursorWrapperFactory.get()).thenReturn(new Lazy<PlantCursorWrapper.Factory>()
        {
            @Override
            public PlantCursorWrapper.Factory get()
            {
                return new PlantCursorWrapper.Factory()
                {
                    @Override
                    public PlantCursorWrapper create(Cursor cursor)
                    {
                        return plantCursorWrapper;
                    }
                };
            }
        });

        when(contentValuesProvider.get()).thenReturn(new Lazy<ContentValues>()
        {
            @Override
            public ContentValues get()
            {
                return contentValues;
            }
        });

        when(plantCursorWrapper.moveToFirst()).thenReturn(true);
        when(plantCursorWrapper.moveToNext()).thenReturn(true);
    }

    @Test
    public void testGetPlants()
    {
        Plant plantOne = PlantMockFactory.getFreezeTolerantPlant();
        Plant plantTwo = PlantMockFactory.getFreezeTenderPlant();

        // Return two plants
        when(plantCursorWrapper.isAfterLast()).thenReturn(false).thenReturn(false).thenReturn(true);
        when(plantCursorWrapper.getPlant()).thenReturn(plantOne).thenReturn(plantTwo);

        PlantDAO plantDAO = new PlantDAOSQLiteImpl(contentValuesProvider, plantCursorWrapperFactory);

        TestObserver<Plant> plantList = plantDAO.getPlants(database).test();

        plantList.assertResult(plantOne, plantTwo);
    }

    @Test
    public void testGetPlant()
    {
        Plant plantOne = PlantMockFactory.getFreezeTolerantPlant();
        Plant plantTwo = PlantMockFactory.getFreezeTenderPlant();

        // Return two plants
        when(plantCursorWrapper.isAfterLast()).thenReturn(false).thenReturn(false).thenReturn(true);
        when(plantCursorWrapper.getPlant()).thenReturn(plantOne).thenReturn(plantTwo);

        PlantDAO plantDAO = new PlantDAOSQLiteImpl(contentValuesProvider, plantCursorWrapperFactory);
        TestObserver<Plant> newPlantOne = plantDAO.getPlant(database, plantOne.getUuid()).test();
        TestObserver<Plant> newPlantTwo = plantDAO.getPlant(database, plantTwo.getUuid()).test();

        newPlantOne.assertResult(plantOne);
        newPlantTwo.assertResult(plantTwo);
    }

    @Test
    public void testGetPlantNotExists()
    {
        Plant plantOne = PlantMockFactory.getFreezeTolerantPlant();
        Plant plantTwo = PlantMockFactory.getFreezeTenderPlant();

        // Return two plants
        when(plantCursorWrapper.isAfterLast()).thenReturn(false).thenReturn(false).thenReturn(true);
        when(plantCursorWrapper.getPlant()).thenReturn(plantOne).thenReturn(plantTwo);

        when(plantCursorWrapper.moveToFirst()).thenReturn(false); // Cursor is empty in this test!

        PlantDAO plantDAO = new PlantDAOSQLiteImpl(contentValuesProvider, plantCursorWrapperFactory);

        TestObserver<Plant> result = plantDAO.getPlant(database, UUID.randomUUID()).test();
        result.assertError(IllegalArgumentException.class);
    }

    @Test
    public void testAddPlant()
    {
        Plant plant = PlantMockFactory.getFreezeTolerantPlant();
        PlantDAO plantDAO = new PlantDAOSQLiteImpl(contentValuesProvider, plantCursorWrapperFactory);

        TestObserver testObserver = plantDAO.addPlant(database, plant).test();
        verify(contentValues, times(1)).put(ColdsnapDbSchema.PlantTable.Cols.UUID, plant.getUuid().toString());
        verify(contentValues, times(1)).put(ColdsnapDbSchema.PlantTable.Cols.NAME, plant.getName());
        verify(contentValues, times(1)).put(ColdsnapDbSchema.PlantTable.Cols.SCIENTIFIC_NAME, plant.getScientificName());
        verify(contentValues, times(1)).put(ColdsnapDbSchema.PlantTable.Cols.COLD_THRESHOLD_DEGREES, plant.getMinimumTolerance().getDegreesKelvin());
        testObserver.assertComplete();
    }

    @Test
    public void testUpdatePlant()
    {
        Plant plant = PlantMockFactory.getFreezeTolerantPlant();
        Temperature newTemperature = new Temperature(273);
        PlantDAO plantDAO = new PlantDAOSQLiteImpl(contentValuesProvider, plantCursorWrapperFactory);

        TestObserver testObserver = plantDAO.updatePlant(database, plant.getUuid(), new Plant("junit", "junitus", newTemperature, plant.getUuid())).test();
        verify(contentValues, times(1)).put(ColdsnapDbSchema.PlantTable.Cols.UUID, plant.getUuid().toString());
        verify(contentValues, times(1)).put(ColdsnapDbSchema.PlantTable.Cols.NAME, "junit");
        verify(contentValues, times(1)).put(ColdsnapDbSchema.PlantTable.Cols.SCIENTIFIC_NAME, "junitus");
        verify(contentValues, times(1)).put(ColdsnapDbSchema.PlantTable.Cols.COLD_THRESHOLD_DEGREES, 273.0);
        testObserver.assertComplete();
    }

    @Test
    public void testDeletePlant()
    {
        Plant plant = PlantMockFactory.getFreezeTolerantPlant();
        PlantDAO plantDAO = new PlantDAOSQLiteImpl(contentValuesProvider, plantCursorWrapperFactory);
        TestObserver testObserver = plantDAO.deletePlant(database, plant.getUuid()).test();
        verify(database, times(1)).delete(ColdsnapDbSchema.PlantTable.NAME, ColdsnapDbSchema.PlantTable.Cols.UUID + " = ?", new String[] { plant.getUuid().toString() });
        testObserver.assertComplete();
    }
}
