package com.yoxjames.coldsnap.ui.controls.temperaturepicker;

import com.google.auto.value.AutoValue;
import com.yoxjames.coldsnap.service.preferences.CSPreferencesService.TemperatureFormat;

import static com.yoxjames.coldsnap.service.preferences.CSPreferencesService.CELSIUS;

@AutoValue
public abstract class TemperaturePickerViewModel
{
    public static TemperaturePickerViewModel EMPTY = builder()
        .setFormat(CELSIUS)
        .setMaxValue(100)
        .setMinValue(0)
        .setValue(0)
        .build();

    public abstract int getValue();
    public abstract int getMinValue();
    public abstract int getMaxValue();
    @TemperatureFormat public abstract int getFormat();

    public static Builder builder()
    {
        return new AutoValue_TemperaturePickerViewModel.Builder();
    }

    public abstract Builder toBuilder();

    public TemperaturePickerViewModel withValue(int value)
    {
        return toBuilder().setValue(value).build();
    }

    @AutoValue.Builder
    public static abstract class Builder
    {
        public abstract Builder setValue(int value);
        public abstract Builder setMinValue(int minValue);
        public abstract Builder setMaxValue(int maxValue);
        public abstract Builder setFormat(@TemperatureFormat int format);

        public abstract TemperaturePickerViewModel build();
    }
}
