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

package com.yoxjames.coldsnap.ui.plantimage;

import java.util.UUID;

/**
 * Created by yoxjames on 11/18/17.
 */

public class PlantMainImageViewModel
{
    private final String fileName;
    private final boolean isPending;
    private final boolean hasImage;
    private final Throwable error;
    private final String takenDateTime;
    private final UUID uuid;

    public PlantMainImageViewModel(String fileName, boolean isPending, boolean hasImage, Throwable error, String takenDateTime, UUID uuid)
    {
        this.fileName = fileName;
        this.isPending = isPending;
        this.hasImage = hasImage;
        this.error = error;
        this.takenDateTime = takenDateTime;
        this.uuid = uuid;
    }

    public PlantMainImageViewModel(String fileName, boolean isPending, boolean hasImage, Throwable error, String tokenDateTime)
    {
        this(fileName, isPending, hasImage, error, tokenDateTime, UUID.randomUUID());
    }

    public String getFileName()
    {
        return fileName;
    }

    public boolean isPending()
    {
        return isPending;
    }

    public boolean isError()
    {
        return error != null;
    }

    public Throwable getError()
    {
        return error;
    }

    public String getTakenDateTime()
    {
        return takenDateTime;
    }

    public boolean isHasImage()
    {
        return hasImage;
    }

    public UUID getUUID()
    {
        return uuid;
    }
}
