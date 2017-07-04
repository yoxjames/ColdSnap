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

package com.example.yoxjames.coldsnap.ui.presenter;

import com.example.yoxjames.coldsnap.model.Plant;
import com.example.yoxjames.coldsnap.service.PlantService;
import com.example.yoxjames.coldsnap.ui.controls.TemperaturePickerAdapter;
import com.example.yoxjames.coldsnap.ui.view.PlantDetailView;

import java.util.UUID;

import javax.inject.Inject;

import dagger.internal.Preconditions;

public class PlantDetailPresenterImpl implements PlantDetailPresenter
{
    private final PlantDetailView view;
    private final PlantService plantService;
    private final TemperaturePickerAdapter temperaturePickerAdapter;

    @Inject
    public PlantDetailPresenterImpl(PlantDetailView view, PlantService plantService, TemperaturePickerAdapter temperaturePickerAdapter)
    {
        this.view = view;
        this.plantService = plantService;
        this.temperaturePickerAdapter = temperaturePickerAdapter;
    }

    @Override
    public void load(UUID plantUUID)
    {
        final Plant plant = plantService.getPlant(Preconditions.checkNotNull(plantUUID));

        view.setMinBound(temperaturePickerAdapter.getMinimumTemperatureValue());
        view.setMaxBound(temperaturePickerAdapter.getMaximumTemperatureValue());
        view.setMinimumTemperatureFormatter(temperaturePickerAdapter.getFormatter());

        if (view.isNewPlantInd())
            view.setAddMode();

        view.setPlantName(plant.getName());
        view.setPlantScientificName(plant.getScientificName());

        view.setMinTemperature(temperaturePickerAdapter.getValueForTemperature(plant.getMinimumTolerance()));
    }

    @Override
    public void savePlantInformation(UUID plantUUID)
    {
        if (!view.isNewPlantInd())
            plantService.deletePlant(Preconditions.checkNotNull(plantUUID));

        final Plant newPlant = new Plant(view.getPlantName(),
                                         view.getPlantScientificName(),
                                         temperaturePickerAdapter.getTemperatureForValue(view.getMinTemperature()),
                                         plantUUID);
        plantService.addPlant(newPlant);
        view.displaySaveMessage(view.isNewPlantInd());
    }

    @Override
    public void deletePlant(UUID plantUUID)
    {
        plantService.deletePlant(Preconditions.checkNotNull(plantUUID));
        view.displayDeleteMessage();
    }
}
