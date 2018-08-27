package com.yoxjames.coldsnap.reducer;

import com.yoxjames.coldsnap.model.Plant;
import com.yoxjames.coldsnap.model.TemperatureValueAdapter;
import com.yoxjames.coldsnap.prefs.CSPreferences;
import com.yoxjames.coldsnap.service.preferences.CSPreferencesService.TemperatureFormat;
import com.yoxjames.coldsnap.ui.controls.temperaturepicker.TemperaturePickerViewModel;

import javax.inject.Inject;

import static com.yoxjames.coldsnap.service.preferences.CSPreferencesService.FAHRENHEIT;

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
            .setFormat(getVMTemperatureFormat())
            .setMinValue(-100)
            .setMaxValue(100)
            .setValue(temperatureValueAdapter.getValue(plant.getMinimumTolerance()))
            .build();
    }

    public TemperaturePickerViewModel reduce()
    {
        return TemperaturePickerViewModel.builder()
            .setFormat(getVMTemperatureFormat())
            .setMinValue(-100)
            .setMaxValue(100) // TODO: These should vary with the temp format
            .setValue(getTemperatureFormat() == FAHRENHEIT ? 32 : 0) // TODO: Handle this better
            .build();
    }

    private @TemperatureFormat int getTemperatureFormat()
    {
        return csPreferences.getTemperatureFormat();
    }

    private @TemperaturePickerViewModel.Format int getVMTemperatureFormat()
    {
        return csPreferences.getTemperatureFormat() == FAHRENHEIT ?
            TemperaturePickerViewModel.FAHRENHEIT : TemperaturePickerViewModel.CELSIUS;
    }
}
