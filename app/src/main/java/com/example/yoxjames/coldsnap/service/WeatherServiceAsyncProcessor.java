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

/**
 * Asynchronous wrapper for {@link WeatherService}.
 */
public interface WeatherServiceAsyncProcessor
{
    /**
     * Executes the WeatherService and then issues weatherServiceCallback when finished.
     * @param weatherServiceCallback  Object who's implementation's callback() function will be called
     *                                when the Async task is finished.
     */
    void execute(WeatherServiceCallback weatherServiceCallback);
}
