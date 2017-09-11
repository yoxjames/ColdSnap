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

import android.util.Log;

import com.yoxjames.coldsnap.model.Plant;
import com.yoxjames.coldsnap.model.Temperature;
import com.yoxjames.coldsnap.model.WeatherData;
import com.yoxjames.coldsnap.model.WeatherLocation;
import com.yoxjames.coldsnap.service.location.GPSLocationService;
import com.yoxjames.coldsnap.service.plant.PlantService;
import com.yoxjames.coldsnap.service.weather.WeatherService;
import com.yoxjames.coldsnap.ui.view.PlantListItemView;
import com.yoxjames.coldsnap.ui.view.PlantListView;
import com.yoxjames.coldsnap.util.LOG;

import java.security.NoSuchProviderException;
import java.security.ProviderException;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableObserver;

import static com.yoxjames.coldsnap.model.Temperature.*;
import static com.yoxjames.coldsnap.model.Temperature.COMPARISON.*;

public class PlantListPresenterImpl implements PlantListPresenter

{
    private final PlantListView view;
    private final PlantService plantService;
    private final WeatherService weatherService;
    private final GPSLocationService gpsLocationService;
    private final List<Plant> plantList;

    private CompositeDisposable disposables;

    private WeatherData weatherData;

    @Inject
    public PlantListPresenterImpl(PlantListView view,
                                  PlantService plantService,
                                  WeatherService weatherService,
                                  GPSLocationService gpsLocationService)
    {
        this.view = view;
        this.plantService = plantService;
        this.weatherService = weatherService;
        this.gpsLocationService = gpsLocationService;
        this.plantList = new ArrayList<>();

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
            COMPARISON temperatureComparison = plant.getMinimumTolerance().compareSignificanceTo(weatherData.getTodayLow());

            switch (temperatureComparison)
            {
                case GREATER:
                    view.setStatus("\uD83D\uDC80"); // Skull and crossbones
                    break;
                case LESSER:
                    view.setStatus("\uD83D\uDE00"); // Happy Face
                    break;
                case MAYBE_GREATER:
                    view.setStatus("\uD83D\uDE1E"); // Sad Face
                    break;
                case MAYBE_LESSER:
                    view.setStatus("\uD83D\uDE10"); // Neutral Face
                    break;
                case TRUE_EQUAL:
                    view.setStatus("\uD83D\uDE10"); // Neutral Face
                    break;
            }
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
        if (disposables != null)
            disposables.dispose();

        plantList.clear();
        disposables = new CompositeDisposable();

        disposables.add(plantService.getPlants()
                .subscribeWith(new DisposableObserver<Plant>()
                {
                    @Override
                    public void onNext(@NonNull Plant plant)
                    {
                        plantList.add(plant);
                    }

                    @Override
                    public void onError(@NonNull Throwable e)
                    {
                        throw new IllegalStateException("FATAL: Cannot load plants. There's no point in carrying on.");
                    }

                    @Override
                    public void onComplete()
                    {
                        view.notifyDataChange();
                    }
                }));

        disposables.add(weatherService.getCurrentForecastData()
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
                                LOG.e(getClass().getName(), "weatherDataSingle failed");

                            }
                        }));

        subscribeToLocationChanges();
    }

    private void subscribeToLocationChanges()
    {
        disposables.add(
                gpsLocationService.getWeatherLocation()
                        .subscribeWith(new DisposableObserver<WeatherLocation>()
                        {
                            @Override
                            public void onNext(@NonNull WeatherLocation weatherLocation)
                            {
                                disposables.add(weatherService.getCurrentForecastData()
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
                                                        // TODO: Do Something
                                                        LOG.e(getClass().getName(), "weatherDataSingle failed");
                                                    }
                                                }));

                            }

                            @Override
                            public void onError(@NonNull Throwable e)
                            {
                                e.printStackTrace();
                                if (e instanceof SecurityException)
                                    view.displayLocationPermissionsError();
                                else if (e instanceof NoSuchProviderException)
                                    view.displayLocationNotAvailableError();
                                else
                                    view.displayDeviceLocationFailureMessage(); // We're not exactly sure what happened

                                LOG.e(getClass().getName(), "weatherLocationObservable failed");
                                subscribeToLocationChanges();
                            }
                            @Override public void onComplete()
                            {
                                subscribeToLocationChanges();
                            }
                        }));
    }

    @Override
    public void newPlant()
    {
        view.openPlant(null);
    }

    @Override
    public void resetLocation()
    {
        gpsLocationService.pushWeatherLocation();
    }

    @Override
    public void unload()
    {
        gpsLocationService.cancelRequestLocation();
        disposables.dispose();
    }
}
