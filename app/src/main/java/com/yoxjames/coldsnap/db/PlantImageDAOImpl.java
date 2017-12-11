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

import com.yoxjames.coldsnap.db.image.PlantImageRow;
import com.yoxjames.coldsnap.db.image.PlantImageRowDAO;
import com.yoxjames.coldsnap.model.PlantImage;
import com.yoxjames.coldsnap.service.ActionReply;
import com.yoxjames.coldsnap.service.image.ImageService;

import org.threeten.bp.Instant;

import java.util.UUID;

import javax.inject.Inject;

import io.reactivex.Observable;

/**
 * Created by yoxjames on 11/18/17.
 */

public class PlantImageDAOImpl implements PlantImageDAO, ImageService
{
    private final PlantImageRowDAO plantImageRowDAO;

    @Inject
    public PlantImageDAOImpl(PlantImageRowDAO plantImageRowDAO)
    {
        this.plantImageRowDAO = plantImageRowDAO;
    }

    @Override
    public Observable<PlantImage> getPlantImage(UUID plantImageUUID)
    {
        return plantImageRowDAO.getImage(plantImageUUID.toString())
                .map(plantImageRow -> new PlantImage(Instant.ofEpochSecond(plantImageRow.getImageDate()),
                        plantImageRow.getImageFilename(),
                        UUID.fromString(plantImageRow.getUuid())));
    }

    @Override
    public Observable<ActionReply> savePlantImage(PlantImage plantImage)
    {
        return plantImageRowDAO.addImage(new PlantImageRow.Builder()
                .imageFilename(plantImage.getFileName())
                .imageDate(plantImage.getImageDate().getEpochSecond())
                .uuid(plantImage.getImageUUID().toString())
                .build());
    }
}
