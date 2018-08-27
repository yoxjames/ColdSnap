package com.yoxjames.coldsnap.prefs;

import com.google.auto.value.AutoValue;
import com.yoxjames.coldsnap.model.Temperature;
import com.yoxjames.coldsnap.service.location.SimpleWeatherLocation;
import com.yoxjames.coldsnap.service.preferences.CSPreferencesService.TemperatureFormat;

@AutoValue
public abstract class PreferenceModel
{
    public abstract Temperature getThreshold();
    public abstract @TemperatureFormat int getTemperatureFormat();
    public abstract float getWeatherDataFuzz();
    public abstract String getColdAlarmTime();
    public abstract String getLocationString();
    public abstract SimpleWeatherLocation getCoords();

    abstract Builder toBuilder();

    public static Builder builder()
    {
        return new AutoValue_PreferenceModel.Builder();
    }

    public PreferenceModel withCoords(SimpleWeatherLocation coords)
    {
        return toBuilder().setCoords(coords).build();
    }

    public PreferenceModel withThreshold(Temperature threshold)
    {
        return toBuilder().setThreshold(threshold).build();
    }

    public PreferenceModel withFuzz(float fuzz)
    {
        return toBuilder().setWeatherDataFuzz(fuzz).build();
    }

    @AutoValue.Builder
    public abstract static class Builder
    {
        public abstract Builder setThreshold(Temperature temperature);
        public abstract Builder setTemperatureFormat(@TemperatureFormat int temperatureFormat);
        public abstract Builder setWeatherDataFuzz(float fuzz);
        public abstract Builder setColdAlarmTime(String coldAlarmTime);
        public abstract Builder setLocationString(String locationString);
        public abstract Builder setCoords(SimpleWeatherLocation coords);

        public abstract PreferenceModel build();
    }
}
