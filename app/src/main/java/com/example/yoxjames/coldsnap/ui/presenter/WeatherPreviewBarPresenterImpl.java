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

import android.content.SharedPreferences;
import android.util.Log;

import com.example.yoxjames.coldsnap.model.TemperatureFormatter;
import com.example.yoxjames.coldsnap.model.WeatherData;
import com.example.yoxjames.coldsnap.model.WeatherLocation;
import com.example.yoxjames.coldsnap.ui.CSPreferencesFragment;
import com.example.yoxjames.coldsnap.ui.view.WeatherPreviewBarView;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableObserver;

public class WeatherPreviewBarPresenterImpl implements WeatherPreviewBarPresenter
{
    private final WeatherPreviewBarView view;
    private final TemperatureFormatter temperatureFormatter;
    private final Single<WeatherData> weatherDataSingle;
    private final Observable<WeatherLocation> weatherLocationObservable;
    private final SharedPreferences sharedPreferences;
    private Disposable weatherDataObserver;
    private Disposable weatherLocationObserver;


    /**
     * Constructor for WeatherPreviewBarPresenterImpl
     * @param view The view (Think MVP pattern)
     * @param temperatureFormatter Object designed to transaction Temperature objects into viewable strings.
     * @param weatherDataSingle Single observable that emits current WeatherData
     * @param weatherLocationObservable RX Subject that emits current WeatherLocations whenever the location changes.
     * @param sharedPreferences Preference service
     */
    @Inject
    public WeatherPreviewBarPresenterImpl(WeatherPreviewBarView view,
                                          TemperatureFormatter temperatureFormatter,
                                          Single<WeatherData> weatherDataSingle,
                                          Observable<WeatherLocation> weatherLocationObservable,
                                          SharedPreferences sharedPreferences)
    {
        this.view = view;
        this.temperatureFormatter = temperatureFormatter;
        this.weatherDataSingle = weatherDataSingle;
        this.weatherLocationObservable = weatherLocationObservable;
        this.sharedPreferences = sharedPreferences;
    }

    @Override
    public void load()
    {
        weatherDataObserver =
                weatherDataSingle
                .subscribe(
                        new Consumer<WeatherData>()
                        {
                            @Override
                            public void accept(@NonNull WeatherData weatherData) throws Exception
                            {
                                updateWeatherData(weatherData);
                            }
                        },
                        new Consumer<Throwable>()
                        {
                            @Override
                            public void accept(@NonNull Throwable throwable) throws Exception
                            {
                                throwable.printStackTrace();
                                Log.e("Observer Error", "weatherDataSingle failed in WeatherPreviewBarPresenterImpl");
                            }
                        });

        weatherLocationObserver =
                weatherLocationObservable
                .subscribeWith(
                        new DisposableObserver<WeatherLocation>()
                        {
                            @Override
                            public void onNext(@NonNull WeatherLocation weatherLocation)
                            {
                                final SharedPreferences.Editor preferenceEditor = sharedPreferences.edit();
                                preferenceEditor.putString(CSPreferencesFragment.LOCATION_STRING, weatherLocation.getPlaceString());
                                preferenceEditor.putString(CSPreferencesFragment.ZIPCODE, weatherLocation.getZipCode());
                                preferenceEditor.apply();

                                weatherDataObserver.dispose();
                                weatherDataObserver =
                                        weatherDataSingle
                                        .subscribe(
                                                new Consumer<WeatherData>()
                                                   {
                                                       @Override
                                                       public void accept(@NonNull WeatherData weatherData) throws Exception
                                                       {
                                                           updateWeatherData(weatherData);
                                                       }
                                                   },
                                                new Consumer<Throwable>()
                                                {
                                                    @Override
                                                    public void accept(@NonNull Throwable throwable) throws Exception
                                                    {
                                                        throwable.printStackTrace();
                                                        Log.e("Observer Error", "weatherDataSingle failed in WeatherPreviewBarPresenterImpl");
                                                    }
                                                });
                            }

                            @Override
                            public void onError(@NonNull Throwable e)
                            {
                                e.printStackTrace();
                                Log.e("Observer Error", "weatherLocationObservable failed in WeatherPreviewBarPresenterImpl");
                            }

                            @Override
                            public void onComplete()
                            {
                                Log.e("Observer Error", "weatherLocationObservable completed in WeatherPreviewBarPresenterImpl");
                            }
                        });
    }

    private void updateWeatherData(WeatherData weatherData)
    {
        view.setLocationText(weatherData.getWeatherLocation().getPlaceString() + " - " + weatherData.getWeatherLocation().getZipCode());
        view.setHighText(temperatureFormatter.format(weatherData.getTodayHigh()));
        view.setLowText(temperatureFormatter.format(weatherData.getTodayLow()));
        view.setLastUpdatedText(weatherData.getForecastDays().get(0).toString());
    }

    @Override
    public void unload()
    {
        if (!(weatherDataObserver == null) && !weatherDataObserver.isDisposed())
            weatherDataObserver.dispose();
        if (!(weatherLocationObserver == null) && !weatherLocationObserver.isDisposed())
            weatherLocationObserver.dispose();
    }
}
