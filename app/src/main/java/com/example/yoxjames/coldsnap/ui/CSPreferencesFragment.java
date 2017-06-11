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

package com.example.yoxjames.coldsnap.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;

import com.example.yoxjames.coldsnap.ColdSnapApplication;
import com.example.yoxjames.coldsnap.R;
import com.example.yoxjames.coldsnap.dagger.CSPreferencesFragmentModule;
import com.example.yoxjames.coldsnap.model.Temperature;
import com.example.yoxjames.coldsnap.model.TemperatureFormatter;
import com.example.yoxjames.coldsnap.ui.controls.TemperatureEditTextPreference;
import com.example.yoxjames.coldsnap.ui.controls.TemperatureValueEditTextPreference;

import javax.inject.Inject;

public class CSPreferencesFragment extends PreferenceFragment
{

    public static final String THRESHOLD = "com.example.yoxjames.coldsnap.THRESHOLD";
    public static final String ZIPCODE = "com.example.yoxjames.coldsnap.ZIPCODE";
    public static final String TEMPERATURE_SCALE = "com.example.yoxjames.coldsnap.TEMPFORMAT";
    public static final String WEATHER_DATA_FUZZ = "com.example.yoxjames.coldsnap.WEATHER_DATA_FUZZ";
    public static final String LOCATION_STRING = "com.example.yoxjames.coldsnap.LOCATION_STRING";

    private TemperatureEditTextPreference eThreshold;
    private TemperatureValueEditTextPreference fuzz;
    private EditTextPreference zipcode;
    private ListPreference temperatureScale;
    private EditTextPreference locationString;
    private SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener;
    @Inject SharedPreferences sharedPreferences;
    @Inject TemperatureFormatter temperatureFormatter;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {

        ((ColdSnapApplication) getActivity().getApplicationContext())
                .getInjector()
                .csPreferencesFragmentSubcomponent(new CSPreferencesFragmentModule())
                .inject(this);

        super.onCreate(savedInstanceState);

        // Load Preferences
        addPreferencesFromResource(R.xml.preferences);
        eThreshold = (TemperatureEditTextPreference) findPreference(THRESHOLD);
        fuzz = (TemperatureValueEditTextPreference) findPreference(WEATHER_DATA_FUZZ);
        zipcode = (EditTextPreference) findPreference(ZIPCODE);
        temperatureScale = (ListPreference) findPreference(TEMPERATURE_SCALE);
        locationString = (EditTextPreference) findPreference(LOCATION_STRING);

        preferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener()
        {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s)
            {
                refreshActivity();
            }
        };

        refreshActivity();


        /*
         * Anon listener that will be triggered when any preference is altered. This allows the UI
         * to update when a preference changes.
         */
        sharedPreferences.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener()
        {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s)
            {
                refreshActivity();
            }
        });
    }

    @Override
    public void onResume()
    {
        super.onResume();
        sharedPreferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener);
    }

    @Override
    public void onStart()
    {
        super.onStart();
        refreshActivity();
    }

    @Override
    public void onPause()
    {
        super.onPause();

        sharedPreferences.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener);
    }

    public void refreshActivity()
    {
        eThreshold.setSummary(temperatureFormatter.format(new Temperature(sharedPreferences.getFloat(THRESHOLD, 273f))));
        fuzz.setSummary(temperatureFormatter.formatFuzz(sharedPreferences.getFloat(WEATHER_DATA_FUZZ, 0f)));
        zipcode.setSummary(sharedPreferences.getString(ZIPCODE, ""));
        temperatureScale.setSummary(sharedPreferences.getString(TEMPERATURE_SCALE, "F"));
        locationString.setSummary(sharedPreferences.getString(LOCATION_STRING, "Location"));
    }
}
