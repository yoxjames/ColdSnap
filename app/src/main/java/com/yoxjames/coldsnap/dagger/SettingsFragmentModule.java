package com.yoxjames.coldsnap.dagger;

import com.yoxjames.coldsnap.model.TemperatureFormatter;
import com.yoxjames.coldsnap.model.TemperatureValueAdapter;
import com.yoxjames.coldsnap.prefs.CSPreferences;
import com.yoxjames.coldsnap.ui.prefs.CSPreferencesPresenter;
import com.yoxjames.coldsnap.ui.prefs.CSPreferencesView;

import dagger.Module;
import dagger.Provides;

@Module
public class SettingsFragmentModule
{
    private final CSPreferencesView view;

    public SettingsFragmentModule(CSPreferencesView view)
    {
        this.view = view;
    }

    @Provides
    public CSPreferencesView provideView()
    {
        return view;
    }

    @Provides
    public CSPreferencesPresenter providePresenter(
            CSPreferencesView view,
            CSPreferences csPreferences,
            TemperatureValueAdapter temperatureValueAdapter,
            TemperatureFormatter temperatureFormatter)
    {
        return new CSPreferencesPresenter(view, csPreferences, temperatureValueAdapter, temperatureFormatter);
    }
}
