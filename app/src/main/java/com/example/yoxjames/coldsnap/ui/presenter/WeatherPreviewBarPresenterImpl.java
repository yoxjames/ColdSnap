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

package com.example.yoxjames.coldsnap.ui.presenter;

import com.example.yoxjames.coldsnap.model.TemperatureFormatter;
import com.example.yoxjames.coldsnap.model.WeatherData;
import com.example.yoxjames.coldsnap.model.WeatherDataNotFoundException;
import com.example.yoxjames.coldsnap.service.WeatherServiceCall;
import com.example.yoxjames.coldsnap.service.WeatherServiceCallback;
import com.example.yoxjames.coldsnap.ui.view.WeatherPreviewBarView;

import javax.inject.Inject;

public class WeatherPreviewBarPresenterImpl implements WeatherPreviewBarPresenter
{
    private final WeatherPreviewBarView view;
    private final TemperatureFormatter temperatureFormatter;
    private final WeatherServiceCall weatherServiceCall;

    /**
     * Constructor for WeatherPreviewBarPresenterImpl
     * @param view The view (Think MVP pattern)
     * @param temperatureFormatter Object designed to transaction Temperature objects into viewable strings.
     * @param weatherServiceCall Asynchronous object designed to call {@link com.example.yoxjames.coldsnap.service.WeatherService}
     */
    @Inject
    public WeatherPreviewBarPresenterImpl(WeatherPreviewBarView view, TemperatureFormatter temperatureFormatter, WeatherServiceCall weatherServiceCall)
    {
        this.view = view;
        this.temperatureFormatter = temperatureFormatter;
        this.weatherServiceCall = weatherServiceCall;
    }

    @Override
    public void load()
    {
        weatherServiceCall.execute(new WeatherPreviewBarUICallback());
    }

    private class WeatherPreviewBarUICallback implements WeatherServiceCallback
    {

        /**
         * This is the callback function that should be called after Weather Data has been loaded (async)
         * @param result The WeatherData result
         * @param e Any exceptions. Good if null, bad if not.
         */
        @Override
        public void callback(WeatherData result, WeatherDataNotFoundException e)
        {
            if (e == null)
            {
                view.setLocationText(result.getLocationString() + " - " + result.getZipCode());
                view.setHighText(temperatureFormatter.format(result.getTodayHigh()));
                view.setLowText(temperatureFormatter.format(result.getTodayLow()));
                view.setLastUpdatedText(result.getForecastDays().get(0).toString());
            }
        }
    }
}
