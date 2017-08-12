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

package com.example.yoxjames.coldsnap.ui;

import android.content.SharedPreferences;

import com.example.yoxjames.coldsnap.mocks.WeatherDataMockFactory;
import com.example.yoxjames.coldsnap.model.TemperatureFormatter;
import com.example.yoxjames.coldsnap.model.TemperatureFormatterImpl;
import com.example.yoxjames.coldsnap.model.WeatherData;
import com.example.yoxjames.coldsnap.model.WeatherDataNotFoundException;
import com.example.yoxjames.coldsnap.model.WeatherLocation;
import com.example.yoxjames.coldsnap.ui.presenter.WeatherPreviewBarPresenterImpl;
import com.example.yoxjames.coldsnap.ui.view.WeatherPreviewBarView;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.concurrent.Callable;

import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WeatherPreviewBarPresenterImplTest
{
    private @Mock WeatherPreviewBarView weatherPreviewBarView;
    private @Mock SharedPreferences sharedPreferences;
    private @Mock SharedPreferences.Editor sharedPreferencesEditor;
    private Single<WeatherData> weatherDataSingle;
    private Subject<WeatherLocation> weatherLocationSubject;
    private WeatherData currentWeatherData;

    private TemperatureFormatter temperatureFormatter;

    @BeforeClass
    public static void setupClass()
    {
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(new Function<Callable<Scheduler>, Scheduler>()
        {
            @Override
            public Scheduler apply(@NonNull Callable<Scheduler> schedulerCallable) throws Exception
            {
                return Schedulers.trampoline();
            }
        });

        RxJavaPlugins.setIoSchedulerHandler(new Function<Scheduler, Scheduler>()
        {
            @Override
            public Scheduler apply(@NonNull Scheduler scheduler) throws Exception
            {
                return Schedulers.trampoline();
            }
        });
    }
    @Before
    public void setup()
    {
        currentWeatherData = WeatherDataMockFactory.getBasicWeatherData();
        weatherDataSingle = Single.create(new SingleOnSubscribe<WeatherData>()
        {
            @Override
            public void subscribe(@NonNull SingleEmitter<WeatherData> e) throws Exception
            {
                e.onSuccess(currentWeatherData);
            }
        });

        weatherLocationSubject = PublishSubject.create();
        when(sharedPreferences.getString(CSPreferencesFragment.TEMPERATURE_SCALE, "F")).thenReturn("F");
        when(sharedPreferences.edit()).thenReturn(sharedPreferencesEditor);
        temperatureFormatter = new TemperatureFormatterImpl(sharedPreferences);

    }

    @Test
    public void testHappyPath() throws WeatherDataNotFoundException
    {
        WeatherPreviewBarPresenterImpl weatherPreviewBarPresenter
                = new WeatherPreviewBarPresenterImpl(weatherPreviewBarView, temperatureFormatter, weatherDataSingle, weatherLocationSubject, sharedPreferences);
        weatherPreviewBarPresenter.load();

        verify(weatherPreviewBarView, times(1)).setLocationText("Kansas City, MO - 64105");
        verify(weatherPreviewBarView, times(1)).setHighText("72°F");
        verify(weatherPreviewBarView, times(1)).setLowText("31°F");
        verify(weatherPreviewBarView, times(1)).setLastUpdatedText("Jun 1 2017");

        weatherPreviewBarPresenter.unload();
    }

    @Test
    public void testOnLocationChange()
    {
        WeatherPreviewBarPresenterImpl weatherPreviewBarPresenter
                = new WeatherPreviewBarPresenterImpl(weatherPreviewBarView, temperatureFormatter, weatherDataSingle, weatherLocationSubject, sharedPreferences);

        weatherPreviewBarPresenter.load();
        weatherLocationSubject.onNext(new WeatherLocation("55555", "Testville, TS", 0f, 0f));

        // All of these are called twice because the observer should reload the view.
        verify(weatherPreviewBarView, times(2)).setLocationText("Kansas City, MO - 64105");
        verify(weatherPreviewBarView, times(2)).setHighText("72°F");
        verify(weatherPreviewBarView, times(2)).setLowText("31°F");
        verify(weatherPreviewBarView, times(2)).setLastUpdatedText("Jun 1 2017");

        verify(sharedPreferencesEditor, times(1)).putString(CSPreferencesFragment.LOCATION_STRING, "Testville, TS");
        verify(sharedPreferencesEditor, times(1)).putString(CSPreferencesFragment.ZIPCODE, "55555");
        verify(sharedPreferencesEditor, times(1)).apply();

        weatherPreviewBarPresenter.unload();
    }

    @AfterClass
    public static void teardownClass()
    {
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(null);
        RxJavaPlugins.setIoSchedulerHandler(null);
    }
}
