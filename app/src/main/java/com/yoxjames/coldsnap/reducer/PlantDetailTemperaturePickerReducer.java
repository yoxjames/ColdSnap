package com.yoxjames.coldsnap.reducer;

import com.yoxjames.coldsnap.model.Plant;
import com.yoxjames.coldsnap.model.TemperatureValueAdapter;
import com.yoxjames.coldsnap.prefs.CSPreferences;
import com.yoxjames.coldsnap.service.preferences.CSPreferencesService.TemperatureFormat;
import com.yoxjames.coldsnap.ui.controls.temperaturepicker.TemperaturePickerViewModel;

import javax.inject.Inject;

public class PlantDetailTemperaturePickerReducer
{
    private final CSPreferences csPreferences;
    private final TemperatureValueAdapter temperatureValueAdapter;

    @Inject
    public PlantDetailTemperaturePickerReducer(CSPreferences csPreferences, TemperatureValueAdapter temperatureValueAdapter)
    {
        this.csPreferences = csPreferences;
        this.temperatureValueAdapter = temperatureValueAdapter;
    }

    public TemperaturePickerViewModel reduce(Plant plant)
    {
        return TemperaturePickerViewModel.builder()
            .setFormat(getTemperatureFormat())
            .setMinValue(-100)
            .setMaxValue(100)
            .setValue(temperatureValueAdapter.getValue(plant.getMinimumTolerance()))
            .build();
    }

    private @TemperatureFormat int getTemperatureFormat()
    {
        return csPreferences.getTemperatureFormat();
    }
}
