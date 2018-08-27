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

package com.yoxjames.coldsnap.service.location;

import com.yoxjames.coldsnap.prefs.CSPreferences;
import com.yoxjames.coldsnap.prefs.PreferenceModel;

import javax.inject.Inject;

import dagger.Reusable;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by yoxjames on 7/8/17.
 */

@Reusable
public class WeatherLocationServicePreferenceImpl implements WeatherLocationService
{
    private final CSPreferences csPreferences;

    @Inject
    public WeatherLocationServicePreferenceImpl(CSPreferences csPreferences)
    {
        this.csPreferences = csPreferences;
    }

    @Override
    public Observable<SimpleWeatherLocation> getWeatherLocation()
    {
        return Observable.fromCallable(csPreferences::getCoords).subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<SimpleWeatherLocation> weatherLocationChanges()
    {
        return csPreferences.getPreferences().map(PreferenceModel::getCoords).subscribeOn(Schedulers.io());
    }

    @Override
    public Completable saveWeatherLocation(final SimpleWeatherLocation weatherLocation)
    {
        return Completable.fromRunnable(() -> csPreferences.setCoords(weatherLocation)).subscribeOn(Schedulers.io());
    }
}
