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

package com.example.yoxjames.coldsnap.androidservice;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.PowerManager;

import com.example.yoxjames.coldsnap.ColdSnapApplication;
import com.example.yoxjames.coldsnap.dagger.ColdAlarmModule;
import com.example.yoxjames.coldsnap.service.WeatherService;

import javax.inject.Inject;

public class ColdAlarm extends BroadcastReceiver
{
    @Inject WeatherService weatherService;
    @Inject SharedPreferences sharedPreferences;

    @Override
    public void onReceive(final Context context, final Intent intent)
    {

        ((ColdSnapApplication) context.getApplicationContext())
                .getInjector()
                .coldAlarmSubcomponent(new ColdAlarmModule())
                .inject(this);
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
        wl.acquire();

        // TODO: Implement some sort of preference to schedule when this goes.
        /*
        new WeatherServiceCallImpl(weatherService, weatherServiceCallback, asyncProcessor)
        {
            @Override
            protected void onPostExecute(WeatherData result)
            {
                if (getError() == null)
                {
                    int coldThreshold;
                    final int mId = 1;
                    final String syncConnPref = sharedPreferences.getString(CSPreferencesFragment.THRESHOLD, "");

                    try
                    {
                        coldThreshold = Integer.parseInt(syncConnPref);
                    }
                    catch (NumberFormatException e)
                    {
                        coldThreshold = 32;
                    }

                    final int coldThresholdPref = coldThreshold;

                    int tonightTemp = result.getTodayLow().getAmt();
                    if (tonightTemp <= coldThresholdPref)
                    {
                        NotificationCompat.Builder mBuilder =
                                (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                                        .setSmallIcon(R.mipmap.ic_launcher)
                                        .setContentTitle("ColdSnap: Cold Warning")
                                        .setContentText("Tonight's low "
                                                + tonightTemp
                                                + " is at or below threshold " + coldThreshold)
                                        .setColor(Color.BLUE);
                        // Creates an explicit intent for an Activity in your app
                        Intent resultIntent = new Intent(context, PlantListActivity.class);

                        // The stack builder object will contain an artificial back stack for the
                        // started Activity.
                        // This ensures that navigating backward from the Activity leads out of
                        // your application to the Home screen.
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                        // Adds the back stack for the Intent (but not the Intent itself)
                        stackBuilder.addParentStack(PlantListActivity.class);
                        // Adds the Intent that starts the Activity to the top of the stack
                        stackBuilder.addNextIntent(resultIntent);
                        PendingIntent resultPendingIntent =
                                stackBuilder.getPendingIntent(
                                        0,
                                        PendingIntent.FLAG_UPDATE_CURRENT
                                );
                        mBuilder.setContentIntent(resultPendingIntent);
                        NotificationManager mNotificationManager =
                                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                        // mId allows you to update the notification later on.
                        mNotificationManager.notify(mId, mBuilder.build());
                    }
                }
            }


        }.execute();
        */
    }

    public void setAlarm(Context context)
    {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, ColdAlarm.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 100 * 10, pi); // Millisec * Second * Minute
    }

    public void cancelAlarm(Context context)
    {
        Intent intent = new Intent(context, ColdAlarm.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }
}
