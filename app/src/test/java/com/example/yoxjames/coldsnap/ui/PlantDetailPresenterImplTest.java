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

import android.widget.NumberPicker;

import com.example.yoxjames.coldsnap.mocks.PlantMockFactory;
import com.example.yoxjames.coldsnap.model.Plant;
import com.example.yoxjames.coldsnap.model.Temperature;
import com.example.yoxjames.coldsnap.service.PlantService;
import com.example.yoxjames.coldsnap.ui.controls.TemperaturePickerAdapter;
import com.example.yoxjames.coldsnap.ui.presenter.PlantDetailPresenter;
import com.example.yoxjames.coldsnap.ui.presenter.PlantDetailPresenterImpl;
import com.example.yoxjames.coldsnap.ui.view.PlantDetailView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PlantDetailPresenterImplTest
{
    private @Mock PlantDetailView plantDetailView;
    private @Mock PlantService plantService;
    private @Mock TemperaturePickerAdapter temperaturePickerAdapter;
    private @Mock NumberPicker.Formatter pickerFormatter;

    @Before
    public void setupTest()
    {
        when(temperaturePickerAdapter.getMinimumTemperatureValue()).thenReturn(0);
        when(temperaturePickerAdapter.getMaximumTemperatureValue()).thenReturn(100);
        when(temperaturePickerAdapter.getFormatter()).thenReturn(pickerFormatter);
    }

    @Test
    public void testLoadExisting()
    {
        Plant plant = PlantMockFactory.getFreezeTolerantPlant();

        PlantDetailPresenter plantDetailPresenter = new PlantDetailPresenterImpl(plantDetailView, plantService, temperaturePickerAdapter);
        when(plantService.getPlant(plant.getUuid())).thenReturn(plant);
        when(plantDetailView.isNewPlantInd()).thenReturn(false);
        when(temperaturePickerAdapter.getValueForTemperature(plant.getMinimumTolerance())).thenReturn(20);
        plantDetailPresenter.load(plant.getUuid());
        verify(plantDetailView,times(1)).setMinBound(0);
        verify(plantDetailView,times(1)).setMaxBound(100);
        verify(plantDetailView,times(1)).setMinimumTemperatureFormatter(pickerFormatter);
        verify(plantDetailView,times(0)).setAddMode();
        verify(plantDetailView,times(1)).setPlantName(plant.getName());
        verify(plantDetailView,times(1)).setPlantScientificName(plant.getScientificName());
        verify(plantDetailView,times(1)).setMinTemperature(20);
    }

    @Test
    public void testLoadNew()
    {
        Plant plant = PlantMockFactory.getFreezeTenderPlant();

        PlantDetailPresenter plantDetailPresenter = new PlantDetailPresenterImpl(plantDetailView, plantService, temperaturePickerAdapter);
        when(plantService.getPlant(plant.getUuid())).thenReturn(plant);
        when(plantDetailView.isNewPlantInd()).thenReturn(true);
        when(temperaturePickerAdapter.getValueForTemperature(plant.getMinimumTolerance())).thenReturn(20);
        plantDetailPresenter.load(plant.getUuid());
        verify(plantDetailView,times(1)).setMinBound(0);
        verify(plantDetailView,times(1)).setMaxBound(100);
        verify(plantDetailView,times(1)).setMinimumTemperatureFormatter(pickerFormatter);
        verify(plantDetailView,times(1)).setAddMode();
        verify(plantDetailView,times(1)).setPlantName(plant.getName());
        verify(plantDetailView,times(1)).setPlantScientificName(plant.getScientificName());
        verify(plantDetailView,times(1)).setMinTemperature(20);
    }

    @Test
    public void deletePlant()
    {
        Plant plant = PlantMockFactory.getFreezeTenderPlant();

        PlantDetailPresenter plantDetailPresenter = new PlantDetailPresenterImpl(plantDetailView, plantService, temperaturePickerAdapter);

        plantDetailPresenter.deletePlant(plant.getUuid());
        verify(plantService,times(1)).deletePlant(plant.getUuid());
        verify(plantDetailView,times(1)).displayDeleteMessage();
    }

    @Test
    public void savePlantInformationExisting()
    {
        Plant plant = PlantMockFactory.getFreezeTenderPlant();
        Temperature newMinTemp = new Temperature(283.0);
        ArgumentCaptor<Plant> addPlantArg = ArgumentCaptor.forClass(Plant.class);

        PlantDetailPresenter plantDetailPresenter = new PlantDetailPresenterImpl(plantDetailView, plantService, temperaturePickerAdapter);
        when(plantDetailView.isNewPlantInd()).thenReturn(false);
        when(plantDetailView.getPlantName()).thenReturn("Junit");
        when(plantDetailView.getPlantScientificName()).thenReturn("Junitus testus");
        when(plantDetailView.getMinTemperature()).thenReturn(30);
        when(temperaturePickerAdapter.getTemperatureForValue(30)).thenReturn(newMinTemp);

        plantDetailPresenter.savePlantInformation(plant.getUuid());
        verify(plantService,times(1)).deletePlant(plant.getUuid());
        verify(plantService).addPlant(addPlantArg.capture());
        Plant capturedPlant = addPlantArg.getValue();
        assertEquals("Junit", capturedPlant.getName());
        assertEquals("Junitus testus", capturedPlant.getScientificName());
        assertEquals(newMinTemp, capturedPlant.getMinimumTolerance());
        assertEquals(plant.getUuid(), capturedPlant.getUuid());
        verify(plantDetailView,times(1)).displaySaveMessage(false);
    }

    @Test
    public void savePlantInformationNew()
    {
        Plant plant = PlantMockFactory.getFreezeTenderPlant();
        Temperature newMinTemp = new Temperature(283.0);
        ArgumentCaptor<Plant> addPlantArg = ArgumentCaptor.forClass(Plant.class);

        PlantDetailPresenter plantDetailPresenter = new PlantDetailPresenterImpl(plantDetailView, plantService, temperaturePickerAdapter);
        when(plantDetailView.isNewPlantInd()).thenReturn(true);
        when(plantDetailView.getPlantName()).thenReturn("Junit");
        when(plantDetailView.getPlantScientificName()).thenReturn("Junitus testus");
        when(plantDetailView.getMinTemperature()).thenReturn(30);
        when(temperaturePickerAdapter.getTemperatureForValue(30)).thenReturn(newMinTemp);

        plantDetailPresenter.savePlantInformation(plant.getUuid());
        verify(plantService,times(0)).deletePlant(plant.getUuid());
        verify(plantService).addPlant(addPlantArg.capture());
        Plant capturedPlant = addPlantArg.getValue();
        assertEquals("Junit", capturedPlant.getName());
        assertEquals("Junitus testus", capturedPlant.getScientificName());
        assertEquals(newMinTemp, capturedPlant.getMinimumTolerance());
        assertEquals(plant.getUuid(), capturedPlant.getUuid());
        verify(plantDetailView,times(1)).displaySaveMessage(true);
    }
}
