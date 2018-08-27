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

package com.yoxjames.coldsnap;

import android.app.Application;

import com.jakewharton.threetenabp.AndroidThreeTen;
import com.yoxjames.coldsnap.dagger.ColdSnapInjector;
import com.yoxjames.coldsnap.dagger.DaggerColdSnapInjector;

import javax.annotation.Nullable;

/**
 * Forecast ColdSnap Application class. Mainly used for Dagger implementation.
 */
public class ColdSnapApplication extends Application
{
    @Nullable ColdSnapInjector injector;

    @Override
    public void onCreate()
    {
        super.onCreate();
        AndroidThreeTen.init(this);
    }

    /**
     * Gets the ColdSnapInjector which can then return submodules to inject each fragment or other
     * component. This is a little hacky but it gets the job done for now.
     *
     * @return A ColdSnapInjector which is an object used to do Dependency Injection.
     */
    public ColdSnapInjector getInjector()
    {

        if (injector == null)
            injector = DaggerColdSnapInjector.builder().context(getApplicationContext()).build();

        return injector;
    }
}
