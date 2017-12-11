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

package com.yoxjames.coldsnap.db.image;

/**
 * Created by yoxjames on 10/14/17.
 */

public class PlantImageRow
{
    private final long id;
    private final String uuid;
    private final String plantUUID;
    private final String title;
    private final String imageFilename;
    private final long imageDate;

    private PlantImageRow(long id, String uuid, String plantUUID, String title, String imageFilename, long imageDate)
    {
        this.id = id;
        this.uuid = uuid;
        this.plantUUID = plantUUID;
        this.title = title;
        this.imageFilename = imageFilename;
        this.imageDate = imageDate;
    }

    public long getId()
    {
        return id;
    }

    public String getUuid()
    {
        return uuid;
    }

    public String getPlantUUID()
    {
        return plantUUID;
    }

    public String getTitle()
    {
        return title;
    }

    public long getImageDate()
    {
        return imageDate;
    }

    public String getImageFilename()
    {
        return imageFilename;
    }

    public static class Builder
    {
        private long id;
        private String uuid;
        private String plantUUID;
        private String title;
        private long imageDate;
        private String imageFilename;

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

        public Builder plantUUID(String plantUUID)
        {
            this.plantUUID = plantUUID;
            return this;
        }

        public Builder title(String title)
        {
            this.title = title;
            return this;
        }

        public Builder imageDate(long imageDate)
        {
            this.imageDate = imageDate;
            return this;
        }

        public Builder imageFilename(String imageFilename)
        {
            this.imageFilename = imageFilename;
            return this;
        }

        public PlantImageRow build()
        {
            return new PlantImageRow(id, uuid, plantUUID, title, imageFilename, imageDate);
        }
    }
}
