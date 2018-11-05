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

package com.yoxjames.coldsnap.model;

import com.google.auto.value.AutoValue;

import org.threeten.bp.Instant;

import java.util.List;

/**
 * Created by James Yox on 4/8/17.
 */
@AutoValue
public abstract class WeatherData
{
    public abstract Instant getSyncInstant();
    public abstract List<ForecastHour> getForecastHours();
    public abstract WeatherLocation getWeatherLocation();

    public abstract Builder toBuilder();

    public WeatherData withWeatherLocation(WeatherLocation weatherLocation)
    {
        return toBuilder().weatherLocation(weatherLocation).build();
    }

    @AutoValue.Builder
    public abstract static class Builder
    {
        public abstract Builder syncInstant(Instant syncInstant);
        public abstract Builder forecastHours(List<ForecastHour> forecastHours);
        public abstract Builder weatherLocation(WeatherLocation weatherLocation);

        public abstract WeatherData build();
    }

    public static Builder builder()
    {
        return new AutoValue_WeatherData.Builder();
    }

    public boolean isStale()
    {
        return this.getForecastHours().size() <= 0 || (getSyncInstant().getEpochSecond() + 60 * 2) <= Instant.now().getEpochSecond();
    }
}
