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

import java.util.UUID;

import dagger.internal.Preconditions;

/**
 * Created by yoxjames on 11/5/17.
 */

public class PlantDetailViewModel
{
    private final String name;
    private final String scientificName;
    private final int minimumTemperature;
    private final boolean isNewPlant;
    private final UUID plantUUID;

    private PlantDetailViewModel(String name, String scientificName, int minimumTemperature, boolean isNewPlant, UUID plantUUID)
    {
        this.name = Preconditions.checkNotNull(name);
        this.scientificName = Preconditions.checkNotNull(scientificName);
        this.minimumTemperature = Preconditions.checkNotNull(minimumTemperature);
        this.isNewPlant = isNewPlant;
        this.plantUUID = Preconditions.checkNotNull(plantUUID);
    }

    public String getName()
    {
        return name;
    }

    public String getScientificName()
    {
        return scientificName;
    }

    public int getMinimumTemperature()
    {
        return minimumTemperature;
    }

    public boolean isNewPlant()
    {
        return isNewPlant;
    }

    public static PlantDetailViewModel newPlant(int minimumTemperature)
    {
        return new PlantDetailViewModel("", "", minimumTemperature, true, UUID.randomUUID());
    }

    public static PlantDetailViewModel existingPlant(String name, String scientificName, int minimumTemperature, UUID plantUUID)
    {
        return new PlantDetailViewModel(name, scientificName, minimumTemperature, false, plantUUID);
    }

    public UUID getPlantUUID()
    {
        return plantUUID;
    }
}
