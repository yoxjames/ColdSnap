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

package com.example.yoxjames.coldsnap.service;

import android.os.AsyncTask;
import android.support.annotation.Nullable;

import com.example.yoxjames.coldsnap.model.WeatherData;
import com.example.yoxjames.coldsnap.model.WeatherDataNotFoundException;

import javax.inject.Inject;

import dagger.internal.Preconditions;

// TODO: Investigate using RxJava for this

/**
 * Async wrapper for WeatherService. The implementation uses AsyncTask.
 */
public class WeatherServiceAsyncProcessorImpl extends AsyncTask<Void, Void, WeatherData> implements WeatherServiceAsyncProcessor
{
    private final WeatherService weatherService;
    private WeatherDataNotFoundException e;
    private WeatherServiceCallback weatherServiceCallback;

    @Inject
    public WeatherServiceAsyncProcessorImpl(WeatherService weatherService)
    {
        this.weatherService = Preconditions.checkNotNull(weatherService);
    }

    @Override
    public void execute(WeatherServiceCallback weatherServiceCallback)
    {
        this.weatherServiceCallback = Preconditions.checkNotNull(weatherServiceCallback);
        super.execute();
    }

    @Override
    @Nullable
    protected WeatherData doInBackground(Void... params)
    {
        WeatherData data = null;

        try
        {
            data = weatherService.getCurrentForecastData();
        }
        catch (WeatherDataNotFoundException e)
        {
            this.e = e;
        }

        return data;
    }

    @Override
    protected void onPostExecute(WeatherData result)
    {
        weatherServiceCallback.callback(result, e);
    }
}
