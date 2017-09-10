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
import com.yoxjames.coldsnap.service.plant.PlantService;
import com.yoxjames.coldsnap.ui.view.PlantDetailPagerView;
import com.yoxjames.coldsnap.util.LOG;

import java.util.ArrayList;
import java.util.UUID;
import javax.annotation.Nullable;
import javax.inject.Inject;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;

public class PlantDetailPagerPresenterImpl implements PlantDetailPagerPresenter
{
    private final PlantService plantService;
    private final ArrayList<UUID> plantUUIDs;
    private CompositeDisposable disposables;
    private final PlantDetailPagerView view;

    @Inject
    public PlantDetailPagerPresenterImpl(PlantService plantService, PlantDetailPagerView view)
    {
        this.plantService = plantService;
        this.view = view;
        this.plantUUIDs = new ArrayList<>();
    }

    @Override
    public int getPlantsSize()
    {
        return plantUUIDs.size();
    }

    @Override
    @Nullable
    public UUID getPlant(int plantIndex)
    {
        if (view.getStartedWithUUID() != null)
            return plantUUIDs.get(plantIndex);
        else
            return null;
    }

    @Override
    public void load()
    {
        if (disposables != null)
            disposables.dispose();

        disposables = new CompositeDisposable();

        disposables.add(plantService.getPlants().map(new Function<Plant, UUID>()
        {
            @Override
            public UUID apply(@NonNull Plant plant) throws Exception
            {
                return plant.getUuid();
            }
        }).subscribeWith(new DisposableObserver<UUID>()
        {
            @Override
            public void onNext(@NonNull UUID uuid)
            {
                plantUUIDs.add(uuid);
            }

            @Override
            public void onError(@NonNull Throwable e)
            {
                LOG.e(getClass().getName(), "PlantService.getPlants() Observable failure");
                e.printStackTrace(); // TODO: Something
            }

            @Override
            public void onComplete()
            {
                view.startUI();
            }
        }));
    }

    @Override
    public void unload()
    {
        disposables.dispose();
    }
}
