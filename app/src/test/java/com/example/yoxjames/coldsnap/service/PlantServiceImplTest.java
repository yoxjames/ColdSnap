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

package com.example.yoxjames.coldsnap.service;

import android.database.sqlite.SQLiteDatabase;

import com.example.yoxjames.coldsnap.db.ColdSnapDBHelper;
import com.example.yoxjames.coldsnap.db.PlantDAO;
import com.example.yoxjames.coldsnap.model.Plant;
import com.example.yoxjames.coldsnap.model.Temperature;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PlantServiceImplTest
{
    private static Plant BLANK_PLANT = new Plant("","");
    private List<Plant> fakeDatabase;
    private PlantDAO plantDAO = mock(PlantDAO.class);
    private ColdSnapDBHelper coldSnapDBHelper = mock(ColdSnapDBHelper.class);
    private SQLiteDatabase sqLiteDatabase = mock(SQLiteDatabase.class);

    public PlantServiceImplTest()
    {
        fakeDatabase = new ArrayList<>();
        fakeDatabase.add(BLANK_PLANT);
        when(coldSnapDBHelper.getReadableDatabase()).thenReturn(sqLiteDatabase);
        when(coldSnapDBHelper.getWritableDatabase()).thenReturn(sqLiteDatabase);
        when(plantDAO.getPlants(sqLiteDatabase)).thenReturn(fakeDatabase);
    }

    @Test
    public void testInitialGetPlants()
    {
        PlantServiceImpl plantService = new PlantServiceImpl(plantDAO, coldSnapDBHelper);
        assertEquals(fakeDatabase, plantService.getMyPlants());
    }

    @Test
    public void testAddPlant()
    {
        Plant newPlant = new Plant("Test", "Junitus test", Temperature.newTemperatureFromF(32), UUID.randomUUID());
        List<Plant> expectedPlants = new ArrayList<>();
        expectedPlants.add(BLANK_PLANT);
        expectedPlants.add(newPlant);
        PlantServiceImpl plantService = new PlantServiceImpl(plantDAO, coldSnapDBHelper);
        plantService.addPlant(newPlant);

        verify(plantDAO, times(1)).addPlant(sqLiteDatabase, newPlant);
        assertTrue(expectedPlants.containsAll(plantService.getMyPlants()));

    }

    @Test
    public void testCachePlant()
    {
        Plant newPlant = new Plant("Test", "Junitus test", Temperature.newTemperatureFromF(32), UUID.randomUUID());
        List<Plant> expectedPlants = new ArrayList<>();
        expectedPlants.add(BLANK_PLANT);
        PlantServiceImpl plantService = new PlantServiceImpl(plantDAO, coldSnapDBHelper);
        plantService.cachePlant(newPlant);

        verify(plantDAO, times(0)).addPlant(sqLiteDatabase, newPlant);
        assertTrue(expectedPlants.containsAll(plantService.getMyPlants()));
        assertEquals(newPlant, plantService.getPlant(newPlant.getUuid()));
    }

    @Test
    public void testDeletePlant()
    {
        List<Plant> expectedPlants = new ArrayList<>();

        PlantServiceImpl plantService = new PlantServiceImpl(plantDAO, coldSnapDBHelper);
        plantService.deletePlant(BLANK_PLANT.getUuid());

        verify(plantDAO, times(1)).deletePlant(sqLiteDatabase, BLANK_PLANT.getUuid());
        assertTrue(expectedPlants.containsAll(plantService.getMyPlants()));
    }

    @Test
    public void testUpdatePlant()
    {
        Plant newPlant = new Plant("Test", "Junitus test", Temperature.newTemperatureFromF(32), UUID.randomUUID());
        List<Plant> expectedPlants = new ArrayList<>();
        expectedPlants.add(newPlant);

        PlantServiceImpl plantService = new PlantServiceImpl(plantDAO, coldSnapDBHelper);
        plantService.updatePlant(BLANK_PLANT.getUuid(), newPlant);

        verify(plantDAO, times(1)).updatePlant(sqLiteDatabase, BLANK_PLANT.getUuid(), newPlant);
        assertTrue(expectedPlants.containsAll(plantService.getMyPlants()));
    }

    @Test
    public void testGetPlantThatExists()
    {
        PlantServiceImpl plantService = new PlantServiceImpl(plantDAO, coldSnapDBHelper);
        Plant actualPlant = plantService.getPlant(BLANK_PLANT.getUuid());

        assertEquals(BLANK_PLANT, actualPlant);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetPlantThatDoesNotExist()
    {
        new PlantServiceImpl(plantDAO, coldSnapDBHelper).getPlant(UUID.randomUUID());
    }
}
