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

package com.example.yoxjames.coldsnap.ui.presenter;

import com.example.yoxjames.coldsnap.service.plant.PlantService;

import java.util.UUID;

public class PlantDetailPagerPresenterImpl implements PlantDetailPagerPresenter
{
    private final PlantService plantService;

    public PlantDetailPagerPresenterImpl(PlantService plantService)
    {
        this.plantService = plantService;
    }

    @Override
    public int getPlantsSize()
    {
        return plantService.getMyPlants().size();
    }

    @Override
    public UUID getPlant(int plantIndex)
    {
        return plantService.getMyPlants().get(plantIndex).getUuid();
    }
}
