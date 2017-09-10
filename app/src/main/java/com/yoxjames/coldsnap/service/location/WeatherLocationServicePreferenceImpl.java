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

import com.yoxjames.coldsnap.model.WeatherLocation;
import com.yoxjames.coldsnap.ui.CSPreferencesFragment;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by yoxjames on 7/8/17.
 */

public class WeatherLocationServicePreferenceImpl implements WeatherLocationService
{
    private final SharedPreferences sharedPreferences;

    @Inject
    public WeatherLocationServicePreferenceImpl(SharedPreferences sharedPreferences)
    {
        this.sharedPreferences = sharedPreferences;
    }

    @Override
    public Single<WeatherLocation> readWeatherLocation()
    {
        return Single.create(new SingleOnSubscribe<WeatherLocation>()
        {
            @Override
            public void subscribe(@NonNull SingleEmitter<WeatherLocation> e) throws Exception
            {
                final String zipCode = sharedPreferences.getString(CSPreferencesFragment.ZIPCODE, "64105");
                final String locationString = sharedPreferences.getString(CSPreferencesFragment.LOCATION_STRING, "Kansas City, MO");
                final double lat = 0f;
                final double lon = 0f;

                e.onSuccess(new WeatherLocation(zipCode, locationString, lat, lon));
            }
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public Completable saveWeatherLocation(final WeatherLocation weatherLocation)
    {
        return Completable.create(new CompletableOnSubscribe()
        {
            @Override
            public void subscribe(@NonNull CompletableEmitter e) throws Exception
            {
                final SharedPreferences.Editor preferenceEditor = sharedPreferences.edit();

                preferenceEditor.putString(CSPreferencesFragment.ZIPCODE, weatherLocation.getZipCode());
                preferenceEditor.putString(CSPreferencesFragment.LOCATION_STRING, weatherLocation.getPlaceString());
                if (preferenceEditor.commit())
                    e.onComplete();
                else
                    e.onError(new IllegalStateException("Saving preferences failed"));
            }
        }).subscribeOn(Schedulers.io());
    }
}
