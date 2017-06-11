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

package com.example.yoxjames.coldsnap.mocks;

import com.example.yoxjames.coldsnap.model.Plant;
import com.example.yoxjames.coldsnap.model.Temperature;

import java.util.ArrayList;
import java.util.List;

public class PlantMockFactory
{
    public static List<Plant> getMockPlantList()
    {
        List<Plant> plantList = new ArrayList<>();
        plantList.add(getFreezeTenderPlant());
        plantList.add(getFreezeTolerantPlant());
        return plantList;
    }

    public static Plant getFreezeTolerantPlant()
    {
        return new Plant("Tolerant Test", "Unitus tolerantus", Temperature.newTemperatureFromF(20));
    }

    public static Plant getFreezeTenderPlant()
    {
        return new Plant("Tender Test", "Unitus tenderus", Temperature.newTemperatureFromF(32));
    }
}
