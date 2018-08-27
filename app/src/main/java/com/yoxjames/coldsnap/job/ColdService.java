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

package com.yoxjames.coldsnap.job;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.yoxjames.coldsnap.ColdSnapApplication;
import com.yoxjames.coldsnap.prefs.CSPreferences;

import javax.annotation.Nullable;
import javax.inject.Inject;

public class ColdService extends Service
{
    @Inject CSPreferences csPreferences;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        ((ColdSnapApplication) getApplicationContext())
            .getInjector()
            .inject(this);

        ColdAlarm.setAlarm(this.getApplicationContext(), csPreferences);
        return START_STICKY;
    }

    @Override
    @Nullable
    public IBinder onBind(Intent intent)
    {
        return null;
    }
}
