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

package com.yoxjames.coldsnap.ui.plantlist;

import com.yoxjames.coldsnap.model.Plant;

import java.util.UUID;

import javax.annotation.Nullable;

import dagger.internal.Preconditions;

/**
 * Created by yoxjames on 10/15/17.
 */

class PlantListItemViewModel
{
    enum Status
    {
        HAPPY, NEUTRAL, SAD, DEAD, PENDING, ERROR
    }

    private final String name;
    private final String scientificName;
    private final Status status;
    private final UUID uuid;
    private final Throwable error;
    private final UUID imageUUID;

    private PlantListItemViewModel(String name, String scientificName, Status status, @Nullable UUID uuid, @Nullable Throwable error, UUID imageUUID)
    {
        this.name = Preconditions.checkNotNull(name);
        this.scientificName = Preconditions.checkNotNull(scientificName);
        this.status = Preconditions.checkNotNull(status);
        this.uuid = uuid;
        this.error = error;
        this.imageUUID = imageUUID;
    }

    public String getPlantName()
    {
        return name;
    }

    public String getPlantScientificName()
    {
        return scientificName;
    }

    public Status getPlantStatus()
    {
        return status;
    }

    public UUID getUUID()
    {
        return uuid;
    }

    public Throwable getError()
    {
        return error;
    }

    public UUID getImageFileName()
    {
        return imageUUID;
    }

    public static PlantListItemViewModel pendingPlant(Plant plant)
    {
        return new PlantListItemViewModel(plant.getName(), plant.getScientificName(), Status.PENDING, plant.getUuid(), null, plant.getMainImageUUID());
    }

    public static PlantListItemViewModel statusPlant(Plant plant, Status status)
    {
        return new PlantListItemViewModel(plant.getName(), plant.getScientificName(), status, plant.getUuid(), null, plant.getMainImageUUID());
    }

    public static PlantListItemViewModel errorPlant(Plant plant, Throwable e)
    {
        return new PlantListItemViewModel(plant.getName(), plant.getScientificName(), Status.ERROR, plant.getUuid(), e, plant.getMainImageUUID());
    }

    public static PlantListItemViewModel errorPlant(Throwable e)
    {
        return new PlantListItemViewModel("", "", Status.ERROR, null, e, null);
    }

    private static String getFileFromUUID(UUID plantImageUUID)
    {
        if (plantImageUUID == null)
            return null;
        return "IMG_" + plantImageUUID.toString() + ".jpg";
    }
}
