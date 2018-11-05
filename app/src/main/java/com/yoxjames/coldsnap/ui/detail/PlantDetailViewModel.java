package com.yoxjames.coldsnap.ui.detail;

import com.google.auto.value.AutoValue;
import com.yoxjames.coldsnap.ui.controls.temperaturepicker.TemperaturePickerViewModel;
import com.yoxjames.coldsnap.ui.plantimage.PlantProfileImageViewModel;

@AutoValue
public abstract class PlantDetailViewModel
{
    public static PlantDetailViewModel EMPTY = PlantDetailViewModel.builder()
        .name("")
        .scientificName("")
        .temperaturePickerViewModel(TemperaturePickerViewModel.EMPTY)
        .plantProfileImageViewModel(PlantProfileImageViewModel.EMPTY)
        .build();

    public abstract String getName();
    public abstract String getScientificName();
    public abstract TemperaturePickerViewModel getTemperaturePickerViewModel();
    public abstract PlantProfileImageViewModel getPlantProfileImageViewModel();

    public static Builder builder()
    {
        return new AutoValue_PlantDetailViewModel.Builder();
    }

    @AutoValue.Builder
    public static abstract class Builder
    {
        public abstract Builder name(String name);
        public abstract Builder scientificName(String scientificName);
        public abstract Builder temperaturePickerViewModel(TemperaturePickerViewModel temperaturePickerViewModel);
        public abstract Builder plantProfileImageViewModel(PlantProfileImageViewModel plantProfileImageViewModel);

        public abstract PlantDetailViewModel build();
    }
}
