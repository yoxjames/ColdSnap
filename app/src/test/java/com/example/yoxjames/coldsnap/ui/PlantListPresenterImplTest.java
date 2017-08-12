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

import com.example.yoxjames.coldsnap.mocks.PlantMockFactory;
import com.example.yoxjames.coldsnap.mocks.WeatherDataMockFactory;
import com.example.yoxjames.coldsnap.model.Plant;
import com.example.yoxjames.coldsnap.model.SimpleWeatherLocation;
import com.example.yoxjames.coldsnap.model.WeatherData;
import com.example.yoxjames.coldsnap.model.WeatherDataNotFoundException;
import com.example.yoxjames.coldsnap.model.WeatherLocation;
import com.example.yoxjames.coldsnap.service.plant.PlantService;
import com.example.yoxjames.coldsnap.service.weather.WeatherService;
import com.example.yoxjames.coldsnap.ui.presenter.PlantListPresenter;
import com.example.yoxjames.coldsnap.ui.presenter.PlantListPresenterImpl;
import com.example.yoxjames.coldsnap.ui.view.PlantListItemView;
import com.example.yoxjames.coldsnap.ui.view.PlantListView;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
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

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.notNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PlantListPresenterImplTest
{
    private @Mock WeatherService weatherService;
    private @Mock PlantService plantService;
    private @Mock PlantListView plantListView;
    private @Mock PlantListItemView plantListViewA;
    private @Mock PlantListItemView plantListViewB;
    private WeatherData currentWeatherData = WeatherDataMockFactory.getBasicWeatherData();
    private PlantListPresenter plantListPresenter;

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

    /**
     * Test the happiest path. We can successfully load all the plants and successfully load
     * all the WeatherData. Ensure that we correctly set plant information including statuses
     * based on our mock WeatherData.
     */
    @Test
    public void testLoadHappyPath()
    {
        // Return a generic plant list. One frost tolerant and one non frost tolerant.
        when(plantService.getMyPlants()).thenReturn(PlantMockFactory.getMockPlantList());

        Single<WeatherData> weatherDataSingle = Single.create(new SingleOnSubscribe<WeatherData>()
        {
            @Override
            public void subscribe(@NonNull SingleEmitter<WeatherData> e) throws Exception
            {
                e.onSuccess(currentWeatherData);
            }
        });

        Subject<SimpleWeatherLocation> simpleWeatherLocationSubject = PublishSubject.create();

        Observable<WeatherLocation> weatherLocationObservable = simpleWeatherLocationSubject.map(new Function<SimpleWeatherLocation, WeatherLocation>()
        {
            @Override
            public WeatherLocation apply(@NonNull SimpleWeatherLocation simpleWeatherLocation) throws Exception
            {
                return new WeatherLocation("55555", "Testville", 40f, 40f);
            }
        });

        plantListPresenter = new PlantListPresenterImpl(plantListView, plantService, weatherDataSingle, simpleWeatherLocationSubject, weatherLocationObservable);

        // Simulate the actions of the android recycler view
        plantListPresenter.load();
        plantListPresenter.loadPlantView(plantListViewA, 0); // Freeze tender
        plantListPresenter.loadPlantView(plantListViewB, 1); // Freeze tolerant

        verify(plantListView, times(1)).notifyDataChange();

        verify(plantListViewA, times(1)).setPlantName(PlantMockFactory.getFreezeTenderPlant().getName());
        verify(plantListViewA, times(1)).setPlantScientificName(PlantMockFactory.getFreezeTenderPlant().getScientificName());
        verify(plantListViewA, times(1)).setUUID(notNull(UUID.class));
        verify(plantListViewA, times(1)).setStatus("\uD83D\uDE1E"); // Sad Face

        verify(plantListViewB, times(1)).setPlantName(PlantMockFactory.getFreezeTolerantPlant().getName());
        verify(plantListViewB, times(1)).setPlantScientificName(PlantMockFactory.getFreezeTolerantPlant().getScientificName());
        verify(plantListViewB, times(1)).setUUID(notNull(UUID.class));
        verify(plantListViewB, times(1)).setStatus("\uD83D\uDE00"); // Happy Face
    }

    /**
     * Test what happens when we can successfully load plants but we cannot load WeatherData.
     * Specifically test when loading WeatherData returns a WeatherDataNotFoundException. This
     * should happen in cases where there is no internet access.
     */
    @Test
    public void testLoadWeatherFails()
    {
        // Return a generic plant list. One frost tolerant and one non frost tolerant.
        when(plantService.getMyPlants()).thenReturn(PlantMockFactory.getMockPlantList());

        Single<WeatherData> weatherDataSingle = Single.create(new SingleOnSubscribe<WeatherData>()
        {
            @Override
            public void subscribe(@NonNull SingleEmitter<WeatherData> e) throws Exception
            {
                e.onError(new WeatherDataNotFoundException("Oops no weather Data :("));
            }
        });

        Subject<SimpleWeatherLocation> simpleWeatherLocationSubject = PublishSubject.create();

        Observable<WeatherLocation> weatherLocationObservable = simpleWeatherLocationSubject.map(new Function<SimpleWeatherLocation, WeatherLocation>()
        {
            @Override
            public WeatherLocation apply(@NonNull SimpleWeatherLocation simpleWeatherLocation) throws Exception
            {
                return new WeatherLocation("55555", "Testville", 40f, 40f);
            }
        });

        plantListPresenter = new PlantListPresenterImpl(plantListView, plantService, weatherDataSingle, simpleWeatherLocationSubject, weatherLocationObservable);

        // Simulate the actions of the android recycler view
        plantListPresenter.load();
        plantListPresenter.loadPlantView(plantListViewA, 0); // Freeze tender
        plantListPresenter.loadPlantView(plantListViewB, 1); // Freeze tolerant

        assertEquals(plantListPresenter.getItemCount(), 2);
        verify(plantListView, times(0)).notifyDataChange();

        verify(plantListViewA, times(1)).setPlantName(PlantMockFactory.getFreezeTenderPlant().getName());
        verify(plantListViewA, times(1)).setPlantScientificName(PlantMockFactory.getFreezeTenderPlant().getScientificName());
        verify(plantListViewA, times(1)).setUUID(notNull(UUID.class));
        verify(plantListViewA, times(1)).setStatus("...");

        verify(plantListViewB, times(1)).setPlantName(PlantMockFactory.getFreezeTolerantPlant().getName());
        verify(plantListViewB, times(1)).setPlantScientificName(PlantMockFactory.getFreezeTolerantPlant().getScientificName());
        verify(plantListViewB, times(1)).setUUID(notNull(UUID.class));
        verify(plantListViewB, times(1)).setStatus("...");
    }

    @Test
    public void testNoPlants()
    {
        // Return a generic plant list. One frost tolerant and one non frost tolerant.
        when(plantService.getMyPlants()).thenReturn(new ArrayList<Plant>());

        Single<WeatherData> weatherDataSingle = Single.create(new SingleOnSubscribe<WeatherData>()
        {
            @Override
            public void subscribe(@NonNull SingleEmitter<WeatherData> e) throws Exception
            {
                e.onSuccess(currentWeatherData);
            }
        });

        Subject<SimpleWeatherLocation> simpleWeatherLocationSubject = PublishSubject.create();

        Observable<WeatherLocation> weatherLocationObservable = simpleWeatherLocationSubject.map(new Function<SimpleWeatherLocation, WeatherLocation>()
        {
            @Override
            public WeatherLocation apply(@NonNull SimpleWeatherLocation simpleWeatherLocation) throws Exception
            {
                return new WeatherLocation("55555", "Testville", 40f, 40f);
            }
        });

        plantListPresenter = new PlantListPresenterImpl(plantListView, plantService, weatherDataSingle, simpleWeatherLocationSubject, weatherLocationObservable);

        // Simulate the actions of the android recycler view
        plantListPresenter.load();

        assertEquals(plantListPresenter.getItemCount(), 0);
        verify(plantListView, times(0)).notifyDataChange();
    }

    @Test
    public void testAddPlant()
    {
        Single<WeatherData> weatherDataSingle = Single.create(new SingleOnSubscribe<WeatherData>()
        {
            @Override
            public void subscribe(@NonNull SingleEmitter<WeatherData> e) throws Exception
            {
                e.onSuccess(currentWeatherData);
            }
        });

        Subject<SimpleWeatherLocation> simpleWeatherLocationSubject = PublishSubject.create();

        Observable<WeatherLocation> weatherLocationObservable = simpleWeatherLocationSubject.map(new Function<SimpleWeatherLocation, WeatherLocation>()
        {
            @Override
            public WeatherLocation apply(@NonNull SimpleWeatherLocation simpleWeatherLocation) throws Exception
            {
                return new WeatherLocation("55555", "Testville", 40f, 40f);
            }
        });

        plantListPresenter = new PlantListPresenterImpl(plantListView, plantService, weatherDataSingle, simpleWeatherLocationSubject, weatherLocationObservable);
        plantListPresenter.load();

        plantListPresenter.newPlant();

        verify(plantService, times(1)).cachePlant(notNull(Plant.class));
        verify(plantListView, times(1)).openPlant(notNull(UUID.class), any(Boolean.class));
    }

    @Test
    public void testChangeWeatherLocation()
    {
        // Return a generic plant list. One frost tolerant and one non frost tolerant.
        when(plantService.getMyPlants()).thenReturn(PlantMockFactory.getMockPlantList());

        Single<WeatherData> weatherDataSingle = Single.create(new SingleOnSubscribe<WeatherData>()
        {
            @Override
            public void subscribe(@NonNull SingleEmitter<WeatherData> e) throws Exception
            {
                e.onSuccess(currentWeatherData);
            }
        });

        Subject<SimpleWeatherLocation> simpleWeatherLocationSubject = PublishSubject.create();

        Observable<WeatherLocation> weatherLocationObservable = simpleWeatherLocationSubject.map(new Function<SimpleWeatherLocation, WeatherLocation>()
        {
            @Override
            public WeatherLocation apply(@NonNull SimpleWeatherLocation simpleWeatherLocation) throws Exception
            {
                return new WeatherLocation("55555", "Testville", 40f, 40f);
            }
        });

        plantListPresenter = new PlantListPresenterImpl(plantListView, plantService, weatherDataSingle, simpleWeatherLocationSubject, weatherLocationObservable);

        plantListPresenter.load();
        plantListPresenter.loadPlantView(plantListViewA, 0); // Freeze tender
        plantListPresenter.loadPlantView(plantListViewB, 1); // Freeze tolerant
        verify(plantListView, times(1)).notifyDataChange();
        simpleWeatherLocationSubject.onNext(new SimpleWeatherLocation(45f, 45f));
        verify(plantListView, times(2)).notifyDataChange();
    }

    @After
    public void teardown()
    {
        plantListPresenter.unload();
        plantListPresenter = null;
    }
}
