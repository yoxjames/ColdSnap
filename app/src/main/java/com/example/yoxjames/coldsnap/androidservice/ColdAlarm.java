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
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;

import com.example.yoxjames.coldsnap.ColdSnapApplication;
import com.example.yoxjames.coldsnap.R;
import com.example.yoxjames.coldsnap.dagger.ColdAlarmModule;
import com.example.yoxjames.coldsnap.model.Temperature;
import com.example.yoxjames.coldsnap.model.TemperatureFormatter;
import com.example.yoxjames.coldsnap.model.WeatherData;
import com.example.yoxjames.coldsnap.model.WeatherDataNotFoundException;
import com.example.yoxjames.coldsnap.service.WeatherService;
import com.example.yoxjames.coldsnap.service.WeatherServiceCall;
import com.example.yoxjames.coldsnap.service.WeatherServiceCallback;
import com.example.yoxjames.coldsnap.ui.CSPreferencesFragment;
import com.example.yoxjames.coldsnap.ui.PlantListActivity;

import java.util.Calendar;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ColdAlarm extends BroadcastReceiver
{
    @Inject WeatherService weatherService;
    @Inject SharedPreferences sharedPreferences;
    @Inject WeatherServiceCall weatherServiceCall;
    @Inject TemperatureFormatter temperatureFormatter;

    @Override
    public void onReceive(final Context context, final Intent intent)
    {

        ((ColdSnapApplication) context.getApplicationContext())
                .getInjector()
                .coldAlarmSubcomponent(new ColdAlarmModule())
                .inject(this);
        weatherServiceCall.execute(new WeatherServiceCallAlarmCallback(context));
    }

    /**
     * Static method to set an alarm using AlarmManager for a BroadcastReciever represented by ColdAlarm.
     * This alarm is responsible for checking the low temperatures and seeing if it is below the defined threshold.
     *
     * @param context Android context for the alarm. Should be the application context generally.
     */
    public static void setAlarm(Context context)
    {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, ColdAlarm.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
        String[] splitTime = PreferenceManager.getDefaultSharedPreferences(context).getString(CSPreferencesFragment.COLD_ALARM_TIME, "7:00").split(":");
        final int hour = Integer.parseInt(splitTime[0]);
        final int minute = Integer.parseInt(splitTime[1]);

        Calendar alarmTime = Calendar.getInstance();

        alarmTime.setTimeInMillis(System.currentTimeMillis());
        alarmTime.set(Calendar.HOUR_OF_DAY, hour);
        alarmTime.set(Calendar.MINUTE, minute);

        am.setInexactRepeating(AlarmManager.RTC, alarmTime.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pi); // Millisec * Second * Minute
    }

    /**
     * Cancels the "Cold Threshold Alarm."
     *
     * @param context Android context for the alarm. Should be the application context generally.
     */
    public static void cancelAlarm(Context context)
    {
        Intent intent = new Intent(context, ColdAlarm.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }

    private class WeatherServiceCallAlarmCallback implements WeatherServiceCallback
    {
        private final Context context;

        private WeatherServiceCallAlarmCallback(Context context)
        {
            this.context = context;
        }

        @Override
        public void callback(@Nullable WeatherData result, WeatherDataNotFoundException e)
        {
            if (e == null)
            {
                final int mId = 1;

                final Temperature coldThresholdPref = new Temperature(sharedPreferences.getFloat(CSPreferencesFragment.THRESHOLD, 273f));
                final Temperature lowTonight = result.getTodayLow();

                if (coldThresholdPref.compareTo(lowTonight) >= 0)
                {
                    NotificationCompat.Builder mBuilder =
                            (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                                    .setSmallIcon(R.mipmap.ic_launcher)
                                    .setContentTitle("ColdSnap: Cold Warning")
                                    .setContentText("Tonight's low "
                                                   + temperatureFormatter.format(lowTonight)
                                                   + " is at or below threshold "
                                                   + temperatureFormatter.format(coldThresholdPref))
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
    }
}
