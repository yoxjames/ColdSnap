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

import android.support.annotation.Nullable;

import com.example.yoxjames.coldsnap.model.WeatherData;
import com.example.yoxjames.coldsnap.model.WeatherDataNotFoundException;

/**
 * Callback object. An implementation of this is passed into {@link WeatherServiceAsyncProcessor}
 * and will have it's callback() function called after the Async task is done.
 */
public interface WeatherServiceCallback
{
    /**
     * Function that is called after the async task is finished.
     *
     * @param result WeatherData returned from {@link WeatherService}.
     * @param e A WeatherDataNotFoundException if there was a problem getting WeatherData from
     *          {@link WeatherService}.
     */
    void callback(@Nullable WeatherData result, WeatherDataNotFoundException e);
}
