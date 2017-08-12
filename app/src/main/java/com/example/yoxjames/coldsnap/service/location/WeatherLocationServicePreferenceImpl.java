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

package com.example.yoxjames.coldsnap.service.location;

import android.content.SharedPreferences;

import com.example.yoxjames.coldsnap.model.WeatherLocation;
import com.example.yoxjames.coldsnap.ui.CSPreferencesFragment;

import javax.inject.Inject;

/**
 * Created by yoxjames on 7/8/17.
 */

public class WeatherLocationServicePreferenceImpl implements WeatherLocationService
{
    private final SharedPreferences sharedPreferences;

    @Inject
    public WeatherLocationServicePreferenceImpl(SharedPreferences sharedPreferences)
    {
        this.sharedPreferences = sharedPreferences;
    }

    @Override
    public WeatherLocation readWeatherLocation()
    {
        final String zipCode = sharedPreferences.getString(CSPreferencesFragment.ZIPCODE, "");
        final String locationString = sharedPreferences.getString(CSPreferencesFragment.LOCATION_STRING, "");
        final double lat = 0f;
        final double lon = 0f;

        return new WeatherLocation(zipCode, locationString, lat, lon);
    }

    @Override
    public void saveWeatherLocation(WeatherLocation weatherLocation)
    {
        final SharedPreferences.Editor preferenceEditor = sharedPreferences.edit();

        preferenceEditor.putString(CSPreferencesFragment.ZIPCODE, weatherLocation.getZipCode());
        preferenceEditor.putString(CSPreferencesFragment.LOCATION_STRING, weatherLocation.getPlaceString());
        preferenceEditor.apply();
    }

    @Override
    public void clearWeatherLocation()
    {
        final SharedPreferences.Editor preferenceEditor = sharedPreferences.edit();

        preferenceEditor.remove(CSPreferencesFragment.ZIPCODE);
        preferenceEditor.remove(CSPreferencesFragment.LOCATION_STRING);
        preferenceEditor.apply();
    }
}
