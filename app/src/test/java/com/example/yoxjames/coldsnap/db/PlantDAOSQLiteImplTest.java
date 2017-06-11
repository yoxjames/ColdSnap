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
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.yoxjames.coldsnap.mocks.PlantMockFactory;
import com.example.yoxjames.coldsnap.model.Plant;
import com.example.yoxjames.coldsnap.model.Temperature;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;
import java.util.UUID;

import javax.inject.Provider;

import dagger.Lazy;

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

    @Before
    public void setupTests()
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
        List<Plant> plantList = plantDAO.getPlants(database);

        assertEquals(plantList.get(0).getName(), plantOne.getName());
        assertEquals(plantList.get(0).getScientificName(), plantOne.getScientificName());
        assertEquals(plantList.get(0).getUuid(), plantOne.getUuid());
        assertEquals(plantList.get(0).getMinimumTolerance().compareTo(plantOne.getMinimumTolerance()), 0);

        assertEquals(plantList.get(1).getName(), plantTwo.getName());
        assertEquals(plantList.get(1).getScientificName(), plantTwo.getScientificName());
        assertEquals(plantList.get(1).getUuid(), plantTwo.getUuid());
        assertEquals(plantList.get(1).getMinimumTolerance().compareTo(plantTwo.getMinimumTolerance()), 0);
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
        Plant newPlantOne = plantDAO.getPlant(database, plantOne.getUuid());
        Plant newPlantTwo = plantDAO.getPlant(database, plantTwo.getUuid());

        assertEquals(newPlantOne.getName(), plantOne.getName());
        assertEquals(newPlantOne.getScientificName(), plantOne.getScientificName());
        assertEquals(newPlantOne.getUuid(), plantOne.getUuid());
        assertEquals(newPlantOne.getMinimumTolerance().compareTo(plantOne.getMinimumTolerance()), 0);

        assertEquals(newPlantTwo.getName(), plantTwo.getName());
        assertEquals(newPlantTwo.getScientificName(), plantTwo.getScientificName());
        assertEquals(newPlantTwo.getUuid(), plantTwo.getUuid());
        assertEquals(newPlantTwo.getMinimumTolerance().compareTo(plantTwo.getMinimumTolerance()), 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetPlantNotExists()
    {
        Plant plantOne = PlantMockFactory.getFreezeTolerantPlant();
        Plant plantTwo = PlantMockFactory.getFreezeTenderPlant();

        // Return two plants
        when(plantCursorWrapper.isAfterLast()).thenReturn(false).thenReturn(false).thenReturn(true);
        when(plantCursorWrapper.getPlant()).thenReturn(plantOne).thenReturn(plantTwo);

        PlantDAO plantDAO = new PlantDAOSQLiteImpl(contentValuesProvider, plantCursorWrapperFactory);

        plantDAO.getPlant(database, UUID.randomUUID());
    }

    @Test
    public void testAddPlant()
    {
        Plant plant = PlantMockFactory.getFreezeTolerantPlant();
        PlantDAO plantDAO = new PlantDAOSQLiteImpl(contentValuesProvider, plantCursorWrapperFactory);

        plantDAO.addPlant(database, plant);
        verify(contentValues, times(1)).put(ColdsnapDbSchema.PlantTable.Cols.UUID, plant.getUuid().toString());
        verify(contentValues, times(1)).put(ColdsnapDbSchema.PlantTable.Cols.NAME, plant.getName());
        verify(contentValues, times(1)).put(ColdsnapDbSchema.PlantTable.Cols.SCIENTIFIC_NAME, plant.getScientificName());
        verify(contentValues, times(1)).put(ColdsnapDbSchema.PlantTable.Cols.COLD_THRESHOLD_DEGREES, plant.getMinimumTolerance().getDegreesKelvin());
    }

    @Test
    public void testUpdatePlant()
    {
        Plant plant = PlantMockFactory.getFreezeTolerantPlant();
        Temperature newTemperature = new Temperature(273);
        PlantDAO plantDAO = new PlantDAOSQLiteImpl(contentValuesProvider, plantCursorWrapperFactory);

        plantDAO.updatePlant(database, plant.getUuid(), new Plant("junit", "junitus", newTemperature, plant.getUuid()));
        verify(contentValues, times(1)).put(ColdsnapDbSchema.PlantTable.Cols.UUID, plant.getUuid().toString());
        verify(contentValues, times(1)).put(ColdsnapDbSchema.PlantTable.Cols.NAME, "junit");
        verify(contentValues, times(1)).put(ColdsnapDbSchema.PlantTable.Cols.SCIENTIFIC_NAME, "junitus");
        verify(contentValues, times(1)).put(ColdsnapDbSchema.PlantTable.Cols.COLD_THRESHOLD_DEGREES, 273.0);
    }

}
