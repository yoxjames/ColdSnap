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

package com.yoxjames.coldsnap.service.plant;

import com.yoxjames.coldsnap.db.PlantDAO;
import com.yoxjames.coldsnap.model.Plant;
import com.yoxjames.coldsnap.service.ActionReply;

import java.util.UUID;

import javax.inject.Inject;

import dagger.Reusable;
import io.reactivex.Observable;

/**
 * Created by yoxjames on 8/26/17.
 */

@Reusable
public class PlantServiceImpl implements PlantService
{
    private final PlantDAO plantDAO;

    @Inject
    public PlantServiceImpl(PlantDAO plantDAO)
    {
        this.plantDAO = plantDAO;
    }

    @Override
    public Observable<Plant> getPlants()
    {
        return plantDAO.getPlants();
    }

    @Override
    public Observable<Plant> getPlant(UUID plantUUID)
    {
        return plantDAO.getPlant(plantUUID);
    }

    @Override
    public Observable<ActionReply> savePlant(Plant plant)
    {
        return plantDAO.savePlant(plant);
    }

    @Override
    public Observable<ActionReply> deletePlant(UUID plantUUID)
    {
        return plantDAO.deletePlant(plantUUID);
    }
}
