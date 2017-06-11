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

package com.example.yoxjames.coldsnap.http.wu;

import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import com.example.yoxjames.coldsnap.http.HTTPWeatherService;
import com.example.yoxjames.coldsnap.model.ForecastDay;
import com.example.yoxjames.coldsnap.model.Temperature;
import com.example.yoxjames.coldsnap.model.WeatherData;
import com.example.yoxjames.coldsnap.model.WeatherDataNotFoundException;
import com.example.yoxjames.coldsnap.ui.CSPreferencesFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Provider;

import dagger.Lazy;
import dagger.internal.Preconditions;

import static com.example.yoxjames.coldsnap.BuildConfig.WUNDERGROUND_API_KEY;

/*
 * See WUNDERGROUND_API_KEY. This is set in private.properties and gradle is responsible for
 * creating this item.
 */

/**
 * Implementation of a WeatherData HTTPWeatherService that uses the Wunderground API.
 */
public class HTTPWeatherServiceWUImpl implements HTTPWeatherService
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

    private final Provider<Lazy<URL>> forecastURL;
    private final SharedPreferences sharedPreferences;

    @Inject
    public HTTPWeatherServiceWUImpl(Provider<Lazy<URL>> url, SharedPreferences sharedPreferences)
    {
        this.forecastURL = Preconditions.checkNotNull(url);
        this.sharedPreferences = sharedPreferences;
    }

    @Override
    public WeatherData getWeatherData() throws WeatherDataNotFoundException
    {
        final double fuzz = sharedPreferences.getFloat(CSPreferencesFragment.WEATHER_DATA_FUZZ,0f);
        final String zipCode = sharedPreferences.getString(CSPreferencesFragment.ZIPCODE, "64105");
        try
        {
            final WUForecast wuForecast = WUForecast.parseJSON(new JSONObject(getURLString(forecastURL.get().get())));
            List<ForecastDay> forecastDayList = new ArrayList<>();

            for (WUForecast.Day day : wuForecast.getDays())
            {
                final Temperature highTemperature = Temperature.newTemperatureFromF(day.getHighTempF(), fuzz);
                final Temperature lowTemperature = Temperature.newTemperatureFromF(day.getLowTempF(), fuzz);
                final ForecastDay forecastDay = new ForecastDay(day.getDateString(), highTemperature, lowTemperature, new Date(), UUID.randomUUID());
                forecastDayList.add(forecastDay);

                // TODO: Use something other than sharedPreferences for the locationString
            }
            return new WeatherData(forecastDayList, sharedPreferences.getString(CSPreferencesFragment.LOCATION_STRING, "Location"), zipCode, new Date());
        }
        catch (JSONException | IOException e)
        {
            throw new WeatherDataNotFoundException(e);
        }
    }

    private String getURLString(URL url) throws IOException
    {
        return new String(getURLBytes(url));
    }

    private byte[] getURLBytes(URL url) throws IOException
    {
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            InputStream in = connection.getInputStream();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() + ": with " + url);
            }

            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
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
            throw new IllegalArgumentException("zipCode is not valid");
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
