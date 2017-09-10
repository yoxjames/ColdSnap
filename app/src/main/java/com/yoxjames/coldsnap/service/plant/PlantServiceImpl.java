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

import com.yoxjames.coldsnap.db.ColdSnapDBHelper;
import com.yoxjames.coldsnap.db.PlantDAO;
import com.yoxjames.coldsnap.model.Plant;

import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;

/**
 * Created by yoxjames on 8/26/17.
 */

@Singleton
public class PlantServiceImpl implements PlantService
{
    private final PlantDAO plantDAO;
    private final ColdSnapDBHelper coldSnapDBHelper;

    @Inject
    public PlantServiceImpl(ColdSnapDBHelper coldSnapDBHelper, PlantDAO plantDAO)
    {
        this.plantDAO = plantDAO;
        this.coldSnapDBHelper = coldSnapDBHelper;
    }

    @Override
    public Observable<Plant> getPlants()
    {
        return plantDAO.getPlants(coldSnapDBHelper.getReadableDatabase()).share();
    }

    @Override
    public Single<Plant> getPlant(UUID plantUUID)
    {
        return plantDAO.getPlant(coldSnapDBHelper.getReadableDatabase(), plantUUID);
    }

    @Override
    public Completable addPlant(Plant plant)
    {
        return plantDAO.addPlant(coldSnapDBHelper.getWritableDatabase(), plant);
    }

    @Override
    public Completable updatePlant(Plant plant)
    {
        return plantDAO.updatePlant(coldSnapDBHelper.getWritableDatabase(), plant.getUuid(), plant);
    }

    @Override
    public Completable deletePlant(Plant plant)
    {
        return plantDAO.deletePlant(coldSnapDBHelper.getWritableDatabase(), plant.getUuid());
    }
}
