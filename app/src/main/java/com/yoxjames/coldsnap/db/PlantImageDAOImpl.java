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

import com.yoxjames.coldsnap.db.image.PlantImageDBMapper;
import com.yoxjames.coldsnap.db.image.PlantImageRow;
import com.yoxjames.coldsnap.db.image.PlantImageRowDAO;
import com.yoxjames.coldsnap.model.PlantImage;
import com.yoxjames.coldsnap.service.ActionReply;

import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.schedulers.Schedulers;

import static com.yoxjames.coldsnap.model.PlantImage.NO_IMAGE;

/**
 * Created by yoxjames on 11/18/17.
 */

public class PlantImageDAOImpl implements PlantImageDAO
{
    private final PlantImageRowDAO plantImageRowDAO;

    @Inject
    public PlantImageDAOImpl(PlantImageRowDAO plantImageRowDAO)
    {
        this.plantImageRowDAO = plantImageRowDAO;
    }

    @Override
    public Observable<PlantImage> getPlantImage(UUID plantUUID)
    {
        return Observable.create((ObservableEmitter<PlantImage> e) ->
        {
            PlantImageRow plantImageRow = plantImageRowDAO.getImageForPlant(plantUUID.toString());
            if (plantImageRow != null)
                e.onNext(PlantImageDBMapper.mapToPOJO(plantImageRow));
            else
                e.onNext(NO_IMAGE);
            e.onComplete();
        })
            .subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<ActionReply> savePlantImage(PlantImage plantImage)
    {
        return Observable.create((ObservableEmitter<ActionReply> e) ->
        {
            plantImageRowDAO.addImage(PlantImageDBMapper.mapToDB(plantImage));
            e.onNext(ActionReply.genericSuccess());
            e.onComplete();
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<ActionReply> deletePlantImage(UUID plantUUID)
    {
        return Observable.create((ObservableEmitter<ActionReply> e) ->
        {
            plantImageRowDAO.deleteImagesForPlant(plantUUID.toString());
            e.onNext(ActionReply.genericSuccess());
            e.onComplete();
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<List<PlantImage>> getAllPlantImages()
    {
        return Observable.create((ObservableEmitter<List<PlantImageRow>> e) ->
        {
            e.onNext(plantImageRowDAO.getPlantImages());
            e.onComplete();
        })
            .subscribeOn(Schedulers.io())
            .flatMap(Observable::fromIterable)
            .map(PlantImageDBMapper::mapToPOJO)
            .toList()
            .toObservable();
    }
}
