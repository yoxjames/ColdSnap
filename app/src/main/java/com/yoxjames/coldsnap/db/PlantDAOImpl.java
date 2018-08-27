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

import com.yoxjames.coldsnap.db.plant.PlantDBMapper;
import com.yoxjames.coldsnap.db.plant.PlantRowDAO;
import com.yoxjames.coldsnap.model.Plant;
import com.yoxjames.coldsnap.service.ActionReply;

import java.util.UUID;

import javax.inject.Inject;

import dagger.Reusable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by yoxjames on 10/14/17.
 */
@Reusable
public class PlantDAOImpl implements PlantDAO
{
    private final PlantRowDAO plantRowDAO;

    @Inject
    public PlantDAOImpl(PlantRowDAO plantRowDAO)
    {
        this.plantRowDAO = plantRowDAO;
    }

    @Override
    public Observable<Plant> getPlants()
    {
        return Observable.fromCallable(plantRowDAO::getPlantRows)
            .concatMap(Observable::fromIterable)
            .map(PlantDBMapper::mapToPOJO)
            .subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<Plant> getPlant(UUID plantUUID)
    {
        return plantRowDAO.getPlantRow(plantUUID.toString()).toObservable().map(PlantDBMapper::mapToPOJO);
    }

    @Override
    public Observable<ActionReply> savePlant(Plant plant)
    {
        return Observable.create((ObservableEmitter<ActionReply> e) ->
        {
            plantRowDAO.addPlantRow(PlantDBMapper.mapToDB(plant));
            e.onNext(ActionReply.genericSuccess());
            e.onComplete();
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<ActionReply> deletePlant(UUID plantUUID)
    {
        return Observable.create((ObservableEmitter<ActionReply> e) ->
        {
            plantRowDAO.deletePlantRow(plantUUID.toString());
            e.onNext(ActionReply.genericSuccess());
            e.onComplete();
        }).subscribeOn(Schedulers.io());
    }
}
