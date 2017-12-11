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

package com.yoxjames.coldsnap.model;

import org.junit.Test;

import java.util.UUID;

import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PlantTest
{
    @Test
    public void testHappyPath()
    {
        final String testName = "Test Plant";
        final String testScientificName = "Junitis plantus";
        final Temperature testMinTemp = Temperature.newTemperatureFromF(32);
        final UUID testUUID = UUID.randomUUID();
        final UUID mainImageUUID = UUID.randomUUID();

        final Plant testPlant = new Plant(testName, testScientificName, testMinTemp, testUUID, mainImageUUID);

        assertEquals(testName, testPlant.getName());
        assertEquals(testScientificName, testPlant.getScientificName());
        assertEquals(testMinTemp, testPlant.getMinimumTolerance());
        assertEquals(testUUID, testPlant.getUuid());
    }

    @Test
    public void testDefaultPlant()
    {
        final String defaultName = "";
        final String defaultScientificName = "";
        final Temperature defaultMinTemp = Temperature.newTemperatureFromF(32);


        final Plant testPlant = new Plant(defaultName, defaultScientificName);

        assertEquals(defaultName, testPlant.getName());
        assertEquals(defaultScientificName, testPlant.getScientificName());
        assertEquals(defaultMinTemp.compareTo(testPlant.getMinimumTolerance()), 0);
        assertNotNull(testPlant.getUuid());
        assertNull(testPlant.getMainImageUUID());
    }
}
