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

package com.example.yoxjames.coldsnap.dagger;

import android.content.SharedPreferences;

import com.example.yoxjames.coldsnap.model.TemperatureFormatter;
import com.example.yoxjames.coldsnap.model.TemperatureFormatterImpl;
import com.example.yoxjames.coldsnap.model.WeatherData;
import com.example.yoxjames.coldsnap.model.WeatherLocation;
import com.example.yoxjames.coldsnap.ui.presenter.WeatherPreviewBarPresenter;
import com.example.yoxjames.coldsnap.ui.presenter.WeatherPreviewBarPresenterImpl;
import com.example.yoxjames.coldsnap.ui.view.WeatherPreviewBarView;

import dagger.Module;
import dagger.Provides;
import io.reactivex.Observable;
import io.reactivex.Single;

@Module
public class WeatherPreviewBarViewModule
{
    private final WeatherPreviewBarView view;

    public WeatherPreviewBarViewModule(WeatherPreviewBarView view)
    {
        this.view = view;
    }

    @Provides
    WeatherPreviewBarView weatherPreviewBarView()
    {
        return view;
    }

    @Provides
    static WeatherPreviewBarPresenter provideWeatherPreviewBarPresenter(WeatherPreviewBarView view,
                                                                        TemperatureFormatter temperatureFormatter,
                                                                        Single<WeatherData> weatherDataSingle,
                                                                        Observable<WeatherLocation> weatherLocationObservable,
                                                                        SharedPreferences sharedPreferences)

    {
        return new WeatherPreviewBarPresenterImpl(view, temperatureFormatter, weatherDataSingle, weatherLocationObservable, sharedPreferences);
    }

    @Provides
    static TemperatureFormatter provideTemperatureFormatter(SharedPreferences sharedPreferences)
    {
        return new TemperatureFormatterImpl(sharedPreferences);
    }
}
