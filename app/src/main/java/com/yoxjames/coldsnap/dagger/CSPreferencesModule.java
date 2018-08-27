package com.yoxjames.coldsnap.dagger;

import com.yoxjames.coldsnap.model.TemperatureFormatter;
import com.yoxjames.coldsnap.model.TemperatureValueAdapter;
import com.yoxjames.coldsnap.prefs.CSPreferences;
import com.yoxjames.coldsnap.ui.prefs.CSPreferencesPresenter;
import com.yoxjames.coldsnap.ui.prefs.CSPreferencesView;

import dagger.Module;
import dagger.Provides;
import dagger.Reusable;

@Module
public class CSPreferencesModule
{
    private final CSPreferencesView view;

    public CSPreferencesModule(CSPreferencesView view)
    {
        this.view = view;
    }

    @Provides
    @Reusable
    static CSPreferencesPresenter provideCSPreferencesPresenter(
        CSPreferencesView view,
        CSPreferences csPreferences,
        TemperatureValueAdapter temperatureValueAdapter,
        TemperatureFormatter temperatureFormatter)
    {
        return new CSPreferencesPresenter(view, csPreferences, temperatureValueAdapter, temperatureFormatter);
    }

    @Provides
    CSPreferencesView provideCSPreferencesView()
    {
        return view;
    }
}
