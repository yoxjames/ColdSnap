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

import android.support.annotation.NonNull;

import java.util.UUID;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import static dagger.internal.Preconditions.checkNotNull;

@Immutable
public class Plant implements Comparable<Plant>
{
    private final String name;
    private final String scientificName;
    private final Temperature minimumTolerance;
    private final UUID uuid;
    private final UUID mainImageUUID;

    public Plant(String name, String scientificName, Temperature minimumTolerance, UUID uuid, @Nullable UUID mainImageUUID)
    {
        this.name = checkNotNull(name);
        this.scientificName = checkNotNull(scientificName);
        this.minimumTolerance = checkNotNull(minimumTolerance);
        this.uuid = checkNotNull(uuid);
        this.mainImageUUID = mainImageUUID;
    }

    public Plant(String name, String scientificName, Temperature minimumTolerance)
    {
        this(name, scientificName, minimumTolerance, UUID.randomUUID(), null);
    }

    public Plant(String name, String scientificName)
    {
        this(name, scientificName, Temperature.newTemperatureFromF(32));
    }

    public String getName()
    {
        return name;
    }

    public String getScientificName()
    {
        return scientificName;
    }

    public Temperature getMinimumTolerance()
    {
        return minimumTolerance;
    }

    public UUID getUuid()
    {
        return uuid;
    }

    @Nullable
    public UUID getMainImageUUID()
    {
        return mainImageUUID;
    }

    @Override
    public String toString()
    {
        return "Plant{" +
                "name='" + name + '\'' +
                ", scientificName='" + scientificName + '\'' +
                ", minimumTolerance=" + minimumTolerance +
                ", uuid=" + uuid +
                ", mainImageUUID=" + mainImageUUID +
        '}';
    }

    @Override
    public int compareTo(@NonNull Plant plant)
    {
        return this.getName().compareTo(plant.getName());
    }
}
