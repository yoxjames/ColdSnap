package com.yoxjames.coldsnap.prefs;

import android.support.annotation.StringDef;

import com.yoxjames.coldsnap.model.Temperature;
import com.yoxjames.coldsnap.service.location.SimpleWeatherLocation;
import com.yoxjames.coldsnap.service.preferences.CSPreferencesService.TemperatureFormat;

import java.lang.annotation.Retention;

import io.reactivex.Completable;
import io.reactivex.Observable;

import static java.lang.annotation.RetentionPolicy.SOURCE;

public interface CSPreferences
{
    @Retention(SOURCE)
    @StringDef({ THRESHOLD, TEMPERATURE_SCALE, WEATHER_DATA_FUZZ, COLD_ALARM_TIME, LOCATION_STRING, LAT, LON })
    @interface PreferenceKey {}

    String THRESHOLD = "com.yoxjames.coldsnap.THRESHOLD";
    String TEMPERATURE_SCALE = "com.yoxjames.coldsnap.TEMPFORMAT";
    String WEATHER_DATA_FUZZ = "com.yoxjames.coldsnap.WEATHER_DATA_FUZZ";
    String COLD_ALARM_TIME = "com.yoxjames.coldsnap.COLD_ALARM_TIME";
    String LOCATION_STRING = "com.yoxjames.coldsnap.LOCATION_STRING";
    String LAT = "com.yoxjames.coldsnap.LAT";
    String LON = "com.yoxjames.coldsnap.LON";

    Observable<PreferenceModel> getPreferences();
    Completable savePreferences(PreferenceModel preferenceModel);

    Temperature getThreshold();
    @TemperatureFormat int getTemperatureFormat();
    float getWeatherDataFuzz();
    String getColdAlarmTime();
    String getLocationString();
    SimpleWeatherLocation getCoords();

    void setThreshold(Temperature temperature);
    void setFuzz(float fuzz);
    void setCoords(SimpleWeatherLocation simpleWeatherLocation);
    void setLocationString(String locationString);
}
