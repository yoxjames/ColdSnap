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

import java.util.UUID;

import io.reactivex.Observable;

/**
 * Created by yoxjames on 11/19/17.
 */

public interface PlantDetailActivityDataProvider
{
    Observable<Plant> getPlant(UUID plantUUID);
    Observable<PlantImage> getPlantImage(UUID plantImageUUID);
    Observable<ActionReply> savePlant(UUID plantUUID, Plant plant);
    Observable<ActionReply> saveImage(UUID imageUUID, PlantImage plantImage);
    Observable<ActionReply> deletePlant(UUID plantUUID);
    Observable<ActionReply> addPlant(Plant plant);
}
