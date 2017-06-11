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

package com.example.yoxjames.coldsnap.ui;

import android.content.SharedPreferences;

import com.example.yoxjames.coldsnap.mocks.WeatherDataMockFactory;
import com.example.yoxjames.coldsnap.model.TemperatureFormatter;
import com.example.yoxjames.coldsnap.model.TemperatureFormatterImpl;
import com.example.yoxjames.coldsnap.model.WeatherDataNotFoundException;
import com.example.yoxjames.coldsnap.service.WeatherService;
import com.example.yoxjames.coldsnap.service.WeatherServiceAsyncProcessor;
import com.example.yoxjames.coldsnap.service.WeatherServiceCall;
import com.example.yoxjames.coldsnap.service.WeatherServiceCallImpl;
import com.example.yoxjames.coldsnap.service.WeatherServiceCallback;
import com.example.yoxjames.coldsnap.ui.presenter.WeatherPreviewBarPresenterImpl;
import com.example.yoxjames.coldsnap.ui.view.WeatherPreviewBarView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import javax.inject.Provider;

import dagger.Lazy;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WeatherPreviewBarPresenterImplTest
{
    private @Mock WeatherService weatherService;
    private @Mock WeatherPreviewBarView weatherPreviewBarView;
    private @Mock SharedPreferences sharedPreferences;
    private @Mock WeatherServiceAsyncProcessor weatherServiceAsyncProcessor;

    private WeatherServiceCall weatherServiceCall;
    private TemperatureFormatter temperatureFormatter;

    @Before
    public void setupTest()
    {
        when(sharedPreferences.getString(CSPreferencesFragment.TEMPERATURE_SCALE,"F")).thenReturn("F");
        doAnswer(new Answer<Void>()
        {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable
            {
                WeatherServiceCallback callback = (WeatherServiceCallback) invocation.getArguments()[0];
                callback.callback(WeatherDataMockFactory.getBasicWeatherData(), null);
                return null;
            }
        }).when(weatherServiceAsyncProcessor).execute(any(WeatherServiceCallback.class));
        temperatureFormatter = new TemperatureFormatterImpl(sharedPreferences);
        weatherServiceCall = new WeatherServiceCallImpl(new Provider<Lazy<WeatherServiceAsyncProcessor>>()
        {
            @Override
            public Lazy<WeatherServiceAsyncProcessor> get()
            {
                return new Lazy<WeatherServiceAsyncProcessor>()
                {
                    @Override
                    public WeatherServiceAsyncProcessor get()
                    {
                        return weatherServiceAsyncProcessor;
                    }
                };
            }
        });
    }

    @Test
    public void testHappyPath() throws WeatherDataNotFoundException
    {
        when(weatherService.getCurrentForecastData()).thenReturn(WeatherDataMockFactory.getBasicWeatherData());
        WeatherPreviewBarPresenterImpl weatherPreviewBarPresenter
                = new WeatherPreviewBarPresenterImpl(weatherPreviewBarView, temperatureFormatter, weatherServiceCall);
        weatherPreviewBarPresenter.load();

        verify(weatherPreviewBarView, times(1)).setLocationText("Kansas City, MO - 64105");
        verify(weatherPreviewBarView, times(1)).setHighText("72°F");
        verify(weatherPreviewBarView, times(1)).setLowText("31°F");
        verify(weatherPreviewBarView, times(1)).setLastUpdatedText("Jun 1 2017");
    }
}
