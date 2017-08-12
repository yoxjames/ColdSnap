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

package com.example.yoxjames.coldsnap.service;

import android.content.SharedPreferences;

import com.example.yoxjames.coldsnap.BuildConfig;
import com.example.yoxjames.coldsnap.http.HTTPWeatherService;
import com.example.yoxjames.coldsnap.http.wu.HTTPWeatherServiceWUImpl;
import com.example.yoxjames.coldsnap.http.wu.WundergroundURLFactory;
import com.example.yoxjames.coldsnap.model.Temperature;
import com.example.yoxjames.coldsnap.model.WeatherData;
import com.example.yoxjames.coldsnap.model.WeatherDataNotFoundException;
import com.example.yoxjames.coldsnap.model.WeatherLocation;
import com.example.yoxjames.coldsnap.ui.CSPreferencesFragment;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

import static org.hamcrest.CoreMatchers.any;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class HTTPForecastServiceWUImplTest
{
    private @Mock HttpURLConnection httpURLConnection;
    private @Mock SharedPreferences sharedPreferences;
    private @Mock WundergroundURLFactory url;
    private WeatherLocation weatherLocation = new WeatherLocation("55555", "Testville, AK", 0f, 0f);
    private URLStreamHandler urlStreamHandler;
    private InputStream successfulJSONResponse;
    private InputStream failureJSONResponse;
    private final static String fakeURLString = "http://api.wunderground.com/api/fake_api_key/forecast/q/fake.json";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    /**
     * Setup some basic mocks. Ensure that our fake URL objects use our static files for their JSON
     * payloads.
     */
    @Before
    public void before() throws IOException
    {
        successfulJSONResponse = getClass().getClassLoader().getResourceAsStream("testForecastDay.json");
        failureJSONResponse = getClass().getClassLoader().getResourceAsStream("testFail.json");
        when(httpURLConnection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_OK);
        urlStreamHandler = new URLStreamHandler()
        {
            @Override
            protected URLConnection openConnection(URL u) throws IOException
            {
                return httpURLConnection;
            }
        };
        when(url.create(weatherLocation)).thenReturn(new URL(null, fakeURLString, urlStreamHandler));

    }

    /**
     * This tests a successful retrieval of a WeatherData via HTTP. A URL injected with a mock
     * URLStreamHandler allows my HTTPWeatherServiceWUImpl to get its payload from a file. In this
     * case the file is testForecastDay.json. This test is to ensure that the file produces POJOs that
     * match what is expected based on the static file.
     */
    @Test
    public void testGetWeatherDataSuccess() throws IOException, WeatherDataNotFoundException
    {
        when(httpURLConnection.getInputStream()).thenReturn(successfulJSONResponse);
        when(sharedPreferences.getFloat(CSPreferencesFragment.WEATHER_DATA_FUZZ, 0f)).thenReturn(0f);

        Temperature highDayOne = Temperature.newTemperatureFromF(79);
        Temperature lowDayOne = Temperature.newTemperatureFromF(62);

        Temperature highDayTwo = Temperature.newTemperatureFromF(81);
        Temperature lowDayTwo = Temperature.newTemperatureFromF(52);

        Temperature highDayThree = Temperature.newTemperatureFromF(55);
        Temperature lowDayThree = Temperature.newTemperatureFromF(41);

        Temperature highDayFour = Temperature.newTemperatureFromF(56);
        Temperature lowDayFour = Temperature.newTemperatureFromF(45);

        HTTPWeatherService httpWeatherService = new HTTPWeatherServiceWUImpl(url, sharedPreferences);
        WeatherData weatherData = httpWeatherService.getWeatherData(weatherLocation);

        assertEquals(weatherData.getTodayHigh().compareTo(highDayOne), 0);
        assertEquals(weatherData.getTodayLow().compareTo(lowDayOne), 0);
        assertNotNull(weatherData.getSyncDate());
        assertEquals(weatherData.getWeatherLocation().getZipCode(), "55555");
        assertEquals(weatherData.getWeatherLocation().getPlaceString(), "Testville, AK");
        assertEquals(weatherData.getForecastDays().get(0).getLowTemperature().compareTo(lowDayOne),0);
        assertEquals(weatherData.getForecastDays().get(0).getHighTemperature().compareTo(highDayOne),0);

        assertEquals(weatherData.getForecastDays().get(1).getLowTemperature().compareTo(lowDayTwo),0);
        assertEquals(weatherData.getForecastDays().get(1).getHighTemperature().compareTo(highDayTwo),0);

        assertEquals(weatherData.getForecastDays().get(2).getLowTemperature().compareTo(lowDayThree),0);
        assertEquals(weatherData.getForecastDays().get(2).getHighTemperature().compareTo(highDayThree),0);

        assertEquals(weatherData.getForecastDays().get(3).getLowTemperature().compareTo(lowDayFour),0);
        assertEquals(weatherData.getForecastDays().get(3).getHighTemperature().compareTo(highDayFour),0);
    }

    /**
     * This tests what happens when we get a bad json response from wunderground. Ensure that the proper
     * exception is thrown with a JSONException cause.
     */
    @Test
    public void testGetWeatherFailureJSON() throws IOException, WeatherDataNotFoundException
    {
        when(httpURLConnection.getInputStream()).thenReturn(failureJSONResponse);
        expectedException.expect(WeatherDataNotFoundException.class);
        expectedException.expectCause(any(JSONException.class));

        HTTPWeatherService httpWeatherService = new HTTPWeatherServiceWUImpl(url, sharedPreferences);
        httpWeatherService.getWeatherData(weatherLocation);
    }

    /**
     * This tests what happens when we get something like a 404 or a bad response from the Wunderground API.
     */
    @Test
    public void testGetWeatherFailureNoConn() throws IOException, WeatherDataNotFoundException
    {
        when(httpURLConnection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_NOT_FOUND); // 404 Error
        expectedException.expect(WeatherDataNotFoundException.class);
        expectedException.expectCause(any(IOException.class));

        HTTPWeatherService httpWeatherService = new HTTPWeatherServiceWUImpl(url, sharedPreferences);
        httpWeatherService.getWeatherData(weatherLocation);
    }

    /**
     * Test the case where everything goes as planned. Ensure we get a valid URL.
     */
    @Test
    public void testHappyPathGetAbsoluteURL()
    {
        URL url = HTTPWeatherServiceWUImpl.getAbsoluteUrl("55555");
        assertEquals(url.toString(), "http://api.wunderground.com/api/" + BuildConfig.WUNDERGROUND_API_KEY + "/forecast/q/55555.json");
    }

    /**
     * Test that a zipcode that does not contain 5 chars throws an exception.
     * @throws IllegalArgumentException If the zipCode inputted is not a size five string.
     */
    @Test
    public void testInvalidZipCode() throws IllegalArgumentException
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("zipCode is not valid");
        HTTPWeatherServiceWUImpl.getAbsoluteUrl("5555");
    }

    /**
     * Test what happens when we send in a blank zipCode. Ensure we respond with the proper exception.
     */
    @Test
    public void testBlankZipCode() throws IllegalArgumentException
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("zipCode cannot be blank");
        HTTPWeatherServiceWUImpl.getAbsoluteUrl("");
    }

    /**
     * Test what happens when we send in a null for zipCode. Ensure the proper exception is sent.
     */
    @Test
    public void testNullZipCode() throws IllegalArgumentException
    {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("zipCode cannot be null");
        HTTPWeatherServiceWUImpl.getAbsoluteUrl(null);
    }
}
