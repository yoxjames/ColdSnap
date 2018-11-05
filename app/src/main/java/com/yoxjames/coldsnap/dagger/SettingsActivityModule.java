package com.yoxjames.coldsnap.dagger;

import com.yoxjames.coldsnap.ui.prefs.SettingsActivityPresenter;
import com.yoxjames.coldsnap.ui.prefs.SettingsActivityView;

import dagger.Module;
import dagger.Provides;

@Module
public class SettingsActivityModule
{
    private final SettingsActivityView view;

    public SettingsActivityModule(SettingsActivityView view)
    {
        this.view = view;
    }

    @Provides
    public SettingsActivityPresenter provideSettingsActivityPresenter(SettingsActivityView view)
    {
        return new SettingsActivityPresenter(view);
    }

    @Provides
    public SettingsActivityView provideCSPreferencesView()
    {
        return view;
    }
}
