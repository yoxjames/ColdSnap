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

package com.example.yoxjames.coldsnap.http.google;

import android.util.Log;

import com.example.yoxjames.coldsnap.http.GenericHTTPService;
import com.example.yoxjames.coldsnap.http.HTTPGeolocationService;
import com.example.yoxjames.coldsnap.model.GeolocationFailureException;
import com.example.yoxjames.coldsnap.model.WeatherLocation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.annotation.Nullable;
import javax.inject.Inject;

import dagger.internal.Preconditions;

/**
 * Created by yoxjames on 7/9/17.
 */

public class HTTPGeolocationServiceGoogleImpl extends GenericHTTPService implements HTTPGeolocationService
{
    private final static String BASE_URL = "https://maps.googleapis.com/maps/api";

    private final static String API_KEY = "AIzaSyAxaVHXD5Bn8BDfjT2ohSrs7oSoyecUJxY";

    private final static String GEOLOCATION_SERVICE = "/geocode/json";

    private final GoogleLocationURLFactory googleLocationURLFactory;

    @Inject
    public HTTPGeolocationServiceGoogleImpl(GoogleLocationURLFactory googleLocationURLFactory)
    {
        this.googleLocationURLFactory = googleLocationURLFactory;
    }

    @Override
    public WeatherLocation getCurrentWeatherLocation(double lat, double lon) throws GeolocationFailureException
    {
        final GoogleLocation googleLocation;
        try
        {
            String rawJSON = getURLString(googleLocationURLFactory.create(lat, lon));
            JSONArray addressJSON =
                    new JSONObject(rawJSON)
                            .getJSONArray("results")
                            .getJSONObject(0)
                            .getJSONArray("address_components");

            googleLocation = new GoogleLocation.Builder()
                    .lat(lat)
                    .lon(lon)
                    .sublocality(resolveSubLocality(addressJSON))
                    .state(resolveState(addressJSON))
                    .postalCode(resolvePostalCode(addressJSON))
                    .build();

            return new WeatherLocation(googleLocation.getPostalCode(),googleLocation.getSublocality() + ", " + googleLocation.getState(), lat, lon);

        }
        catch (JSONException | IOException e)
        {
            Log.e("GeoLocation", "GeolocationFailureException");
            e.printStackTrace();
            throw new GeolocationFailureException("Geolocation failed", e);
        }
    }

    @Nullable
    public static URL getAbsoluteUrl(double lat, double lon)
    {
        try
        {
            return new URL(BASE_URL +
                    GEOLOCATION_SERVICE +
                    "?latlng=" +
                    Double.toString(lat) + "," + Double.toString(lon) +
                    "&key=" + API_KEY);
        }
        catch (MalformedURLException e)
        {
            Log.e("URL", "Malformed URL");
            return null;
        }
    }

    @Nullable
    private JSONObject getType(JSONArray addressComponents, String type) throws JSONException
    {
        JSONArray types;

        for (int i = 0; i < addressComponents.length(); i++)
        {
            types = addressComponents.getJSONObject(i).getJSONArray("types");

            for (int j = 0; j < types.length(); j++)
            {
                if (types.getString(j).equals(type))
                    return addressComponents.getJSONObject(i);
            }
        }

        return null;
    }

    private String resolvePostalCode(JSONArray addressComponents) throws JSONException
    {
        Preconditions.checkNotNull(addressComponents);
        JSONObject postalCodeJSON;

        postalCodeJSON = getType(addressComponents, "postal_code");
        if (postalCodeJSON != null)
            return postalCodeJSON.getString("short_name");

        throw new JSONException("Could not resolve postal code");
    }

    private String resolveState(JSONArray addressComponents)
    {
        Preconditions.checkNotNull(addressComponents);
        JSONObject stateJSON;

        try
        {
            stateJSON = getType(addressComponents, "administrative_area_level_1");
            if (stateJSON != null)
                return stateJSON.getString("short_name");
            return "??";
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            return "??";
        }
    }

    private String resolveSubLocality(JSONArray addressComponents)
    {
        Preconditions.checkNotNull(addressComponents);
        JSONObject subLocalityJSON;

        try
        {
            // First attempt to resolve SubLocality:
            subLocalityJSON = getType(addressComponents, "sublocality");
            if (subLocalityJSON != null)
                return subLocalityJSON.getString("long_name");

            // Second try Locality:
            subLocalityJSON = getType(addressComponents, "locality");
            if (subLocalityJSON != null)
                return subLocalityJSON.getString("long_name");

            // Third try admin level 2.
            subLocalityJSON = getType(addressComponents, "administrative_area_level_2");
            if (subLocalityJSON != null)
                return subLocalityJSON.getString("long_name");

            // Fourth just give up and return unknown so the UI can do something and not crash.
            return "Unknown";
        }
        // Not worth crashing the app. Just swallow the exception and return Unknown.
        // TODO: Might be worth some sort of handling? Not sure here.
        catch (JSONException e)
        {
            e.printStackTrace();
            return "Unknown";
        }

    }


}
