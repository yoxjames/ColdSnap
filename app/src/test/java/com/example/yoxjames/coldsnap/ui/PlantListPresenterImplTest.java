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
import com.example.yoxjames.coldsnap.model.WeatherDataNotFoundException;
import com.example.yoxjames.coldsnap.service.PlantService;
import com.example.yoxjames.coldsnap.service.WeatherService;
import com.example.yoxjames.coldsnap.service.WeatherServiceAsyncProcessor;
import com.example.yoxjames.coldsnap.service.WeatherServiceCall;
import com.example.yoxjames.coldsnap.service.WeatherServiceCallImpl;
import com.example.yoxjames.coldsnap.service.WeatherServiceCallback;
import com.example.yoxjames.coldsnap.ui.presenter.PlantListPresenter;
import com.example.yoxjames.coldsnap.ui.presenter.PlantListPresenterImpl;
import com.example.yoxjames.coldsnap.ui.view.PlantListItemView;
import com.example.yoxjames.coldsnap.ui.view.PlantListView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.UUID;

import javax.inject.Provider;

import dagger.Lazy;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.notNull;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PlantListPresenterImplTest
{
    private @Mock WeatherService weatherService;
    private @Mock PlantService plantService;
    private @Mock PlantListView plantListView;
    private @Mock WeatherServiceAsyncProcessor weatherServiceAsyncProcessor;
    private @Mock PlantListItemView plantListViewA;
    private @Mock PlantListItemView plantListViewB;

    private WeatherServiceCall weatherServiceCall;

    @Before
    public void setup()
    {
        weatherServiceCall = new WeatherServiceCallImpl(new Provider<Lazy<WeatherServiceAsyncProcessor>>()
        {
            @Override
            public Lazy<WeatherServiceAsyncProcessor> get()
            {
                return new Lazy<WeatherServiceAsyncProcessor>()
                {
                    @Override
                    public WeatherServiceAsyncProcessor get()
                    {
                        return weatherServiceAsyncProcessor;
                    }
                };
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

        // Load mock WeatherData. This dataset has the low for tonight at 32°F so our tender plant
        // should show up as "sad."
        doAnswer(new Answer<Void>()
        {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable
            {
                WeatherServiceCallback callback = (WeatherServiceCallback) invocation.getArguments()[0];
                callback.callback(WeatherDataMockFactory.getBasicWeatherData(), null);
                return null;
            }
        }).when(weatherServiceAsyncProcessor).execute(any(WeatherServiceCallback.class));

        PlantListPresenter plantListPresenter = new PlantListPresenterImpl(plantListView, plantService, weatherServiceCall);

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

        // Fail to load weather data. Simulate what would happen if we dont have an internet connection
        doAnswer(new Answer<Void>()
        {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable
            {
                WeatherServiceCallback callback = (WeatherServiceCallback) invocation.getArguments()[0];
                callback.callback(null, new WeatherDataNotFoundException());
                return null;
            }
        }).when(weatherServiceAsyncProcessor).execute(any(WeatherServiceCallback.class));
        PlantListPresenter plantListPresenter = new PlantListPresenterImpl(plantListView, plantService, weatherServiceCall);

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
        // Return n empty plant list
        when(plantService.getMyPlants()).thenReturn(new ArrayList<Plant>());

        PlantListPresenter plantListPresenter = new PlantListPresenterImpl(plantListView, plantService, weatherServiceCall);

        // Simulate the actions of the android recycler view
        plantListPresenter.load();

        assertEquals(plantListPresenter.getItemCount(), 0);
        verify(plantListView, times(0)).notifyDataChange();
    }

    @Test
    public void testAddPlant()
    {
        // Return n empty plant list
        when(plantService.getMyPlants()).thenReturn(new ArrayList<Plant>());

        PlantListPresenter plantListPresenter = new PlantListPresenterImpl(plantListView, plantService, weatherServiceCall);

        plantListPresenter.load();

        plantListPresenter.newPlant();

        verify(plantService, times(1)).cachePlant(notNull(Plant.class));
        verify(plantListView, times(1)).openPlant(notNull(UUID.class), any(Boolean.class));
    }
}
