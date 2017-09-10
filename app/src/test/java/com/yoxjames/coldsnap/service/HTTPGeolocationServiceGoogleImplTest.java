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

package com.yoxjames.coldsnap.service;

import com.yoxjames.coldsnap.http.HTTPGeolocationService;
import com.yoxjames.coldsnap.http.google.GoogleLocationURLFactory;
import com.yoxjames.coldsnap.http.google.HTTPGeolocationServiceGoogleImpl;
import com.yoxjames.coldsnap.model.GeolocationFailureException;
import com.yoxjames.coldsnap.model.WeatherLocation;
import com.yoxjames.coldsnap.utils.RxColdSnap;

import org.junit.Before;
import org.junit.BeforeClass;
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
import java.util.List;

import io.reactivex.functions.Consumer;
import io.reactivex.observers.TestObserver;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Created by yoxjames on 7/9/17.
 */

@RunWith(MockitoJUnitRunner.class)
public class HTTPGeolocationServiceGoogleImplTest
{
    private @Mock HttpURLConnection httpURLConnection;
    private @Mock GoogleLocationURLFactory googleLocationURLFactory;
    private URLStreamHandler urlStreamHandler;
    private InputStream successfulJSONResponse;
    private InputStream failureJSONResponse;

    private static final String fakeURLString = "https://maps.googleapis.com/maps/api/geocode/json?latlng=40.714224,-73.961452&key=my_fake_key";
    private static final double lat = 40.714224;
    private static final double lon = -73.961452;

    @BeforeClass
    public static void setupTests()
    {
        RxColdSnap.forceSynchronous();
    }

    @Before
    public void setup() throws IOException
    {
        successfulJSONResponse = getClass().getClassLoader().getResourceAsStream("testGoogleGeolocationSuccess.json");
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
        when(googleLocationURLFactory.create(lat, lon)).thenReturn(new URL(null, fakeURLString, urlStreamHandler));
    }

    @Test
    public void testGetGoogleLocationSuccess() throws IOException, GeolocationFailureException
    {
        when(httpURLConnection.getInputStream()).thenReturn(successfulJSONResponse);

        HTTPGeolocationService httpGeolocationService = new HTTPGeolocationServiceGoogleImpl(googleLocationURLFactory);

        TestObserver<WeatherLocation> weatherLocationTestObserver = httpGeolocationService.getCurrentWeatherLocation(lat, lon).test();

        List<WeatherLocation> weatherLocations = weatherLocationTestObserver.values();

        weatherLocationTestObserver.assertComplete();
        assertEquals(weatherLocations.get(0).getLat(), 40.714224, 0.000001);
        assertEquals(weatherLocations.get(0).getLon(), -73.961452, 0.000001);
        assertEquals(weatherLocations.get(0).getPlaceString(), "Brooklyn, NY");
        assertEquals(weatherLocations.get(0).getZipCode(), "11211");
    }

    @Test
    public void testGeoGoogleLocationFailure() throws IOException, GeolocationFailureException
    {
        when(httpURLConnection.getInputStream()).thenReturn(failureJSONResponse);

        HTTPGeolocationService httpGeolocationService = new HTTPGeolocationServiceGoogleImpl(googleLocationURLFactory);

        TestObserver<WeatherLocation> weatherLocationTestObserver = httpGeolocationService.getCurrentWeatherLocation(lat, lon).test();

        weatherLocationTestObserver.assertError(GeolocationFailureException.class);
    }

}
