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
 * Wrapper for WeatherService that is called from the front end.
 * In practice this service is responsible for calling the async processor model.
 */
public interface WeatherServiceCall
{
    /**
     * Execute WeatherService to get the current forecast data and then call the callback with that
     * data.
     * @param callback Callback implementation that will include things to be done after this service
     *                 call is made.
     */
    void execute(WeatherServiceCallback callback);
}
