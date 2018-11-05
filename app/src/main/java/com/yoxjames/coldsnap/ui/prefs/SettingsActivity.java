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
import android.support.annotation.IdRes;
import android.support.v7.widget.Toolbar;

import com.yoxjames.coldsnap.ColdSnapApplication;
import com.yoxjames.coldsnap.R;
import com.yoxjames.coldsnap.core.BaseColdsnapActivity;
import com.yoxjames.coldsnap.dagger.SettingsActivityModule;

import javax.annotation.Nullable;

import butterknife.ButterKnife;

public class SettingsActivity extends BaseColdsnapActivity<SettingsActivityPresenter> implements SettingsActivityView
{
    @Override
    protected void inject()
    {
        ((ColdSnapApplication) getApplicationContext())
            .getInjector()
            .settingsActivitySubcomponent(new SettingsActivityModule(this))
            .inject(this);
    }

    @Nullable
    @Override
    protected Toolbar getToolbar()
    {
        return null;
    }

    @Override @IdRes
    protected int getNavigationId()
    {
        return R.id.menu_settings;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        bnvNavigation.setSelectedItemId(getNavigationId());

        getFragmentManager().beginTransaction()
            .replace(R.id.fl_fragment_container, new SettingsFragment())
            .commit();
    }
}



