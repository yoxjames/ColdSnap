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

package com.yoxjames.coldsnap.ui.presenter;

import com.yoxjames.coldsnap.model.Plant;
import com.yoxjames.coldsnap.model.Temperature;
import com.yoxjames.coldsnap.model.TemperatureValueAdapter;
import com.yoxjames.coldsnap.service.plant.PlantService;
import com.yoxjames.coldsnap.ui.view.PlantDetailView;
import com.yoxjames.coldsnap.util.LOG;

import java.util.UUID;
import javax.inject.Inject;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.observers.ResourceCompletableObserver;

public class PlantDetailPresenterImpl implements PlantDetailPresenter
{
    private final PlantDetailView view;
    private final PlantService plantService;
    private final TemperatureValueAdapter temperatureValueAdapter;
    private CompositeDisposable disposables;
    private UUID plantUUID;

    @Inject
    public PlantDetailPresenterImpl(PlantDetailView view, PlantService plantService, TemperatureValueAdapter temperatureValueAdapter)
    {
        this.view = view;
        this.plantService = plantService;
        this.temperatureValueAdapter = temperatureValueAdapter;
    }

    @Override
    public void load(UUID plantUUID)
    {
        this.plantUUID = plantUUID;
        if (disposables != null)
            disposables.dispose();

        if (isNewPlant())
            view.setAddMode();

        disposables = new CompositeDisposable();

        if (!isNewPlant())
            disposables.add(plantService.getPlant(plantUUID).subscribeWith(new DisposableSingleObserver<Plant>()
            {
                @Override
                public void onSuccess(@NonNull Plant plant)
                {
                    view.setPlantName(plant.getName());
                    view.setPlantScientificName(plant.getScientificName());
                    view.setMinTemperature(temperatureValueAdapter.getValue(plant.getMinimumTolerance()));
                    view.showView();
                }

                @Override
                public void onError(@NonNull Throwable e)
                {
                    view.showLoadError();
                    LOG.e(getClass().getName(), "Get Plant Observer Error");
                    e.printStackTrace();
                }
            }));
        else
        {
            view.showView();
            view.setMinTemperature(temperatureValueAdapter.getValue(new Temperature(Temperature.WATER_FREEZING_KELVIN)));
        }
    }

    @Override
    public void savePlantInformation()
    {
        if (isNewPlant())
            disposables.add(plantService.addPlant(getPlantFromView()).subscribeWith(new ResourceCompletableObserver()
            {
                @Override
                public void onComplete()
                {
                    view.displaySaveMessage(isNewPlant());
                    view.finishView();
                }

                @Override
                public void onError(@NonNull Throwable e)
                {
                    view.showUnableToAddError();
                    LOG.e(getClass().getName(), "Add Plant Observer Error");
                    e.printStackTrace();
                }
            }));
        else
            disposables.add(plantService.updatePlant(getPlantFromView()).subscribeWith(new ResourceCompletableObserver()
            {
                @Override
                public void onComplete()
                {
                    view.displaySaveMessage(isNewPlant());
                    view.finishView();
                }

                @Override
                public void onError(@NonNull Throwable e)
                {
                    view.showUnableToSaveError();
                    LOG.e(getClass().getName(), "Update Plant Observer Error");
                    e.printStackTrace();
                }
            }));
    }

    @Override
    public void deletePlant()
    {
        disposables.add(plantService.deletePlant(getPlantFromView()).subscribeWith(new ResourceCompletableObserver()
                {
                    @Override
                    public void onComplete()
                    {
                        view.displayDeleteMessage();
                        view.finishView();
                    }

                    @Override
                    public void onError(@NonNull Throwable e)
                    {
                        view.showUnableToDeleteError();
                        LOG.e(getClass().getName(), "Delete Plant Observer Error");
                        e.printStackTrace();
                    }
                }));
    }

    @Override
    public void unload()
    {
        plantUUID = null;
        disposables.dispose();
    }

    private Plant getPlantFromView()
    {
        return new Plant(
                view.getPlantName(),
                view.getPlantScientificName(),
                temperatureValueAdapter.getTemperature(view.getMinTemperature()),
                (plantUUID != null) ? plantUUID : UUID.randomUUID());
    }

    private boolean isNewPlant()
    {
        return (plantUUID == null);
    }
}
