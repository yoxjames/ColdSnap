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

package com.yoxjames.coldsnap.ui.plantdetail;

import com.yoxjames.coldsnap.model.Plant;
import com.yoxjames.coldsnap.model.PlantImage;
import com.yoxjames.coldsnap.service.ActionReply;
import com.yoxjames.coldsnap.service.image.ImageService;
import com.yoxjames.coldsnap.service.plant.PlantService;

import java.util.UUID;

import javax.inject.Inject;

import io.reactivex.Observable;

/**
 * Created by yoxjames on 11/19/17.
 */

public class PlantDetailActivityDataProviderImpl implements PlantDetailActivityDataProvider
{
    private final PlantService plantService;
    private final ImageService imageService;

    @Inject
    public PlantDetailActivityDataProviderImpl(PlantService plantService, ImageService imageService)
    {
        this.plantService = plantService;
        this.imageService = imageService;
    }

    @Override
    public Observable<Plant> getPlant(UUID plantUUID)
    {
        return plantService.getPlant(plantUUID);
    }

    @Override
    public Observable<PlantImage> getPlantImage(UUID plantImageUUID)
    {
        return imageService.getPlantImage(plantImageUUID);
    }

    @Override
    public Observable<ActionReply> savePlant(UUID plantUUID, Plant plant)
    {
        return plantService.updatePlant(plantUUID, plant);
    }

    @Override
    public Observable<ActionReply> saveImage(UUID imageUUID, PlantImage plantImage)
    {
        return imageService.savePlantImage(plantImage);
    }

    @Override
    public Observable<ActionReply> deletePlant(UUID plantUUID)
    {
        return plantService.deletePlant(plantUUID);
    }

    @Override
    public Observable<ActionReply> addPlant(final Plant plant)
    {
        return plantService.addPlant(plant);
    }
}
