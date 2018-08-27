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

package com.yoxjames.coldsnap.ui.prefs;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.yoxjames.coldsnap.ColdSnapApplication;
import com.yoxjames.coldsnap.R;
import com.yoxjames.coldsnap.dagger.CSPreferencesModule;
import com.yoxjames.coldsnap.model.TemperatureFormatter;
import com.yoxjames.coldsnap.prefs.CSPreferences;
import com.yoxjames.coldsnap.ui.controls.temperaturepicker.TemperaturePickerViewModel;

import javax.inject.Inject;

import io.reactivex.Observable;

public class CSPreferencesFragment extends PreferenceFragment implements CSPreferencesView
{
    private static final String THRESHOLD = "com.yoxjames.coldsnap.THRESHOLD";
    private static final String TEMPERATURE_SCALE = "com.yoxjames.coldsnap.TEMPFORMAT";
    private static final String WEATHER_DATA_FUZZ = "com.yoxjames.coldsnap.WEATHER_DATA_FUZZ";
    private static final String COLD_ALARM_TIME = "com.yoxjames.coldsnap.COLD_ALARM_TIME";

    private TemperaturePickerDialog eThreshold;
    private TemperaturePickerDialog fuzz;
    private ListPreference temperatureScale;
    private TimePickerDialogPreference coldAlarmTimePicker;

    @Inject TemperatureFormatter temperatureFormatter;
    @Inject CSPreferences csPreferences;
    @Inject CSPreferencesPresenter presenter;

    private TemperaturePickerViewModel temperaturePickerViewModel = TemperaturePickerViewModel.EMPTY;
    private TemperaturePickerViewModel fuzzPickerViewModel = TemperaturePickerViewModel.EMPTY;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {

        ((ColdSnapApplication) getActivity().getApplicationContext())
            .getInjector()
            .csPreferencesFragmentSubcomponent(new CSPreferencesModule(this))
            .inject(this);

        super.onCreate(savedInstanceState);

        // Load Preferences
        addPreferencesFromResource(R.xml.preferences);
        eThreshold = (TemperaturePickerDialog) findPreference(THRESHOLD);
        fuzz = (TemperaturePickerDialog) findPreference(WEATHER_DATA_FUZZ);
        temperatureScale = (ListPreference) findPreference(TEMPERATURE_SCALE);
        coldAlarmTimePicker = (TimePickerDialogPreference) findPreference(COLD_ALARM_TIME);

        eThreshold.setOnPreferenceClickListener(this::onClickThreshold);
        fuzz.setOnPreferenceClickListener(this::onClickFuzz);

        // TODO: This is a placeholder.Plan to make application smarter here.
        presenter.load();
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    @Override
    public void onStart()
    {
        super.onStart();
    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    private boolean onClickThreshold(Preference ignored)
    {
        eThreshold.bindView(temperaturePickerViewModel);
        presenter.onEnterThresholdPrefView();
        return true;
    }

    private boolean onClickFuzz(Preference ignored)
    {
        fuzz.bindView(fuzzPickerViewModel);
        presenter.onEnterFuzzprefView();
        return true;
    }

    @Override
    public void bindView(PreferencesViewModel viewModel)
    {
        temperaturePickerViewModel = viewModel.getThresholdViewModel();
        fuzzPickerViewModel = viewModel.getFuzzViewModel();
        temperatureScale.setSummary(viewModel.getTemperatureScale());
        coldAlarmTimePicker.setSummary(viewModel.getColdAlarmTime());
        eThreshold.setSummary(viewModel.getThresholdSummary());
        fuzz.setSummary(viewModel.getFuzzSummary());
    }

    @Override
    public Observable<Integer> thresholdChanges()
    {
        return eThreshold.valueChanged();
    }

    @Override
    public Observable<Integer> fuzzChanges()
    {
        return fuzz.valueChanged();
    }
}
