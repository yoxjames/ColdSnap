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

package com.yoxjames.coldsnap.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;

import com.yoxjames.coldsnap.ColdSnapApplication;
import com.yoxjames.coldsnap.R;
import com.yoxjames.coldsnap.androidservice.ColdAlarm;
import com.yoxjames.coldsnap.dagger.CSPreferencesFragmentModule;
import com.yoxjames.coldsnap.model.Temperature;
import com.yoxjames.coldsnap.model.TemperatureFormatter;
import com.yoxjames.coldsnap.ui.controls.preference.TemperaturePickerRelativeDialogPreference;
import com.yoxjames.coldsnap.ui.controls.preference.TemperaturePickerValueDialogPreference;
import com.yoxjames.coldsnap.ui.controls.preference.TimePickerDialogPreference;

import javax.inject.Inject;

public class CSPreferencesFragment extends PreferenceFragment
{

    public static final String THRESHOLD = "com.yoxjames.coldsnap.THRESHOLD";
    public static final String ZIPCODE = "com.yoxjames.coldsnap.ZIPCODE";
    public static final String TEMPERATURE_SCALE = "com.yoxjames.coldsnap.TEMPFORMAT";
    public static final String WEATHER_DATA_FUZZ = "com.yoxjames.coldsnap.WEATHER_DATA_FUZZ";
    public static final String LOCATION_STRING = "com.yoxjames.coldsnap.LOCATION_STRING";
    public static final String COLD_ALARM_TIME = "com.yoxjames.coldsnap.COLD_ALARM_TIME";

    private TemperaturePickerRelativeDialogPreference eThreshold;
    private TemperaturePickerValueDialogPreference fuzz;
    private EditTextPreference zipcode;
    private ListPreference temperatureScale;
    private EditTextPreference locationString;
    private TimePickerDialogPreference coldAlarmTimePicker;
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
        eThreshold = (TemperaturePickerRelativeDialogPreference) findPreference(THRESHOLD);
        fuzz = (TemperaturePickerValueDialogPreference) findPreference(WEATHER_DATA_FUZZ);
        zipcode = (EditTextPreference) findPreference(ZIPCODE);
        zipcode.setOnPreferenceClickListener(preference ->
        {
            EditTextPreference editPref = (EditTextPreference) preference;
            editPref.getEditText().setSelection(editPref.getText().length());
            return true;
        });
        temperatureScale = (ListPreference) findPreference(TEMPERATURE_SCALE);
        locationString = (EditTextPreference) findPreference(LOCATION_STRING);
        locationString.setOnPreferenceClickListener(preference ->
        {
            EditTextPreference editPref = (EditTextPreference) preference;
            editPref.getEditText().setSelection(editPref.getText().length());
            return true;
        });

        // TODO: This is a placeholder.Plan to make application smarter here.
        coldAlarmTimePicker = (TimePickerDialogPreference) findPreference(COLD_ALARM_TIME);

        preferenceChangeListener = (sharedPreferences1, s) ->
        {
            refreshActivity();

            // Reset alarm for the new alarm aime inputted
            if (s.equals(COLD_ALARM_TIME))
            {
                ColdAlarm.cancelAlarm(getActivity().getApplicationContext());
                ColdAlarm.setAlarm(getActivity().getApplicationContext());
            }
        };

        refreshActivity();
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
        temperatureScale.setSummary("°" + sharedPreferences.getString(TEMPERATURE_SCALE, "F"));
        locationString.setSummary(sharedPreferences.getString(LOCATION_STRING, "Kansas City, MO"));
        coldAlarmTimePicker.setSummary(TimePickerDialogPreference.formatTime(sharedPreferences.getString(COLD_ALARM_TIME, "19:00")));
    }
}
