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

package com.yoxjames.coldsnap.ui.weatherpreviewbar;

import com.yoxjames.coldsnap.model.TemperatureFormatter;
import com.yoxjames.coldsnap.ui.MainActivityDataProvider;

import org.threeten.bp.Instant;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneId;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.FormatStyle;

import javax.inject.Inject;

import io.reactivex.Observable;

/**
 * Created by yoxjames on 10/15/17.
 */

public class WeatherBarModelAdapterImpl implements WeatherBarModelAdapter
{
    private final MainActivityDataProvider presenter;
    private final TemperatureFormatter temperatureFormatter;

    @Inject
    public WeatherBarModelAdapterImpl(MainActivityDataProvider presenter, TemperatureFormatter temperatureFormatter)
    {
        this.presenter = presenter;
        this.temperatureFormatter = temperatureFormatter;
    }

    @Override
    public Observable<WeatherBarViewModel> getWeatherData()
    {
        return presenter.getWeatherDataProvider()
                .map(weatherData -> new WeatherBarViewModel(weatherData.getWeatherLocation().getPlaceString(),
                        temperatureFormatter.format(weatherData.getTodayHigh()),
                        temperatureFormatter.format(weatherData.getTodayLow()),
                        localizeDateTime(weatherData.getSyncInstant())))
                .onErrorReturn(WeatherBarViewModel::errorInstance)
                .startWith(WeatherBarViewModel.nullInstance());
    }

    private String localizeDateTime(Instant instant)
    {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        DateTimeFormatter localFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT);

        return localDateTime.format(localFormatter);
    }
}
