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

import com.yoxjames.coldsnap.db.plant.PlantRow;
import com.yoxjames.coldsnap.db.plant.PlantRowDAO;
import com.yoxjames.coldsnap.model.Plant;
import com.yoxjames.coldsnap.model.Temperature;
import com.yoxjames.coldsnap.service.ActionReply;

import java.util.UUID;

import javax.inject.Inject;

import dagger.Reusable;
import io.reactivex.Observable;

/**
 * Created by yoxjames on 10/14/17.
 */
@Reusable
public class PlantDAOImpl implements PlantDAO
{
    private final PlantRowDAO plantRowDAO;

    @Inject
    public PlantDAOImpl(PlantRowDAO plantRowDAO)
    {
        this.plantRowDAO = plantRowDAO;
    }

    @Override
    public Observable<Plant> getPlants()
    {
        return plantRowDAO.getPlantRows().map(this::plantFromRow);
    }

    @Override
    public Observable<Plant> getPlant(UUID plantUUID)
    {
        return plantRowDAO.getPlantRow(plantUUID.toString()).map(this::plantFromRow);
    }

    @Override
    public Observable<ActionReply> addPlant(Plant plant)
    {
        return plantRowDAO.addPlantRow(new PlantRow.Builder()
                .uuid(plant.getUuid().toString())
                .name(plant.getName())
                .scientificName(plant.getScientificName())
                .coldThresholdK(plant.getMinimumTolerance().getDegreesKelvin())
                .build());

    }

    @Override
    public Observable<ActionReply> deletePlant(UUID plantUUID)
    {
        return plantRowDAO.deletePlantRow(plantUUID.toString());
    }

    @Override
    public Observable<ActionReply> replacePlant(UUID oldPlantUUID, Plant newPlant)
    {
        return plantRowDAO.updatePlantRow(oldPlantUUID.toString(), new PlantRow.Builder()
                        .uuid(newPlant.getUuid().toString())
                        .name(newPlant.getName())
                        .scientificName(newPlant.getScientificName())
                        .coldThresholdK(newPlant.getMinimumTolerance().getDegreesKelvin())
                        .mainImageUUID((newPlant.getMainImageUUID() != null) ? newPlant.getMainImageUUID().toString() : null)
                        .build());
    }

    private Plant plantFromRow(PlantRow plantRow)
    {
        return new Plant(plantRow.getName(),
                plantRow.getScientificName(),
                new Temperature(plantRow.getColdThresholdK(), 0.0),
                UUID.fromString(plantRow.getUuid()),
                (plantRow.getMainImageUUID() == null) ? null : UUID.fromString(plantRow.getMainImageUUID()));

    }
}
