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

package com.yoxjames.coldsnap.dagger;

import android.content.Context;

import com.yoxjames.coldsnap.job.AutoStart;
import com.yoxjames.coldsnap.job.ColdService;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;

@Component(modules = ColdSnapApplicationModule.class)
@Singleton
public interface ColdSnapInjector
{
    MainActivitySubcomponent mainActivitySubcomponent(PlantListModule module);
    SettingsActivitySubcomponent settingsActivitySubcomponent(SettingsActivityModule module);
    SettingsFragmentSubcomponent settingsFragmentSubcomponent(SettingsFragmentModule module);
    ColdAlarmSubcomponent coldAlarmSubcomponent(ColdAlarmModule coldAlarmModule);
    PlantDetailSubcomponent plantDetailSubcomponent(PlantDetailModule plantDetailModule);
    FeedActivitySubcomponent feedActivitySubcomponent(FeedModule feedModule);

    void inject(AutoStart autoStart);
    void inject(ColdService coldService);

    @Component.Builder
    interface Builder
    {
        @BindsInstance Builder context(Context context);
        ColdSnapInjector build();
    }
}
