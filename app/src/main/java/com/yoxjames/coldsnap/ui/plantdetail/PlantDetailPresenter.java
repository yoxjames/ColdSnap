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
import com.yoxjames.coldsnap.model.Temperature;
import com.yoxjames.coldsnap.model.TemperatureValueAdapter;
import com.yoxjames.coldsnap.service.image.ImageService;
import com.yoxjames.coldsnap.service.plant.PlantService;
import com.yoxjames.coldsnap.ui.plantimage.PlantMainImageViewModel;

import java.util.UUID;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

public class PlantDetailPresenter
{
    private final PlantDetailView view;
    private final PlantService plantService;
    private final ImageService imageService;
    private final TemperatureValueAdapter temperatureValueAdapter;
    private CompositeDisposable disposables;

    @Inject
    public PlantDetailPresenter(PlantDetailView view, PlantService plantService, ImageService imageService, TemperatureValueAdapter temperatureValueAdapter)
    {
        this.view = view;
        this.plantService = plantService;
        this.imageService = imageService;
        this.temperatureValueAdapter = temperatureValueAdapter;
    }

    public void load(UUID plantUUID)
    {
        if (disposables != null)
            disposables.dispose();
        disposables = new CompositeDisposable();

        if (plantUUID != null)
        {
            Observable<Plant> plantObservable = plantService.getPlant(plantUUID).cache();

            disposables.add(plantObservable
                    .map(plant -> PlantDetailViewModel.existingPlant(plant.getName(), plant.getScientificName(), temperatureValueAdapter.getValue(plant.getMinimumTolerance()), plant.getUuid()))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(view::bindViewModel));
        }
        else
            disposables.add(Observable.just(PlantDetailViewModel.newPlant(temperatureValueAdapter.getValue(Temperature.WATER_FREEZING_KELVIN)))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(view::bindViewModel));


    }

    public Observable<PlantMainImageViewModel> imageVM(UUID plantUUID)
    {
        return plantService.getPlant(plantUUID)
                .filter(plant -> plant.getMainImageUUID() != null)
                .flatMap(plant -> imageService.getPlantImage(plant.getMainImageUUID()))
                .map(plantImage -> new PlantMainImageViewModel(plantImage.getFileName(), false, true, null, "unimplemented", plantImage.getImageUUID()))
                .startWith(new PlantMainImageViewModel("", false, false, null, "nope"))
                .observeOn(AndroidSchedulers.mainThread());
    }


}
