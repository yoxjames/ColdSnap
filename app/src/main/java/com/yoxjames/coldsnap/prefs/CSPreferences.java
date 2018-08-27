package com.yoxjames.coldsnap.prefs;

import com.yoxjames.coldsnap.model.Temperature;
import com.yoxjames.coldsnap.service.location.SimpleWeatherLocation;
import com.yoxjames.coldsnap.service.preferences.CSPreferencesService.TemperatureFormat;

import io.reactivex.Completable;
import io.reactivex.Observable;

public interface CSPreferences
{
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
