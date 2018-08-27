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

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;

import com.yoxjames.coldsnap.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TimePickerDialogPreference extends DialogPreference
{
    @BindView(R.id.cold_alarm_time_picker) TimePicker timePicker;

    public TimePickerDialogPreference(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public TimePickerDialogPreference(Context context)
    {
        super(context);
    }

    public TimePickerDialogPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public TimePickerDialogPreference(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public View onCreateDialogView()
    {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_coldalarm_time, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onBindView(View view)
    {
        super.onBindView(view);
    }

    @Override
    public void onDialogClosed(boolean positiveResult)
    {
        if (positiveResult) // If we clicked "OK"
        {
            final int minute = timePicker.getCurrentMinute();
            final int hour = timePicker.getCurrentHour();

            String timeString = Integer.toString(hour) + ":" + Integer.toString(minute);

            persistString(timeString);
        }
    }

    public static String formatTime(String timeString)
    {
        boolean isAM = true;
        int hour = Integer.parseInt(timeString.split(":")[0]);
        int minute = Integer.parseInt(timeString.split(":")[1]);

        if (hour >= 12)
            isAM = false;
            if (hour >= 13)
                hour -= 12;

        final String hourString = (hour == 0) ? "12" : Integer.toString(hour);
        final String minuteString = (Integer.toString(minute).length() == 1) ? "0"+Integer.toString(minute) : Integer.toString(minute);

        return hourString + ":" + minuteString + " " + ((isAM) ? "am" : "pm");
    }
}
