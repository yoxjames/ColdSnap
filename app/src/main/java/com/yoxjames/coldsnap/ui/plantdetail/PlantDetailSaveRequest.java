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

import org.threeten.bp.Instant;

import java.util.UUID;

import javax.annotation.Nullable;

import dagger.internal.Preconditions;

/**
 * Created by yoxjames on 11/18/17.
 */

public class PlantDetailSaveRequest
{
    private final UUID uuid;
    private final String name;
    private final String scientificName;
    private final int minimumTemperature;
    private final ProfileImage image;
    private final boolean isNewPlant;

    public PlantDetailSaveRequest(UUID uuid, String name, String scientificName, int minimumTemperature, ProfileImage image, boolean isNewPlant)
    {
        if (isNewPlant)
            this.uuid = UUID.randomUUID();
        else
            this.uuid = Preconditions.checkNotNull(uuid);
        this.name = Preconditions.checkNotNull(name);
        this.scientificName = Preconditions.checkNotNull(scientificName);
        this.minimumTemperature = minimumTemperature;
        this.image = image;
        this.isNewPlant = isNewPlant;
    }

    public UUID getUuid()
    {
        return uuid;
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

    public ProfileImage getImage()
    {
        return image;
    }

    public boolean isNewPlant()
    {
        return isNewPlant;
    }

    public static class ProfileImage
    {
        public ProfileImage(@Nullable UUID uuid, String fileName, Instant photoTime)
        {
            this.uuid = uuid;
            this.fileName = fileName;
            this.photoTime = photoTime;
        }

        private final UUID uuid;
        private final String fileName;
        private final Instant photoTime;

        public UUID getUuid()
        {
            return uuid;
        }

        public String getFileName()
        {
            return fileName;
        }

        public Instant getPhotoTime()
        {
            return photoTime;
        }
    }
}
