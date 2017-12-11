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

package com.yoxjames.coldsnap.dagger;

import com.yoxjames.coldsnap.model.TemperatureValueAdapter;
import com.yoxjames.coldsnap.service.image.ImageService;
import com.yoxjames.coldsnap.service.plant.PlantService;
import com.yoxjames.coldsnap.ui.plantdetail.PlantDetailActivityDataProvider;
import com.yoxjames.coldsnap.ui.plantdetail.PlantDetailActivityDataProviderImpl;
import com.yoxjames.coldsnap.ui.plantdetail.PlantDetailPresenter;
import com.yoxjames.coldsnap.ui.plantdetail.PlantDetailView;
import com.yoxjames.coldsnap.ui.plantimage.PlantImageViewAdapter;

import java.util.UUID;

import dagger.Module;
import dagger.Provides;

@Module
public class PlantDetailFragmentModule
{
    private final PlantDetailView view;
    private final UUID plantUUID;

    public PlantDetailFragmentModule(PlantDetailView view, UUID plantUUID)
    {
        this.view = view;
        this.plantUUID = plantUUID;
    }

    @Provides
    PlantDetailView providePlantDetailView()
    {
        return view;
    }

    @Provides
    static PlantDetailPresenter providePlantDetailPresenter(PlantDetailView view, PlantService plantService, TemperatureValueAdapter temperatureValueAdapter, ImageService imageService)
    {
        return new PlantDetailPresenter(view, plantService, imageService, temperatureValueAdapter);
    }

    @Provides
    static PlantImageViewAdapter providePlantImageViewAdapter(PlantDetailPresenter plantDetailPresenter)
    {
        return new PlantImageViewAdapter(plantDetailPresenter);
    }

    @Provides
    UUID providePlantUUID()
    {
        return plantUUID;
    }

    @Provides
    static PlantDetailActivityDataProvider providePlantDetailActivityDataProvider(PlantService plantService, ImageService imageService)
    {
        return new PlantDetailActivityDataProviderImpl(plantService, imageService);
    }
}
