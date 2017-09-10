/*
 * Copyright (c) 2017 James Yox
 *
 * This file is part of ColdSnap.
 *
 * ColdSnap is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ColdSnap is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ColdSnap.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.yoxjames.coldsnap.http.wu;

import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import com.yoxjames.coldsnap.http.GenericHTTPService;
import com.yoxjames.coldsnap.http.HTTPWeatherService;
import com.yoxjames.coldsnap.model.ForecastDay;
import com.yoxjames.coldsnap.model.Temperature;
import com.yoxjames.coldsnap.model.WeatherData;
import com.yoxjames.coldsnap.model.WeatherLocation;
import com.yoxjames.coldsnap.ui.CSPreferencesFragment;
import com.yoxjames.coldsnap.util.LOG;
import org.json.JSONObject;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import static com.yoxjames.coldsnap.BuildConfig.WUNDERGROUND_API_KEY;


/*
 * See WUNDERGROUND_API_KEY. This is set in private.properties and gradle is responsible for
 * creating this item.
 */

/**
 * Implementation of a WeatherData HTTPWeatherService that uses the Wunderground API.
 */
public class HTTPWeatherServiceWUImpl extends GenericHTTPService implements HTTPWeatherService
{
    /*
     * URL For Wunderground.
     */
    private final static String BASE_URL = "http://api.wunderground.com/api/";

    /*
     * Private API Key for Wunderground. The value is contained in private.properties which is not
     * committed to any Git repository. If you are compiling this yourself please use your own API key.
     */
    private final static String API_KEY = WUNDERGROUND_API_KEY;

    /*
     * Forecast service will be used to populate most of this data.
     */
    private final static String FORECAST_SERVICE = "/forecast/q";

    private final WundergroundURLFactory urlFactory;
    private final SharedPreferences sharedPreferences;

    @Inject
    public HTTPWeatherServiceWUImpl(WundergroundURLFactory urlFactory, SharedPreferences sharedPreferences)
    {
        this.urlFactory = urlFactory;
        this.sharedPreferences = sharedPreferences;
    }

    @Override
    public Single<WeatherData> getWeatherData(final WeatherLocation weatherLocation)
    {
        return Single.create(new SingleOnSubscribe<WeatherData>()
        {
            @Override
            public void subscribe(@NonNull SingleEmitter<WeatherData> e) throws Exception
            {
                final double fuzz = sharedPreferences.getFloat(CSPreferencesFragment.WEATHER_DATA_FUZZ, 0f);
                LOG.d("API", "Wunderground API Call ->" + FORECAST_SERVICE);
                final WUForecast wuForecast = WUForecast.parseJSON(new JSONObject(getURLString(urlFactory.create(weatherLocation))));
                List<ForecastDay> forecastDayList = new ArrayList<>();

                for (WUForecast.Day day : wuForecast.getDays())
                {
                    final Temperature highTemperature = Temperature.newTemperatureFromF(day.getHighTempF(), fuzz);
                    final Temperature lowTemperature = Temperature.newTemperatureFromF(day.getLowTempF(), fuzz);
                    final ForecastDay forecastDay = new ForecastDay(day.getDateString(), highTemperature, lowTemperature, new Date(), UUID.randomUUID());
                    forecastDayList.add(forecastDay);
                }

                e.onSuccess(new WeatherData(forecastDayList, new Date(), weatherLocation));
            }
        }).subscribeOn(Schedulers.io());
    }

    /**
     * Returns a URL object to get Forecast data from Wunderground for a given zipcode.
     *
     * @param zipcode The zipcode we want to obtain WeatherData for.
     * @return A URL object that will return the forecast data we desire as JSON.
     */
    @Nullable
    public static URL getAbsoluteUrl(String zipcode)
    {
        if (zipcode == null)
            throw new IllegalArgumentException("zipCode cannot be null");
        if (zipcode.equals(""))
            throw new IllegalArgumentException("zipCode cannot be blank");
        if (zipcode.length() != 5)
            throw new IllegalArgumentException("zipCode is not valid: " + zipcode);
        try
        {
            return new URL(BASE_URL + API_KEY + FORECAST_SERVICE + "/" + zipcode + ".json");
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
            return null;
        }
    }
}
