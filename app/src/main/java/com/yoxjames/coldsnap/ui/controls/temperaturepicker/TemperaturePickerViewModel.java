package com.yoxjames.coldsnap.ui.controls.temperaturepicker;

import android.support.annotation.IntDef;

import com.google.auto.value.AutoValue;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.SOURCE;

@AutoValue
public abstract class TemperaturePickerViewModel
{
    @Retention(SOURCE)
    @IntDef({ FAHRENHEIT, CELSIUS })
    public @interface Format {}

    public static final int FAHRENHEIT = 2;
    public static final int CELSIUS = 1;

    public static TemperaturePickerViewModel EMPTY = builder()
        .setFormat(CELSIUS)
        .setMaxValue(100)
        .setMinValue(0)
        .setValue(0)
        .build();

    public abstract int getValue();
    public abstract int getMinValue();
    public abstract int getMaxValue();
    @Format public abstract int getFormat();

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
        public abstract Builder setFormat(@Format int format);

        public abstract TemperaturePickerViewModel build();
    }
}
