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

package com.yoxjames.coldsnap.db.plant;

import cz.msebera.android.httpclient.annotation.Immutable;

/**
 * Created by yoxjames on 10/14/17.
 */

@Immutable
public class PlantRow
{
    private final long id;
    private final String uuid;
    private final String name;
    private final String scientificName;
    private final double coldThresholdK;
    private final String mainImageUUID;

    private PlantRow(long id, String uuid, String name, String scientificName, double coldThresholdK, String mainImageUUID)
    {
        this.id = id;
        this.uuid = uuid;
        this.name = name;
        this.scientificName = scientificName;
        this.coldThresholdK = coldThresholdK;
        this.mainImageUUID = mainImageUUID;
    }

    public long getId()
    {
        return id;
    }

    public String getUuid()
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

    public double getColdThresholdK()
    {
        return coldThresholdK;
    }

    public String getMainImageUUID()
    {
        return mainImageUUID;
    }

    public static class Builder
    {
        private long id;
        private String uuid;
        private String name;
        private String scientificName;
        private double coldThresholdK;
        private String mainImageUUID;

        public Builder id(long id)
        {
            this.id = id;
            return this;
        }

        public Builder uuid(String uuid)
        {
            this.uuid = uuid;
            return this;
        }

        public Builder name(String name)
        {
            this.name = name;
            return this;
        }

        public Builder scientificName(String scientificName)
        {
            this.scientificName = scientificName;
            return this;
        }

        public Builder coldThresholdK(double coldThresholdK)
        {
            this.coldThresholdK = coldThresholdK;
            return this;
        }

        public Builder mainImageUUID(String mainImageUUID)
        {
            this.mainImageUUID = mainImageUUID;
            return this;
        }

        public PlantRow build()
        {
            return new PlantRow(id, uuid, name, scientificName, coldThresholdK, mainImageUUID);
        }
    }
}
