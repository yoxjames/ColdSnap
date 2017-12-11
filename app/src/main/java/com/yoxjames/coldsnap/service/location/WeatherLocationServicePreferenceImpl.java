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

import android.content.SharedPreferences;

import com.yoxjames.coldsnap.ui.CSPreferencesFragment;

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
    private final SharedPreferences sharedPreferences;

    @Inject
    public WeatherLocationServicePreferenceImpl(SharedPreferences sharedPreferences)
    {
        this.sharedPreferences = sharedPreferences;
    }

    @Override
    public Observable<SimpleWeatherLocation> getWeatherLocation()
    {
        return Observable.fromCallable(() ->
        {
            final double lat = sharedPreferences.getFloat(CSPreferencesFragment.LAT, 39.098579f);
            final double lon = sharedPreferences.getFloat(CSPreferencesFragment.LON, -94.582596f);

            return new SimpleWeatherLocation(lat, lon);

        }).subscribeOn(Schedulers.io());
    }

    @Override
    public Completable saveWeatherLocation(final SimpleWeatherLocation weatherLocation)
    {
        return Completable.fromRunnable(() ->
        {
            final SharedPreferences.Editor preferenceEditor = sharedPreferences.edit();

            preferenceEditor.putFloat(CSPreferencesFragment.LAT, (float) weatherLocation.getLat());
            preferenceEditor.putFloat(CSPreferencesFragment.LON, (float) weatherLocation.getLon());

            if (!preferenceEditor.commit())
                throw new IllegalStateException("Saving preferences failed");

        }).subscribeOn(Schedulers.io());
    }

}
