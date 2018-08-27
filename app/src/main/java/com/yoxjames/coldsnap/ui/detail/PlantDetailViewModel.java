package com.yoxjames.coldsnap.ui.detail;

import com.google.auto.value.AutoValue;
import com.yoxjames.coldsnap.ui.controls.temperaturepicker.TemperaturePickerViewModel;
import com.yoxjames.coldsnap.ui.plantimage.PlantProfileImageViewModel;

@AutoValue
public abstract class PlantDetailViewModel
{
    public static PlantDetailViewModel EMPTY = PlantDetailViewModel.builder()
        .setName("")
        .setScientificName("")
        .setTemperaturePickerViewModel(TemperaturePickerViewModel.EMPTY)
        .setPlantProfileImageViewModel(PlantProfileImageViewModel.EMPTY)
        .build();

    public abstract String getName();
    public abstract String getScientificName();
    public abstract TemperaturePickerViewModel getTemperaturePickerViewModel();
    public abstract PlantProfileImageViewModel getPlantProfileImageViewModel();

    public abstract Builder toBuilder();

    public PlantDetailViewModel withPlantProfileImageViewModel(PlantProfileImageViewModel vm)
    {
        return toBuilder().setPlantProfileImageViewModel(vm).build();
    }

    public static Builder builder()
    {
        return new AutoValue_PlantDetailViewModel.Builder();
    }

    @AutoValue.Builder
    public static abstract class Builder
    {
        public abstract Builder setName(String name);
        public abstract Builder setScientificName(String scientificName);
        public abstract Builder setTemperaturePickerViewModel(TemperaturePickerViewModel temperaturePickerViewModel);
        public abstract Builder setPlantProfileImageViewModel(PlantProfileImageViewModel plantProfileImageViewModel);

        public abstract PlantDetailViewModel build();
    }
}
