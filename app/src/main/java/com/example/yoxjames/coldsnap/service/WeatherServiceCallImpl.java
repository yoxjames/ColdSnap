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

import javax.inject.Inject;
import javax.inject.Provider;

import dagger.Lazy;
import dagger.internal.Preconditions;

/**
 * Created by James Yox on 3/9/17.
 *
 * This class is designed to be the main interface through which the main thread will call WeatherService.
 * Inheriting from AsyncTask, this class already has doInBackground defined to call WeatherService. This
 * leaves onPostExecute abstract so that it can be easily implemented in the parent class through an
 * anonymous class.
 *
 */

public class WeatherServiceCallImpl implements WeatherServiceCall
{
    private Exception e;
    private final Provider<Lazy<WeatherServiceAsyncProcessor>> asyncProcessor;

    @Inject
    public WeatherServiceCallImpl(Provider<Lazy<WeatherServiceAsyncProcessor>> asyncProcessor)
    {
        this.asyncProcessor = Preconditions.checkNotNull(asyncProcessor);
    }

    public Exception getError()
    {
        return e;
    }

    @Override
    public void execute(WeatherServiceCallback callback)
    {
        asyncProcessor.get().get().execute(callback);
    }
}
