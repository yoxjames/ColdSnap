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

import com.yoxjames.coldsnap.service.plant.PlantService;
import com.yoxjames.coldsnap.ui.presenter.PlantDetailPagerPresenter;
import com.yoxjames.coldsnap.ui.presenter.PlantDetailPagerPresenterImpl;
import com.yoxjames.coldsnap.ui.view.PlantDetailPagerView;

import dagger.Module;
import dagger.Provides;

@Module
public class PlantDetailActivityModule
{
    private final PlantDetailPagerView view;

    public PlantDetailActivityModule(PlantDetailPagerView view)
    {
        this.view = view;
    }

    @Provides
    PlantDetailPagerView providePlantDetailPagerView()
    {
        return view;
    }

    @Provides
    static PlantDetailPagerPresenter providePlantDetailPagerPresenter(PlantService plantService, PlantDetailPagerView view)
    {
        return new PlantDetailPagerPresenterImpl(plantService, view);
    }
}
