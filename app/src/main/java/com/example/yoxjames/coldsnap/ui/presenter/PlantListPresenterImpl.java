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

import android.util.Log;

import com.example.yoxjames.coldsnap.model.Plant;
import com.example.yoxjames.coldsnap.model.SimpleWeatherLocation;
import com.example.yoxjames.coldsnap.model.WeatherData;
import com.example.yoxjames.coldsnap.model.WeatherLocation;
import com.example.yoxjames.coldsnap.service.plant.PlantService;
import com.example.yoxjames.coldsnap.ui.view.PlantListItemView;
import com.example.yoxjames.coldsnap.ui.view.PlantListView;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.subjects.Subject;

public class PlantListPresenterImpl implements PlantListPresenter

{
    private final PlantListView view;
    private final PlantService plantService;
    private final Single<WeatherData> weatherDataSingle;
    private final Subject<SimpleWeatherLocation> simpleWeatherLocationSubject;
    private final Observable<WeatherLocation> weatherLocationObservable;
    private Disposable weatherDataDisposableObserver;
    private Disposable weatherLocationObserver;
    private Disposable viewLocationObserver;

    private List<Plant> plantList;
    private WeatherData weatherData;

    @Inject
    public PlantListPresenterImpl(PlantListView view,
                                  PlantService plantService,
                                  Single<WeatherData> weatherDataSingle,
                                  Subject<SimpleWeatherLocation> simpleWeatherLocationSubject,
                                  Observable<WeatherLocation> weatherLocationObservable)
    {
        this.view = view;
        this.plantService = plantService;
        this.weatherDataSingle = weatherDataSingle;
        this.simpleWeatherLocationSubject = simpleWeatherLocationSubject;
        this.weatherLocationObservable = weatherLocationObservable;
    }

    @Override
    public void loadPlantView(PlantListItemView view, int position)
    {
        Plant plant = plantList.get(position);
        view.setPlantName(plant.getName());
        view.setPlantScientificName(plant.getScientificName());
        view.setUUID(plant.getUuid());
        if ((weatherData == null) || weatherData.getForecastDays().size() == 0)
            view.setStatus("...");
        else
        {
            if (plant.getMinimumTolerance().compareTo(weatherData.getTodayLow()) == 0)
                view.setStatus("\uD83D\uDE10"); // Neutral Face
            else if (plant.getMinimumTolerance().compareTo(weatherData.getTodayLow()) == -1)
                view.setStatus("\uD83D\uDE00"); // Happy Face
            else
                view.setStatus("\uD83D\uDE1E"); // Sad Face
        }
    }

    @Override
    public int getItemCount()
    {
        return plantList.size();
    }

    @Override
    public void load()
    {
        plantList = plantService.getMyPlants();
        if (plantList.size() > 0)
        {
            weatherDataDisposableObserver = weatherDataSingle
                    .subscribe(
                            new Consumer<WeatherData>()
                            { // Success
                                @Override
                                public void accept(@NonNull WeatherData weatherData) throws Exception
                                {
                                    PlantListPresenterImpl.this.weatherData = weatherData;
                                    view.notifyDataChange();
                                }
                            },
                            new Consumer<Throwable>()
                            { // Failure
                                @Override
                                public void accept(@NonNull Throwable throwable) throws Exception
                                {
                                    throwable.printStackTrace();
                                    Log.e("Observer Error", "weatherDataSingle failed in PlantListPresenterImpl");
                                }
                            });
        }

        weatherLocationObserver =
                weatherLocationObservable
                .subscribeWith(new DisposableObserver<WeatherLocation>()
                {
                    @Override
                    public void onNext(@NonNull WeatherLocation weatherLocation)
                    {
                        weatherDataDisposableObserver.dispose();
                        weatherDataDisposableObserver = weatherDataSingle
                                .subscribe(
                                        new Consumer<WeatherData>()
                                        { // Success
                                            @Override
                                            public void accept(@NonNull WeatherData weatherData) throws Exception
                                            {
                                                PlantListPresenterImpl.this.weatherData = weatherData;
                                                view.notifyDataChange();
                                            }
                                        },
                                        new Consumer<Throwable>()
                                        { // Failure
                                            @Override
                                            public void accept(@NonNull Throwable throwable) throws Exception
                                            {
                                                throwable.printStackTrace();
                                                Log.e("Observer Error", "weatherDataSingle failed in PlantListPresenterImpl");
                                            }
                                        });

                    }

                    @Override public void onError(@NonNull Throwable e)
                    {
                        e.printStackTrace();
                        Log.e("Observer Error", "weatherLocationObservable failed in PlantListPresenterImpl");
                    }
                    @Override public void onComplete() { }
                });
    }

    @Override
    public void newPlant()
    {
        Plant plant = new Plant("", "");
        plantService.cachePlant(plant);
        view.openPlant(plant.getUuid(), true);
    }

    @Override
    public void resetLocation()
    {
        viewLocationObserver = view.provideLocationObservable().subscribe(
                new Consumer<SimpleWeatherLocation>()
                {
                    @Override
                    public void accept(SimpleWeatherLocation simpleWeatherLocation) throws Exception
                    {
                        simpleWeatherLocationSubject.onNext(simpleWeatherLocation);
                    }
                },
                new Consumer<Throwable>()
                {
                    @Override
                    public void accept(Throwable throwable) throws Exception
                    {
                        throwable.printStackTrace();
                        view.displayDeviceLocationFailureMessage();
                    }
                });
    }

    @Override
    public void unload()
    {
        if (!(weatherDataDisposableObserver == null) && !weatherDataDisposableObserver.isDisposed())
            weatherDataDisposableObserver.dispose();
        if (!(weatherLocationObserver == null) && !weatherLocationObserver.isDisposed())
            weatherLocationObserver.dispose();
        if (!(viewLocationObserver == null) && !viewLocationObserver.isDisposed())
            viewLocationObserver.dispose();
    }
}
