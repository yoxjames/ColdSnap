package com.yoxjames.coldsnap.service.preferences;

import android.support.annotation.IntDef;

import com.yoxjames.coldsnap.service.location.SimpleWeatherLocation;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.SOURCE;

public interface CSPreferencesService
{
    @Retention(SOURCE)
    @IntDef( {KELVIN, CELSIUS, FAHRENHEIT} )
    @interface TemperatureFormat {}

    int KELVIN = 0;
    int CELSIUS = 1;
    int FAHRENHEIT = 2;

    @TemperatureFormat int getTemperatureFormat();
    SimpleWeatherLocation getLocation();
    String getLocationString();
    String getColdAlarmTime();
    double getFuzz();
    int getThreshold();
}
