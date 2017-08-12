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

package com.example.yoxjames.coldsnap.service.plant;

import android.database.sqlite.SQLiteDatabase;

import com.example.yoxjames.coldsnap.db.ColdSnapDBHelper;
import com.example.yoxjames.coldsnap.db.PlantDAO;
import com.example.yoxjames.coldsnap.model.Plant;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.internal.Preconditions;

@Singleton
public class PlantServiceImpl implements PlantService
{
    private final PlantDAO dbService;
    private final ColdSnapDBHelper dbHelper;
    private final Map<UUID, Plant> cachedPlants;
    private Plant pendingPlant;

    @Inject
    public PlantServiceImpl(PlantDAO dbService, ColdSnapDBHelper dbHelper)
    {
        final SQLiteDatabase database = dbHelper.getReadableDatabase();
        this.dbService = Preconditions.checkNotNull(dbService);
        this.dbHelper = Preconditions.checkNotNull(dbHelper);
        this.cachedPlants = new ConcurrentHashMap<>();
        final List<Plant> dbResults = dbService.getPlants(database);
        for (Plant plant : dbResults)
            cachedPlants.put(plant.getUuid(), plant);
    }

    @Override
    public List<Plant> getMyPlants()
    {
        return new ArrayList<>(cachedPlants.values());
    }

    @Override
    public void addPlant(Plant plant)
    {
        Preconditions.checkNotNull(plant);

        final SQLiteDatabase database = dbHelper.getWritableDatabase();
        dbService.addPlant(database, plant);
        cachedPlants.put(plant.getUuid(), plant);
    }

    @Override
    public void cachePlant(Plant plant)
    {
        Preconditions.checkNotNull(plant);

        pendingPlant = plant;
    }

    @Override
    public Plant getPlant(UUID plantUUID)
    {
        Preconditions.checkNotNull(plantUUID);

        final Plant plant = cachedPlants.get(plantUUID);
        if (plant == null && pendingPlant != null && pendingPlant.getUuid().equals(plantUUID))
            return pendingPlant;
        if (plant == null)
            throw new IllegalArgumentException("UUID:" + plantUUID + " Not Found");
        return plant;
    }

    @Override
    public void updatePlant(UUID plantUUID, Plant newPlant)
    {
        Preconditions.checkNotNull(plantUUID);
        Preconditions.checkNotNull(newPlant);

        final SQLiteDatabase database = dbHelper.getWritableDatabase();
        dbService.updatePlant(database, plantUUID, newPlant);
        cachedPlants.put(plantUUID, newPlant);

    }

    @Override
    public void deletePlant(UUID plantUUID)
    {
        Preconditions.checkNotNull(plantUUID);

        final SQLiteDatabase database = dbHelper.getWritableDatabase();
        dbService.deletePlant(database, plantUUID);
        cachedPlants.remove(plantUUID);
    }
}
