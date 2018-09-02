package com.yoxjames.coldsnap.prefs;

import android.content.SharedPreferences;

import com.yoxjames.coldsnap.model.Temperature;
import com.yoxjames.coldsnap.service.location.SimpleWeatherLocation;
import com.yoxjames.coldsnap.service.preferences.CSPreferencesService.TemperatureFormat;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

import static com.yoxjames.coldsnap.service.preferences.CSPreferencesService.CELSIUS;
import static com.yoxjames.coldsnap.service.preferences.CSPreferencesService.FAHRENHEIT;
import static com.yoxjames.coldsnap.service.preferences.CSPreferencesService.KELVIN;

public class CSSharedPreferencesImpl implements CSPreferences
{
    private static final String THRESHOLD = "com.yoxjames.coldsnap.THRESHOLD";
    private static final String TEMPERATURE_SCALE = "com.yoxjames.coldsnap.TEMPFORMAT";
    private static final String WEATHER_DATA_FUZZ = "com.yoxjames.coldsnap.WEATHER_DATA_FUZZ";
    private static final String COLD_ALARM_TIME = "com.yoxjames.coldsnap.COLD_ALARM_TIME";
    private static final String LOCATION_STRING = "com.yoxjames.coldsnap.LOCATION_STRING";
    private static final String LAT = "com.yoxjames.coldsnap.LAT";
    private static final String LON = "com.yoxjames.coldsnap.LON";

    private final SharedPreferences sharedPreferences;

    // Must be held here since simple registering it to sharedPrefs holds it with a WeakReference. This ensures it wont be GCed.
    private final SharedPreferences.OnSharedPreferenceChangeListener prefChangeListener;

    private final PublishSubject<PreferenceModel> prefChanges = PublishSubject.create();

    public CSSharedPreferencesImpl(SharedPreferences sharedPreferences)
    {
        this.sharedPreferences = sharedPreferences;
        this.prefChangeListener = (i, ii) -> prefChanges.onNext(getPreferenceModel());
        sharedPreferences.registerOnSharedPreferenceChangeListener(prefChangeListener);
    }

    @Override
    public Observable<PreferenceModel> getPreferences()
    {
        return Observable.just(getPreferenceModel()).mergeWith(prefChanges).subscribeOn(Schedulers.io()).serialize();
    }

    private PreferenceModel getPreferenceModel()
    {
        return PreferenceModel.builder()
            .setThreshold(getThreshold())
            .setTemperatureFormat(getTemperatureFormat())
            .setWeatherDataFuzz(getWeatherDataFuzz())
            .setColdAlarmTime(getColdAlarmTime())
            .setLocationString(getLocationString())
            .setCoords(getCoords())
            .build();
    }

    @Override
    public Completable savePreferences(PreferenceModel preferenceModel)
    {
        return Completable.complete();
    }

    @Override
    public Temperature getThreshold()
    {
        return Temperature.fromKelvin(sharedPreferences.getFloat(THRESHOLD, 273f));
    }

    @Override
    public @TemperatureFormat int getTemperatureFormat()
    {
        String stringVal = sharedPreferences.getString(TEMPERATURE_SCALE, "F");
        switch (stringVal) {
            case "F":
                return FAHRENHEIT;
            case "C":
                return CELSIUS;
            case "K":
                return KELVIN;
            default:
                throw new IllegalStateException("Invalid pref format");
        }
    }

    @Override
    public float getWeatherDataFuzz()
    {
        return sharedPreferences.getFloat(WEATHER_DATA_FUZZ, 0f);
    }

    @Override
    public String getColdAlarmTime()
    {
        return sharedPreferences.getString(COLD_ALARM_TIME, "19:00");
    }

    @Override
    public String getLocationString()
    {
        return sharedPreferences.getString(LOCATION_STRING, "UNKNOWN LOCATION");
    }

    @Override
    public SimpleWeatherLocation getCoords()
    {
        return SimpleWeatherLocation.create(
            sharedPreferences.getFloat(LAT, 39.098579f),
            sharedPreferences.getFloat(LON, -94.582596f));
    }

    @Override
    public void setThreshold(Temperature temperature)
    {
        savePrefs(getPreferenceModel().withThreshold(temperature));
    }

    @Override
    public void setFuzz(float fuzz)
    {
        savePrefs(getPreferenceModel().withFuzz(fuzz));
    }

    @Override
    public void setCoords(SimpleWeatherLocation simpleWeatherLocation)
    {
        savePrefs(getPreferenceModel().withCoords(simpleWeatherLocation));
    }

    @Override
    public void setLocationString(String locationString)
    {
        savePrefs(getPreferenceModel().toBuilder().setLocationString(locationString).build());
    }

    private void savePrefs(PreferenceModel preferenceModel)
    {

        final SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putFloat(LAT, (float) preferenceModel.getCoords().getLat());
        editor.putFloat(LON, (float) preferenceModel.getCoords().getLon());
        editor.putFloat(THRESHOLD, (float) preferenceModel.getThreshold().getKelvin());
        editor.putFloat(WEATHER_DATA_FUZZ, preferenceModel.getWeatherDataFuzz());
        editor.putString(COLD_ALARM_TIME, preferenceModel.getColdAlarmTime());
        editor.putString(LOCATION_STRING, preferenceModel.getLocationString());

        if (!editor.commit())
            throw new IllegalStateException("Saving preferences failed");
    }
}
