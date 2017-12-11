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

package com.yoxjames.coldsnap.ui.weatherpreviewbar;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by yoxjames on 11/4/17.
 */

public class WeatherPreviewBarPresenter
{
    private final WeatherBarModelAdapter weatherBarModelAdapter;
    private CompositeDisposable disposables;
    private final WeatherPreviewBarView view;

    @Inject
    public WeatherPreviewBarPresenter(WeatherBarModelAdapter weatherBarModelAdapter, WeatherPreviewBarView view)
    {
        this.weatherBarModelAdapter = weatherBarModelAdapter;
        this.view = view;
        disposables = new CompositeDisposable();
    }

    public void load()
    {
        if (disposables != null)
            disposables.dispose();

        disposables = new CompositeDisposable();

        disposables.add(weatherBarModelAdapter.getWeatherData()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(view::bindViewModel));
    }

    public void unload()
    {
        disposables.dispose();
    }
}
