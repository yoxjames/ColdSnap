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
import com.yoxjames.coldsnap.model.TemperatureValueAdapter;
import com.yoxjames.coldsnap.ui.UIReply;

import java.util.UUID;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by yoxjames on 11/19/17.
 */

public class PlantDetailViewAdapter
{
    private final PlantDetailActivityDataProvider dataProvider;
    private final TemperatureValueAdapter temperatureValueAdapter;

    @Inject
    public PlantDetailViewAdapter(PlantDetailActivityDataProvider dataProvider, TemperatureValueAdapter temperatureValueAdapter)
    {
        this.dataProvider = dataProvider;
        this.temperatureValueAdapter = temperatureValueAdapter;
    }

    public Observable<PlantDetailViewModel> getViewModel(UUID plantUUID)
    {
        if (plantUUID != null)
            return dataProvider.getPlant(plantUUID)
                    .map(plant -> PlantDetailViewModel.existingPlant(plant.getName(), plant.getScientificName(), temperatureValueAdapter.getValue(plant.getMinimumTolerance()), plant.getUuid()))
                    .observeOn(AndroidSchedulers.mainThread());
        else
            return Observable.just(PlantDetailViewModel.newPlant(32)); // TODO: This should default properly
    }

    public ObservableTransformer<PlantDetailSaveRequest, UIReply> savePlantDetail()
    {
        return saveRequest -> saveRequest
                .flatMap(req -> dataProvider.savePlant(req.getUuid(),
                new Plant(req.getName(),
                        req.getScientificName(),
                        temperatureValueAdapter.getTemperature(req.getMinimumTemperature()),
                        req.getUuid(),
                        (req.getImage() == null) ? null : req.getImage().getUuid()))
                .concatWith(dataProvider.saveImage(req.getImage().getUuid(),
                        new PlantImage(req.getImage().getPhotoTime(), req.getImage().getFileName(), req.getImage().getUuid()))))
                .map(reply -> UIReply.genericSuccess());
    }

    public ObservableTransformer<PlantDetailDeleteRequest, UIReply> deletePlant()
    {
        return deleteRequest -> deleteRequest.flatMap(req -> dataProvider.deletePlant(req.getPlantUUID()))
                .map(actionReply -> UIReply.genericSuccess());
    }

    public ObservableTransformer<PlantDetailSaveRequest, UIReply> addPlant()
    {
        return addRequest -> addRequest
                .flatMap(req -> dataProvider.addPlant(
                        new Plant(req.getName(),
                                req.getScientificName(),
                                temperatureValueAdapter.getTemperature(req.getMinimumTemperature()),
                                req.getUuid(),
                                (req.getImage() == null) ? null : req.getImage().getUuid())))
                .map(reply -> UIReply.genericSuccess());
    }
}
