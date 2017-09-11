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

import android.content.SharedPreferences;

import com.yoxjames.coldsnap.model.TemperatureFormatter;
import com.yoxjames.coldsnap.model.WeatherData;
import com.yoxjames.coldsnap.model.WeatherLocation;
import com.yoxjames.coldsnap.service.location.GPSLocationService;
import com.yoxjames.coldsnap.service.weather.WeatherService;
import com.yoxjames.coldsnap.ui.view.WeatherPreviewBarView;
import com.yoxjames.coldsnap.util.LOG;

import javax.inject.Inject;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableObserver;

public class WeatherPreviewBarPresenterImpl implements WeatherPreviewBarPresenter
{
    private final WeatherPreviewBarView view;
    private final TemperatureFormatter temperatureFormatter;
    private final GPSLocationService gpsLocationService;
    private final SharedPreferences sharedPreferences;
    private final WeatherService weatherService;
    private CompositeDisposable disposables;


    /**
     * Constructor for WeatherPreviewBarPresenterImpl
     *
     * @param view                 The view (Think MVP pattern)
     * @param temperatureFormatter Object designed to transaction Temperature objects into viewable strings.
     * @param gpsLocationService   Rx API for GPS location change events
     * @param sharedPreferences    Preference service
     * @param weatherService       Async provider of Reactive objects to obtain {@link WeatherData}
     */
    @Inject
    public WeatherPreviewBarPresenterImpl(WeatherPreviewBarView view,
                                          TemperatureFormatter temperatureFormatter,
                                          GPSLocationService gpsLocationService,
                                          SharedPreferences sharedPreferences,
                                          WeatherService weatherService)
    {
        this.view = view;
        this.temperatureFormatter = temperatureFormatter;
        this.gpsLocationService = gpsLocationService;
        this.sharedPreferences = sharedPreferences;
        this.weatherService = weatherService;
    }

    @Override
    public void load()
    {
        if (disposables != null)
            disposables.dispose();

        this.disposables = new CompositeDisposable();

        disposables.add(weatherService.getCurrentForecastData()
                .subscribe(
                        new Consumer<WeatherData>()
                        {
                            @Override
                            public void accept(@NonNull WeatherData weatherData) throws Exception
                            {
                                view.setLocationText(weatherData.getWeatherLocation().getPlaceString() + " - " + weatherData.getWeatherLocation().getZipCode());
                                view.setHighText(temperatureFormatter.format(weatherData.getTodayHigh()));
                                view.setLowText(temperatureFormatter.format(weatherData.getTodayLow()));
                                view.setLastUpdatedText(weatherData.getForecastDays().get(0).toString());
                            }
                        },
                        new Consumer<Throwable>()
                        {
                            @Override
                            public void accept(@NonNull Throwable throwable) throws Exception
                            {
                                throwable.printStackTrace();
                                // TODO: Do something
                                LOG.e(getClass().getName(), "weatherDataSingle failed");
                            }
                        }));

        subscribeToLocationChanges();
    }

    // This just feels wrong somehow... I feel like I am missing something about RxJava.
    private void subscribeToLocationChanges()
    {
        disposables.add(
                gpsLocationService.getWeatherLocation()
                        .subscribeWith(
                                new DisposableObserver<WeatherLocation>()
                                {
                                    @Override
                                    public void onNext(@NonNull WeatherLocation weatherLocation)
                                    {
                                        disposables.add(weatherService.getCurrentForecastData()
                                                .subscribe(
                                                        new Consumer<WeatherData>()
                                                        {
                                                            @Override
                                                            public void accept(@NonNull WeatherData weatherData) throws Exception
                                                            {
                                                                view.setLocationText(weatherData.getWeatherLocation().getPlaceString() + " - " + weatherData.getWeatherLocation().getZipCode());
                                                                view.setHighText(temperatureFormatter.format(weatherData.getTodayHigh()));
                                                                view.setLowText(temperatureFormatter.format(weatherData.getTodayLow()));
                                                                view.setLastUpdatedText(weatherData.getForecastDays().get(0).toString());
                                                            }
                                                        },
                                                        new Consumer<Throwable>()
                                                        {
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
                                        subscribeToLocationChanges();
                                        LOG.e(getClass().getName(), "weatherLocationObservable failed");
                                    }

                                    @Override
                                    public void onComplete()
                                    {
                                        subscribeToLocationChanges();
                                    }
                                }));
    }

    @Override
    public void unload()
    {
        disposables.dispose();
    }
}
